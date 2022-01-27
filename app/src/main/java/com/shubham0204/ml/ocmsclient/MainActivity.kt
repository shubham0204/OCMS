package com.shubham0204.ml.ocmsclient

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shubham0204.ml.ocmsclient.databinding.ActivityMainBinding
import android.app.AppOpsManager
import android.os.*
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding : ActivityMainBinding
    private val handler = Handler( Looper.getMainLooper() )
    private lateinit var onScreenAppListener : OnScreenAppListener
    private val userID = "shubham_panchal"
    private lateinit var firebaseDBManager: FirebaseDBManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate( layoutInflater )
        setContentView( viewBinding.root )

        // foregroundAppListener = ForegroundAppListener( this )

        /*if ( checkCameraPermission() ) {
            startCameraPreview()
        }
        else {
            requestCameraPermission()
        }*/

        // lifecycle.addObserver( lifecycleEventObserver )

        //foregroundAppListener.getForegroundApp()
        firebaseDBManager = FirebaseDBManager( userID )
           
        val ref = database.reference.child( "world" ).push()
        ref.setValue( "hello" )
            .addOnFailureListener {
                Log.e( "APP" , it.message!! )
            }
            .addOnSuccessListener {
                Log.e( "APP" , "success" )
            }
            .addOnCanceledListener {
                Log.e( "APP" , "cancelled" )
            }


    }

    private val activityLifecycleCallback = object : OnScreenStatusListener.Callback {

        override fun inForeground(secondsSinceBackground: Int?) {
            firebaseDBManager.updateOnScreenStatus( true )
        }

        override fun inBackground() {
            firebaseDBManager.updateOnScreenStatus( false )
        }

    }

    private fun scheduleCheck() {
        handler.postDelayed( runnable , 5000 )
    }

    private val runnable = Runnable() {
        onScreenAppListener.getForegroundApp()
        scheduleCheck()
    }

    private val lifecycleEventObserver = LifecycleEventObserver { source, event ->
        if (event == Lifecycle.Event.ON_RESUME ) {
            Log.e( "APP" , "resumed" )
        }
        else if ( event == Lifecycle.Event.ON_PAUSE ) {
            Log.e( "APP" , "paused" )
            //scheduleCheck()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(
                    Intent( this, ForegroundAppService::class.java) )
            }
            else {
                startService( Intent( this, ForegroundAppService::class.java) )
            }
            /*Handler( Looper.getMainLooper() ).postDelayed( Runnable {
                val activityManager = getSystemService( Context.ACTIVITY_SERVICE ) as ActivityManager
                for ( process in activityManager.getRunningTasks() ) {
                    Log.e( "APP" , process.processName )
                    if ( process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ) {
                        Log.e( "APP" , process.processName )
                    }
                }

            } , 5000 )*/
        }
    }

    // The `PACKAGE_USAGE_STATS` permission is a not a runtime permission and hence cannot be
    // requested directly using `ActivityCompat.requestPermissions`. All special permissions
    // are handled by `AppOpsManager`.
    private fun checkUsageStatsPermission() : Boolean {
        val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        // `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), packageName
            )
        }
        else {
            appOpsManager.checkOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }


    private fun requestCameraPermission() {
        cameraPermissionRequestLauncher.launch( Manifest.permission.CAMERA )
    }

    private val cameraPermissionRequestLauncher = registerForActivityResult( ActivityResultContracts.RequestPermission() ) {
            isGranted ->
        if ( isGranted ) {
            startCameraPreview()
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

    private fun checkCameraPermission() : Boolean {
        return ActivityCompat.checkSelfPermission( this , Manifest.permission.CAMERA ) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance( this )
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview : Preview = Preview.Builder().build()
            val cameraSelector : CameraSelector = CameraSelector.Builder()
                .requireLensFacing( CameraSelector.LENS_FACING_BACK )
                .build()
            // preview.setSurfaceProvider( viewBinding.previewView.surfaceProvider )
            val imageFrameAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio( AspectRatio.RATIO_4_3 )
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            //imageFrameAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), frameAnalyzer )
            cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview , imageFrameAnalysis )
        }, ContextCompat.getMainExecutor(this) )
    }

}
