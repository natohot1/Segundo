package com.electros.electrocardiogramas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.electros.electrocardiogramas.databinding.ActivityInicialBinding


class ActivityInicial : AppCompatActivity() {

    private lateinit var binding: ActivityInicialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicialBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
