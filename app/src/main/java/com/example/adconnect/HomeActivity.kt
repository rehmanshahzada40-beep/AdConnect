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

        binding.rvCampaigns.layoutManager = LinearLayoutManager(this)

        setupRoleBasedUI()
        loadAdCampaigns()

        binding.fabCreate.setOnClickListener {
            startActivity(Intent(this, CreateCampaignActivity::class.java))
        }
    }

    // Hide FAB if user is a Publisher
    private fun setupRoleBasedUI() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("Users").document(uid).get().addOnSuccessListener {
            val role = it.getString("role")
            if (role == "Advertiser") {
                binding.fabCreate.visibility = View.VISIBLE
            } else {
                binding.fabCreate.visibility = View.GONE
            }
        }
    }

    // Use Case 2: Publisher Views Ads [cite: 808]
    private fun loadAdCampaigns() {
        db.collection("Campaigns")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener

                val campaigns = value?.toObjects(Campaign::class.java) ?: emptyList()
                binding.rvCampaigns.adapter = CampaignAdapter(campaigns)

                if (campaigns.isEmpty()) {
                    Toast.makeText(this, "No active ads found", Toast.LENGTH_SHORT).show()
                }
            }
    }
}