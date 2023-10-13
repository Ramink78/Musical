package rk.musical.utils

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.CancellationSignal

fun ContentResolver.kuery(
    uri: Uri,
    columns: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null,
    cancellationSignal: CancellationSignal? = null,
): Cursor? {
    return query(
        uri,
        columns,
        selection,
        selectionArgs,
        sortOrder,
        cancellationSignal,
    )
}
