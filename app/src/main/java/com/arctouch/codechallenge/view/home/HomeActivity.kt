package com.arctouch.codechallenge.view.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import com.arctouch.codechallenge.model.api.TmdbApi
import com.arctouch.codechallenge.model.data.Cache
import com.arctouch.codechallenge.util.networkOn
import com.arctouch.codechallenge.util.showToast
import com.arctouch.codechallenge.view.base.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : BaseActivity() {

    private lateinit var mComposite: CompositeDisposable
    private lateinit var mScrollListener: RecyclerView.OnScrollListener

    private var mPage: Long = 1
    private var mTotalMovies = 0

    private lateinit var mMoviesAdapter: HomeAdapter
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private var mMoviesWithGenres: MutableList<Movie> = arrayListOf()

    private val mLastVisibleItemPosition: Int
        get() = mLinearLayoutManager.findLastVisibleItemPosition()

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        mComposite = CompositeDisposable()

        setRecyclerView()
        getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        mComposite.dispose()
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    private fun setRecyclerView() {
        mLinearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLinearLayoutManager
    }

    private fun setAdapter() {
        mMoviesAdapter = HomeAdapter(this, mMoviesWithGenres)
    }

    private fun getData() {
        if (!networkOn()) showToast(R.string.no_internet)
        else getMovies()
    }

    private fun getMovies() {
        val subscription = api.upcomingMovies(
            TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, mPage, TmdbApi.DEFAULT_REGION)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onSuccess(it)
            }
        mComposite.add(subscription)
    }

    private fun onSuccess(response: UpcomingMoviesResponse) {
        mTotalMovies = response.totalResults
        val moviesList = response.results.map { movie ->
            movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
        }
        if (mMoviesWithGenres.isNotEmpty()) mMoviesWithGenres.clear()

        mMoviesWithGenres.addAll(moviesList)
        Cache.cacheMovies(mMoviesWithGenres)
        showMovies(mMoviesWithGenres)
    }

    private fun showMovies(movieResponse: MutableList<Movie>) {
        if (movieResponse.isNotEmpty()) {
            mMoviesWithGenres = movieResponse
        }

        if (recyclerView.adapter == null) {
            setAdapter()
            recyclerView.adapter = mMoviesAdapter
        } else {
            mMoviesAdapter.notifyDataSetChanged()
        }
        progressBar.visibility = View.GONE
        setRecyclerViewScrollListener()
    }

    private fun setRecyclerViewScrollListener() {
        mScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                onScrollChanged()
            }
        }
        recyclerView.addOnScrollListener(mScrollListener)
    }

    private fun onScrollChanged() {
        val totalItemCount = mLinearLayoutManager.itemCount
        if (totalItemCount == mLastVisibleItemPosition + 1) {
            checkScrollVisibility(totalItemCount)
            if (!networkOn()) showToast(R.string.no_internet)
            else {
                mPage += 1
                getMovies()
                recyclerView!!.removeOnScrollListener(mScrollListener)
            }
        }
    }

    private fun checkScrollVisibility(totalItemCount: Int) {
        if (totalItemCount == mTotalMovies) progressBar.visibility = View.GONE
        else progressBar.visibility = View.VISIBLE
    }
}