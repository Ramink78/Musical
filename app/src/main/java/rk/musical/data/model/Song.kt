package rk.musical.data.model

import android.net.Uri

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val songUri: Uri,
    val albumId: Int
)
