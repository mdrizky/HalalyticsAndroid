package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ═══════════════════════════════════════════════════════════════════
// COLOR CONSTANTS — Emerald Forest Premium + Semantic Quiz Colors
// ═══════════════════════════════════════════════════════════════════
private val EmeraldDark = Color(0xFF004D40)
private val EmeraldMedium = Color(0xFF00695C)
private val EmeraldLight = Color(0xFF26A69A)
private val SageBg = Color(0xFFF4F9F8)
private val SoftSage = Color(0xFFE0F2F1)
private val CardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF212121)
private val TextMedium = Color(0xFF757575)
private val TextLight = Color(0xFF9E9E9E)

// Semantic Quiz Colors
private val QuizBlue = Color(0xFF1565C0)
private val QuizPurple = Color(0xFF6A1B9A)
private val QuizGreen = Color(0xFF2E7D32)
private val QuizOrange = Color(0xFFF57C00)
private val QuizRed = Color(0xFFD32F2F)

data class QuizQuestion(
    val id: String,
    val text: String,
)

data class QuizOption(
    val value: Int,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentalHealthQuizScreen(
    navController: NavController,
    quizType: String = "gad7"
) {
    val isGad7 = quizType == "gad7"
    val quizTitle = if (isGad7) "Tes Kecemasan (GAD-7)" else "Tes Depresi (PHQ-9)"
    val themeColor = if (isGad7) QuizBlue else QuizPurple

    val questions = if (isGad7) {
        listOf(
            QuizQuestion("q1", "Merasa gugup, cemas, atau gelisah"),
            QuizQuestion("q2", "Tidak mampu menghentikan atau mengendalikan rasa khawatir"),
            QuizQuestion("q3", "Terlalu khawatir tentang berbagai hal"),
            QuizQuestion("q4", "Sulit untuk rileks"),
            QuizQuestion("q5", "Sangat gelisah sehingga sulit untuk duduk diam"),
            QuizQuestion("q6", "Mudah kesal atau mudah tersinggung"),
            QuizQuestion("q7", "Merasa takut seolah-olah sesuatu yang buruk akan terjadi"),
        )
    } else {
        listOf(
            QuizQuestion("q1", "Kurang tertarik atau kurang berminat melakukan sesuatu"),
            QuizQuestion("q2", "Merasa sedih, murung, atau putus asa"),
            QuizQuestion("q3", "Sulit tidur atau tidur terlalu banyak"),
            QuizQuestion("q4", "Merasa lelah atau kurang bertenaga"),
            QuizQuestion("q5", "Kurang nafsu makan atau makan terlalu banyak"),
            QuizQuestion("q6", "Merasa buruk tentang diri sendiri"),
            QuizQuestion("q7", "Sulit berkonsentrasi pada sesuatu"),
            QuizQuestion("q8", "Bergerak atau berbicara sangat lambat/gelisah"),
            QuizQuestion("q9", "Pikiran bahwa lebih baik mati atau menyakiti diri sendiri"),
        )
    }

    val options = listOf(
        QuizOption(0, "Tidak sama sekali"),
        QuizOption(1, "Beberapa hari"),
        QuizOption(2, "Lebih dari separuh waktu"),
        QuizOption(3, "Hampir setiap hari")
    )

    val answers = remember { mutableStateMapOf<String, Int>() }
    var showResult by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }

    val totalScore = answers.values.sum()
    val allAnswered = answers.size == questions.size
    val progress = answers.size.toFloat() / questions.size

    // Score interpretation
    val severity = if (isGad7) {
        when {
            totalScore <= 4 -> "Minimal"
            totalScore <= 9 -> "Ringan"
            totalScore <= 14 -> "Sedang"
            else -> "Berat"
        }
    } else {
        when {
            totalScore <= 4 -> "Minimal"
            totalScore <= 9 -> "Ringan"
            totalScore <= 14 -> "Sedang"
            totalScore <= 19 -> "Cukup Berat"
            else -> "Berat"
        }
    }

    val severityColor = when (severity) {
        "Minimal" -> QuizGreen
        "Ringan" -> QuizBlue
        "Sedang" -> QuizOrange
        "Cukup Berat" -> Color(0xFFE65100)
        "Berat" -> QuizRed
        else -> TextLight
    }

    Scaffold(containerColor = SageBg) { padding ->
        if (!showResult) {
            // ── QUIZ QUESTIONS ──
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Emerald Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldDark, EmeraldMedium, EmeraldLight)
                            )
                        )
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable { navController.popBackStack() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack, null,
                                    tint = Color.White, modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    quizTitle,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "${answers.size}/${questions.size} dijawab",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                            // Score badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "${if (isGad7) "😰" else "😔"} $totalScore",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Dalam 2 minggu terakhir, seberapa sering Anda merasa terganggu oleh hal-hal berikut:",
                        fontSize = 13.sp,
                        color = TextMedium,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    questions.forEachIndexed { index, question ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (answers.containsKey(question.id))
                                    themeColor.copy(alpha = 0.04f) else CardBg
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (answers.containsKey(question.id)) 0.dp else 1.dp
                            ),
                            border = if (answers.containsKey(question.id))
                                BorderStroke(1.dp, themeColor.copy(alpha = 0.2f)) else null
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (answers.containsKey(question.id))
                                                    themeColor.copy(alpha = 0.12f)
                                                else SoftSage
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (answers.containsKey(question.id)) {
                                            Icon(
                                                Icons.Default.Check, null,
                                                tint = themeColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        } else {
                                            Text(
                                                "${index + 1}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldDark
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        question.text,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 19.sp,
                                        color = TextDark
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                options.forEach { option ->
                                    val isSelected = answers[question.id] == option.value
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .clickable { answers[question.id] = option.value },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) themeColor.copy(alpha = 0.08f) else Color.Transparent,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) themeColor else Color(0xFFE0E0E0)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { answers[question.id] = option.value },
                                                colors = RadioButtonDefaults.colors(selectedColor = themeColor),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                option.label,
                                                fontSize = 13.sp,
                                                color = if (isSelected) themeColor else TextDark,
                                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showResult = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        enabled = allAnswered
                    ) {
                        Text(
                            if (allAnswered) "Lihat Hasil" else "Jawab semua pertanyaan (${answers.size}/${questions.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        } else {
            // ── RESULT SCREEN ──
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldDark, EmeraldMedium, EmeraldLight)
                            )
                        )
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 28.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable { navController.popBackStack() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack, null,
                                    tint = Color.White, modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Hasil $quizTitle",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            if (isGad7) "😰" else "😔",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "$totalScore",
                            fontSize = 52.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            "dari ${questions.size * 3}",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Severity Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = severityColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, severityColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            severity,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = severityColor
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Score Scale
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SoftSage),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.BarChart, null,
                                        tint = EmeraldDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Skala Penilaian",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextDark
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            val scales = if (isGad7) {
                                listOf(
                                    Triple("0-4", "Minimal", QuizGreen),
                                    Triple("5-9", "Ringan", QuizBlue),
                                    Triple("10-14", "Sedang", QuizOrange),
                                    Triple("15-21", "Berat", QuizRed)
                                )
                            } else {
                                listOf(
                                    Triple("0-4", "Minimal", QuizGreen),
                                    Triple("5-9", "Ringan", QuizBlue),
                                    Triple("10-14", "Sedang", QuizOrange),
                                    Triple("15-19", "Cukup Berat", Color(0xFFE65100)),
                                    Triple("20-27", "Berat", QuizRed)
                                )
                            }

                            scales.forEach { (range, label, color) ->
                                val isCurrent = label == severity
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (isCurrent) Modifier.background(
                                                color.copy(alpha = 0.08f),
                                                RoundedCornerShape(8.dp)
                                            ) else Modifier
                                        )
                                        .padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        range,
                                        fontSize = 13.sp,
                                        modifier = Modifier.width(50.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        label,
                                        fontSize = 13.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCurrent) color else TextDark
                                    )
                                    if (isCurrent) {
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            "← Anda",
                                            fontSize = 11.sp,
                                            color = color,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recommendation
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = severityColor.copy(alpha = 0.05f)),
                        border = BorderStroke(1.dp, severityColor.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(severityColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("💡", fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Rekomendasi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextDark
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                when (severity) {
                                    "Minimal" -> "Hasil kuis menunjukkan tingkat yang minimal. Ini adalah tanda positif. Tetap jaga kesehatan mental Anda dengan istirahat cukup, olahraga, dan hobi yang Anda sukai."
                                    "Ringan" -> "Hasil menunjukkan tingkat ringan. Coba praktikkan teknik pernapasan dalam, bicarakan perasaan Anda dengan orang terdekat, dan meditasi 10 menit per hari."
                                    "Sedang" -> "Hasil menunjukkan tingkat sedang. Kami menyarankan untuk bicara dengan orang yang Anda percaya dan pertimbangkan konsultasi dengan psikolog."
                                    "Cukup Berat" -> "Hasil menunjukkan tingkat cukup berat. Sangat disarankan untuk berkonsultasi dengan psikolog atau psikiater. Anda tidak sendirian."
                                    else -> "Hasil menunjukkan bahwa Anda mungkin membutuhkan bantuan profesional. Segera hubungi psikolog/psikiater atau hotline 119 ext. 8."
                                },
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = TextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Disclaimer
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("⚠️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Kuis ini adalah alat skrining awal, bukan diagnosis. Untuk diagnosis resmi, konsultasikan dengan profesional kesehatan mental.",
                                fontSize = 11.sp,
                                color = TextMedium,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                    ) {
                        Text(
                            "Kembali ke Kesehatan Mental",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
