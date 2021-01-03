package xyz.teamgravity.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import xyz.teamgravity.myapplication.R
import xyz.teamgravity.myapplication.databinding.ActivityMainBinding
import xyz.teamgravity.myapplication.viewmodel.RunDao
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var dao: RunDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        println("debug: ${dao.hashCode()}")
    }
}