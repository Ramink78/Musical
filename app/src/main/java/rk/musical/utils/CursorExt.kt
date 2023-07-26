package rk.musical.utils

import android.database.Cursor

val Cursor.id: Int
    get() {
        return getInt(getColumnIndexOrThrow(MEDIA_ID))
    }
val Cursor.dateAdded: Int
    get() {
        return getInt(getColumnIndexOrThrow(MEDIA_DATE_ADDED))
    }
val Cursor.artist: String
    get() {
        return getString(getColumnIndexOrThrow(MEDIA_ARTIST))
    }
val Cursor.contentUri: String
    get() {
        return getString(getColumnIndexOrThrow(MEDIA_ARTIST))
    }
val Cursor.albumId: Int
    get() {
        return getInt(getColumnIndexOrThrow(MEDIA_ALBUM_ID))
    }
val Cursor.duration: Int
    get() {
        return getInt(getColumnIndexOrThrow(MEDIA_DURATION))
    }
val Cursor.title: String
    get() {
        return getString(getColumnIndexOrThrow(MEDIA_TITLE))
    }