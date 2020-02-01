package com.arctouch.codechallenge.view.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arctouch.codechallenge.AppConfiguration
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.view.util.MovieImageUrlBuilder
import com.arctouch.codechallenge.view.util.openActivityExtras
import com.arctouch.codechallenge.view.detail.DetailActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.movie_item.view.*

class HomeAdapter(private val activity: HomeActivity, private val movies: MutableList<Movie>)
    : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(activity, movies[position])

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val movieImageUrlBuilder = MovieImageUrlBuilder()

        fun bind(activity: HomeActivity, movie: Movie) {
            itemView.titleTextView.text = movie.title
            itemView.genresTextView.text = movie.genres?.joinToString(separator = ", ") { it.name }
            itemView.releaseDateTextView.text = movie.releaseDate

            Glide.with(itemView)
                .load(movie.posterPath?.let { movieImageUrlBuilder.buildPosterUrl(it) })
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(itemView.posterImageView)

            itemView.cardView.setOnClickListener {
                activity.openActivityExtras(activity, DetailActivity::class.java,
                    AppConfiguration.MOVIE_ID_EXTRA, movie.id)
            }
        }
    }
}