package rk.musical.data.model

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val songsCount: Int,
    val coverUri: String? = null

)
