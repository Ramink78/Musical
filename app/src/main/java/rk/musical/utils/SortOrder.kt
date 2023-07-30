package rk.musical.utils

sealed interface SortOrder {
    object Ascending : SortOrder {
        const val title = "$SONG_TITLE ASC"
        const val dateAdded = "$SONG_DATE_ADDED ASC"
        const val artist = "$SONG_ARTIST ASC"
    }

    object Descending : SortOrder {
        const val title = "$SONG_TITLE DESC"
        const val dateAdded = "$SONG_DATE_ADDED DESC"
        const val artist = "$SONG_ARTIST DESC"
    }
}
