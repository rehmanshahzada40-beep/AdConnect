package com.example.adconnect

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adconnect.databinding.ActivityHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup UI
        binding.rvCampaigns.layoutManager = LinearLayoutManager(this)

        // Load Data from Firestore (SRS Section 3.2.4)
        loadAdCampaigns()

        binding.fabCreate.setOnClickListener {
            // Navigate to Create Campaign screen (Use Case 1)
        }
    }

    private fun loadAdCampaigns() {
        db.collection("Campaigns").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            val campaigns = value?.toObjects(Campaign::class.java) ?: emptyList()
            if (campaigns.isEmpty()) {
                Toast.makeText(this, "No campaigns found", Toast.LENGTH_SHORT).show() [cite: 126]
            }
            // Logic to update your RecyclerView Adapter goes here
            //logic needs to add here
        }
    }
}