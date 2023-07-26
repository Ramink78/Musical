package rk.musical.utils

sealed interface SortOrder {
    object Ascending : SortOrder {
        const val title = "$MEDIA_TITLE ASC"
        const val dateAdded = "$MEDIA_DATE_ADDED ASC"
        const val artist = "$MEDIA_ARTIST ASC"
    }

    object Descending : SortOrder {
        const val title = "$MEDIA_TITLE DESC"
        const val dateAdded = "$MEDIA_DATE_ADDED DESC"
        const val artist = "$MEDIA_ARTIST DESC"
    }
}
