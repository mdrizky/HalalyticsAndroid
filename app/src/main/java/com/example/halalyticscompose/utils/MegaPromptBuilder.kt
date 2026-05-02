package com.example.halalyticscompose.utils

object MegaPromptBuilder {

    /**
     * ═══════════════════════════════════════════════════════════
     * MEGA SYSTEM PROMPT — AI MEDICAL ASSISTANT HALALYTIC
     * Versi: 2.0 | Bahasa: Indonesia + English
     * ═══════════════════════════════════════════════════════════
     */
    fun buildSystemPrompt(): String = """
Kamu adalah dr. Halal AI, asisten kesehatan edukatif Halalytics untuk komunitas Muslim Indonesia.

Tugasmu:
1. Memahami keluhan user dengan empatik.
2. Menyusun analisis edukatif, bukan diagnosis final.
3. Memberikan rekomendasi obat, dosis umum, status halal, efek samping, first aid, pola makan, pencegahan, dan tanda bahaya.
4. Menjaga jawaban tetap aman, panjang, terstruktur, dan mudah dipahami.

Aturan mutlak:
1. Jawab HANYA dalam JSON valid.
2. Jangan keluarkan teks di luar JSON.
3. Gunakan Bahasa Indonesia.
4. Jika informasi obat tidak pasti, tulis "perlu cek label/sertifikasi" dengan jujur.
5. Jika kondisi berbahaya, set severity = "emergency" dan beri emergency_warning yang kuat.
6. Jangan jawab terlalu singkat. Isi seluruh struktur 16 poin di bawah dengan detail yang nyata.

FORMAT JSON WAJIB:
{
  "ringkasan_keluhan": "string",
  "severity": "mild|moderate|emergency",
  "tingkat_keparahan_label": "Ringan|Sedang|Perlu perhatian medis",
  "alasan_keparahan": "string",
  "condition": "string",
  "possible_causes": [
    {"name": "string", "percentage": 60, "reason": "string"}
  ],
  "gejala_terkait": ["string"],
  "disease_explanations": [
    {"name": "string", "description": "string", "relation_to_case": "string"}
  ],
  "trigger_factors": ["string"],
  "recommended_ingredients": ["string"],
  "recommended_medicines_list": [
    {
      "name": "string",
      "function": "string",
      "dosage": "string",
      "how_to_take": "string",
      "duration": "string",
      "when_to_take": "string",
      "halal_status": "Halal|Perlu cek label|Syubhat",
      "price_range": "Rp ... - Rp ...",
      "safety_note": "string",
      "side_effects": ["string"]
    }
  ],
  "dosage_guidelines": "string",
  "drug_mechanism": "string",
  "halal_check": {"status": "string", "notes": "string"},
  "alternative_medicines": ["string"],
  "usage_instructions": "string",
  "lifestyle_advice": "string",
  "first_aid_steps": ["string"],
  "prevention": ["string"],
  "emergency_warning": "string",
  "follow_up_questions": ["string"],
  "confidence_level": "Rendah|Sedang|Tinggi",
  "recommendation": "string",
  "tldr": "string"
}
    """.trimIndent()

    /**
     * Build user message dengan konteks lengkap
     */
    fun buildUserMessage(
        symptoms: String,
        additionalContext: String = ""
    ): String = """
Keluhan pasien: $symptoms

${if (additionalContext.isNotEmpty()) "Konteks tambahan: $additionalContext" else ""}

Tolong susun analisis super lengkap 16 poin.
Pastikan ada:
- ringkasan keluhan yang rapi
- klasifikasi tingkat keparahan + alasannya
- differential diagnosis dengan persentase
- penjelasan penyakit utama
- faktor pemicu personal
- rekomendasi obat detail + dosis + durasi
- mekanisme obat
- status halal
- efek samping
- pola makan
- first aid step-by-step
- pencegahan
- red flag
- pertanyaan lanjutan
- confidence level
- TLDR
    """.trimIndent()

    /**
     * Build personalized user message with full profile context
     */
    fun buildPersonalizedUserMessage(
        symptoms: String,
        age: Int? = null,
        weight: Float? = null,
        height: Float? = null,
        gender: String? = null,
        allergies: String? = null,
        medicalHistory: String? = null,
        isGlutenFree: Boolean = false,
        hasNutAllergy: Boolean = false
    ): String {
        val profileContext = StringBuilder()
        profileContext.append("Profil Pasien:\n")
        age?.let { profileContext.append("- Umur: $it tahun\n") }
        weight?.let { profileContext.append("- Berat: $it kg\n") }
        height?.let { profileContext.append("- Tinggi: $it cm\n") }
        gender?.let { profileContext.append("- Jenis Kelamin: $it\n") }
        allergies?.let { profileContext.append("- Alergi: $it\n") }
        medicalHistory?.let { profileContext.append("- Riwayat Medis: $it\n") }
        if (isGlutenFree) profileContext.append("- Diet: Bebas Gluten (Gluten Free)\n")
        if (hasNutAllergy) profileContext.append("- Kondisi: Alergi Kacang (Nut Allergy)\n")

        return """
$profileContext
Keluhan pasien saat ini: $symptoms

Tolong berikan analisis MEDIS EDUKATIF yang disesuaikan dengan PROFIL PASIEN di atas.
Jika ada obat yang berinteraksi negatif dengan riwayat medis atau alergi, berikan PERINGATAN KERAS.
Pastikan status HALAL obat diperhatikan dengan ketat.

Susun analisis super lengkap 16 poin sesuai format JSON yang ditentukan.
        """.trimIndent()
    }

    /**
     * Build prompt untuk kasus tidak spesifik (agar tidak UNKNOWN lagi)
     */
    fun buildFallbackMessage(symptoms: String): String = """
Pasien menyampaikan keluhan: "$symptoms"

Meskipun gejalanya mungkin tidak spesifik, tolong tetap berikan:
1. Kemungkinan penyebab paling umum berdasarkan konteks
2. Rekomendasi penanganan umum yang aman
3. Tanda-tanda bahaya yang perlu diwaspadai
4. Kapan harus ke dokter

Jangan pernah jawab "tidak dapat dianalisis" - selalu berikan rekomendasi yang berguna.
Format respons tetap JSON seperti yang ditentukan di system prompt.
    """.trimIndent()
}
