package com.example.skycast.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class AlertSettings (var lat:Double=36.4761,
                          var lon:Double=-119.4432,
                          var isALarm:Boolean=true,var
                          isNotification:Boolean=false)

@Entity(tableName = "Alert")
data class MyAlert(
    @PrimaryKey(autoGenerate = true) val id: Int =0 ,
    var startTime: Long? = 0,
    val duration: Long? = 0,
    val type: String? = "",
    val useDefaultSound : Boolean? = false,
    val snooze: Boolean? = false,
    val createdAt: Long = System.currentTimeMillis()
)
