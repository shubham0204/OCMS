package com.shubham0204.ml.ocmsclient

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDBManager( userID : String ) {

    private val firebaseDatabaseURL = "https://ocms-12aea-default-rtdb.firebaseio.com"
    private var userDBReference : DatabaseReference = FirebaseDatabase
        .getInstance( firebaseDatabaseURL )
        .reference
        .child( userID )

    private val ON_SCREEN_STATUS = "on_screen_status"
    private val ON_SCREEN_APP = "on_screen_app"
    private val CAMERA_PERMISSION_STATUS = "camera_status"
    private val AUDIO_PERMISSION_STATUS = "audio_status"
    private val USAGE_STATS_PERMISSION_STATUS = "usage_status"
    private val LOCATION_STATUS = "location"
    private val PRESENCE_STATUS = "presence"
    private val NOTIFICATION_ACCESS_PERMISSION_STATUS = "notification_access"


    fun updateOnScreenStatus( status : Boolean ) {
        Log.e( "APP" , "called" )
        userDBReference.child( ON_SCREEN_STATUS )
            .setValue( status )
            .addOnSuccessListener {
                Log.e( "App" , "updated" )
            }
            .addOnFailureListener { exception ->
                Log.e( "App" , "exception ${exception.message}" )
            }
    }

    fun updateOnScreenApp( appName : String ) {
        Log.e( "APP" , "called 2" )
        userDBReference.child( ON_SCREEN_APP )
            .setValue( appName )
            .addOnSuccessListener {
                Log.e( "App" , "updated" )
            }
            .addOnFailureListener { exception ->
                Log.e( "App" , "exception ${exception.message}" )
            }
    }

    fun updateNotificationAccessPermissionStatus( isEnabled: Boolean ) {
        userDBReference.child( NOTIFICATION_ACCESS_PERMISSION_STATUS )
            .setValue( isEnabled )
            .addOnSuccessListener {
                Log.e( "App" , "updated" )
            }
            .addOnFailureListener { exception ->
                Log.e( "App" , "exception ${exception.message}" )
            }
    }

    fun updateCameraPermissionStatus( isEnabled : Boolean ) {
        userDBReference.child( CAMERA_PERMISSION_STATUS )
            .setValue( isEnabled )
            .addOnSuccessListener {
                Log.e( "App" , "updated" )
            }
            .addOnFailureListener { exception ->
                Log.e( "App" , "exception ${exception.message}" )
            }
    }

    fun updateAudioPermissionStatus( isEnabled: Boolean ) {
        userDBReference.child( AUDIO_PERMISSION_STATUS )
            .setValue( isEnabled )
            .addOnSuccessListener {
                Log.e( "App" , "updated" )
            }
            .addOnFailureListener { exception ->
                Log.e( "App" , "exception ${exception.message}" )
            }
    }

    fun updateAppUsagePermissionStatus( isEnabled: Boolean ) {
        userDBReference.child( USAGE_STATS_PERMISSION_STATUS )
            .setValue( isEnabled )
            .addOnSuccessListener {
                Log.e( "App" , "updated" )
            }
            .addOnFailureListener { exception ->
                Log.e( "App" , "exception ${exception.message}" )
            }
    }

    fun updatePresenceStatus( status : String ) {
        userDBReference.child( PRESENCE_STATUS )
            .setValue( status )
            .addOnSuccessListener {
                Log.e( "App" , "updated" )
            }
            .addOnFailureListener { exception ->
                Log.e( "App" , "exception ${exception.message}" )
            }
    }

    fun updateLocationStatus( localityName : String ) {
        userDBReference.child( LOCATION_STATUS )
            .setValue( localityName )
            .addOnSuccessListener {
                Log.e( "App" , "updated" )
            }
            .addOnFailureListener { exception ->
                Log.e( "App" , "exception ${exception.message}" )
            }
    }





}