package com.example.storyapp

import com.example.storyapp.response.ListStoryItem

object DataDummy {

    fun generateDummyResponse(): List<ListStoryItem> {
        val items = arrayListOf<ListStoryItem>()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "name + $i",
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "description $i",
                i.toDouble() * 10,
                i.toDouble() * 10
            )
            items.add(quote)
        }
        return items
    }

}