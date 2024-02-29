package rk.musical.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import rk.musical.data.model.Lyric
import rk.musical.data.model.Song

@Database(entities = [Lyric::class, Song::class], version = 1)
abstract class MusicalDatabase : RoomDatabase() {
    abstract fun lyricDao(): LyricDao
    abstract fun favoriteDao(): FavoriteDao
}
