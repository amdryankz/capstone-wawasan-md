package id.capstone.wawasan.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import id.capstone.wawasan.R
import id.capstone.wawasan.ui.welcome.WelcomeActivity
import id.capstone.wawasan.ui.home.HomeActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        firebaseAuth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            if (firebaseAuth.currentUser != null) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                startActivity(Intent(this@SplashScreen, WelcomeActivity::class.java))
                finish()
            }
        },3000)
    }

}