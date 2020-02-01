package com.arctouch.codechallenge.model.data

import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie

object Cache {

    var genres = listOf<Genre>()
    var movies = listOf<Movie>()

    fun cacheGenres(genres: List<Genre>) {
        this.genres = genres
    }

    fun cacheMovies(movies: List<Movie>) {
        this.movies = movies
    }
}
