package com.dicoding.storydicoding.view.main

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storydicoding.DataDummy
import com.dicoding.storydicoding.MainDispatcherRule
import com.dicoding.storydicoding.data.repository.StoryRepository
import com.dicoding.storydicoding.data.repository.UserRepository
import com.dicoding.storydicoding.data.response.ListStoryItem
import com.dicoding.storydicoding.getOrAwaitValue
import com.dicoding.storydicoding.view.story.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private lateinit var mainViewModel: MainViewModel
    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        storyRepository = Mockito.mock(StoryRepository::class.java)
        userRepository = Mockito.mock(UserRepository::class.java)
        mainViewModel = MainViewModel(userRepository, storyRepository)
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = PagingData.from(dummyStory)
        val expectedStory: LiveData<PagingData<ListStoryItem>> = MutableLiveData(data)
        //expectedStory.value = data

        `when`(storyRepository.getStories()).thenReturn(expectedStory)
        val actualStory = mainViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory: LiveData<PagingData<ListStoryItem>> = MutableLiveData(data)
        //expectedStory.value = data

        `when`(storyRepository.getStories()).thenReturn(expectedStory)
        val actualStory = mainViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(0, differ.snapshot().size)
    }

    class StoryPagingSource : PagingSource<Int, ListStoryItem>() {
        companion object {
            fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
            return null
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }
    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}