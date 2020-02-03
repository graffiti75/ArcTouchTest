package com.arctouch.codechallenge.presenter.home

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.View
import android.widget.SearchView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import com.arctouch.codechallenge.model.api.RetrofitClient
import com.arctouch.codechallenge.model.api.TmdbApi
import com.arctouch.codechallenge.model.data.Cache
import com.arctouch.codechallenge.view.MoviesViewModel
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

    private var mComposite = CompositeDisposable()
    private lateinit var mScrollListener: RecyclerView.OnScrollListener

    private lateinit var mMoviesAdapter: HomeAdapter
    private lateinit var mLinearLayoutManager: LinearLayoutManager

    private var mMoviesWithGenres: MutableList<Movie> = arrayListOf()
    private lateinit var mMovieName : String
    private var mTotalResultsFromApi: Long = 1

    private lateinit var mSearchView: SearchView
    private var mIsToolbarMenuSearch = false

    private val mRetrofitApi by lazy { RetrofitClient.create() }
    private var mMovieViewModel = ViewModelProviders.of(activity).get(MoviesViewModel::class.java)

    //--------------------------------------------------
    // Override Methods
    //--------------------------------------------------

    override fun checkSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) getData()
        else {
            val moviesViewModel = ViewModelProviders.of(mActivity).get(MoviesViewModel::class.java)
            val moviesList = moviesViewModel.getMovieList()
            showMovies(moviesList)
        }
    }

    override fun setRecyclerView() {
        mLinearLayoutManager = LinearLayoutManager(mActivity)
        mActivity.recyclerView.layoutManager = mLinearLayoutManager
    }

    override fun setAdapter() {
        mMoviesAdapter = HomeAdapter(mActivity, mMoviesWithGenres)
    }

    override fun getData() {
        if (!mActivity.networkOn()) mActivity.showToast(R.string.no_internet)
        else getGenres()
    }

    override fun getGenres() {
        val subscription = mRetrofitApi.genres(
        TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Cache.cacheGenres(it.genres)
                getMovies()
            }
        mComposite.add(subscription)
    }

    override fun initMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.search)
        mSearchView = searchItem.actionView as SearchView
        mSearchView.queryHint = mActivity.getString(R.string.search_hint)
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                try {
                    if (!mActivity.networkOn()) mActivity.showToast(R.string.no_internet)
                    else {
                        clearMoviesList(query)
                        searchMovies()
                    }
                } catch (e: Exception) {
                    mActivity.showToast(R.string.search_error)
                }
                searchItem.collapseActionView()
                return false
            }
            override fun onQueryTextChange(newText: String) = true
        })
    }

    override fun clearMoviesList(query: String) {
        mMovieName = query
        mMoviesWithGenres.clear()
        Cache.clearCacheMovies()
        mMovieViewModel.page = 1
        mMovieViewModel.noMoreScrolling = false
        mMovieViewModel.pagesNeeded = 1
    }

    override fun searchMovies() {
        if (mMovieViewModel.page <= mMovieViewModel.pagesNeeded) {
            val subscription = mRetrofitApi.search(
            TmdbApi.API_KEY, mMovieName, mMovieViewModel.page.toLong())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mIsToolbarMenuSearch = true
                    getMoviesOnSuccess(it)
                }
            mComposite.add(subscription)
        }
    }

    override fun getMovies() {
        if (!mActivity.networkOn()) mActivity.showToast(R.string.no_internet)
        else {
            if (mMovieViewModel.page <= mMovieViewModel.pagesNeeded) {
                val subscription = mRetrofitApi.upcomingMovies(
                    TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, mMovieViewModel.page.toLong(), TmdbApi.DEFAULT_REGION)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mIsToolbarMenuSearch = false
                        getMoviesOnSuccess(it)
                    }
                mComposite.add(subscription)
            }
        }
    }

    override fun getMoviesOnSuccess(response: UpcomingMoviesResponse) {
        val moviesList = response.results.map { movie ->
            movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
        }
        mTotalResultsFromApi = response.totalResults

        if (!mMovieViewModel.noMoreScrolling) {
            mMoviesWithGenres.addAll(moviesList)
            Cache.cacheMovies(moviesList as MutableList<Movie>)
        }

        mMovieViewModel.checkScrollingEnd(mTotalResultsFromApi)
        showMovies(mMoviesWithGenres)
    }

    override fun showMovies(movieList: MutableList<Movie>) {
        if (movieList.isNotEmpty()) mMoviesWithGenres = movieList

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
        if (!mMovieViewModel.noMoreScrolling) {
                mActivity.progressBar.visibility = View.VISIBLE
                if (!mActivity.networkOn()) mActivity.showToast(R.string.no_internet)
                else {
                    mMovieViewModel.page += 1
                    if (mIsToolbarMenuSearch) searchMovies()
                    else getMovies()
                    mActivity.recyclerView!!.removeOnScrollListener(mScrollListener)
                }
        } else mActivity.progressBar.visibility = View.GONE
    }

    /*
    override fun checkScrollingEnd() {
        val pagesNeeded = ceil((mTotalResultsFromApi.toFloat() / AppConfiguration.MOVIES_PER_PAGE.toFloat()))
        mMovieViewModel.pagesNeeded = pagesNeeded.toInt()
        val noMoreScrolling : Boolean = mMovieViewModel.page >= pagesNeeded.toLong()
        mMovieViewModel.noMoreScrolling = noMoreScrolling
    }
     */

    override fun dispose() {
        mComposite.dispose()
    }
}