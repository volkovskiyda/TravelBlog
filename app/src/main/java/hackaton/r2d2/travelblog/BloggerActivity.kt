package hackaton.r2d2.travelblog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hackaton.r2d2.travelblog.camera.CameraFragment
import hackaton.r2d2.travelblog.databinding.ActivityBloggerBinding

class BloggerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBloggerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBloggerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CameraFragment())
                .commit()
    }
}