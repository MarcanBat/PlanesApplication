package com.example.planeapplication
import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import com.example.planeapplication.data.AirportManager

/**
 * Created by sergio on 2019-11-10
 * All rights reserved GoodBarber
 */
class FlightApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        application = this
        appContext = getApplicationContext()
        AirportManager.getInstance()
    }

    companion object {
        private val TAG = FlightApplication::class.java.simpleName

        var appContext: Context? = null
            private set

        var application: Application? = null
            private set

        val appResources: Resources
            get() = appContext!!.resources

        val appAssetManager: AssetManager
            get() = appContext!!.assets
    }
}