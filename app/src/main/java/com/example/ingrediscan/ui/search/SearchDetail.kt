package com.example.ingrediscan.ui.search

import com.example.ingrediscan.R 
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.SearchView
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ingrediscan.databinding.FragmentSearchBinding
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent
// import com.bumptech.glide.Glide

class SearchDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_detail)

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        // val imageUrl = intent.getStringExtra("imageUrl")

        // Now set these to your TextViews, ImageViews, etc.
        findViewById<TextView>(R.id.titleTextView).text = title
        findViewById<TextView>(R.id.descriptionTextView).text = description
        // Load image using a library like Glide or Coil:
        // Glide.with(this).load(imageUrl).into(findViewById(R.id.imageView))
    }
}
