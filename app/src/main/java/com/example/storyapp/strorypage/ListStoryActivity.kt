package com.example.storyapp.strorypage

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.addStory.AddActivity
import com.example.storyapp.databinding.ActivityListStoryBinding
import com.example.storyapp.map.MapsActivity
import com.example.storyapp.model.ViewModelFactory

class ListStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListStoryBinding

    private lateinit var vieModelFactory: ViewModelFactory

    private lateinit var listStoryAdapter: ListStoryAdapter

    private var token = ""

    private val listViewModel: ListViewModel by viewModels { vieModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel()
        adapter()
        user()
        action()
    }

    //bagian intent pindah ke halaman Add
    private fun action() {
        binding.buttonAdd.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    private fun user() {
        loading()
        listViewModel.getSession().observe(this@ListStoryActivity) {
            token = it.token
            if (!it.isLogin) {
                move()
            } else {
                setupData()
            }
        }
        toast()
    }

    private fun toast() {
        listViewModel.Toast.observe(this@ListStoryActivity) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(this@ListStoryActivity, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loading() {
        listViewModel.Loading.observe(this@ListStoryActivity) {
            binding.loading.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun setupData() {
        listViewModel.ListStories.observe(this@ListStoryActivity) { pagingData ->
            listStoryAdapter.submitData(lifecycle, pagingData)
        }
    }

    private fun move() {
        startActivity(Intent(this@ListStoryActivity, MainActivity::class.java))
        finish()
    }


    private fun adapter() {
        listStoryAdapter = ListStoryAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@ListStoryActivity)
            adapter = listStoryAdapter.withLoadStateFooter(footer = LoadingAdapter {
                listStoryAdapter.retry()
            }
            )
        }
    }

    private fun viewModel() {
        vieModelFactory = ViewModelFactory.getInstance(this)
    }

    //bagian menu Logout

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_logout -> {
                listViewModel.logout()
                true
            }
            R.id.btn_map -> {
                startActivity(Intent(this@ListStoryActivity, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}