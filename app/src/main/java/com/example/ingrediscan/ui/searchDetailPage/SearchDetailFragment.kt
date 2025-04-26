package com.example.ingrediscan.ui.searchDetailPage

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
import com.example.ingrediscan.databinding.FragmentSearchDetailBinding
import com.example.ingrediscan.ui.searchDetailPage.SearchDetailViewModel
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent
// import com.bumptech.glide.Glide

class SearchDetailFragment : Fragment() {

    private var _binding: FragmentSearchDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// class SearchDetailFragment : AppCompatActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         setContentView(R.layout.search_detail)

//         val title = intent.getStringExtra("title")
//         val description = intent.getStringExtra("description")
//         val imageID = intent.getIntExtra("imageID", 0) // <-- Correct key and method


//         // Now set these to your TextViews, ImageViews, etc.
//         findViewById<TextView>(R.id.titleTextView).text = title
//         findViewById<TextView>(R.id.descriptionTextView).text = description
//         findViewById<ImageView>(R.id.imageView).setImageResource(imageID)
//     }
// }
