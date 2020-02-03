package com.arctouch.codechallenge.view

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.data.Cache

class MoviesViewModel(application: Application) : AndroidViewModel(application) {
    var page: Int = 1
    var noMoreScrolling: Boolean = false
    var pagesNeeded: Int = 1

    fun getMovieList(): MutableList<Movie> {
        return Cache.movies
    }
}