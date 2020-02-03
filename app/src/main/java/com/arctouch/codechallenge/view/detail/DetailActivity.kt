package com.arctouch.codechallenge.view.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.presenter.detail.DetailPresenterImpl
import com.arctouch.codechallenge.view.util.NavigationUtils

class DetailActivity : AppCompatActivity() {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private lateinit var mDetailPresenter: DetailPresenterImpl

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        mDetailPresenter = DetailPresenterImpl(this)
        mDetailPresenter.getExtras()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        NavigationUtils.animate(this, NavigationUtils.Animation.BACK)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}