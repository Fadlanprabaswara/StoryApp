package com.example.storyapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.Repository
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.properti
import com.example.storyapp.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {

    val Loading: LiveData<Boolean> = repository.isLoading

    val loginResponse: LiveData<LoginResponse> = repository.loginResponse

    val toast: LiveData<properti<String>> =repository.toast

    fun LoginSesion(email: String, password: String) {
        viewModelScope.launch {
            repository.Login(email, password)
        }
    }

    fun saveSession(session: UserModel) {
        viewModelScope.launch {
            repository.saveSession(session)
        }
    }

    fun login() {
        viewModelScope.launch {
            repository.login()
        }
    }
}