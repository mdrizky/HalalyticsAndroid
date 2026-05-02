package com.example.halalyticscompose.data.ocr

import com.example.halalyticscompose.data.local.Dao.HaramIngredientDao
import com.example.halalyticscompose.data.local.Entities.HaramIngredientEntity
import com.example.halalyticscompose.data.local.Entities.UserHealthProfileEntity
import javax.inject.Inject
import kotlin.math.absoluteValue

data class DetectedIngredient(
    val originalName: String,
    val matchedIngredient: HaramIngredientEntity,
    val isAllergen: Boolean = false,
)

class IngredientMatcher @Inject constructor(
    private val haramIngredientDao: HaramIngredientDao,
) {
    suspend fun match(
        rawText: String,
        profile: UserHealthProfileEntity?,
    ): List<DetectedIngredient> {
        val normalizedText = normalize(rawText)
        if (normalizedText.isBlank()) {
            return emptyList()
        }

        val detected = linkedMapOf<Int, DetectedIngredient>()

        haramIngredientDao.getAllActiveList().forEach { ingredient ->
            val candidates = listOf(ingredient.name) + ingredient.aliases
            val matchedName = candidates.firstOrNull { candidate ->
                val normalizedCandidate = normalize(candidate)
                normalizedCandidate.isNotBlank() && normalizedText.contains(normalizedCandidate)
            }

            if (matchedName != null) {
                val allergenMatch = profile?.allergies.orEmpty().any { allergy ->
                    val normalizedAllergy = normalize(allergy)
                    normalizedAllergy.isNotBlank() &&
                        (normalize(matchedName).contains(normalizedAllergy) || normalizedAllergy.contains(normalize(matchedName)))
                }

                detected[ingredient.id] = DetectedIngredient(
                    originalName = matchedName,
                    matchedIngredient = ingredient,
                    isAllergen = allergenMatch,
                )
            }
        }

        profile?.allergies.orEmpty().forEach { allergy ->
            addProfileSensitiveMatch(
                detected = detected,
                rawText = normalizedText,
                keyword = allergy,
                category = "alergen_umum",
                severity = 3,
                description = "Bahan ini cocok dengan alergi yang tersimpan di profil kesehatanmu.",
                isAllergen = true,
            )
        }

        profile?.avoidIngredients.orEmpty().forEach { ingredient ->
            addProfileSensitiveMatch(
                detected = detected,
                rawText = normalizedText,
                keyword = ingredient,
                category = "syubhat",
                severity = 2,
                description = "Bahan ini ada dalam daftar bahan yang kamu pilih untuk dihindari.",
                isAllergen = false,
            )
        }

        return detected.values.toList()
    }

    private fun addProfileSensitiveMatch(
        detected: MutableMap<Int, DetectedIngredient>,
        rawText: String,
        keyword: String,
        category: String,
        severity: Int,
        description: String,
        isAllergen: Boolean,
    ) {
        val normalizedKeyword = normalize(keyword)
        if (normalizedKeyword.isBlank() || !rawText.contains(normalizedKeyword)) {
            return
        }

        val syntheticId = ("profile:$category:$normalizedKeyword").hashCode().absoluteValue
        if (detected.containsKey(syntheticId)) {
            return
        }

        detected[syntheticId] = DetectedIngredient(
            originalName = keyword,
            matchedIngredient = HaramIngredientEntity(
                id = syntheticId,
                name = keyword.trim(),
                aliases = emptyList(),
                category = category,
                severity = severity,
                description = description,
                isActive = true,
                updatedAt = System.currentTimeMillis(),
            ),
            isAllergen = isAllergen,
        )
    }

    private fun normalize(value: String): String {
        return value
            .trim()
            .lowercase()
            .replace(Regex("\\s+"), " ")
    }
}
