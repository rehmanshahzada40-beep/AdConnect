package com.example.adconnect

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.adconnect.databinding.ActivityCreateCampaignBinding
import com.example.adconnect.model.Campaign
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CreateCampaignActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCampaignBinding
    private var imageUri: Uri? = null

    // Pick image from Gallery
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.ivPreview.setImageURI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCampaignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectImage.setOnClickListener { pickImage.launch("image/*") }
        binding.btnPublish.setOnClickListener { uploadCampaign() }
    }

    private fun uploadCampaign() {
        if (imageUri == null || binding.etTitle.text.isEmpty()) return

        val filename = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("/campaigns/$filename")

        // 1. Upload Image to Storage [cite: 787]
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveToFirestore(uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToFirestore(imageUrl: String) {
        // 2. Save Data to Firestore [cite: 807]
        val campaign = Campaign(
            campaignId = UUID.randomUUID().toString(),
            title = binding.etTitle.text.toString(),
            description = binding.etDescription.text.toString(),
            imageUrl = imageUrl,
            createdByUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            timestamp = Timestamp.now()
        )

        FirebaseFirestore.getInstance().collection("Campaigns")
            .document(campaign.campaignId).set(campaign)
            .addOnSuccessListener {
                Toast.makeText(this, "Campaign Published", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}