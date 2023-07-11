package com.example.storyapp.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.Repository
import com.example.storyapp.model.properti
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    val isLoading: LiveData<Boolean> = repository.isLoading

    val registerRespon: LiveData<RegisterRespon> = repository.registerRespon

    val toastText: LiveData<properti<String>> = repository.toast

    fun RegisteSession(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.postRegister(name, email, password)
        }
    }
}