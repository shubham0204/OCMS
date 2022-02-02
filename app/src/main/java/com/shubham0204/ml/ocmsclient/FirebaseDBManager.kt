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

    val ON_SCREEN_STATUS = "on_screen_status"
    val ON_SCREEN_APP = "on_screen_app"

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



}