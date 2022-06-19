package com.shubham0204.ocmsdashboard

import com.google.firebase.database.DataSnapshot

// The class that holds statistics for each student
class StudentStats {

    companion object {

        private val STUDENT_NAME = "name"
        private val IS_ACTIVE = "is_active"

        private val CAMERA_PERMISSION_STATUS = "camera_status"
        private val AUDIO_PERMISSION_STATUS = "audio_status"
        private val USAGE_STATS_PERMISSION_STATUS = "usage_status"

        private val ON_SCREEN_STATUS = "on_screen_status"
        private val ON_SCREEN_APP = "on_screen_app"
        private val PRESENCE_STATUS = "presence"

        fun fromSnapshot( studentInfoSnapshot : DataSnapshot ) : StudentStats {
            val studentStats = StudentStats()
            studentStats.id = studentInfoSnapshot.key ?: ""
            studentStats.name = studentInfoSnapshot.child( STUDENT_NAME ).getValue( String::class.java ) ?: ""
            studentStats.isActive = studentInfoSnapshot.child( IS_ACTIVE ).getValue( Boolean::class.java ) ?: true
            studentStats.cameraPermission = studentInfoSnapshot.child( CAMERA_PERMISSION_STATUS ).getValue( Boolean::class.java ) ?: false
            studentStats.audioPermission = studentInfoSnapshot.child( AUDIO_PERMISSION_STATUS ).getValue( Boolean::class.java ) ?: false
            studentStats.usageStatsPermission = studentInfoSnapshot.child( USAGE_STATS_PERMISSION_STATUS ).getValue( Boolean::class.java ) ?: false
            studentStats.presenceStatus = studentInfoSnapshot.child( PRESENCE_STATUS ).getValue( String::class.java ) ?: ""
            studentStats.onScreenStatus = studentInfoSnapshot.child( ON_SCREEN_STATUS ).getValue( Boolean::class.java ) ?: false
            studentStats.onScreenApp = studentInfoSnapshot.child( ON_SCREEN_APP ).getValue( String::class.java ) ?: ""
            return studentStats
        }

    }

    override fun equals(other: Any?): Boolean {
        return ( other as StudentStats ).id == id
    }

    override fun toString(): String {
        return "User name $name \n User ID $id \n User on screen: $presenceStatus \nApp on screen: $onScreenApp \n" +
                "Camera permission: $cameraPermission \nAudio permission: $audioPermission \n" +
                "Usage access permission: $usageStatsPermission \nPresence: $presenceStatus"
    }

    var id : String = ""
    var name : String = ""
    var isActive : Boolean = true

    var cameraPermission : Boolean = false
    var audioPermission : Boolean = false
    var usageStatsPermission : Boolean = false

    var presenceStatus : String = ""
    var onScreenStatus : Boolean = false
    var onScreenApp : String = ""

}