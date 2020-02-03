package com.arctouch.codechallenge.view

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.arctouch.codechallenge.AppConfiguration
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.data.Cache
import kotlin.math.ceil

class MoviesViewModel(application: Application) : AndroidViewModel(application) {
    var page: Int = 1
    var noMoreScrolling: Boolean = false
    var pagesNeeded: Int = 1

    fun getMovieList(): MutableList<Movie> {
        return Cache.movies
    }

    fun checkScrollingEnd(totalResultsFromApi: Long) {
        val pagesNeeded = ceil((totalResultsFromApi.toFloat() / AppConfiguration.MOVIES_PER_PAGE.toFloat()))
        this.pagesNeeded = pagesNeeded.toInt()
        val noMoreScrolling : Boolean = this.page >= pagesNeeded.toLong()
        this.noMoreScrolling = noMoreScrolling
    }
}