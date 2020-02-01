package com.arctouch.codechallenge.presenter

import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse

interface HomePresenter {
    fun setRecyclerView()
    fun setAdapter()
    fun getData()
    fun getMovies()
    fun getMoviesOnSuccess(response: UpcomingMoviesResponse)
    fun showMovies(movieResponse: MutableList<Movie>)
    fun setRecyclerViewScrollListener()
    fun onScrollChanged()
    fun dispose()
}