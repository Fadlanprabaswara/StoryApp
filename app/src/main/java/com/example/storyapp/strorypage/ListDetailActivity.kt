package com.example.storyapp.strorypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityListDetailBinding
import com.example.storyapp.response.ListStoryItem

class ListDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListDetailBinding

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.title_detail)
            setDisplayHomeAsUpEnabled(true)
        }

        Detail()

    }

    private fun Detail() {
        val data = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA)
        if (data != null) {
            binding.apply {
                tvDetailName.text = data.name
                tvDetailDescription.text = data.description
                Glide.with(this@ListDetailActivity)
                    .load(data.photo)
                    .placeholder(R.drawable.ic_baseline_loading)
                    .error(R.drawable.ic_baseline_error)
                    .into(ivDetailPhoto)
            }
        } else {
            finish()
            Toast.makeText(this, "Error: Data not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}