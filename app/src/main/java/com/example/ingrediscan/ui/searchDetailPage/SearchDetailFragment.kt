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
import com.example.ingrediscan.ui.search.SearchResult


class SearchDetailFragment : Fragment() {

    private var _binding: FragmentSearchDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchDetailBinding.inflate(inflater, container, false)

        val title = arguments?.getString("title") ?: "No Title"
        val description = arguments?.getString("description") ?: "No Description"
        val imageID = arguments?.getInt("imageID") ?: 0
        val calories = arguments?.getInt("calories")?.let { "$it cal" } ?: "No Calories"
        val protein = arguments?.getInt("protein")?.let { "Protein: $it g" } ?: "Protein: N/A"
        val carbs = arguments?.getInt("carbs")?.let { "Carbs: $it g" } ?: "No Carbs"
        val fat = arguments?.getInt("fat")?.let { "Fat: $it g" } ?: "No Fat"
        val grade = arguments?.getString("grade") ?: "No Grade"

        binding.titleTextView.text = title
        binding.descriptionTextView.text = description
        binding.imageView.setImageResource(imageID)
        binding.calorieTextView.text = calories
        binding.proteinTextView.text = protein
        binding.carbTextView.text = carbs
        binding.fatTextView.text = fat
        binding.gradeTextView.text = grade

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}