package com.shubham0204.ml.ocmsclient

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log

class OnScreenAppListener(private var context: Context ) {

    private var usageStatsManager : UsageStatsManager =
        context.getSystemService( Context.USAGE_STATS_SERVICE ) as UsageStatsManager
    private var packageManager : PackageManager = context.packageManager
    private var nonSystemAppInfoMap : Map<String,String>

    init {
        nonSystemAppInfoMap = getNonSystemAppsList()
    }

    // https://github.com/ricvalerio/foregroundappchecker/blob/master/fgchecker/src/main/java/com/rvalerio/fgchecker/detectors/LollipopDetector.java
    fun getForegroundApp() : String? {
        var foregroundAppPackageName : String? = null
        val currentTime = System.currentTimeMillis()
        val usageEvents = usageStatsManager.queryEvents( currentTime - (1000*60*10) , currentTime )
        val usageEvent = UsageEvents.Event()
        while ( usageEvents.hasNextEvent() ) {
            usageEvents.getNextEvent( usageEvent )
            //Log.e( "APP" , "${usageEvent.packageName} ${usageEvent.timeStamp}" )
            if ( usageEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED ) {
                foregroundAppPackageName = usageEvent.packageName
                Log.e( "APP" , usageEvent.packageName )
            }
        }
        return foregroundAppPackageName
    }

    private fun getNonSystemAppsList() : Map<String,String> {
        // https://medium.com/androiddevelopers/package-visibility-in-android-11-cc857f221cd9
        val appInfos = packageManager.getInstalledApplications( PackageManager.GET_META_DATA )
        val appInfoMap = HashMap<String,String>()
        for ( appInfo in appInfos ) {
            // https://stackoverflow.com/a/8483920/13546426
            if ( appInfo.flags != ApplicationInfo.FLAG_SYSTEM ) {
                appInfoMap[ appInfo.packageName ]= packageManager.getApplicationLabel( appInfo ).toString()
            }
        }
        return appInfoMap
    }

}