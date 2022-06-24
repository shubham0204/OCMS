package com.shubham0204.ocmsdashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.shubham0204.ocmsdashboard.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding : ActivityMainBinding
    private lateinit var studentStatsAdapter: StudentStatsAdapter
    private lateinit var reportString : String
    private lateinit var studentSummaryBottomSheet : SummaryBottomSheetFragment
    private lateinit var summaryBottomSheetViewModel : SummaryBottomSheetViewModel
    private val handler = Handler( Looper.getMainLooper() )
    private val COUNT_UPDATE_INTERVAL = 10000L
    private val idNameMap = HashMap<String,String>()
    private val startTimeMap = HashMap<String,Long>()
    private val endTimeMap = HashMap<String,Long>()
    private val ATTENDANCE_THRESHOLD = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate( layoutInflater )
        changeLanguage()
        setContentView( activityMainBinding.root )

        // Documentation
        val dataRepository = StudentStatsRepository( FirebaseDatabase.getInstance().reference )
        studentStatsAdapter = StudentStatsAdapter(
            this ,
            dataRepository.studentStatsFlow ,
            studentStatInteractCallback
        )
        activityMainBinding.recyclerview.layoutManager = LinearLayoutManager( this )
        activityMainBinding.recyclerview.adapter = studentStatsAdapter
        activityMainBinding.recyclerview.itemAnimator = null

        summaryBottomSheetViewModel = ViewModelProvider( this )[SummaryBottomSheetViewModel::class.java]

        activityMainBinding.swipeRefreshLayout.setOnRefreshListener{
            activityMainBinding.swipeRefreshLayout.isRefreshing = false
        }

        activityMainBinding.downloadReportButton.setOnClickListener{ downloadReport() }
        activityMainBinding.downloadReportButton.setOnLongClickListener{ _ ->
            studentStatsAdapter.clearAll()
            FirebaseDatabase.getInstance().reference.removeValue().addOnSuccessListener {
                Toast.makeText( this@MainActivity , "Database cleared." , Toast.LENGTH_LONG ).show()
            }
            true
        }

        activityMainBinding.attendanceReportButton.setOnClickListener { downloadAttendanceReport() }
        studentSummaryBottomSheet = SummaryBottomSheetFragment()
        BottomSheetBehavior.from( activityMainBinding.summarySheetContainer ).apply {
            isHideable = false
            peekHeight = 100
        }

        activityMainBinding.showSummaryButton.setOnClickListener {
            studentSummaryBottomSheet.show( supportFragmentManager , SummaryBottomSheetFragment.TAG )
        }

        startCountUpdater()


    }

    private fun startCountUpdater() {
        handler.postDelayed( countUpdateRunnable , COUNT_UPDATE_INTERVAL )
    }

    private val countUpdateRunnable = Runnable {
        var presentStudentsCount = 0
        var onScreenStudentsCount = 0
        studentStatsAdapter.studentStatsList.forEach {
            if ( it.presenceStatus == "Present" ) {
                presentStudentsCount += 1
            }
            if ( it.onScreenStatus ) {
                onScreenStudentsCount += 1
            }
        }
        summaryBottomSheetViewModel.presentStudentsCount.value = presentStudentsCount
        summaryBottomSheetViewModel.onScreenStudentsCount.value = onScreenStudentsCount
        startCountUpdater()
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
        startTimeMap.forEach{ id , startTime ->
            val endTime = if ( endTimeMap.contains( id ) ) {
                endTimeMap[ id ]!!
            }
            else {
                System.currentTimeMillis()
            }
            val eta = ( endTime - startTime ) / ( 1000 * 60 )
            if ( eta > ATTENDANCE_THRESHOLD ) {

            }
            reportBuilder.append( "${idNameMap[id]} $eta minutes\n" )
        }
        reportString = reportBuilder.toString()
        openCreateFileDialog( "attendance_report.txt" )
    }

    private fun changeLanguage() {
        val locale = Locale( "mr" )
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext( config )
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

    private val studentStatInteractCallback = object : StudentStatsAdapter.StudentStatsInteractCallback {

        override fun onStudentStatClick(studentStats: StudentStats) {
            TODO("Not yet implemented")
        }

        override fun onStudentStatLongClick(studentStats: StudentStats) {

        }

        override fun onStudentEntered(studentStats: StudentStats) {
            Snackbar.make( activityMainBinding.root , "Entered" , Snackbar.LENGTH_SHORT ).show()
            summaryBottomSheetViewModel.totalStudentsCount.value =
                summaryBottomSheetViewModel.totalStudentsCount.value!! + 1
            idNameMap[ studentStats.id ] = studentStats.name
            startTimeMap[ studentStats.id ] = System.currentTimeMillis()
        }

        override fun onStudentLeft(studentStats: StudentStats) {
            summaryBottomSheetViewModel.totalStudentsCount.value =
                summaryBottomSheetViewModel.totalStudentsCount.value!! - 1
            endTimeMap[ studentStats.id ] = System.currentTimeMillis()
        }

    }

}