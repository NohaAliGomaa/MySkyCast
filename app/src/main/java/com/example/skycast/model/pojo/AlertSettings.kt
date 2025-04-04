package com.example.skycast.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class AlertSettings (var lat:Double=36.4761,
                          var lon:Double=-119.4432,
                          var isALarm:Boolean=true,var
                          isNotification:Boolean=false)

@Entity(tableName = "alert")
data class MyAlert(
    @PrimaryKey(autoGenerate = true) val dbId: Int =0,
    val id: String? = "" ,
    var startTime: Long? = 2,
    val duration: Long? = 10,
    val type: String? = "",
    val useDefaultSound : Boolean? = false,
    val snooze: Boolean? = false
):Serializable
