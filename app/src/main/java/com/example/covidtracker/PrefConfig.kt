package com.example.covidtracker

import android.content.Context
import android.preference.PreferenceManager
import com.example.covidtracker.models.NotificationModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object PrefConfig {
    private val LIST_KEY = "list_key"

    fun writeListInPref(context: Context, list: List<NotificationModel> ) {
        val gson = Gson()
        val jsonString = gson.toJson(list)

        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putString(LIST_KEY, jsonString)
        editor.apply()
    }

    fun readListFromPref(context: Context): MutableList<NotificationModel>? {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val jsonString = pref.getString(LIST_KEY, "")

        val gson = Gson()
        val type = object : TypeToken<ArrayList<NotificationModel?>?>() {}.type
        val list: MutableList<NotificationModel>? = gson.fromJson(jsonString, type)
        return list
    }
}
