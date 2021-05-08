package com.example.theworkofalllife.model

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object PrefenceHelper{
    val ID_INCREMENT = "ID_INCREMENT"

    fun defaultPreference(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun customPreference(context: Context, name: String): SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation:
                                            (SharedPreferences.Editor) -> Unit){
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }
    var SharedPreferences.productID
        get() = getInt(ID_INCREMENT, 0)
        set(value){
            editMe { it.putInt(ID_INCREMENT, value) }
        }
    var SharedPreferences.clearValues
        get() = { }
        set(value) {
            editMe {
                it.clear()
            }
        }
}