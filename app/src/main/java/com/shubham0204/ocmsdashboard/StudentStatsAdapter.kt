package com.shubham0204.ocmsdashboard

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class StudentStatsAdapter(
    private var context: Context ,
    private var studentStatsFlow : Flow<StudentStats>
    ) : RecyclerView.Adapter<StudentStatsAdapter.StudentStatsViewHolder>() {

    private val studentStatsList = ArrayList<StudentStats>()

    init {
        CoroutineScope( Dispatchers.Main ).launch {
            studentStatsFlow.collect {
                if ( !studentStatsList.contains( it ) ) {
                    studentStatsList.add( it )
                    notifyItemInserted( studentStatsList.indexOf( it ) )
                }
                else {
                    val position = studentStatsList.indexOf( it )
                    when ( it.isActive ) {
                        true -> {
                            studentStatsList[ position ] = it
                            notifyItemChanged( position )
                        }
                        false -> {
                            studentStatsList.removeAt( position )
                            notifyItemRemoved( position )
                        }
                    }
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
    }

    override fun getItemCount(): Int {
        return studentStatsList.size
    }


    class StudentStatsViewHolder( itemView : View ) : RecyclerView.ViewHolder( itemView ) {

        var studentName : TextView = itemView.findViewById( R.id.student_stats_name )
        var studentAppName : TextView = itemView.findViewById( R.id.student_stats_app_on_screen )
        var studentPresence : TextView = itemView.findViewById( R.id.student_stats_presence )

    }


}