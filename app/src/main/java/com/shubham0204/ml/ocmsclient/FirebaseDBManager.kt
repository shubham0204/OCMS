package com.shubham0204.ml.ocmsclient

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDBManager( userID : String ) {

    private val firebaseDatabaseURL = "https://ocms-12aea-default-rtdb.firebaseio.com"
    private var userDBReference : DatabaseReference = FirebaseDatabase
        .getInstance( firebaseDatabaseURL )
        .reference
        .child( userID )

    val STATUS_ON_SCREEN = "on_screen_status"

    fun updateOnScreenStatus( status : Boolean ) {
        userDBReference.child( STATUS_ON_SCREEN )
            .setValue( status )
            .addOnSuccessListener {

            }
            .addOnFailureListener { exception ->

            }
    }



}