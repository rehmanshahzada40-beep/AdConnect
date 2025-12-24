package com.example.adconnect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adconnect.adapter.CampaignAdapter
import com.example.adconnect.databinding.ActivityHomeBinding
import com.example.adconnect.model.Campaign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.rvCampaigns.layoutManager = LinearLayoutManager(this)

        // Run logic
        setupRoleBasedUI()
        loadAdCampaigns()

        // Setup Create Button
        binding.fabCreate.setOnClickListener {
            startActivity(Intent(this, CreateCampaignActivity::class.java))
        }
    }

    private fun setupRoleBasedUI() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("Users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    if (role == "Advertiser") {
                        binding.fabCreate.visibility = View.VISIBLE
                    } else {
                        binding.fabCreate.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch role", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadAdCampaigns() {
        db.collection("Campaigns")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (value != null) {
                    val campaigns = value.toObjects(Campaign::class.java)
                    binding.rvCampaigns.adapter = CampaignAdapter(campaigns)

                    if (campaigns.isEmpty()) {
                        Toast.makeText(this, "No active ads found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}