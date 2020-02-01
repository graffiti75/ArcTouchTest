package com.arctouch.codechallenge.presenter

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
    private lateinit var mMovieName : String

    private val mLastVisibleItemPosition: Int
        get() = mLinearLayoutManager.findLastVisibleItemPosition()

    private lateinit var mSearchView: SearchView

    private val mRetrofitApi by lazy { RetrofitClient.create() }

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
        mComposite = CompositeDisposable()
        if (!mActivity.networkOn()) mActivity.showToast(R.string.no_internet)
        else getGenres()
    }

    override fun getGenres() {
        val subscription = mRetrofitApi.genres(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Cache.cacheGenres(it.genres)
                getMovies(false)
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
                    clearMoviesList(query)
                    searchMovies()
                } catch (e: Exception) {
                    mActivity.showToast(R.string.search_error)
                }
                searchItem.collapseActionView()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }

    override fun clearMoviesList(query: String) {
        mPage = 1
        mMovieName = query
        mMoviesWithGenres.clear()
    }

    override fun searchMovies() {
        val subscription = mRetrofitApi.search(TmdbApi.API_KEY, mMovieName, mPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                getMoviesOnSuccess(true, it)
            }
        mComposite.add(subscription)
    }

    override fun getMovies(isToolbarMenuSearch: Boolean) {
        val subscription = mRetrofitApi.upcomingMovies(
            TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, mPage, TmdbApi.DEFAULT_REGION)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                getMoviesOnSuccess(isToolbarMenuSearch, it)
            }
        mComposite.add(subscription)
    }

    override fun getMoviesOnSuccess(isToolbarMenuSearch: Boolean, response: UpcomingMoviesResponse) {
        val moviesList = response.results.map { movie ->
            movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
        }

        mMoviesWithGenres.addAll(moviesList)
        Cache.cacheMovies(mMoviesWithGenres)
        showMovies(isToolbarMenuSearch, mMoviesWithGenres)
    }

    override fun showMovies(isToolbarMenuSearch: Boolean, movieList: MutableList<Movie>) {
        if (movieList.isNotEmpty()) {
            mMoviesWithGenres = movieList
        }

        if (mActivity.recyclerView.adapter == null) {
            setAdapter()
            mActivity.recyclerView.adapter = mMoviesAdapter
        } else {
            mMoviesAdapter.notifyDataSetChanged()
        }
        mActivity.progressBar.visibility = View.GONE
        setRecyclerViewScrollListener(isToolbarMenuSearch)
    }

    override fun setRecyclerViewScrollListener(isToolbarMenuSearch: Boolean) {
//        if (!isToolbarMenuSearch) {
            mScrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    onScrollChanged(isToolbarMenuSearch)
                }
            }
            mActivity.recyclerView.addOnScrollListener(mScrollListener)
//        }
    }

    override fun onScrollChanged(isToolbarMenuSearch: Boolean) {
        val totalItemCount = mLinearLayoutManager.itemCount
        if (totalItemCount == mLastVisibleItemPosition + 1) {
            mActivity.progressBar.visibility = View.VISIBLE
            if (!mActivity.networkOn()) mActivity.showToast(R.string.no_internet)
            else {
                mPage += 1
                if (isToolbarMenuSearch) searchMovies()
                else getMovies(false)
                mActivity.recyclerView!!.removeOnScrollListener(mScrollListener)
            }
        }
    }

    override fun dispose() {
        mComposite.dispose()
    }
}