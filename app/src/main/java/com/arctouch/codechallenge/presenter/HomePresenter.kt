package com.arctouch.codechallenge.presenter

import android.view.Menu
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse

interface HomePresenter {
    fun setRecyclerView()
    fun setAdapter()

    fun getData()
    fun getGenres()

    fun initMenu(menu: Menu)
    fun clearMoviesList(query: String)
    fun searchMovies()

    fun getMovies(isToolbarMenuSearch: Boolean)
    fun getMoviesOnSuccess(isToolbarMenuSearch: Boolean, response: UpcomingMoviesResponse)
    fun showMovies(isToolbarMenuSearch: Boolean, movieResponse: MutableList<Movie>)

    fun setRecyclerViewScrollListener(isToolbarMenuSearch: Boolean)
    fun onScrollChanged(isToolbarMenuSearch: Boolean)
    fun dispose()
}