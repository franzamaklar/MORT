package com.masters.mort.utilities

import android.R
import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat


class Utils {

    companion object {
        fun switchActivity(
            oldActivity: Activity,
            newActivityClass: Class<out Activity?>?,
            killOld: Boolean
        ) {
            val intent = Intent(oldActivity, newActivityClass)
            val bundle =
                ActivityOptionsCompat.makeCustomAnimation(
                    oldActivity,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                    .toBundle()
            oldActivity.startActivity(intent, bundle)
            if (killOld) oldActivity.finish()
        }
    }
}