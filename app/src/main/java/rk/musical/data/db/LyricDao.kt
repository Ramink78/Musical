package rk.musical.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import rk.musical.data.model.Lyric

@Dao
interface LyricDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLyric(lyric: Lyric)

    @Query("SELECT * FROM lyric WHERE song_id=:songId")
    suspend fun getLyricBySongId(songId: String): Lyric?

    @Update
    suspend fun updateLyric(lyric: Lyric)

    @Delete
    suspend fun deleteLyric(lyric: Lyric)


}
