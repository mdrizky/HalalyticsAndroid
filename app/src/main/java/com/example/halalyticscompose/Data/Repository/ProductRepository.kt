package com.example.halalyticscompose.Data.Repository

import com.example.halalyticscompose.Data.Model.*
import com.example.halalyticscompose.Data.Network.ApiConfig
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Network.ExternalApiService

class ProductRepository(
    private val apiService: ApiService = ApiConfig.apiService,
    private val externalApiService: ExternalApiService = ApiConfig.getExternalApiService(),
    private val cachedDao: com.example.halalyticscompose.Data.Local.Dao.CachedScanResultDao? = null
) {
    suspend fun getProductWithHalalInfo(barcode: String, token: String? = null): Result<Product> {
        println("🔍 getProductWithHalalInfo called for barcode: $barcode")
        try {
            // 1. Try Unified Scan if token is available
            if (token != null) {
                println("🚀 Trying Unified Scan for barcode: $barcode")
                val unifiedResponse = apiService.scanUnified("Bearer $token", barcode)
                if (unifiedResponse.isSuccessful && unifiedResponse.body()?.success == true) {
                    val data = unifiedResponse.body()!!.data
                    if (data != null) {
                        return Result.success(mapUnifiedToProduct(data))
                    }
                }
            }

            // 2. Try Halalytics External API (Public Endpoint)
            println("🌐 Trying Halalytics External API for barcode: $barcode")
            try {
                val externalResponse = externalApiService.getProductDetail(barcode)
                if (externalResponse.isSuccessful && externalResponse.body()?.responseCode == 200) {
                    val productItem = externalResponse.body()?.content
                    if (productItem != null) {
                        return Result.success(mapProductItemToProduct(productItem))
                    }
                }
            } catch (e: Exception) {
                println("❌ External API Exception: ${e.message}")
            }

            // 3. Fallback to Open Food Facts API directly (Raw Data)
            println("📡 Trying Raw Open Food Facts for barcode: $barcode")
            try {
                val offResponse = apiService.getOpenFoodFactsProduct(barcode)
                if (offResponse.isSuccessful && offResponse.body()?.status == 1) {
                    val offProduct = offResponse.body()?.product
                    if (offProduct != null) {
                        return Result.success(mapOpenFoodFactsToProduct(offProduct, barcode))
                    }
                }
            } catch (e: Exception) {
                println("❌ Open Food Facts Exception: ${e.message}")
            }

            // 4. Fallback to Legacy local API
            println("🔌 Trying legacy API for barcode: $barcode")
            val response = apiService.getProduct(barcode)
            
            if (response.isSuccessful && response.body()?.success == true) {
                println("✅ Halalytics API success for barcode: $barcode")
                val responseData = response.body()!!.data
                val productInfo = responseData.product
                val product = Product(
                    id = productInfo.id,
                    barcode = productInfo.barcode,
                    name = productInfo.name,
                    brand = productInfo.brand,
                    category = productInfo.category,
                    image = productInfo.image,
                    halalInfo = HalalInfo(
                        halalStatus = HalalStatus.fromString(responseData.halal_info.halal_status),
                        certificateNumber = responseData.halal_info.halal_certificate_number,
                        certificationBody = responseData.halal_info.certification_body,
                        validUntil = responseData.halal_info.certificate_valid_until,
                        lastChecked = responseData.halal_info.last_checked_at,
                        source = responseData.halal_source
                    )
                )
                return Result.success(product)
            } 


            // 5. Fallback to local cache
            val cached = cachedDao?.getByBarcode(barcode)
            if (cached != null) {
                println("📦 Found cached result for barcode: $barcode")
                return Result.success(mapCachedToProduct(cached))
            }

            return Result.failure(Exception("Product not found anywhere"))

        } catch (e: Exception) {
            println("❌ General Error: ${e.message}")
            val cached = cachedDao?.getByBarcode(barcode)
            return if (cached != null) {
                Result.success(mapCachedToProduct(cached))
            } else {
                Result.failure(e)
            }
        }
    }

    private fun mapUnifiedToProduct(data: UnifiedProductData): Product {
        return Product(
            id = data.id,
            barcode = data.barcode,
            name = data.namaProduct,
            brand = "Unknown", // Backend can be improved to return brand
            category = data.kategori ?: "Umum",
            image = data.image,
            halalInfo = HalalInfo(
                halalStatus = HalalStatus.fromString(data.halalStatus),
                certificateNumber = null,
                certificationBody = null,
                validUntil = null,
                lastChecked = null,
                source = data.source
            ),
            ingredientsText = data.komposisi?.joinToString(", "),
            isVerified = data.isVerified,
            verificationStatus = data.verificationStatus
        )
    }

    private fun createDummyProduct(barcode: String, source: String): Product {
        return Product(
            id = 0,
            barcode = barcode,
            name = "Product $barcode",
            brand = "Unknown",
            category = "Unknown",
            image = "",
            halalInfo = HalalInfo(
                HalalStatus.UNKNOWN, "", "", "", "", source
            )
        )
    }

    private fun mapCachedToProduct(cached: com.example.halalyticscompose.Data.Local.Entities.CachedScanResult): Product {
        return Product(
            id = cached.id,
            barcode = cached.barcode ?: "",
            name = cached.productName,
            brand = "Cached Brand",
            category = "Cached Category",
            image = cached.imageUrl ?: "",
            halalInfo = HalalInfo(
                halalStatus = HalalStatus.fromString(cached.halalStatus),
                certificateNumber = null,
                certificationBody = null,
                validUntil = null,
                lastChecked = java.util.Date(cached.scannedAt).toString(),
                source = "offline_mode"
            ),
            ingredientsTags = cached.ingredients,
            halalNotes = cached.halalNotes,
            // ... rest with nulls
            nutriScore = null,
            ingredientsText = null,
            allergens = null,
            traces = null,
            additives = null,
            nutrientLevels = null,
            nutritionFacts = null,
            quantity = null,
            servingSize = null,
            countries = null,
            packaging = null,
            stores = null,
            brandsTags = null,
            categoriesTags = null,
            labelsTags = null,
            ingredientsAnalysisTags = null,
            novaGroup = null,
            novaGroupName = null,
            uniqueScansCount = null,
            lastModified = null,
            created = null,
            imageUrl = null,
            imageFrontUrl = cached.imageUrl,
            imageIngredientsUrl = null,
            imageNutritionUrl = null
        )
    }
    
    private fun mapOpenFoodFactsToProduct(offProduct: OpenFoodFactsProduct, barcode: String): Product {
        return Product(
            id = offProduct.id.hashCode(),
            barcode = barcode,
            name = offProduct.product_name ?: offProduct.generic_name ?: "Unknown Product",
            brand = offProduct.brands ?: "Unknown Brand",
            category = offProduct.categories_tags?.firstOrNull() ?: "Unknown Category",
            image = offProduct.image_front_url ?: offProduct.image_url,
            halalInfo = HalalInfo(
                halalStatus = HalalStatus.UNKNOWN,
                certificateNumber = "",
                certificationBody = "",
                validUntil = "",
                lastChecked = "",
                source = "open_food_facts"
            ),
            // Open Food Facts data
            nutriScore = offProduct.nutriscore_grade,
            ingredientsText = offProduct.ingredients_text,
            allergens = parseAllergens(offProduct.allergens),
            traces = parseAllergens(offProduct.traces),
            additives = parseAdditives(offProduct.additives),
            nutrientLevels = parseNutrientLevels(offProduct.nutrient_levels, offProduct.nutriments),
            nutritionFacts = parseNutritionFacts(offProduct.nutriments),
            quantity = offProduct.quantity,
            servingSize = offProduct.serving_size,
            countries = offProduct.countries_tags?.filter { it.isNotEmpty() },
            packaging = offProduct.packaging,
            stores = offProduct.stores,
            brandsTags = offProduct.brands_tags?.filter { it.isNotEmpty() },
            categoriesTags = offProduct.categories_tags?.filter { it.isNotEmpty() },
            labelsTags = offProduct.labels_tags?.filter { it.isNotEmpty() },
            ingredientsAnalysisTags = offProduct.ingredients_analysis_tags?.filter { it.isNotEmpty() },
            novaGroup = offProduct.nova_group,
            novaGroupName = offProduct.nova_group_name,
            uniqueScansCount = offProduct.unique_scans_n,
            lastModified = offProduct.last_modified_t,
            created = offProduct.created_t,
            imageUrl = offProduct.image_url,
            imageFrontUrl = offProduct.image_front_url,
            imageIngredientsUrl = offProduct.image_ingredients_url,
            imageNutritionUrl = offProduct.image_nutrition_url
        )
    }

    private fun mapProductItemToProduct(item: ProductItem): Product {
        return Product(
            id = item.id?.hashCode() ?: 0,
            barcode = item.code ?: "",
            name = item.getDisplayName(),
            brand = item.brands ?: "Unknown Brand",
            category = item.categories ?: "Unknown Category",
            image = item.getBestImageUrl(),
            halalInfo = HalalInfo(
                halalStatus = HalalStatus.fromString(item.getHalalStatus()),
                certificateNumber = null,
                certificationBody = null,
                validUntil = null,
                lastChecked = null,
                source = "halalytics_proxy"
            ),
            nutriScore = item.nutriscoreGrade,
            ingredientsText = item.ingredientsText,
            quantity = item.quantity,
            imageUrl = item.imageUrl,
            imageFrontUrl = item.imageFrontUrl,
            brandsTags = item.brandsTags,
            categoriesTags = item.categoriesTags,
            labelsTags = item.labelsTags,
            novaGroup = item.novaGroup,
            halalNotes = item.halalAnalysis?.recommendation
        )
    }
    
    private fun parseAllergens(allergens: String?): List<String> {
        return allergens?.split(",")
            ?.map { it.trim().replace("[", "").replace("]", "") }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }
    
    private fun parseAdditives(additives: String?): List<String> {
        return additives?.split(",")
            ?.map { it.trim().replace("[", "").replace("]", "") }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }
    
    private fun parseNutrientLevels(nutrientLevels: Map<String, String>?, nutriments: Map<String, Double>?): NutrientLevels? {
        if (nutrientLevels == null && nutriments == null) return null
        
        return NutrientLevels(
            fat = parseNutrientLevel("fat", nutrientLevels, nutriments),
            saturatedFat = parseNutrientLevel("saturated-fat", nutrientLevels, nutriments),
            sugars = parseNutrientLevel("sugars", nutrientLevels, nutriments),
            salt = parseNutrientLevel("salt", nutrientLevels, nutriments),
            energy = parseNutrientLevel("energy", nutrientLevels, nutriments),
            fiber = parseNutrientLevel("fiber", nutrientLevels, nutriments),
            proteins = parseNutrientLevel("proteins", nutrientLevels, nutriments),
            alcohol = parseNutrientLevel("alcohol", nutrientLevels, nutriments)
        )
    }
    
    private fun parseNutrientLevel(nutrient: String, levels: Map<String, String>?, nutriments: Map<String, Double>?): NutrientLevel? {
        val level = levels?.get(nutrient) ?: "unknown"
        val value = nutriments?.get(nutrient) ?: return null
        val unit = getNutrientUnit(nutrient)
        
        return NutrientLevel(
            level = level,
            value = value,
            unit = unit,
            percentOfDailyNeeds = null
        )
    }
    
    private fun getNutrientUnit(nutrient: String): String {
        return when (nutrient) {
            "energy", "energy-kcal" -> "kcal"
            "fat" -> "g"
            "saturated-fat" -> "g"
            "carbohydrates" -> "g"
            "sugars" -> "g"
            "fiber" -> "g"
            "proteins" -> "g"
            "salt" -> "g"
            "sodium" -> "g"
            "alcohol" -> "g"
            "vitamin-a" -> "µg"
            "vitamin-d" -> "µg"
            "vitamin-e" -> "mg"
            "vitamin-k" -> "µg"
            "vitamin-c" -> "mg"
            "calcium" -> "mg"
            "iron" -> "mg"
            else -> "g"
        }
    }
    
    private fun parseNutritionFacts(nutriments: Map<String, Double>?): NutritionFacts? {
        if (nutriments == null) return null
        
        return NutritionFacts(
            energy = nutriments["energy-kcal"]?.let { NutritionItem(it, null, "kcal", "Energy") },
            fat = nutriments["fat"]?.let { NutritionItem(it, null, "g", "Fat") },
            saturatedFat = nutriments["saturated-fat"]?.let { NutritionItem(it, null, "g", "Saturated Fat") },
            carbohydrates = nutriments["carbohydrates"]?.let { NutritionItem(it, null, "g", "Carbohydrates") },
            sugars = nutriments["sugars"]?.let { NutritionItem(it, null, "g", "Sugars") },
            fiber = nutriments["fiber"]?.let { NutritionItem(it, null, "g", "Fiber") },
            proteins = nutriments["proteins"]?.let { NutritionItem(it, null, "g", "Proteins") },
            salt = nutriments["salt"]?.let { NutritionItem(it, null, "g", "Salt") },
            sodium = nutriments["sodium"]?.let { NutritionItem(it, null, "g", "Sodium") },
            vitaminA = nutriments["vitamin-a"]?.let { NutritionItem(it, null, "µg", "Vitamin A") },
            vitaminD = nutriments["vitamin-d"]?.let { NutritionItem(it, null, "µg", "Vitamin D") },
            vitaminE = nutriments["vitamin-e"]?.let { NutritionItem(it, null, "mg", "Vitamin E") },
            vitaminK = nutriments["vitamin-k"]?.let { NutritionItem(it, null, "µg", "Vitamin K") },
            vitaminC = nutriments["vitamin-c"]?.let { NutritionItem(it, null, "mg", "Vitamin C") },
            vitaminB1 = nutriments["vitamin-b1"]?.let { NutritionItem(it, null, "mg", "Vitamin B1") },
            vitaminB2 = nutriments["vitamin-b2"]?.let { NutritionItem(it, null, "mg", "Vitamin B2") },
            vitaminB6 = nutriments["vitamin-b6"]?.let { NutritionItem(it, null, "mg", "Vitamin B6") },
            vitaminB9 = nutriments["vitamin-b9"]?.let { NutritionItem(it, null, "mg", "Vitamin B9") },
            vitaminB12 = nutriments["vitamin-b12"]?.let { NutritionItem(it, null, "mg", "Vitamin B12") },
            calcium = nutriments["calcium"]?.let { NutritionItem(it, null, "mg", "Calcium") },
            iron = nutriments["iron"]?.let { NutritionItem(it, null, "mg", "Iron") },
            magnesium = nutriments["magnesium"]?.let { NutritionItem(it, null, "mg", "Magnesium") },
            phosphorus = nutriments["phosphorus"]?.let { NutritionItem(it, null, "mg", "Phosphorus") },
            potassium = nutriments["potassium"]?.let { NutritionItem(it, null, "mg", "Potassium") },
            zinc = nutriments["zinc"]?.let { NutritionItem(it, null, "mg", "Zinc") },
            cholesterol = nutriments["cholesterol"]?.let { NutritionItem(it, null, "mg", "Cholesterol") },
            alcohol = nutriments["alcohol"]?.let { NutritionItem(it, null, "g", "Alcohol") }
        )
    }

    suspend fun checkHalalStatus(
        barcode: String,
        productName: String,
        brand: String?
    ): Result<HalalInfo> {
        return try {
            val response = apiService.checkHalal(
                HalalCheckRequest(barcode, productName, brand)
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!
                val halalInfo = HalalInfo(
                    halalStatus = HalalStatus.fromString(data.halal_status),
                    certificateNumber = data.certificate_number,
                    certificationBody = data.certification_body,
                    validUntil = data.valid_until,
                    lastChecked = data.last_checked,
                    source = data.source
                )
                Result.success(halalInfo)
            } else {
                Result.failure(Exception("Failed to check halal status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductAlternatives(barcode: String, token: String? = null): Result<com.example.halalyticscompose.data.api.HalalAlternativeResponse> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val authToken = if (token != null && !token.startsWith("Bearer ")) "Bearer $token" else token
                val response = apiService.getProductAlternatives(barcode, authToken)
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    if (data != null) {
                        Result.success(data)
                    } else {
                        Result.failure(Exception("Alternative data is null"))
                    }
                } else {
                    Result.failure(Exception("Failed to get alternatives: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
