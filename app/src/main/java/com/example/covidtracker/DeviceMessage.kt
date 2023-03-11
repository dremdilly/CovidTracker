package com.example.covidtracker

import com.google.android.gms.nearby.messages.Message
import java.nio.charset.Charset
import com.google.gson.Gson

data class DeviceMessage(val userUUID: String, val username: String, val messageBody: String, val creationTime: Long) {

    val message: Message
        get() = Message(gson.toJson(this).toByteArray(Charset.forName("UTF-8")))

    companion object {
        private val gson = Gson()

        fun fromNearbyMessage(message: Message): DeviceMessage {
            val nearbyMessageString = String(message.content).trim { it <= ' ' }
            return gson.fromJson(
                String(nearbyMessageString.toByteArray(Charset.forName("UTF-8"))),
                DeviceMessage::class.java)
        }
    }
}