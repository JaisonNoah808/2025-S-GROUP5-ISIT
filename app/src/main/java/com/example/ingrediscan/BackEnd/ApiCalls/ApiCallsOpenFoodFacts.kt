package com.example.ingrediscan.BackEnd.ApiCalls

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import com.google.gson.annotations.SerializedName

/**
 * API client for OpenFoodFacts to fetch food data by barcode.
 */
object OpenFoodFactsApiService {

    // Base URL for OpenFoodFacts API
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    // Retrofit instance
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // API interface definition
    private val api: OpenFoodFactsApi = retrofit.create(OpenFoodFactsApi::class.java)

    interface OpenFoodFactsApi {
        @GET("api/v0/product/{barcode}.json")
        suspend fun getProductByBarcode(@Path("barcode") barcode: String): OpenFoodFactsResponse
    }

    // Data classes to model API responses
    data class OpenFoodFactsResponse(
        val status: Int,             // 1 if found, 0 if not found
        val product: Product?        // Product details if found
    )

    data class Product(
        val product_name: String?,   // Product name (ex: Oreo)
        val brands: String?,         // Brand name (ex: Nabisco)
        val nutriments: Nutriments?, // Nutrition information
        val ingredients_text: String? // Ingredients text
    )

    /**
     * Nutritional information pulled from OpenFoodFacts for each product.
     */
    data class Nutriments(
        @SerializedName("energy-kcal_100g")
        val energyKcal100g: Double?,   // Calories per 100g

        @SerializedName("proteins_100g")
        val proteins100g: Double?,     // Protein per 100g

        @SerializedName("carbohydrates_100g")
        val carbohydrates100g: Double?, // Carbs per 100g

        @SerializedName("fat_100g")
        val fat100g: Double?  ,          // Fat per 100g

        @SerializedName("nutrition_grade_fr")
        val nutritionGrade: String?    // Nutrition grade (A, B, C, D, E)
    )

    /**
     * Wrapper function to fetch product details by barcode.
     * Returns null if no product is found or an error occurs.
     */
    suspend fun fetchProduct(barcode: String): Product? {
        return try {
            val response = api.getProductByBarcode(barcode)
            if (response.status == 1) {
                response.product
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error fetching OpenFoodFacts data: ${e.message}")
            null
        }
    }
}
