package com.example.storyapp.strorypage

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.Api.ApiService
import com.example.storyapp.model.UserPrefence
import com.example.storyapp.response.ListStoryItem
import kotlinx.coroutines.flow.first

class StorySource(private val userPrefence: UserPrefence, private val apiService: ApiService, ) : PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val Initial_Page = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: Initial_Page
            val token = userPrefence.getUser().first().token

            val responseData = apiService.getListStories(token, position, params.loadSize)
            val data = responseData.body()?.listStory ?: emptyList()
            val prevKey = if (position == Initial_Page) null else position - 1
            val nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1

            LoadResult.Page(data, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}