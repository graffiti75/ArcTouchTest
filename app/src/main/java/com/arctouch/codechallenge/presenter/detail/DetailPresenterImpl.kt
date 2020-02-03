package com.arctouch.codechallenge.presenter.detail

import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View
import com.arctouch.codechallenge.AppConfiguration
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.data.Cache
import com.arctouch.codechallenge.view.detail.DetailActivity
import com.arctouch.codechallenge.view.util.MovieImageUrlBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.detail_activity.*

class DetailPresenterImpl(activity: DetailActivity) : DetailPresenter {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    var mActivity = activity

    //--------------------------------------------------
    // Override Methods
    //--------------------------------------------------

    override fun setToolbar(title: String) {
        mActivity.setSupportActionBar(mActivity.findViewById(R.id.toolbar))
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val color = ContextCompat.getColor(mActivity, android.R.color.transparent)
        mActivity.collapsing_toolbar.setExpandedTitleColor(color)
        mActivity.collapsing_toolbar.title = title
    }

    override fun getExtras() {
        val extras = mActivity.intent.extras
        if (extras != null) {
            val movieId = extras.getInt(AppConfiguration.MOVIE_ID_EXTRA)
            val movie = Cache.movies.filter { movie ->
                movie.id == movieId
            }[0]

            setToolbar(movie.title)
            setLayout(movie)
        }
    }

    override fun setLayout(movie: Movie) {
        setImages(movie)
        setGenresTextView(movie)
        setOtherTexts(movie)
    }

    override fun setImages(movie: Movie) {
        val movieImageUrlBuilder = MovieImageUrlBuilder()

        Glide.with(mActivity.id_details__poster__image_view)
            .load(movie.posterPath?.let { movieImageUrlBuilder.buildPosterUrl(it) })
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(mActivity.id_details__poster__image_view)

        Glide.with(mActivity.id_details__backdrop__image_view)
            .load(movie.backdropPath?.let { movieImageUrlBuilder.buildBackdropUrl(it) })
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?,
                    isFirstResource: Boolean): Boolean = false
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?,
                    dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    mActivity.id_details__backdrop__image_view__progress_bar.visibility = View.GONE
                    return false
                }
            })
            .into(mActivity.id_details__backdrop__image_view)
    }

    override fun setGenresTextView(movie: Movie) {
        var allGenres = ""
        Cache.genres.forEach { genre ->
            movie.genres!!
                .filter { it.id == genre.id }
                .fold(listOf<String>()) { list, genre -> list + genre.name }
                .forEach { allGenres += "$it " }
        }
        mActivity.id_details__genres__text_view.text = allGenres
    }

    override fun setOtherTexts(movie: Movie) {
        mActivity.id_details__title__text_view.text = movie.title
        mActivity.id_details__name__text_view.text = movie.title
        mActivity.id_details__release_date__text_view.text = movie.releaseDate
        mActivity.id_details__overview__text_view.text = movie.overview
    }
}