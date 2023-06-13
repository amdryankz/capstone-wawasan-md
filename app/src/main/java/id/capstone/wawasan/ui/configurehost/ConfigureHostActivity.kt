package id.capstone.wawasan.ui.configurehost

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import id.capstone.wawasan.R
import id.capstone.wawasan.databinding.ActivityConfigureHostBinding
import id.capstone.wawasan.ui.home.HomeActivity
import id.capstone.wawasan.ui.setting.SettingActivity

class ConfigureHostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigureHostBinding
    private var isButtonPressed = false
    private val configureHostViewModel: ConfigureHostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigureHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            isButtonPressed = true

            val username = binding.etUsername.text.toString()
            val pass = binding.etPassword.text.toString()
            val host = binding.etHost.text.toString()
            val port = binding.etPort.text.toString()
            val db = binding.etDb.text.toString()

            val validationError =
                configureHostViewModel.validateInput(username, pass, host, port, db)
            if (validationError != null) {
                Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            configureHostViewModel.connectToDatabase(username, pass, host, port, db)
        }

        configureHostViewModel.isConfigured.observe(this) { isConfigured ->
            if (isConfigured) {
                Toast.makeText(this, "Database connected", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }

        configureHostViewModel.isConnected.observe(this) { isConnected ->
            if (isButtonPressed && !isConnected) {
                Toast.makeText(this, "Failed to connect to the database", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
