package com.arctouch.codechallenge.view.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.arctouch.codechallenge.AppConfiguration
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.data.Cache
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.arctouch.codechallenge.util.NavigationUtils
import com.arctouch.codechallenge.view.base.BaseActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.detail_activity.*

class DetailActivity : BaseActivity() {

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        val extras = intent.extras
        if (extras != null) {
            val movieId = extras.getInt(AppConfiguration.MOVIE_ID_EXTRA)
            val movie = Cache.movies.filter { movie ->
                movie.id == movieId
            }[0]
            setLayout(movie)
            progressBar.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        NavigationUtils.animate(this, NavigationUtils.Animation.BACK)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    private fun setLayout(movie: Movie) {
        setImages(movie)
        setGenresTextView(movie)
        setOtherTexts(movie)
    }

    private fun setImages(movie: Movie) {
        val movieImageUrlBuilder = MovieImageUrlBuilder()

        Glide.with(id_details__backdrop__image_view)
            .load(movie.backdropPath?.let { movieImageUrlBuilder.buildBackdropUrl(it) })
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(id_details__backdrop__image_view)

        Glide.with(id_details__poster__image_view)
            .load(movie.posterPath?.let { movieImageUrlBuilder.buildPosterUrl(it) })
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(id_details__poster__image_view)
    }

    private fun setGenresTextView(movie: Movie) {
        var allGenres = ""
        Cache.genres.forEach { genre ->
            movie.genres!!
                .filter { it.id == genre.id }
                .fold(listOf<String>()) { list, genre -> list + genre.name }
                .forEach { allGenres += "$it " }
        }
        id_details__genres__text_view.text = allGenres
    }

    private fun setOtherTexts(movie: Movie) {
        id_details__name__text_view.text = movie.title
        id_details__release_date__text_view.text = movie.releaseDate
        id_details__overview__text_view.text = movie.overview
    }
}