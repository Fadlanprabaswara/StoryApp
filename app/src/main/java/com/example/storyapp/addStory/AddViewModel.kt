package com.example.storyapp.addStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.Repository
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.properti
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val repository: Repository) : ViewModel() {

    val upload: LiveData<AddStoryResponse> = repository.upload

    val Loading: LiveData<Boolean> = repository.isLoading

    val toast: LiveData<properti<String>> = repository.toast

    fun Story(token: String, file: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            repository.Story(token, file, description)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }

}