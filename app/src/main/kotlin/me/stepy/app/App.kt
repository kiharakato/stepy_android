package me.stepy.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Point
import android.os.Build
import android.view.Display
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*
import kotlin.properties.Delegates

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        App.context = applicationContext

        val realmConfiguration = RealmConfiguration.Builder(this).build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }

    companion object {
        var context: Context by Delegates.notNull()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
    }

}