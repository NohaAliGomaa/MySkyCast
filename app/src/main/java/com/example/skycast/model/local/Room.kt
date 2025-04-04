package com.example.skycast.model.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.room.Update
import com.example.skycast.model.pojo.MyAlert
import com.example.skycast.model.pojo.WeatherAlert
import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.util.Converters
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * From Alert")
    fun getAlerts(): Flow<List<MyAlert>>
    @Query("SELECT * From Alert Where id=:id")
    fun getAlert(id:Long): Flow<MyAlert>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: MyAlert): Long
    @Delete
    suspend fun deleteAlert(alert:MyAlert):Int
}
@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insetWeather(weather :WeatherResponse): Long

    @Query("SELECT * FROM weather where isFavorite= true")
    fun getFavoriteWeathers(): Flow<List<WeatherResponse>?>

    @Delete
    suspend fun deleteFavorite(weather :WeatherResponse):Int

    @Query("DELETE FROM weather where isFavorite= false")
    suspend fun deleteCurrent():Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weather :WeatherResponse): Long

    @Query("SELECT * FROM weather ")
    fun getCurrentWeathers(): Flow<List<WeatherResponse>?>

    @Transaction
    suspend fun insertOrUpdateCurrentWeather(weather :WeatherResponse)
    { val existingWeather=getCurrentWeathers()
        existingWeather?.let {
            deleteCurrent()
        }
        insertCurrentWeather(weather)
    }
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFavWeather(weather :WeatherResponse)

}

@Database(entities = arrayOf( WeatherResponse::class, MyAlert::class) , version = 26)
@TypeConverters(Converters::class)
abstract class WeatherDataBse : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao
    abstract fun alertDao():AlertDao
    companion object {
        @Volatile
        private var INSTANCE: WeatherDataBse? = null
        fun getInstance(ctx: Context): WeatherDataBse {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, WeatherDataBse::class.java, "weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
// return instance
                instance
            }
        }
    }
}