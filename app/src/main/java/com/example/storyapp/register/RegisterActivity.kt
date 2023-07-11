package com.example.storyapp.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.login.LoginActivity
import com.example.storyapp.model.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var viewfactory: ViewModelFactory

    private val registerViewModel: RegisterViewModel by viewModels {
        viewfactory
    }
    private fun viewModel() {
        viewfactory = ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.title_register)
            setDisplayHomeAsUpEnabled(true)
        }

        animation()
        viewModel()
        Register()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    //bagian animation
    private fun animation() {
        ObjectAnimator.ofFloat(binding.pgRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleText = ObjectAnimator.ofFloat(binding.registerTitle, View.ALPHA, 1f)
        val nameText = ObjectAnimator.ofFloat(binding.titleName, View.ALPHA, 1f)
        val nameFrom = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f)
        val emailText = ObjectAnimator.ofFloat(binding.titleEmail, View.ALPHA, 1f)
        val emailFrom = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f)
        val passwordTExt = ObjectAnimator.ofFloat(binding.titlePassword, View.ALPHA, 1f)
        val passwordFrom = ObjectAnimator.ofFloat(binding.formPassword, View.ALPHA, 1f)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f)

        val sequentially = AnimatorSet().apply {
            playSequentially(
                titleText,
                nameText,
                nameFrom,
                emailText,
                emailFrom,
                passwordTExt,
                passwordFrom,
                register
            )
            startDelay = 500
        }

        sequentially.start()
    }


    //setup untuk register
    private fun EditText.isEmpty(): Boolean {
        return this.length() == 0
    }

    private fun Register() {
        binding.btnRegister.setOnClickListener {
            val namefrom = binding.fromName.text.toString().trim()
            val emailfrom = binding.fromEmail.text.toString().trim()
            val passwordfrom = binding.edtPassword.text.toString().trim()

            if (namefrom.isEmpty() || emailfrom.isEmpty() || passwordfrom.isEmpty()) {
                binding.fromName.error = getString(R.string.required_field)
                binding.fromEmail.error = getString(R.string.required_field)
                binding.edtPassword.setError(getString(R.string.required_field), null)
            } else {
                Loading()
                post()
                Toast()
                move()
            }
        }
    }

    private fun Loading() {
        registerViewModel.isLoading.observe(this@RegisterActivity) {
            binding.pbRegister.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun post() {
        binding.apply {
            val name = fromName.text.toString()
            val email = fromEmail.text.toString()
            val password = edtPassword.text.toString()
            val formattedEmail = email.toLowerCase()

            registerViewModel.RegisteSession(name, formattedEmail, password)
        }
    }

    private fun Toast() {
        registerViewModel.toastText.observe(this@RegisterActivity) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(this@RegisterActivity, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //bagian intent untuk berpindah activity
    private fun move() {
        registerViewModel.registerRespon.observe(this@RegisterActivity) { response ->
            if (!response.error) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }


}