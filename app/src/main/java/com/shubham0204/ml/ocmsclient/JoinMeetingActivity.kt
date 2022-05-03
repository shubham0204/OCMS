package com.shubham0204.ml.ocmsclient

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shubham0204.ml.ocmsclient.databinding.ActivityJoinMeetingBinding

class JoinMeetingActivity : AppCompatActivity() {

    private lateinit var joinMeetingBinding : ActivityJoinMeetingBinding
    private lateinit var notificationManager : NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        joinMeetingBinding = ActivityJoinMeetingBinding.inflate( layoutInflater )
        setContentView( joinMeetingBinding.root )

        notificationManager = getSystemService( Context.NOTIFICATION_SERVICE) as NotificationManager

        joinMeetingBinding.joinMeetingButton.apply {
            setOnClickListener( onJoinMeetingClickListener )
            isEnabled = false
        }

        if ( !checkCameraPermission() ) {
            requestCameraPermission()
        }

        setTextListenerOnJoinButton()



    }

    private val onJoinMeetingClickListener = View.OnClickListener {
        Intent( this , MainActivity::class.java ).apply {
            this.putExtra( "user_name" , joinMeetingBinding.nameTextInputEdittext.text.toString() )
            startActivity( this )
            finish()
        }
    }

    // Check if the permission for Do Not Disturb is enabled
    // See this SO answer -> https://stackoverflow.com/a/36162332/13546426
    private fun checkNotificationAccessPermission() : Boolean {
        return notificationManager.isNotificationPolicyAccessGranted
    }

    // Check if the camera permission has been granted by the user.
    private fun checkCameraPermission() : Boolean = checkSelfPermission( Manifest.permission.CAMERA ) ==
            PackageManager.PERMISSION_GRANTED

    // Check if the audio permission has been granted by the user.
    private fun checkAudioPermission() : Boolean = checkSelfPermission( Manifest.permission.RECORD_AUDIO ) ==
            PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() {
        cameraPermissionRequestLauncher.launch( Manifest.permission.CAMERA )
    }

    private val cameraPermissionRequestLauncher = registerForActivityResult( ActivityResultContracts.RequestPermission() ) {
            isGranted ->
        if ( isGranted ) {

        }
        else {
            val alertDialog = MaterialAlertDialogBuilder( this ).apply {
                setTitle( "Camera Permission")
                setMessage( "The app couldn't function without the camera permission." )
                setCancelable( false )
                setPositiveButton( "ALLOW" ) { dialog, which ->
                    dialog.dismiss()
                    requestCameraPermission()
                }
                setNegativeButton( "CLOSE" ) { dialog, which ->
                    dialog.dismiss()
                    finish()
                }
                create()
            }
            alertDialog.show()
        }
    }

    private fun setTextListenerOnJoinButton() {
        joinMeetingBinding.nameTextInputEdittext.addTextChangedListener( object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                joinMeetingBinding.joinMeetingButton.isEnabled = s!!.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

}