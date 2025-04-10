package com.dicoding.storydicoding.view.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storydicoding.data.response.ListStoryItem
import com.dicoding.storydicoding.databinding.ItemStoryBinding

class StoryAdapter(private val onItemClick: (String) -> Unit) :
    PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem, onItemClick: (String) -> Unit) {
            binding.tvItemName.text = story.name
            binding.tvItemDescription.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            itemView.setOnClickListener {
                story.id?.let { id -> onItemClick(id) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story, onItemClick)
        }
        //holder.bind(getItem(position), onItemClick)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}