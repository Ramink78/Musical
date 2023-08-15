package rk.musical.data.model

import android.util.Log


fun Any.logger(message: String) {
    Log.i(javaClass.simpleName, message)
}
