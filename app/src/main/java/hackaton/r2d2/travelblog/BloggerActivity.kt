package hackaton.r2d2.travelblog

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.log_out, menu)
        menu?.findItem(R.id.log_out)?.setOnMenuItemClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return@setOnMenuItemClickListener true
        }
        return super.onCreateOptionsMenu(menu)
    }
}