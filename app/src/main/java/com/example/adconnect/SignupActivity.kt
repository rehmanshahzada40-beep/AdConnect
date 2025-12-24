package com.example.adconnect

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adconnect.databinding.ActivitySignupBinding
import com.example.adconnect.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateAccount.setOnClickListener {
            if (validateInputs()) {
                registerUser()
            }
        }

        binding.tvLogin.setOnClickListener { finish() }
    }

    private fun validateInputs(): Boolean {
        if (binding.etEmail.text.isNullOrEmpty()) {
            binding.etEmail.error = "Required"
            return false
        }
        if (binding.etPassword.text.length < 6) {
            binding.etPassword.error = "Min 6 chars"
            return false
        }
        if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            binding.etConfirmPassword.error = "Passwords mismatch"
            return false
        }
        return true
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val name = binding.etUsername.text.toString().trim()

        // Get Role Selection
        val selectedRoleId = binding.rgRole.checkedRadioButtonId
        val role = findViewById<RadioButton>(selectedRoleId).text.toString()

        binding.btnCreateAccount.isEnabled = false // Prevent double-tap
        binding.btnCreateAccount.text = "Creating..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: return@addOnSuccessListener
                // Save extra details (Role, Name) to Firestore [cite: 837]
                val newUser = User(userId, name, email, role)

                db.collection("Users").document(userId).set(newUser)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finishAffinity()
                    }
            }
            .addOnFailureListener {
                binding.btnCreateAccount.isEnabled = true
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}