package rk.musical.utils

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import coil.request.ImageRequest
import okio.IOException
import rk.musical.data.model.Album
import rk.musical.data.model.Song
import java.util.concurrent.TimeUnit

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

fun MediaItem.loadCover(context: Context): ImageRequest? {
    return try {
        ImageRequest.Builder(context)
            .data(mediaMetadata.artworkUri)
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

fun readableDuration(millis: Long) =
    buildString {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        if (minutes >= 10) append(minutes)
        else append("0$minutes")
        append(":")
        if (seconds >= 10) append(seconds)
        else append("0$seconds")
    }