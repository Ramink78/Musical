package rk.musical.data.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val songUri: String,
    val albumId: Long,
    val albumName: String,
    val coverUri: String? = null
)
