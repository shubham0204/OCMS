package com.shubham0204.ocmsdashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.shubham0204.ocmsdashboard.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding : ActivityMainBinding
    private val ON_SCREEN_STATUS = "on_screen_status"
    private val ON_SCREEN_APP = "on_screen_app"
    private val CAMERA_PERMISSION_STATUS = "camera_status"
    private val AUDIO_PERMISSION_STATUS = "audio_status"
    private val USAGE_STATS_PERMISSION_STATUS = "usage_status"
    private val PRESENCE_STATUS = "presence"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate( layoutInflater )
        setContentView( activityMainBinding.root )

        /*val studentStatusRef = FirebaseDatabase.getInstance().reference.child( "shubham_panchal" )
        studentStatusRef.addValueEventListener( object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.child( ON_SCREEN_STATUS ).getValue( Boolean::class.java )
                val app = snapshot.child( ON_SCREEN_APP ).getValue( String::class.java )
                val cameraPermission = snapshot.child( CAMERA_PERMISSION_STATUS ).getValue( Boolean::class.java )
                val audioPermission = snapshot.child( AUDIO_PERMISSION_STATUS ).getValue( Boolean::class.java )
                val usagePermission = snapshot.child( USAGE_STATS_PERMISSION_STATUS ).getValue( Boolean::class.java )
                val presenceStatus = snapshot.child( PRESENCE_STATUS ).getValue( String::class.java )
                activityMainBinding.textView.text =
                    "User on screen: $status \nApp on screen: $app \n" +
                            "Camera permission: $cameraPermission \nAudio permission: $audioPermission \n" +
                            "Usage access permission: $usagePermission \nPresence: $presenceStatus"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })*/

        val dataRepository = StudentStatsRepository( FirebaseDatabase.getInstance().reference )
        val studentStatsAdapter = StudentStatsAdapter( this , dataRepository.studentStatsFlow )
        activityMainBinding.recyclerview.layoutManager = LinearLayoutManager( this )
        activityMainBinding.recyclerview.adapter = studentStatsAdapter


    }

}