package com.masters.mort.ui.welcome

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.masters.mort.databinding.ActivityWelcomeBinding
import com.masters.mort.ui.loading.LoadingActivity
import com.masters.mort.utilities.Utils

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.onBackPressedDispatcher.addCallback {
            this.isEnabled = false
        }
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startRedirect()
    }

    private fun startRedirect() {
        Handler(Looper.getMainLooper()).postDelayed({
            Utils.switchActivity(
                this@WelcomeActivity,
                LoadingActivity::class.java,
                true
            )
        }, 2000)
    }
}