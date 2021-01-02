package xyz.teamgravity.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.teamgravity.myapplication.R
import xyz.teamgravity.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}