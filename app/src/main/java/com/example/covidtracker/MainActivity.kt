package com.example.covidtracker

import android.R.attr.password
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.covidtracker.databinding.ActivityMainBinding
import com.example.covidtracker.databinding.AppbarBinding
import com.example.covidtracker.models.NotificationModel
import com.example.covidtracker.models.StatusModel
import com.example.covidtracker.models.User
import com.example.covidtracker.ui.home.HomeFragment
import com.example.covidtracker.ui.home.HomeViewModel
import com.example.covidtracker.ui.notifications.NotificationAdapter
import com.example.covidtracker.ui.notifications.NotificationsFragment
import com.example.covidtracker.ui.settings.SettingsFragment
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val CHANNEL_ID = "Covid Tracker"
    val label: ArrayList<String> = ArrayList()
    val text: ArrayList<String> = ArrayList()
    val date: ArrayList<String> = ArrayList()
    lateinit var notificationsFragment: NotificationsFragment
    lateinit var navView: BottomNavigationView
    lateinit var topAppBar: AppbarBinding
    private lateinit var homeViewModel: HomeViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val TTL_IN_SECONDS: Int = 3 * 60 // Three minutes.

    private val PUB_SUB_STRATEGY = Strategy.Builder()
        .setTtlSeconds(TTL_IN_SECONDS).build()
    private val MISSING_API_KEY = "It's possible that you haven't added your" +
            " API-KEY. See  " +
            "https://developers.google.com/nearby/messages/android/get-started#step_4_configure_your_project"
    /**
     * The [Message] object used to broadcast information about the device to nearby devices.
     */
    private var mMessage: Message? = null

    /**
     * A [MessageListener] for processing messages from nearby devices.
     */
    private var mMessageListener: MessageListener? = null
    /**
     * Adapter for working with messages from nearby publishers.
     */
    private var mNearbyDevicesArrayAdapter: ArrayAdapter<String>? = null

    public fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Covid tracker App"
            val descriptionText = "Covid Tracker App"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            Toast.makeText(this, "IT SHOULD WORK", Toast.LENGTH_SHORT).show()

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val settingsIcon = findViewById<ImageView>(R.id.menu_settings)

        notificationsFragment = NotificationsFragment()
        topAppBar = binding.topAppbar

        navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_information
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        supportActionBar?.hide()

        settingsIcon.setOnClickListener {
//            val settingsFragment = SettingsFragment()
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.nav_host_fragment_activity_main, settingsFragment, "findThisFragment")
//                .addToBackStack(null)
//                .commit()
            navController.navigate(R.id.settingsFragment)

        }

        createNotificationChannel()

        GlobalScope.launch {
            getStatus().child("color").get().addOnSuccessListener {
                mMessage = Message(it.value.toString().toByteArray(Charset.forName("UTF-8")))
            }
        }
        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message) {
                val date = System.currentTimeMillis()

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val mDate = sdf.format(date)

                GlobalScope.launch {
                    getStatus().child("color").get().addOnSuccessListener {
                        if (it.value.toString() == "blue" || it.value.toString() == "yellow" || it.value.toString() == "green") {
                            Toast.makeText(this@MainActivity, it.value.toString(), Toast.LENGTH_LONG).show()
                            Toast.makeText(this@MainActivity, String(message.content), Toast.LENGTH_LONG).show()

                            if (String(message.content) == "red") {
                                val jsonstring =
                                    "{\n" + "  \"Notifications\": [\n" +
                                            "    {\n" + "      \"label\": \"Infected person is found\",\n" +
                                            "      \"text\": \"Please take precautions\",\n" +
                                            "      \"date\": \"${mDate}\"\n" + "    },\n" +
                                            "  ]\n" +
                                            "}"
                                val statusModel = StatusModel(
                                    "yellow",
                                    "On $mDate",
                                    "High risk possibility of infection",
                                    "Updated: Today, $mDate"
                                )
                                GlobalScope.launch {
                                    App().getUser().child("status").setValue(statusModel)
                                }
                                mMessage = Message(it.value.toString().toByteArray(Charset.forName("UTF-8")))

                                notify(jsonstring)
                            } else if (String(message.content) == "yellow") {
                                val jsonstring =
                                    "{\n" + "  \"Notifications\": [\n" +
                                            "    {\n" + "      \"label\": \"Person with high risk infection is found\",\n" +
                                            "      \"text\": \"Please take precautions\",\n" +
                                            "      \"date\": \"${mDate}\"\n" + "    },\n" +
                                            "  ]\n" +
                                            "}"
                                val statusModel = StatusModel(
                                    "yellow",
                                    "On $mDate",
                                    "High risk possibility of infection",
                                    "Updated: Today, $mDate"
                                )
                                mMessage = Message(it.value.toString().toByteArray(Charset.forName("UTF-8")))

                                GlobalScope.launch {
                                    App().getUser().child("status").setValue(statusModel)
                                }
                                notify(jsonstring)
                            }
                        }
                    }
                }
            }

            override fun onLost(message: Message) {
                // Called when a message is no longer detectable nearby.
                val msgBody = String(message.content)
            }
        }

        navView.setupWithNavController(navController)
    }

    private fun setStatus(status: StatusModel) {
        GlobalScope.launch {
            App.database.getReference("users")
                .child(PreferenceManager.getUserId())
                .child("status").setValue(status)
        }
    }

    fun notify(jsonstring: String) {
        try {

            val json = JSONObject(jsonstring)
            val jsonArray = json.getJSONArray("Notifications")
            var notifcationArray = mutableListOf<NotificationModel>()
            var notificationList = PrefConfig.readListFromPref(this)

            if(notificationList == null) {
                notificationList = mutableListOf<NotificationModel>()
            }

            for (i in 0 until jsonArray.length()-1) {
                val notificationModel = NotificationModel(
                    jsonArray.getJSONObject(i).getString("label"),
                    jsonArray.getJSONObject(i).getString("text"),
                    jsonArray.getJSONObject(i).getString("date")
                )
                notifcationArray.add(notificationModel)
                notificationList.add(notificationModel)
                PrefConfig.writeListInPref(applicationContext, notificationList)
                val fragment = supportFragmentManager.findFragmentById(R.id.navigation_notifications) as NotificationsFragment?
                fragment?.updateAdapter(notificationModel)
            }
            notifcationArray.sortBy {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                sdf.parse(it.date)
            }

            for (i in notifcationArray) {
                label.add(i.title)
                text.add(i.text)
                date.add(i.date)
            }
//            var notificationContext: Context = this
//            if (intent.hasExtra("notificationData")) {
//                notificationContext = applicationContext
//            }
//
//            val notificationBundle = Bundle()
//            notificationBundle.putBoolean("notificationData", true)



            val resultIntent = Intent(this, MainActivity().javaClass)
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            val bundle = Bundle()
//            bundle.putString("NotificationData", "notification")
            val pi = PendingIntent.getActivity(this, 0, resultIntent, 0)

            var builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Covid Tracker App")
                .setContentText(text[text.size - 1])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setContentIntent(pi).setAutoCancel(true)
                .setColor(2)

            val notification: Notification = builder.build()
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(text.size, notification)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

     fun publish() {
        val options = PublishOptions.Builder()
            .setStrategy(PUB_SUB_STRATEGY)
            .setCallback(object : PublishCallback() {
                override fun onExpired() {
                    super.onExpired()
                }
            }).build()
        Nearby.getMessagesClient(this).publish(mMessage!!, options)
            .addOnFailureListener { e: Exception? ->
                Toast.makeText(this, MISSING_API_KEY, Toast.LENGTH_SHORT).show()
            }
    }


    /**
     * Stops publishing message to nearby devices.
     */
     fun unpublish() {
        Nearby.getMessagesClient(this).unpublish(mMessage!!)
    }

    /**
     * Subscribes to messages from nearby devices and updates the UI if the subscription either
     * fails or TTLs.
     */
     fun subscribe() {
        mNearbyDevicesArrayAdapter?.clear()
        val options = SubscribeOptions.Builder()
            .setStrategy(PUB_SUB_STRATEGY)
            .setCallback(object : SubscribeCallback() {
                override fun onExpired() {
                    super.onExpired()
                }
            }).build()
        Nearby.getMessagesClient(this).subscribe(mMessageListener!!, options)
    }

    /**
     * Stops subscribing to messages from nearby devices.
     */
     fun unsubscribe() {
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener!!)
    }

    private suspend fun getStatus(): DatabaseReference {
        return App.database.getReference("users")
            .child(PreferenceManager.getUserId())
            .child("status")
    }
}