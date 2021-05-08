package de.schnettler.scrobbler.profile.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.scrobbler.model.User
import de.schnettler.scrobbler.profile.api.ProfileApi
import de.schnettler.scrobbler.profile.db.UserDao
import de.schnettler.scrobbler.profile.map.UserMapper
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val profileApi: ProfileApi,
//    private val authProvider: LastFmAuthProviderImpl
) {
    val userStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            UserMapper.map(profileApi.getUserInfo())
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
            val result = profileApi.getUserLikedTracksAmount()
//            authProvider.getSession()?.name?.let { userName ->
//                userDao.updateLovedTracksCount(userName, result.total)
//            }
            result.total
        }).build()
}