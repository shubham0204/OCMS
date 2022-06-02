package com.shubham0204.ocmsdashboard

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class StudentStatsAdapter(
    private var context: Context ,
    private var studentStatsFlow : Flow<StudentStats> ,
    private var longClickListener : StudentStatsInteractCallback
    ) : RecyclerView.Adapter<StudentStatsAdapter.StudentStatsViewHolder>() {

    private val dateFormat = SimpleDateFormat( "kk:mm dd-MM-yyyy" , Locale.getDefault() )
    private val studentStatsList = ArrayList<StudentStats>()
    val report = ArrayList<Pair<String,String>>()

    interface StudentStatsInteractCallback {
        fun onStudentStatLongClickListener( studentStats: StudentStats )
    }

    init {
        CoroutineScope( Dispatchers.Main ).launch {
            studentStatsFlow.collect {
                if ( !studentStatsList.contains( it ) ) {
                    studentStatsList.add( it )
                    notifyItemInserted( studentStatsList.indexOf( it ) )
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
        holder.studentAppName.text = studentStat.onScreenApp
        holder.studentPresence.text = studentStat.presenceStatus
        if ( !studentStat.isActive ) {
            holder.studentStatsItemView.background = ColorDrawable( Color.RED )
            arrayOf( holder.studentAppName , holder.studentPresence , holder.studentPresence ).forEach {
                it.setTextColor( Color.WHITE )
            }
        }
        else {
            holder.studentStatsItemView.background = ColorDrawable( Color.WHITE )
        }
        holder.studentStatsItemView.setOnLongClickListener {
            longClickListener.onStudentStatLongClickListener( studentStat )
            true
        }
    }

    override fun getItemCount(): Int {
        return studentStatsList.size
    }

    fun clearAll() {
        studentStatsList.clear()
        notifyItemRangeChanged( 0 , 0 )
        notifyDataSetChanged()
    }

    private fun compare( stats1 : StudentStats , stats2 : StudentStats ) {
        if ( stats1.presenceStatus != stats2.presenceStatus ) {
            var message = ""
            message = if ( stats1.presenceStatus == "Present" ) {
                "is in the meeting"
            } else {
                "is not in the meeting"
            }
            report.add( Pair( getCurrentTime() , "${stats2.name} $message"))
        }
        if ( stats1.onScreenStatus != stats2.onScreenStatus ) {
            var message = ""
            message = if ( stats1.onScreenStatus ) {
                "is using the meeting app"
            } else {
                "switched to some other app"
            }
            report.add( Pair( getCurrentTime() , "${stats2.name} $message"))
        }
    }

    private fun getCurrentTime() : String = dateFormat.format( Date() )

    class StudentStatsViewHolder( itemView : View ) : RecyclerView.ViewHolder( itemView ) {

        var studentName : TextView = itemView.findViewById( R.id.student_stats_name )
        var studentAppName : TextView = itemView.findViewById( R.id.student_stats_app_on_screen )
        var studentPresence : TextView = itemView.findViewById( R.id.student_stats_presence )
        var studentStatsItemView : ConstraintLayout = itemView.findViewById( R.id.student_stats_constraint_layout )

    }


}