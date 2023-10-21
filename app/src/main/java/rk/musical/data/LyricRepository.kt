package rk.musical.data

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rk.musical.data.db.LyricDao
import rk.musical.data.model.Lyric

class LyricRepository @Inject constructor(
    private val lyricDao: LyricDao
) {

    suspend fun addLyric(lyric: Lyric) = withContext(Dispatchers.IO) {
        lyricDao.addLyric(lyric)
    }

    suspend fun getLyricBySongId(songId: String): Lyric? = withContext(Dispatchers.IO) {
        lyricDao.getLyricBySongId(songId)
    }

    suspend fun updateLyric(lyric: Lyric) = withContext(Dispatchers.IO) {
        lyricDao.updateLyric(lyric)
    }

    suspend fun deleteLyric(lyric: Lyric) = withContext(Dispatchers.IO) {
        lyricDao.deleteLyric(lyric)
    }
}
