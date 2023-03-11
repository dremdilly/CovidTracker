package com.example.covidtracker.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.covidtracker.PreferenceManager
import com.example.covidtracker.R
import com.example.covidtracker.models.NotificationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val NotificationList: MutableList<NotificationModel>): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewLabel: TextView = view.findViewById(R.id.notification_label)
        val textViewText : TextView = view.findViewById(R.id.notification_text)
        val textViewTime : TextView = view.findViewById(R.id.notification_time)
        val textViewToday: TextView = view.findViewById(R.id.Date_Label_textView)
        val messageIcon: ImageView = view.findViewById(R.id.message_icon2)
        val textViewNotreeded: TextView = view.findViewById(R.id.textView2)

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false)

        return NotificationViewHolder(adapterLayout)
    }

    override fun getItemCount() = NotificationList.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val label: ArrayList<String> = ArrayList()
        val text: ArrayList<String> = ArrayList()
        val date: ArrayList<String> = ArrayList()
        val datetolabel: ArrayList<String> = ArrayList()
        for (i in NotificationList) {
            label.add(i.title)
            text.add(i.text)
            date.add(i.date)
        }
        val notification = label[position]
        val text_notification= text[position]
        val date_notification= date[position]


        fun Date.dateToString(format: String): String {
            val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
            return dateFormatter.format(this)
        }
        val i =1
        val yesterday = Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24))
        holder.textViewToday.visibility = TextView.VISIBLE
        //call the extension function on a date object
        val dateFormat: DateFormat

        dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")

        val mDate: Date = dateFormat.parse(date_notification)
        if (mDate.dateToString("DD").equals(Date().dateToString("DD"))){
            holder.textViewToday.text = "Today"

        }
        else if (mDate.dateToString("DD").equals(yesterday.dateToString("DD"))){
            holder.textViewToday.text = "Yesterday"
        }
        else{
            holder.textViewToday.text = mDate.dateToString("yyyy.MM.dd")
            i+1
            if (i>1){
                holder.textViewToday.visibility = TextView.INVISIBLE
            }
        }

        if(position >= 1) {
            val previous_date_notification= date[position-1]
            val pDate: Date = dateFormat.parse(previous_date_notification)

            if (mDate.dateToString("yyyy-MM-dd") == pDate.dateToString("yyyy-MM-dd")) {
                holder.textViewToday.visibility = View.GONE
            }
        }
        val time: String = mDate.dateToString("k:mm")

        holder.textViewLabel.text=notification
        holder.textViewText.text=text_notification
        holder.textViewTime.text= time

        GlobalScope.launch(Dispatchers.Main) {
            PreferenceManager.preferencesFlow.collect {
                if (position==NotificationList.size-it[PreferenceManager.PreferencesKeys.lastNotification_counter]!! && it[PreferenceManager.PreferencesKeys.lastNotification_counter]!=0 ){
                    print(it[PreferenceManager.PreferencesKeys.lastNotification_counter])
                    holder.textViewNotreeded.visibility= TextView.VISIBLE
                    PreferenceManager.updateLastNotificationsCount(0)
                }
            }
        }
    }

    fun addNotification(item: NotificationModel, position: Int) {
        NotificationList.add(item)
        notifyItemInserted(position)
    }
}