package com.shubham0204.ocmsdashboard

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class StudentStatsRepository(private var firebaseDBRef : DatabaseReference ) {

    var studentStatsFlow : Flow<StudentStats> = callbackFlow {

        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                trySend( StudentStats.fromSnapshot( snapshot ) )
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                trySend( StudentStats.fromSnapshot( snapshot ))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        firebaseDBRef.addChildEventListener( childEventListener )
        awaitClose {
            firebaseDBRef.removeEventListener( childEventListener )
        }
    }


}