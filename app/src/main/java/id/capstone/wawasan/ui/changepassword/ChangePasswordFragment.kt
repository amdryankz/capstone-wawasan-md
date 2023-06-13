package id.capstone.wawasan.ui.changepassword

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import id.capstone.wawasan.R
import id.capstone.wawasan.databinding.FragmentChangePasswordBinding
import id.capstone.wawasan.ui.AuthManager
import id.capstone.wawasan.ui.setting.SettingActivity
import java.lang.ref.WeakReference

class ChangePasswordFragment : Fragment() {

    private var bindingRef: WeakReference<FragmentChangePasswordBinding>? = null
    private val binding get() = bindingRef?.get()
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        bindingRef = WeakReference(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(FirebaseAuth.getInstance())
        val user = authManager.getCurrentUser()

        binding?.btnChange?.setOnClickListener {
            val currentPassword = binding?.etPassword?.text.toString().trim()
            val newPassword = binding?.etNewpassword?.text.toString().trim()
            val confirmPassword = binding?.etCpassword?.text.toString().trim()

            if (newPassword.isEmpty() || confirmPassword.isEmpty() || currentPassword.isEmpty()) {
                Snackbar.make(view, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Snackbar.make(view, "Password confirmation does not match", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)
            user?.reauthenticate(credential)
                ?.addOnSuccessListener {
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            Snackbar.make(view, "Password successfully changed", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener { exception ->
                            Snackbar.make(
                                view,
                                "Failed to change password: ${exception.message}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                }
                ?.addOnFailureListener { exception ->
                    Snackbar.make(
                        view,
                        "Gagal melakukan authentikasi: ${exception.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
        }


        binding?.backBtn?.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingRef = null
    }
}
