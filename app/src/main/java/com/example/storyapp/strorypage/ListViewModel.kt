package com.example.storyapp.strorypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.model.Repository
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.properti
import com.example.storyapp.response.ListStoryItem
import com.example.storyapp.response.StoriesResponse
import kotlinx.coroutines.launch

class ListViewModel(private val repository: Repository) : ViewModel() {

    val Toast: LiveData<properti<String>> = repository.toast

    val list: LiveData<StoriesResponse> = repository.list

    val Loading: LiveData<Boolean> = repository.isLoading

    val ListStories: LiveData<PagingData<ListStoryItem>> =
        repository.getStory().cachedIn(viewModelScope)


    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }

    fun getLocation(token: String) {
        viewModelScope.launch {
            repository.getLocation(token)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}