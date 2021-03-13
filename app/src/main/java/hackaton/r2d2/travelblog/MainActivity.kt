package hackaton.r2d2.travelblog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hackaton.r2d2.travelblog.databinding.ActivityMainBinding
import hackaton.r2d2.travelblog.location.LocationFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LocationFragment())
                .commit()
    }
}