package com.arctouch.codechallenge.view.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.presenter.home.HomePresenterImpl

class HomeActivity : AppCompatActivity() {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private lateinit var mHomePresenter: HomePresenterImpl

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        mHomePresenter = HomePresenterImpl(this)
        mHomePresenter.setRecyclerView()
        mHomePresenter.checkSavedInstanceState(savedInstanceState)
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
}