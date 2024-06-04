package com.example.harryerayaudiorecorder.data

data class SearchResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)