package de.schnettler.repo.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.Toplist
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ChartRemoteMediator<T : LastFmEntity, Output : Toplist>(
    private val pageSize: Int = 50,
    private val fetcher: suspend (Int) -> List<Output>,
    private val writer: suspend (List<Output>, Boolean) -> Unit,
) : RemoteMediator<Int, Output>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Output>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    Timber.d("Paging Refresh")
                    1
                } // First Page
                LoadType.PREPEND -> {
                    // No Prepend, because only first page is refreshed
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val lastItem =
                        state.lastItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = true)
                    val result = lastItem.listing.index.div(pageSize) + 2
                    Timber.d("Paging: ${lastItem.value.name} - Index ${lastItem.listing.index} -> Page $result")
                    result
                }
            }

            val response = fetcher(page)

            writer(response, loadType == LoadType.REFRESH)

//            Timber.d("Paging: Index ${lastItem.listing.index} Page $page, Result: ${response.size}")
            return MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}