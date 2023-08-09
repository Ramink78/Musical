package rk.musical.utils

import android.database.Cursor

val Cursor.songIdColumnIndex: Int
    get() =
        getColumnIndexOrThrow(SONG_ID)

val Cursor.dateAddedColumnIndex
    get() =
        getColumnIndexOrThrow(SONG_DATE_ADDED)

val Cursor.artistColumnIndex
    get() =
        getColumnIndexOrThrow(SONG_ARTIST)

val Cursor.albumIdColumnIndex
    get() =
        getColumnIndexOrThrow(ALBUM_ID)
val Cursor.albumArtColumnIndex
    get() =
        getColumnIndexOrThrow(ALBUM_ART)

val Cursor.songDurationColumnIndex
    get() =
        getColumnIndexOrThrow(SONG_DURATION)

val Cursor.songNameColumnIndex
    get() =
        getColumnIndexOrThrow(SONG_TITLE)

val Cursor.albumNameColumnIndex
    get() =
        getColumnIndexOrThrow(ALBUM_NAME)


val Cursor.albumSongsCountColumnIndex: Int
    get() =
        getColumnIndexOrThrow(ALBUM_SONGS_COUNT)



