package com.dicoding.storydicoding.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storydicoding.data.api.StoryService
import com.dicoding.storydicoding.data.response.ListStoryItem

class StoryPagingSource(private val storyService: StoryService) : PagingSource<Int, ListStoryItem>() {
    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = storyService.getStories(position, params.loadSize)
            LoadResult.Page(
                data = response.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (response.listStory.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}