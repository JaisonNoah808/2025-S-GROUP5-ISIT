package com.example.ingrediscan.ui.scan


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ingrediscan.BackEnd.ApiCalls.OpenFoodFactsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ScanViewModel : ViewModel() {


    // State for whether the camera is active
    private val _isCameraActive = MutableLiveData<Boolean>()
    val isCameraActive: LiveData<Boolean> = _isCameraActive

    // State for the currently scanned item
    private val _scannedItem = MutableLiveData<ScannedItem?>()
    val scannedItem: LiveData<ScannedItem?> = _scannedItem

    // State for whether flash is on
    private val _isFlashOn = MutableLiveData<Boolean>()
    val isFlashOn: LiveData<Boolean> = _isFlashOn

    fun startCamera() {
        _isCameraActive.value = true
        // Logic to start the camera
    }

    fun stopCamera() {
        _isCameraActive.value = false
        // Logic to stop the camera
    }

    fun onImageButtonClick() {
        // Logic to handle the click on the image button
    }

    fun updateScannedItem(item: String?) {
        _scannedItem.value = item as ScannedItem?
        // Logic to handle when item is scanned
    }

    fun toggleFlash() {
        _isFlashOn.value = !(_isFlashOn.value ?: false)
        // Logic to turn on or off flash
    }

    fun onArrowClick(navigateHome: () -> Unit) {
        // Logic to handle the back arrow click
        // Example: Navigate back
        navigateHome()
    }

    fun onSettingsClick() {
        // Logic to handle the settings icon click
        // Example: Open settings
    }

    /**
     * Fetch item details from OpenFoodFacts by barcode.
     * Updates LiveData with scanned item information.
     */
    fun fetchItemDetails(barcode: String) {
        viewModelScope.launch {
            // Try fetching from OpenFoodFacts
            val product = OpenFoodFactsApiService.fetchProduct(barcode)

            if (product != null) {
                // Product found — map OpenFoodFacts fields to our local model
                _scannedItem.value = ScannedItem(
                    name = product.product_name ?: "Unknown Item",
                    brand = product.brands ?: "Unknown Brand",
                    calories = product.nutriments?.energyKcal100g?.toInt() ?: 0,  // Correct fields from API
                    protein = product.nutriments?.proteins100g?.toInt() ?: 0,
                    carbs = product.nutriments?.carbohydrates100g?.toInt() ?: 0,
                    fat = product.nutriments?.fat100g?.toInt() ?: 0,
                    grade = product.nutriments?.nutritionGrade?.uppercase() ?: "C", // Default grade for now
                    description = product.ingredients_text ?: "No ingredients listed."
                )
            }
        }
    }

    data class ScannedItem(
        val name: String,
        val brand: String,
        val calories: Int,
        val protein: Int,
        val carbs: Int,
        val fat: Int,
        val grade: String?,
        val description: String
    )

}