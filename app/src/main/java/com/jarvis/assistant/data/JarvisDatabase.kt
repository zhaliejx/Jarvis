package com.jarvis.assistant.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import java.util.Date

// Data entities
@Entity(tableName = "user_preferences")
data class UserPreference(
    @PrimaryKey val key: String,
    val value: String,
    val timestamp: Date = Date()
)

@Entity(tableName = "user_contexts")
data class UserContextEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val location: String = "",
    val currentTime: String = "",
    val currentActivity: String = "",
    val preferences: String = "",
    val timestamp: Date = Date()
)

// Type converters for Room
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

// DAO interfaces
@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences")
    suspend fun getAllPreferences(): List<UserPreference>

    @Query("SELECT * FROM user_preferences WHERE key = :key")
    suspend fun getPreference(key: String): UserPreference?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(preference: UserPreference)

    @Update
    suspend fun updatePreference(preference: UserPreference)

    @Delete
    suspend fun deletePreference(preference: UserPreference)

    @Query("DELETE FROM user_preferences WHERE key = :key")
    suspend fun deletePreferenceByKey(key: String)
}

@Dao
interface UserContextDao {
    @Query("SELECT * FROM user_contexts ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestContext(): UserContextEntity?

    @Insert
    suspend fun insertContext(context: UserContextEntity)

    @Update
    suspend fun updateContext(context: UserContextEntity)

    @Query("DELETE FROM user_contexts")
    suspend fun deleteAllContexts()
}

// Main database class
@Database(
    entities = [UserPreference::class, UserContextEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class JarvisDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun userContextDao(): UserContextDao

    companion object {
        private const val DATABASE_NAME = "jarvis_database"

        @Volatile
        private var INSTANCE: JarvisDatabase? = null

        fun getInstance(context: Context): JarvisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JarvisDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration() // For development purposes
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}