package com.shubham0204.ml.ocmsclient

import android.graphics.*
import android.media.Image
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class FrameAnalyzer( private val firebaseDBManager: FirebaseDBManager ) : ImageAnalysis.Analyzer {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode( FaceDetectorOptions.PERFORMANCE_MODE_FAST )
        .build()
    private val detector = FaceDetection.getClient(realTimeOpts)
    private lateinit var currentFrameImage : ImageProxy
    private var isProcessing = false


    override fun analyze(image: ImageProxy) {
        Log.e( "APP" , "started" )
        currentFrameImage = image
        if ( !isProcessing ) {
            isProcessing = true
            val inputImage = InputImage.fromMediaImage( image.image!! , image.imageInfo.rotationDegrees )
            detector.process( inputImage )
                .addOnSuccessListener { faces ->
                    if ( faces.size == 1 ) {
                        firebaseDBManager.updatePresenceStatus( "Present" )
                        Log.e( "APP" , "present" )
                    }
                    else {
                        firebaseDBManager.updatePresenceStatus( "Absent" )
                        Log.e( "APP" , "absent" )
                    }
                }
                .addOnFailureListener {  exception ->

                }
                .addOnCompleteListener {
                    isProcessing = false
                    currentFrameImage.close()
                }
        }
        else {
            currentFrameImage.close()
        }
    }

    // Rotate the given `source` by `degrees`.
    // See this SO answer -> https://stackoverflow.com/a/16219591/10878733
    private fun rotateBitmap( source: Bitmap , degrees : Float ): Bitmap {
        val matrix = Matrix()
        matrix.postRotate( degrees )
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix , false )
    }

    // Flip the given `Bitmap` horizontally.
    // See this SO answer -> https://stackoverflow.com/a/36494192/10878733
    private fun flipBitmap( source: Bitmap ): Bitmap {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f, source.width / 2f, source.height / 2f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

}