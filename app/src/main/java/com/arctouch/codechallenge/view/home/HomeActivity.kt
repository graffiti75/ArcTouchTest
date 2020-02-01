package com.arctouch.codechallenge.view.home

import android.os.Bundle
import android.view.View
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.api.TmdbApi
import com.arctouch.codechallenge.view.base.BaseActivity
import com.arctouch.codechallenge.model.data.Cache
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : BaseActivity() {

    private lateinit var mComposite: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        mComposite = CompositeDisposable()

        val subscription = api.upcomingMovies(
            TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, 1, TmdbApi.DEFAULT_REGION)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val moviesWithGenres = it.results.map { movie ->
                    movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                }
                Cache.cacheMovies(moviesWithGenres)
                recyclerView.adapter = HomeAdapter(this, moviesWithGenres)
                progressBar.visibility = View.GONE
            }
        mComposite.add(subscription)
    }

    override fun onDestroy() {
        super.onDestroy()
        mComposite.dispose()
    }
}
