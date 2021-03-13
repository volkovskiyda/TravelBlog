package hackaton.r2d2.travelblog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hackaton.r2d2.travelblog.camera.CameraFragment
import hackaton.r2d2.travelblog.databinding.ActivityAuthBinding
import hackaton.r2d2.travelblog.databinding.ActivityBloggerBinding
import hackaton.r2d2.travelblog.databinding.ActivityMainBinding

class BloggerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBloggerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBloggerBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}