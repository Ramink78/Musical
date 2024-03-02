package rk.musical.utils

import android.net.Uri
import android.provider.MediaStore.Audio.Albums
import android.provider.MediaStore.Audio.Media

val SONGS_URI: Uri = Media.EXTERNAL_CONTENT_URI
val ALBUMS_URI: Uri = Albums.EXTERNAL_CONTENT_URI

const val SONG_ID = Media._ID
const val SONG_TITLE = Media.TITLE
const val SONG_ARTIST = Media.ARTIST
const val SONG_DURATION = Media.DURATION
const val SONG_DATE_ADDED = Media.DATE_ADDED
const val ALBUM_ID = Albums.ALBUM_ID
const val ALBUM_ART = Albums.ALBUM_ART
const val ALBUM_NAME = Media.ALBUM
const val ALBUM_SONGS_COUNT = Albums.NUMBER_OF_SONGS

val songColumns =
    arrayOf(
        SONG_ID,
        SONG_TITLE,
        SONG_ARTIST,
        SONG_DURATION,
        ALBUM_NAME,
        ALBUM_ID
    )
val albumColumns =
    arrayOf(
        ALBUM_ID,
        SONG_ARTIST,
        ALBUM_NAME,
        ALBUM_SONGS_COUNT,
        ALBUM_ART
    )
const val IS_MUSIC_CLAUSE = "${Media.IS_MUSIC}!=0"
