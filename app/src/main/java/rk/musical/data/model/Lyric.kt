package rk.musical.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lyric")
data class Lyric(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "song_id") val songId: String,
    @ColumnInfo(name = "lyric_text") val lyricText: String
)
