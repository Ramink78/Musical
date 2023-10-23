package rk.musical.data

import javax.inject.Inject
import rk.musical.data.db.LyricDao
import rk.musical.data.model.Lyric

class LyricRepository @Inject constructor(
    private val lyricDao: LyricDao
) {

    suspend fun addLyric(lyric: Lyric) =
        lyricDao.addLyric(lyric)

    suspend fun getLyricBySongId(songId: String): Lyric? =
        lyricDao.getLyricBySongId(songId)

    suspend fun updateLyric(lyric: Lyric) =
        lyricDao.updateLyric(lyric)

    suspend fun deleteLyric(lyric: Lyric) =
        lyricDao.deleteLyric(lyric)
}
