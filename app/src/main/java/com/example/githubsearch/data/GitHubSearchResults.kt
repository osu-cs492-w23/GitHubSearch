package com.example.githubsearch.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubSearchResults(
    val items: List<GitHubRepo>
)
