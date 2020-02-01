package com.arctouch.codechallenge.view.home

import android.os.Bundle
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.presenter.HomePresenterImpl
import com.arctouch.codechallenge.view.base.BaseActivity

class HomeActivity : BaseActivity() {

    private lateinit var mHomePresenter: HomePresenterImpl

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
}