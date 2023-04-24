package com.toddy.comunicacaodemandas.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.toddy.comunicacaodemandas.databinding.ActivitySplashBinding
import com.toddy.comunicacaodemandas.extensions.iniciaActivity

class SplashActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            iniciaActivity(MainActivity::class.java)
            finish()
        }, 3000)
    }
}