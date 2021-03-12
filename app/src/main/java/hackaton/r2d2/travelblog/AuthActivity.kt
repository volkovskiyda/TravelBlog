package hackaton.r2d2.travelblog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hackaton.r2d2.travelblog.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val signIn = registerForActivityResult(StartActivityForResult()) { result ->
        val response = IdpResponse.fromResultIntent(result.data)
        if (result.resultCode == Activity.RESULT_OK) {
            openUser(FirebaseAuth.getInstance().currentUser)
        } else {
            val errorMsg = response?.error?.message ?: "Failed to auth"
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnEnter.setOnClickListener { auth() }
    }

    override fun onStart() {
        super.onStart()
        openUser(Firebase.auth.currentUser)
    }

    private fun auth() {
        if (binding.chbBlogger.isChecked) {
            signIn.launch(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                    .build()
            )
        }
        Firebase.auth.signInAnonymously().addOnCompleteListener { openConsumer() }
    }

    private fun openUser(user: FirebaseUser?) {
        if (user != null) {
            if (user.isAnonymous) openConsumer() else openBlogger()
        }
    }

    private fun openConsumer() {
        Toast.makeText(this, "openConsumer", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun openBlogger() {
        Toast.makeText(this, "openBlogger", Toast.LENGTH_SHORT).show()
    }
}