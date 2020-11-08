package de.schnettler.repo.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import de.schnettler.database.daos.BaseDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.Toplist
import de.schnettler.repo.mapping.IndexedMapper
import de.schnettler.repo.mapping.forPagedLists
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class ChartRemoteMediator<T : LastFmEntity, Input, Output : Toplist>(
    private val pageSize: Int = 50,
    private val entityDao: BaseDao<T>,
    private val chartDao: ChartDao,
    private val mapper: IndexedMapper<Input, Output>,
    private val backend: suspend (Int) -> List<Input>,
) : RemoteMediator<Int, Output>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Output>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1 // First Page
            LoadType.PREPEND -> {
                // No Prepend, because only first page is refreshed
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = true)
                lastItem.listing.index.div(pageSize) + 2
            }
        }

        val response = mapper.forPagedLists(page, 50)(backend(page))
        entityDao.insertAll(response.map { it.value as T })
        chartDao.forceInsertAll(response.map { it.listing })

        Timber.d("Paging: Page $page, Result: ${response.size}")
        return MediatorResult.Success(endOfPaginationReached = response.isEmpty())
    }
}