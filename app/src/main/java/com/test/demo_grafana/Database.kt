package com.test.demo_grafana

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity
data class SensorData(
    @PrimaryKey(autoGenerate = true) val key: Long,
    val timestamp: Long,
    val x: Double,
    val y: Double,
    val z: Double
)

@Dao
interface SensorDao {
    @Insert
    suspend fun insert(data: SensorData)

    @Query("SELECT * FROM SensorData")
    suspend fun getAll(): List<SensorData>
}

@Database(entities = [SensorData::class], version = 2)
abstract class Database: RoomDatabase() {
    abstract fun sensorDao(): SensorDao

}