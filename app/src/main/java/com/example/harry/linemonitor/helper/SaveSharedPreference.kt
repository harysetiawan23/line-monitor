package com.example.harry.linemonitor.helper

import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.harry.linemonitor.helper.PreferencesUtility.LOGGED_IN_PREF
import com.example.harry.linemonitor.helper.PreferencesUtility.USER_TOKEN

class SaveSharedPreference {


    internal fun getPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     */
    fun setLoggedIn(context: Context, loggedIn: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(LOGGED_IN_PREF, loggedIn)
        editor.apply()
    }

    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */
    fun getLoggedStatus(context: Context): Boolean {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false)
    }


    fun getToken(context: Context):String{
        return getPreferences(context).getString(USER_TOKEN, "")
    }
}