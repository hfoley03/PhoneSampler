package com.example.harryerayaudiorecorder.data

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

class SearchResponseTest {

    @Test
    fun searchResponseCreate() {
        val results = listOf("Item1", "Item2", "Item3")
        val searchResponse = SearchResponse(
            count = results.size,
            next = "nextPageUrl",
            previous = "previousPageUrl",
            results = results
        )

        assertEquals(3, searchResponse.count)
        assertEquals("nextPageUrl", searchResponse.next)
        assertEquals("previousPageUrl", searchResponse.previous)
        assertEquals(results, searchResponse.results)
    }

    @Test
    fun searchResponseNullNextPrev() {
        val results = listOf("Item1", "Item2")
        val searchResponse = SearchResponse(
            count = results.size,
            next = null,
            previous = null,
            results = results
        )

        assertEquals(2, searchResponse.count)
        assertNull(searchResponse.next)
        assertNull(searchResponse.previous)
        assertEquals(results, searchResponse.results)
    }

    @Test
    fun searchResponseEmpty() {
        val results = emptyList<String>()
        val searchResponse = SearchResponse(
            count = results.size,
            next = "nextPageUrl",
            previous = "previousPageUrl",
            results = results
        )

        assertEquals(0, searchResponse.count)
        assertEquals("nextPageUrl", searchResponse.next)
        assertEquals("previousPageUrl", searchResponse.previous)
        assertTrue(searchResponse.results.isEmpty())
    }


}