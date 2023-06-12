package id.capstone.wawasan.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import id.capstone.wawasan.databinding.ActivityLoginBinding
import id.capstone.wawasan.ui.AuthManager
import id.capstone.wawasan.ui.home.HomeActivity
import id.capstone.wawasan.util.ToastUtil

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager(FirebaseAuth.getInstance())

        binding.loginButton.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()

            if (!validateEmail(email)) {
                return@setOnClickListener
            }

            if (!validatePassword(pass)) {
                return@setOnClickListener
            }

            authManager.signInWithEmailAndPassword(email, pass) { success, error ->
                if (success) {
                    navigateToHomeActivity()
                } else {
                    ToastUtil.showShortToast(this, error ?: "Login Gagal")
                }
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email harus diisi"
            binding.etEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Email tidak valid"
            binding.etEmail.requestFocus()
            return false
        }

        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            binding.etPassword.error = "Password harus diisi"
            binding.etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}