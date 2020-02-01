package com.arctouch.codechallenge.presenter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import com.arctouch.codechallenge.model.api.TmdbApi
import com.arctouch.codechallenge.model.data.Cache
import com.arctouch.codechallenge.view.home.HomeActivity
import com.arctouch.codechallenge.view.home.HomeAdapter
import com.arctouch.codechallenge.view.util.networkOn
import com.arctouch.codechallenge.view.util.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_activity.*

class HomePresenterImpl(activity: HomeActivity) : HomePresenter {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    var mActivity = activity

    private lateinit var mComposite: CompositeDisposable
    private lateinit var mScrollListener: RecyclerView.OnScrollListener

    private lateinit var mMoviesAdapter: HomeAdapter
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private var mMoviesWithGenres: MutableList<Movie> = arrayListOf()

    private var mPage: Long = 1
    private var mTotalMovies = 0

    private val mLastVisibleItemPosition: Int
        get() = mLinearLayoutManager.findLastVisibleItemPosition()

    //--------------------------------------------------
    // Override Methods
    //--------------------------------------------------

    override fun setRecyclerView() {
        mLinearLayoutManager = LinearLayoutManager(mActivity)
        mActivity.recyclerView.layoutManager = mLinearLayoutManager
    }

    override fun setAdapter() {
        mMoviesAdapter = HomeAdapter(mActivity, mMoviesWithGenres)
    }

    override fun getData() {
        if (!mActivity.networkOn()) mActivity.showToast(R.string.no_internet)
        else getMovies()
    }

    override fun getMovies() {
        val subscription = mActivity.api.upcomingMovies(
            TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, mPage, TmdbApi.DEFAULT_REGION)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                getMoviesOnSuccess(it)
            }
        mComposite.add(subscription)
    }

    override fun getMoviesOnSuccess(response: UpcomingMoviesResponse) {
        mTotalMovies = response.totalResults
        val moviesList = response.results.map { movie ->
            movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
        }
        if (mMoviesWithGenres.isNotEmpty()) mMoviesWithGenres.clear()

        mMoviesWithGenres.addAll(moviesList)
        Cache.cacheMovies(mMoviesWithGenres)
        showMovies(mMoviesWithGenres)
    }

    override fun showMovies(movieResponse: MutableList<Movie>) {
        if (movieResponse.isNotEmpty()) {
            mMoviesWithGenres = movieResponse
        }

        if (mActivity.recyclerView.adapter == null) {
            setAdapter()
            mActivity.recyclerView.adapter = mMoviesAdapter
        } else mMoviesAdapter.notifyDataSetChanged()
        mActivity.progressBar.visibility = View.GONE
        setRecyclerViewScrollListener()
    }

    override fun setRecyclerViewScrollListener() {
        mScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                onScrollChanged()
            }
        }
        mActivity.recyclerView.addOnScrollListener(mScrollListener)
    }

    override fun onScrollChanged() {
        val totalItemCount = mLinearLayoutManager.itemCount
        if (totalItemCount == mLastVisibleItemPosition + 1) {
            checkScrollVisibility(totalItemCount)
            if (!mActivity.networkOn()) mActivity.showToast(R.string.no_internet)
            else {
                mPage += 1
                getMovies()
                mActivity.recyclerView!!.removeOnScrollListener(mScrollListener)
            }
        }
    }

    override fun checkScrollVisibility(totalItemCount: Int) {
        if (totalItemCount == mTotalMovies) mActivity.progressBar.visibility = View.GONE
        else mActivity.progressBar.visibility = View.VISIBLE
    }

    override fun dispose() {
        mComposite.dispose()
    }
}