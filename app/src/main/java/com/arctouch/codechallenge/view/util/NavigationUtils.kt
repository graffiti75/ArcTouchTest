package com.arctouch.codechallenge.view.util

import android.app.Activity
import com.arctouch.codechallenge.R

object NavigationUtils {
    enum class Animation {
        GO,
        BACK
    }

    fun animate(activity: Activity, animation: Animation) {
        if (animation == Animation.GO) {
            activity.overridePendingTransition(R.anim.open_next, R.anim.close_previous)
        } else {
            activity.overridePendingTransition(R.anim.open_previous, R.anim.close_next)
            activity.finish()
        }
    }
}