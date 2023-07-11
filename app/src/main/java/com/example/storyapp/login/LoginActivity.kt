package com.example.storyapp.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.ViewModelFactory
import com.example.storyapp.strorypage.ListStoryActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModelFactory: ViewModelFactory

    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.title_login)
            setDisplayHomeAsUpEnabled(true)
        }

        Animation()
        Login()
        ViewModel()
    }

    companion object {
        private const val Api_Key = "Bearer "
    }

    private fun ViewModel() {
        viewModelFactory = ViewModelFactory.getInstance(this)
    }

    //bagian setup login
    private fun Login() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                when {
                    email.isEmpty() -> {
                        edtEmail.error = getString(R.string.required_field)
                    }
                    password.isEmpty() -> {
                        edtPassword.error = getString(R.string.required_field)
                    }
                    else -> {
                        Loading()
                        post()
                        Toast()
                        loginViewModel.login()
                        move()
                    }
                }
            }
        }
    }

    private fun Loading() {
        loginViewModel.Loading.observe(this@LoginActivity) {
            binding.pbLogin.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun post() {
        binding.apply {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            loginViewModel.LoginSesion(email, password)
        }

        loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
            saveSession(
                UserModel(
                    response.loginResult?.name.toString(),
                    Api_Key+ (response.loginResult?.token.toString()),
                    true
                )
            )
        }
    }


    private fun saveSession(session: UserModel) {
        loginViewModel.saveSession(session)
    }

    private fun Toast() {
        loginViewModel.toast.observe(this@LoginActivity) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(this@LoginActivity, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    }


    //bagian intent berpindah Activity
    private fun move() {
        loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
            if (!response.error) {
                startActivity(Intent(this@LoginActivity, ListStoryActivity::class.java))
                finish()
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    //bagian Animation
    private fun Animation() {
        val pgLoginAnimator =
            ObjectAnimator.ofFloat(binding.pgLogin, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }

        val titletext =
            ObjectAnimator.ofFloat(binding.titleLogin, View.ALPHA, 0f, 1f).setDuration(500)
        val messagetext =
            ObjectAnimator.ofFloat(binding.titleMessage, View.ALPHA, 0f, 1f).setDuration(500)
        val emailtext =
            ObjectAnimator.ofFloat(binding.titleEmail, View.ALPHA, 0f, 1f).setDuration(500)
        val emailFrom =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 0f, 1f).setDuration(500)
        val passwordtext =
            ObjectAnimator.ofFloat(binding.titlePassword, View.ALPHA, 0f, 1f).setDuration(500)
        val passwordFrom =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 0f, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 0f, 1f).setDuration(500)

        AnimatorSet().apply {
            playTogether(
                pgLoginAnimator,
                titletext,
                messagetext,
                emailtext,
                emailFrom,
                passwordtext,
                passwordFrom,
                login
            )
            startDelay = 500
            start()
        }
    }


}