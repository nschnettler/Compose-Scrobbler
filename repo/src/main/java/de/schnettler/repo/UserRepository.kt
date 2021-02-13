package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.database.daos.UserDao
import de.schnettler.database.models.User
import de.schnettler.lastfm.api.lastfm.UserService
import de.schnettler.repo.authentication.provider.LastFmAuthProviderImpl
import de.schnettler.repo.mapping.user.UserMapper
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userService: UserService,
    private val authProvider: LastFmAuthProviderImpl
) {
    val userStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            UserMapper.map(userService.getUserInfo())
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = {
                userDao.getUser()
            },
            writer = { _: Any, user: User ->
                val oldUser = userDao.getUserOnce()
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
            val result = userService.getUserLikedTracksAmount()
            authProvider.getSession()?.name?.let { userName ->
                userDao.updateLovedTracksCount(userName, result.total)
            }
            result.total
        }).build()
}