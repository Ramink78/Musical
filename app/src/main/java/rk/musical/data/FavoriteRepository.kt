package rk.musical.data

import javax.inject.Inject
import rk.musical.data.db.FavoriteDao
import rk.musical.data.model.Song

class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    suspend fun like(song: Song) =
        favoriteDao.like(song)

    suspend fun removeLike(song: Song) =
        favoriteDao.removeLike(song)

    suspend fun getAllFavorites() =
        favoriteDao.getAllFavorites()

    suspend fun isLiked(song: Song): Boolean {
        return favoriteDao.isLiked(song.id).isNotEmpty()
    }

    suspend fun toggleLike(song: Song) {
        val isLiked = favoriteDao.isLiked(song.id).isNotEmpty()
        if (isLiked) {
            favoriteDao.removeLike(song)
        } else {
            favoriteDao.like(song)
        }
    }
}
