package id.capstone.wawasan.ui.configurehost

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import id.capstone.wawasan.databinding.ActivityConfigureHostBinding
import id.capstone.wawasan.ui.home.HomeActivity
import id.capstone.wawasan.ui.setting.SettingActivity

class ConfigureHostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigureHostBinding
    private val configureHostViewModel by viewModels<ConfigureHostViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigureHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val pass = binding.etPassword.text.toString()
            val host = binding.etHost.text.toString()
            val port = binding.etPort.text.toString()
            val db = binding.etDb.text.toString()

            if (username.isEmpty()) {
                binding.etUsername.error = "Username is required"
                binding.etUsername.requestFocus()
                return@setOnClickListener
            }

            if (pass.isEmpty()) {
                binding.etPassword.error = "Password is required"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            if (host.isEmpty()) {
                binding.etHost.error = "Host is required"
                binding.etHost.requestFocus()
                return@setOnClickListener
            }

            if (port.isEmpty()) {
                binding.etPort.error = "Port is required"
                binding.etPort.requestFocus()
                return@setOnClickListener
            }

            if (db.isEmpty()) {
                binding.etDb.error = "Database is required"
                binding.etDb.requestFocus()
                return@setOnClickListener
            }

            if (username.isNotEmpty() && pass.isNotEmpty() && host.isNotEmpty() && port.isNotEmpty() && db.isNotEmpty()) {
                val isConnected = configureHostViewModel.connectToDatabase(username, pass, host, port, db)

                if (isConnected) {
                    configureHostViewModel.saveConfiguration(username, pass, host, port, db)
                    Toast.makeText(this, "Database connected successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to connect to the database", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}