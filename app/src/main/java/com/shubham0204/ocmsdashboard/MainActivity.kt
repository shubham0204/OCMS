package com.shubham0204.ocmsdashboard

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.shubham0204.ocmsdashboard.databinding.ActivityMainBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.StringBuilder
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding : ActivityMainBinding
    private lateinit var studentStatsAdapter: StudentStatsAdapter
    private lateinit var reportString : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate( layoutInflater )
        setContentView( activityMainBinding.root )

        // Documentation
        val dataRepository = StudentStatsRepository( FirebaseDatabase.getInstance().reference )
        studentStatsAdapter = StudentStatsAdapter(
            this ,
            dataRepository.studentStatsFlow ,
            studentStatLongClickListener
        )
        activityMainBinding.recyclerview.layoutManager = LinearLayoutManager( this )
        activityMainBinding.recyclerview.adapter = studentStatsAdapter
        activityMainBinding.recyclerview.itemAnimator = null

        activityMainBinding.downloadReportButton.setOnClickListener{ downloadReport() }
        activityMainBinding.downloadReportButton.setOnLongClickListener{ _ ->
            studentStatsAdapter.clearAll()
            FirebaseDatabase.getInstance().reference.removeValue().addOnSuccessListener {
                Toast.makeText( this@MainActivity , "Database cleared." , Toast.LENGTH_LONG ).show()
            }
            true
        }

        activityMainBinding.attendanceReportButton.setOnClickListener { downloadAttendanceReport() }

    }

    private fun downloadReport() {
        val reportBuilder = StringBuilder()
        studentStatsAdapter.report.forEach{
            reportBuilder.append( it.first + " " + it.second + "\n" )
        }
        reportString = reportBuilder.toString()
        openCreateFileDialog( "report.txt" )
    }

    private fun downloadAttendanceReport() {
        val reportBuilder = StringBuilder()
        val names = studentStatsAdapter.idNameMap
        studentStatsAdapter.attendanceTimeReport.forEach{
            reportBuilder.append( names[ it.component1() ] + " " + millisToMinutes( it.component2()[0] ) + "\n" )
        }
        reportString = reportBuilder.toString()
        openCreateFileDialog( "attendance_report.txt" )
    }

    private fun millisToMinutes( millis : Long ) : Double {
        return ( millis / ( 1000.0 * 60 ) )
    }

    private fun openCreateFileDialog( filename : String ) {
        // https://developer.android.com/training/data-storage/shared/documents-files#create-file
        val intent = Intent( Intent.ACTION_CREATE_DOCUMENT ).apply {
            addCategory( Intent.CATEGORY_OPENABLE )
            type = "text/plain"
            putExtra( Intent.EXTRA_TITLE , filename )
        }
        createFileIntentLauncher.launch( intent )
    }

    private val createFileIntentLauncher = registerForActivityResult( ActivityResultContracts.StartActivityForResult() ) {
        val fileUri = it.data?.data
        if ( fileUri != null ) {
            try {
                val outputStream = contentResolver.openOutputStream( fileUri )
                outputStream?.write( reportString.toByteArray() )
                outputStream?.close()
            }
            catch ( e : FileNotFoundException ) {
                e.printStackTrace()
            }
        }
    }

    private val studentStatLongClickListener = object : StudentStatsAdapter.StudentStatsInteractCallback {

        override fun onStudentStatLongClickListener(studentStats: StudentStats) {

        }

    }

}