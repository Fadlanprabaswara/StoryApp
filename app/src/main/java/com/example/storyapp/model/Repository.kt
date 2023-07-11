package com.example.storyapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.Api.ApiService
import com.example.storyapp.addStory.AddStoryResponse
import com.example.storyapp.register.RegisterRespon
import com.example.storyapp.response.*
import com.example.storyapp.strorypage.StorySource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Repository private constructor(
    private val userPrefence: UserPrefence,
    private val apiService: ApiService,
) {

    private val _registerRespon = MutableLiveData<RegisterRespon>()
    val registerRespon: LiveData<RegisterRespon> = _registerRespon

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _upload = MutableLiveData<AddStoryResponse>()
    val upload: LiveData<AddStoryResponse> = _upload

    private val _toast = MutableLiveData<properti<String>>()
    val toast: LiveData<properti<String>> = _toast

    private val _list = MutableLiveData<StoriesResponse>()
    val list: LiveData<StoriesResponse> = _list

    fun getSession(): LiveData<UserModel> {
        return userPrefence.getUser().asLiveData()
    }

    suspend fun saveSession(session: UserModel) {
        userPrefence.saveSession(session)
    }

    suspend fun login() {
        userPrefence.login()
    }

    suspend fun logout() {
        userPrefence.logout()
    }

    companion object {
        private const val TAG = "Repository"

        @Volatile
        private var instance: Repository? = null
        fun getInstance(preferences: UserPrefence, apiService: ApiService): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(preferences, apiService)
            }.also { instance = it }
    }

    //bagian memanggil Api Login

    fun Login(email: String, password: String) {
        _isLoading.value = true
        val client = apiService.postLogin(email, password)

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>,
            ) {
                _isLoading.value = false
                when {
                    response.isSuccessful && response.body() != null -> {
                        _loginResponse.value = response.body()
                        _toast.value = properti(response.body()?.message.toString())
                    }
                    else -> {
                        _toast.value = properti(response.message().toString())
                        Log.e(
                            TAG,
                            "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _toast.value = properti(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    //bagian memanggil Api Register
    fun postRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.postRegister(name, email, password)

        client.enqueue(object : Callback<RegisterRespon> {
            override fun onResponse(
                call: Call<RegisterRespon>,
                response: Response<RegisterRespon>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _registerRespon.value = response.body()
                    _toast.value = properti(response.body()?.message.toString())
                } else {
                    _toast.value = properti(response.message().toString())
                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<RegisterRespon>, t: Throwable) {
                _toast.value = properti(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    //api untuk mengambil stories sudah menggunakan paging 3
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        _isLoading.value = true

        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StorySource(userPrefence, apiService)
            }
        ).liveData.map { pagingData ->
            _isLoading.value = false
            pagingData
        }
    }


    //api untuk uploadstory

    fun Story(token: String, file: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        val client = apiService.postStory(token, file, description)

        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>,
            ) {
                _isLoading.value = false
                when {
                    response.isSuccessful && response.body() != null -> {
                        _upload.value = response.body()
                        _toast.value = properti(response.body()?.message.toString())
                    }
                    else -> {
                        _toast.value = properti(response.message().toString())
                        Log.e(
                            TAG,
                            "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                Log.d("error upload", t.message.toString())
            }
        })
    }


    //untuk location story
    fun getLocation(token: String) {
        _isLoading.value = true
        val client = apiService.getLocation(token)

        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _list.value = response.body()
                } else {
                    _toast.value = properti(response.message().toString())
                }
            }
            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _toast.value = properti(t.message.toString())
            }
        })
    }

}





