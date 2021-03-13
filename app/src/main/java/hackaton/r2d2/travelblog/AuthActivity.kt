package hackaton.r2d2.travelblog

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.services.youtube.YouTube
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hackaton.r2d2.travelblog.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private lateinit var googleSignInClient: GoogleSignInClient

    private val signInLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        try {
            val account =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)!!

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val auth = Firebase.auth
            auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) openUser(auth.currentUser) else showAuthFailed(task.exception)
            }
        } catch (e: ApiException) {
            showAuthFailed(e)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted: Map<String, Boolean> ->
            if (granted.containsValue(false)) showAcceptAllPermissions(true) else auth()
        }

    private val permissions = listOf(permission.CAMERA, permission.RECORD_AUDIO, permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope("https://www.googleapis.com/auth/youtube"))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnEnter.setOnClickListener { checkAllPermissions() }
    }

    override fun onStart() {
        super.onStart()

        val user = Firebase.auth.currentUser ?: return
        if (checkPermissions(permissions)) openUser(user) else showAcceptAllPermissions(true)
    }

    private fun showAuthFailed(exception: Exception?) {
        Toast.makeText(this, exception?.message ?: "Failed to auth", Toast.LENGTH_LONG).show()
    }

    private fun checkAllPermissions() {
        if (checkPermissions(permissions)) auth()
        else {
            if (shouldShowRequestPermissionRationale(permissions)) showAcceptAllPermissions(false)
            requestPermissions(permissions)
        }
    }

    private fun auth() {
        if (binding.chbBlogger.isChecked) signInLauncher.launch(googleSignInClient.signInIntent)
        else Firebase.auth.signInAnonymously()
            .addOnSuccessListener { openConsumer() }
            .addOnFailureListener { showAuthFailed(it) }
    }

    private fun checkPermissions(permissions: List<String>): Boolean {
        permissions.forEach { permission ->
            if (checkSelfPermission(this, permission) != PERMISSION_GRANTED) return false
        }
        return true
    }

    private fun shouldShowRequestPermissionRationale(permissions: List<String>): Boolean {
        permissions.forEach { permission ->
            if (shouldShowRequestPermissionRationale(this, permission)) return true
        }
        return false
    }

    private fun requestPermissions(permissions: List<String>) {
        requestPermissionLauncher.launch(permissions.toTypedArray())
    }

    private fun showAcceptAllPermissions(openPermissionSettings: Boolean) {
        Toast.makeText(this, "You should accept all permissions", Toast.LENGTH_SHORT).show()
        if (openPermissionSettings) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null))
            startActivity(intent)
        }
    }

    private fun openUser(user: FirebaseUser?) {
        if (user != null) if (user.isAnonymous) openConsumer() else openBlogger()
    }

    private fun openConsumer() {
        Toast.makeText(this, "openConsumer", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun openBlogger() {
        Toast.makeText(this, "openBlogger", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, BloggerActivity::class.java))
        finish()
    }
}