package com.arctouch.codechallenge.presenter.home

import android.os.Bundle
import android.view.Menu
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse

interface HomePresenter {
    fun checkSavedInstanceState(savedInstanceState: Bundle?)
    fun setRecyclerView()
    fun setAdapter()

    fun getData()
    fun getGenres()

    fun initMenu(menu: Menu)
    fun clearMoviesList(query: String)
    fun searchMovies()

    fun getMovies()
    fun getMoviesOnSuccess(response: UpcomingMoviesResponse)
    fun showMovies(movieList: MutableList<Movie>)

    fun setRecyclerViewScrollListener()
    fun onScrollChanged()
    fun checkScrollingEnd()
    fun dispose()
}