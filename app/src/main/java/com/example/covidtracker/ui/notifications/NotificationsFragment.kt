package com.example.covidtracker.ui.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.covidtracker.MainActivity
import com.example.covidtracker.PrefConfig
import com.example.covidtracker.PreferenceManager
import com.example.covidtracker.R
import com.example.covidtracker.databinding.FragmentNotificationsBinding
import com.example.covidtracker.models.NotificationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var notificationAdapter1: NotificationAdapter? = null

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotificationsBinding.bind(view)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        binding.notificationsRecycler.layoutManager = linearLayoutManager
        var notifcationArray = PrefConfig.readListFromPref(requireContext())



        if(notifcationArray == null) {
            notifcationArray = mutableListOf<NotificationModel>()
            binding.noMessagesTextview.visibility = View.VISIBLE
        } else {
            binding.noMessagesTextview.visibility = View.GONE
        }

        notificationReader(notifcationArray)
        ///Notification test model for server get data
        notificationAdapter1 = NotificationAdapter(notifcationArray)

        binding.notificationsRecycler.adapter = notificationAdapter1
        GlobalScope.launch(Dispatchers.Main) {
            PreferenceManager.preferencesFlow.collect {
                if (it[PreferenceManager.PreferencesKeys.lastNotification_counter] != 0) {
                    binding.notificationsRecycler.scrollToPosition(notifcationArray.size - it[PreferenceManager.PreferencesKeys.lastNotification_counter]!!)
                }
            }
        }
    }

    fun notificationReader(notifcationArray: MutableList<NotificationModel>) {
        try {
            notifcationArray.sortBy {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                sdf.parse(it.date)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun updateAdapter(item: NotificationModel) {
        notificationAdapter1?.addNotification(item, notificationAdapter1!!.itemCount)
    }

}
