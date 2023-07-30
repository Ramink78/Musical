package rk.musical.utils

import android.content.Context
import android.util.Log
import coil.request.ImageRequest
import okio.IOException
import rk.musical.data.model.Album
import rk.musical.data.model.Song

fun Song.loadCover(context: Context): ImageRequest? {
    return try {
        ImageRequest.Builder(context)
            .data(coverUri)
            .crossfade(250)
            .build()
    } catch (e: IOException) {
        Log.e("MediaUtil: LoadSongCover", "Error: ${e.message}")
        null
    }
}

fun Album.loadCover(context: Context): ImageRequest? {
    return try {
        ImageRequest.Builder(context)
            .data(coverUri)
            .crossfade(250)
            .build()
    } catch (e: IOException) {
        Log.e("MediaUtil: LoadAlbumCover", "Error: ${e.message}")
        null
    }
}