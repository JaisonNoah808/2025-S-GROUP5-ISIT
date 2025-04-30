package com.example.ingrediscan.ui.previous_results

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import android.util.Log

class PreviousResultsViewModel : ViewModel() {

    private val _scannedItems = MutableLiveData<List<ScannedItem>>()
    val scannedItems: LiveData<List<ScannedItem>> = _scannedItems

    private val db = FirebaseFirestore.getInstance()

    init {
        loadItemsFromFirestore() // Load actual Firestore data
    }

    private fun loadItemsFromFirestore() {
        db.collection("loggedMeals")
            .get()
            .addOnSuccessListener { documents ->
                val scannedItemsList = mutableListOf<ScannedItem>()

                for (document in documents) {
                    // Extract each field
                    val foodNameWithTimestamp = document.id
                    // Modify this line where you extract the food name
                    val actualFoodName = foodNameWithTimestamp.split("+").getOrNull(1) ?: foodNameWithTimestamp
                    val caloriesStr = document.getString("Calories") ?: "0 Calories"
                    val proteinStr = document.getString("Protein") ?: "0.0 G"
                    val fatStr = document.getString("Fat") ?: "0.0 G"
                    val carbsStr = document.getString("Carbs") ?: "0.0 G"
                    val mealType = "Meal: " + (document.getString("MealType") ?: "Unknown")

                    // Parse fields properly
                    val calories = caloriesStr.split(" ").firstOrNull()?.toDoubleOrNull()?.toInt() ?: 0
                    val protein = proteinStr.split(" ").firstOrNull()?.toDoubleOrNull()?.toInt() ?: 0
                    val fat = fatStr.split(" ").firstOrNull()?.toDoubleOrNull()?.toInt() ?: 0
                    val carbs = carbsStr.split(" ").firstOrNull()?.toDoubleOrNull()?.toInt() ?: 0
                    var grade = "A"

                    if(actualFoodName == "BLT")
                        grade = "C"
                    else if(actualFoodName == "Steak")
                        grade = "B+"
                    else if(actualFoodName == "Greek Yogurt")
                        grade = "A"
                    else if(actualFoodName == "McFlurry")
                        grade = "D"
                    else
                        grade = "C"

                    // Once everything is ready, call ScannedItem()
                    scannedItemsList.add(
                        ScannedItem(
                            actualFoodName,
                            mealType,
                            calories,
                            protein,
                            carbs,
                            fat,
                            grade
                        )
                    )
                }

                // Update the LiveData
                viewModelScope.launch {
                    _scannedItems.value = scannedItemsList
                }
            }
            .addOnFailureListener { exception ->
                // Handle error if needed (optional: show a Toast, log error, etc.)
            }
    }

    fun onAccountClick() {
        // Handle profile/account button click later if needed
    }
}
