package com.shubham0204.ocmsdashboard

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StudentStatsAdapter(
    private var context: Context ,
    private var studentStatsFlow : Flow<StudentStats> ,
    private var studentStatsInteractCallback : StudentStatsInteractCallback ) :
    RecyclerView.Adapter<StudentStatsAdapter.StudentStatsViewHolder>() {

    private val dateFormat = SimpleDateFormat( "kk:mm dd-MM-yyyy" , Locale.getDefault() )
    val studentStatsList = ArrayList<StudentStats>()
    val report = ArrayList<Pair<String,String>>()
    val idNameMap = HashMap<String,String>()
    var meetingStartTime = 0L

    interface StudentStatsInteractCallback {
        fun onStudentStatClick( studentStats: StudentStats )
        fun onStudentStatLongClick(studentStats: StudentStats )
        fun onStudentEntered( studentStats: StudentStats )
        fun onStudentLeft( studentStats: StudentStats )
    }

    init {
        CoroutineScope( Dispatchers.Main ).launch {
            studentStatsFlow.collect {
                if ( !studentStatsList.contains( it ) ) {
                    if ( studentStatsList.size == 0 ) {
                        if ( meetingStartTime == 0L ) {
                            meetingStartTime = System.currentTimeMillis()
                        }
                    }
                    if ( it.isActive ) {
                        studentStatsList.add( it )
                        idNameMap[ it.id ] = it.name
                        notifyItemInserted( studentStatsList.indexOf( it ) )
                        studentStatsInteractCallback.onStudentEntered( it )
                    }
                }
                else if ( !it.isActive ) {
                    val position = studentStatsList.indexOf( it )
                    studentStatsList.remove( it )
                    notifyItemRemoved( position )
                    studentStatsInteractCallback.onStudentLeft( it )
                }
                else {
                    val position = studentStatsList.indexOf( it )
                    compare( studentStatsList[ position ] , it )
                    studentStatsList[ position ] = it
                    notifyItemChanged( position )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentStatsViewHolder {
        return StudentStatsViewHolder( ( context as Activity )
            .layoutInflater.inflate( R.layout.student_stats_item_layout , parent , false ))
    }

    override fun onBindViewHolder(holder: StudentStatsViewHolder, position: Int) {
        val studentStat = studentStatsList[ position ]
        holder.studentName.text = studentStat.name
        holder.studentAppName.text = if ( studentStat.onScreenStatus ) { context.getString(R.string.active_in_classroom_display) }
        else { studentStat.onScreenApp }
        holder.studentPresence.text = if ( studentStat.presenceStatus == "Present" ) {
            context.getString(R.string.present_display)
        }
        else { context.getString(R.string.absent_display) }
        holder.studentCameraStatus.apply {
            if ( studentStat.cameraPermission ) {
                setImageResource( R.drawable.camera_icon_24_dark )
            }
            else {
                setImageDrawable( null )
            }
        }
        holder.studentUsagePermissionStatus.apply {
            if ( studentStat.usageStatsPermission ) {
                setImageResource( R.drawable.usage_access_24_dark )
            }
            else {
                setImageDrawable( null )
            }
        }
        if ( !studentStat.onScreenStatus ) {
            emphasize( holder )
        }
        else {
            deemphasize( holder )
        }
        holder.studentStatsItemView.setOnLongClickListener {
            studentStatsInteractCallback.onStudentStatLongClick( studentStat )
            true
        }
        holder.studentStatsItemView.setOnClickListener { studentStatsInteractCallback.onStudentStatClick( studentStat ) }
    }

    override fun getItemCount(): Int {
        return studentStatsList.size
    }

    private fun emphasize( itemViewHolder : StudentStatsViewHolder ) {
        itemViewHolder.studentStatsItemView.background =
            AppCompatResources.getDrawable( context , R.drawable.student_stat_item_background_highlighted )
        arrayOf(
            itemViewHolder.studentAppName ,
            itemViewHolder.studentPresence ,
            itemViewHolder.studentName ).forEach {
            it.setTextColor( Color.WHITE )
        }
    }

    private fun deemphasize( itemViewHolder : StudentStatsViewHolder ) {
        itemViewHolder.studentStatsItemView.background =
            AppCompatResources.getDrawable( context , R.drawable.student_stat_item_background_normal )
        arrayOf(
            itemViewHolder.studentAppName ,
            itemViewHolder.studentPresence ,
            itemViewHolder.studentName ).forEach {
            it.setTextColor( Color.BLACK )
        }
    }

    fun clearAll() {
        studentStatsList.clear()
        notifyItemRangeChanged( 0 , 0 )
        notifyDataSetChanged()
    }

    private fun compare( stats1 : StudentStats , stats2 : StudentStats ) {
        if ( stats1.presenceStatus != stats2.presenceStatus ) {
            var message = ""
            if ( stats2.presenceStatus == "Present" ) {
                message = context.getString(R.string.in_meeting_display)
            }
            else {
                message = context.getString(R.string.not_in_meeting_display)
            }
            report.add( Pair( getCurrentTime() , "${stats2.name} $message"))
        }
        if ( stats1.onScreenStatus != stats2.onScreenStatus ) {
            var message = ""
            message = if ( stats2.onScreenStatus ) {
                context.getString(R.string.on_screen_display)
            } else {
                context.getString(R.string.not_on_screen_display)
            }
            report.add( Pair( getCurrentTime() , "${stats2.name} $message"))
        }
        if ( stats1.onScreenApp != stats2.onScreenApp ) {
            if ( stats2.onScreenApp != "" && stats1.onScreenApp != "" ) {
                val message = "is using ${stats2.onScreenApp}"
                report.add( Pair( getCurrentTime() , "${stats2.name} $message"))
            }
        }
    }

    private fun getCurrentTime() : String = dateFormat.format( Date() )

    class StudentStatsViewHolder( itemView : View ) : RecyclerView.ViewHolder( itemView ) {

        var studentName : TextView = itemView.findViewById( R.id.student_stats_name )
        var studentAppName : TextView = itemView.findViewById( R.id.student_stats_app_on_screen )
        var studentPresence : TextView = itemView.findViewById( R.id.student_stats_presence )
        var studentStatsItemView : ConstraintLayout = itemView.findViewById( R.id.student_stats_constraint_layout )
        var studentCameraStatus : ImageView = itemView.findViewById( R.id.student_stats_camera_status )
        var studentUsagePermissionStatus : ImageView = itemView.findViewById( R.id.student_stats_usage_access_status )

    }


}