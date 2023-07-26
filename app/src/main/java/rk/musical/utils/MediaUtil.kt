package rk.musical.utils

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.core.net.toUri
import coil.request.ImageRequest
import rk.musical.data.model.Song
import java.io.IOException

fun loadMediaCover(context: Context, song: Song): ImageRequest? {
    val imageRequestBuilder = ImageRequest.Builder(context)
        .crossfade(true)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return try {
            imageRequestBuilder.data(
                context.contentResolver.loadThumbnail(
                    song.songUri,
                    Size(150, 150),
                    null
                )
            )
                .build()
        } catch (e: IOException) {
            Log.e("MediaUtil: loadMediaCover Method", "Error: ${e.message}")
            null
        }
    } else {
        val sArtworkUri = "content://media/external/audio/albumart".toUri()
        val coverUri = ContentUris.withAppendedId(sArtworkUri, song.albumId.toLong())
        return ImageRequest.Builder(context)
            .data(coverUri)
            .size(150)
            .build()
    }
}