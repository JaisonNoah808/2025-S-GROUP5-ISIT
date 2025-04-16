package com.example.ingrediscan.ui.search

import com.example.ingrediscan.R 
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.SearchView
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ingrediscan.databinding.FragmentSearchBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.core.content.ContextCompat
import android.widget.ImageView
import android.widget.LinearLayout

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val searchTitleTextView: TextView = binding.searchTitle

        searchTitleTextView.text = "Need meal inspiration?"

        return root
    }
    
    // Creates layout for 1 SearchResult item
    private fun createSearchResultView(item: SearchResult): View {
        // get new search_result.xml file 
        val view = layoutInflater.inflate(R.layout.search_result, null)

        // initialize text, image, pie chart views
        val titleText = view.findViewById<TextView>(R.id.foodDrinkTitle)
        val calorieText = view.findViewById<TextView>(R.id.foodDrinkCaloriesLabel)
        val imageView = view.findViewById<ImageView>(R.id.foodDrinkIcon)
        val pieChartView = view.findViewById<PieChart>(R.id.pieChart)
        
        // assign variables based on SearchResult object params
        titleText.text = item.name
        calorieText.text = "${item.calories} cal"
        item.makePieChart(requireContext(), pieChartView)
        imageView.setImageResource(item.imageID)
        
        return view
    }

    // Display search bar after start up
    override fun onViewCreated(
        view: View, 
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            
            // Call the function when user submits a search
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) } 
                return true
            }

            // Future TODO: filter results live as the user types (set to false right now)
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        
        // Create test items
        val searchResultsList = listOf(
            SearchResult(1, R.drawable.salad, "Caesar Salad", 200, 7, 10, 18, "B-", "A classic Caesar salad offers crisp romaine lettuce tossed with creamy dressing, crunchy croutons, and a sprinkle of parmesan. While it's flavorful and has some fiber and calcium, it can be high in fat and sodium without much protein unless you add chicken or another topping."),
            SearchResult(2, R.drawable.sandwich, "BLT Sandwich", 500, 13, 30, 25, "C+", "A BLT is a satisfying sandwich with crispy bacon, fresh lettuce, and juicy tomato layered between toasted bread and a swipe of mayo. It's tasty and filling but relatively high in fat and sodium, and not especially nutrient-dense."),
            SearchResult(3, R.drawable.spaghetti, "Spaghetti", 500, 10, 60, 4, "B", "Spaghetti with marinara is a simple, comforting dish made mostly of pasta and tomato-based sauce. It’s low in fat and can be a decent source of fiber and vitamins if whole wheat pasta or extra veggies are added, but it's fairly carb-heavy.")
        )

        // Combine test items into 1 XML container
        val container = binding.searchResultList
        
        // Build view for each search result
        for (item in searchResultsList) {
            // create view
            val cardView = createSearchResultView(item)
            
            // increase spacing between each item
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.bottomMargin = (16 * resources.displayMetrics.density).toInt() // 8dp
            cardView.layoutParams = layoutParams
            
            // add item to screen
            container.addView(cardView)
        }
    }

    // Future TODO: Implement search logic 
    private fun performSearch(query: String) {
        Log.d("SearchFragment", "Searching for: $query")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
