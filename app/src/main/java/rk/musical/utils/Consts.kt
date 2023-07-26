package rk.musical.utils

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media

val SONGS_URI: Uri =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        Media.EXTERNAL_CONTENT_URI
    }
const val MEDIA_ID = Media._ID
const val MEDIA_TITLE = Media.TITLE
const val MEDIA_ARTIST = Media.ARTIST
const val MEDIA_DURATION = Media.DURATION
const val MEDIA_DATE_ADDED = Media.DATE_ADDED
const val MEDIA_ALBUM_ID = Media.ALBUM_ID

val songColumns = arrayOf(
    MEDIA_ID,
    MEDIA_TITLE,
    MEDIA_ARTIST,
    MEDIA_DATE_ADDED,
    MEDIA_DURATION,
    MEDIA_ALBUM_ID,

    )
val IS_MUSIC_CLAUSE = "${Media.IS_MUSIC}!=0"

