package com.example.skycast.model.pojo

import com.example.skycast.model.util.AppConstants

data class Settings(var lang:String=AppConstants.LANG_EN,var isMap:Boolean=false,
                    var unit:String=AppConstants.UNITS_DEFAULT,
                    var lat:Double=0.0,var lon:Double=0.0)