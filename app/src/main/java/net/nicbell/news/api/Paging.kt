package net.nicbell.news.api


data class Paging<T>(val totalItems: Int,
                     val content: List<T>)