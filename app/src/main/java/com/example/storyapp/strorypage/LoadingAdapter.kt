package com.example.storyapp.strorypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.databinding.ItemLoadingBinding

class LoadingAdapter(private val retry: () -> Unit) :

    LoadStateAdapter<LoadingAdapter.LoadingStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadingStateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.titleError.text = loadState.error.localizedMessage
            }
            binding.apply {
                loading.isVisible = loadState is LoadState.Loading
                titleError.isVisible = loadState is LoadState.Error
                btnRetry.isVisible = loadState is LoadState.Error
            }
        }

        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
            bind(loadState)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState, ): LoadingStateViewHolder {

        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)

    }
}