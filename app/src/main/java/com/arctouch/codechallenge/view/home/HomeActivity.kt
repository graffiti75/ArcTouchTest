package com.arctouch.codechallenge.view.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.presenter.HomePresenterImpl

class HomeActivity : AppCompatActivity() {

    private lateinit var mHomePresenter: HomePresenterImpl

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        mHomePresenter = HomePresenterImpl(this)
        mHomePresenter.setRecyclerView()
        mHomePresenter.getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHomePresenter.dispose()
    }

    //--------------------------------------------------
    // Menu
    //--------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        mHomePresenter.initMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.upcoming -> {
                mHomePresenter.clearMoviesList()
                mHomePresenter.getMovies(false)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
     */
}