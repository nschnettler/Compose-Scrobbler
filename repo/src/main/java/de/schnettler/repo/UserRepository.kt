package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.database.daos.UserDao
import de.schnettler.database.models.User
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.UserMapper
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val service: LastFmService,
    private val authProvider: LastFmAuthProvider
) {
    val userStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            UserMapper.map(service.getUserInfo(authProvider.getSessionKeyOrThrow()))
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = {
                userDao.getUser(authProvider.session?.name)
            },
            writer = { _: Any, user: User ->
                val oldUser = userDao.getUserOnce(authProvider.getSessionOrThrow().name)
                oldUser?.let {
                    user.artistCount = it.artistCount
                    user.lovedTracksCount = it.lovedTracksCount
                }
                userDao.forceInsert(user)
            }
        )
    ).build()

    val lovedTracksStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            val session = authProvider.session!!
            val result = service.getUserLikedTracksAmount(session.key)
            userDao.updateLovedTracksCount(session.name, result.total)
            result.total
        }).build()
}