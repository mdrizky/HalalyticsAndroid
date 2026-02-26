package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

// Symptoms Analysis Response
data class SymptomsAnalysisResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("symptoms_analysis") val symptoms_analysis: SymptomsAnalysis? = null,
    @SerializedName("recommended_medicines") val recommended_medicines: List<com.example.halalyticscompose.Data.Model.MedicineData>? = null,
    @SerializedName("all_medicines") val all_medicines: List<com.example.halalyticscompose.Data.Model.MedicineData>? = null,
    @SerializedName("message") val message: String? = null
)

data class SymptomsAnalysis(
    @SerializedName("condition") val condition: String = "Unknown Condition",
    @SerializedName("gejala_terkait") val gejala_terkait: List<String> = emptyList(),
    @SerializedName(value = "recommended_ingredients", alternate = ["active_ingredients"]) 
    val recommended_ingredients: List<String> = emptyList(),
    @SerializedName("severity") val severity: String = "mild",
    @SerializedName("recommendation") val recommendation: String = "Konsultasikan dengan dokter",
    @SerializedName("emergency_warning") val emergency_warning: String? = null,
    @SerializedName("possible_causes") val possible_causes: List<String> = emptyList(),
    @SerializedName("triage_action") val triage_action: String? = null,
    @SerializedName("doctor_recommendation") val doctor_recommendation: String? = null,
    @SerializedName("should_seek_doctor") val should_seek_doctor: Boolean = false,
    @SerializedName("halal_check") val halal_check: HalalCheck? = null,
    @SerializedName("usage_instructions") val usage_instructions: String? = null,
    @SerializedName("lifestyle_advice") val lifestyle_advice: String? = null,
    @SerializedName("dosage_guidelines") val dosage_guidelines: String? = null,
    @SerializedName("recommended_medicines_list") val recommended_medicines_list: List<String> = emptyList()
) {
    // Backward compatibility
    val active_ingredients: List<String> get() = recommended_ingredients
}

data class HalalCheck(
    @SerializedName("status") val status: String = "unknown",
    @SerializedName("notes") val notes: String = "Belum dianalisis"
)

// Medicine Responses
data class MedicineSearchResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("source") val source: String? = null,
    @SerializedName("data") val data: List<com.example.halalyticscompose.Data.Model.MedicineData>? = null,
    @SerializedName("message") val message: String? = null
)

// Medication Reminder Response Wrapper
data class MedicationReminderResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("reminder") val reminder: com.example.halalyticscompose.Data.Model.MedicationReminderItem? = null,
    @SerializedName("data") val data: Any? = null // For variants that use "data" field
)

// User Reminders Response Wrapper
data class UserRemindersResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<com.example.halalyticscompose.Data.Model.MedicationReminderItem>? = null,
    @SerializedName("message") val message: String? = null
)

// Next Dose Response
data class NextDoseResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("next_doses") val next_doses: List<NextDose>? = null,
    @SerializedName("message") val message: String? = null
)

data class NextDose(
    @SerializedName("reminder_id") val reminder_id: Int,
    @SerializedName("medicine_name") val medicine_name: String,
    @SerializedName("next_dose_time") val next_dose_time: String,
    @SerializedName("dose_info") val dose_info: String? = null
)

// AI Health Suite Responses
data class DrugInteractionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("source") val source: String?,
    @SerializedName("data") val data: com.example.halalyticscompose.Data.Model.DrugInteractionData
)

data class PillIdentifyResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: com.example.halalyticscompose.Data.Model.PillIdentifyData,
    @SerializedName("image_url") val imageUrl: String?
)

data class LabAnalysisResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: com.example.halalyticscompose.Data.Model.LabAnalysisData,
    @SerializedName("image_url") val imageUrl: String?
)

data class HealthMetricResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<com.example.halalyticscompose.Data.Model.HealthMetricData>
)

data class HalalAlternativeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("source") val source: String?,
    @SerializedName("data") val data: com.example.halalyticscompose.Data.Model.HalalAlternativeData
)

// Safe Schedule Response
data class SafeScheduleResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: SafeScheduleData? = null
)

data class SafeScheduleData(
    @SerializedName("medicine") val medicine: SafeScheduleMedicine? = null,
    @SerializedName("dosage") val dosage: String? = null,
    @SerializedName("frequency_per_day") val frequencyPerDay: Int? = null,
    @SerializedName("meal_relation") val mealRelation: String? = null,
    @SerializedName("meal_instruction") val mealInstruction: String? = null,
    @SerializedName("schedule_times") val scheduleTimes: List<String> = emptyList(),
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("duration_days") val durationDays: Int? = null,
    @SerializedName("disclaimer") val disclaimer: String? = null
)

data class SafeScheduleMedicine(
    @SerializedName("id_medicine") val idMedicine: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("generic_name") val genericName: String? = null,
    @SerializedName("source") val source: String? = null
)

data class PersonalRiskScoreResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("date") val date: String? = null,
    @SerializedName("risk_score") val riskScore: Int? = null,
    @SerializedName("risk_level") val riskLevel: String? = null,
    @SerializedName("alerts") val alerts: List<String> = emptyList(),
    @SerializedName("recommendation") val recommendation: String? = null,
    @SerializedName("disclaimer") val disclaimer: String? = null,
    @SerializedName("totals") val totals: RiskTotals? = null,
    @SerializedName("limits") val limits: RiskLimits? = null
)

data class RiskTotals(
    @SerializedName("sugar_g") val sugarG: Double = 0.0,
    @SerializedName("sodium_mg") val sodiumMg: Double = 0.0,
    @SerializedName("fat_g") val fatG: Double = 0.0,
    @SerializedName("calories") val calories: Int = 0
)

data class RiskLimits(
    @SerializedName("sugar_g") val sugarG: Double = 50.0,
    @SerializedName("sodium_mg") val sodiumMg: Double = 2300.0,
    @SerializedName("fat_g") val fatG: Double = 67.0,
    @SerializedName("calories") val calories: Double = 2000.0
)

data class DrugFoodConflictResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("data") val data: DrugFoodConflictData? = null,
    @SerializedName("message") val message: String? = null
)

data class DrugFoodConflictData(
    @SerializedName("medicine_name") val medicineName: String? = null,
    @SerializedName("has_conflict") val hasConflict: Boolean = false,
    @SerializedName("severity") val severity: String? = null,
    @SerializedName("lookback_minutes") val lookbackMinutes: Int = 180,
    @SerializedName("matches") val matches: List<DrugFoodConflictMatch> = emptyList(),
    @SerializedName("recommendation") val recommendation: String? = null,
    @SerializedName("disclaimer") val disclaimer: String? = null
)

data class DrugFoodConflictMatch(
    @SerializedName("food_name") val foodName: String? = null,
    @SerializedName("matched_keyword") val matchedKeyword: String? = null,
    @SerializedName("severity") val severity: String? = null,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("time") val time: String? = null
)
