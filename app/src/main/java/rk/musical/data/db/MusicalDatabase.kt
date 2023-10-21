package rk.musical.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import rk.musical.data.model.Lyric

@Database(entities = [Lyric::class], version = 1)
abstract class MusicalDatabase : RoomDatabase() {
    abstract fun lyricDao(): LyricDao
}
