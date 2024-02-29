package rk.musical.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import rk.musical.data.model.Song

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun like(song: Song)

    @Delete
    suspend fun removeLike(song: Song)

    @Query("Select * from favorite")
    suspend fun getAllFavorites(): List<Song>

    @Query("Select * from favorite where id=:id")
    suspend fun isLiked(id: String): List<Song>
}
