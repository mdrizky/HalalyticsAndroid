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

private val QuizMint = Color(0xFF00BFA6)
private val QuizBlue = Color(0xFF3B82F6)
private val QuizPurple = Color(0xFF7C3AED)
private val QuizGreen = Color(0xFF10B981)
private val QuizOrange = Color(0xFFF59E0B)
private val QuizRed = Color(0xFFEF4444)

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
        "Cukup Berat" -> Color(0xFFFF6B35)
        "Berat" -> QuizRed
        else -> Color.Gray
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quizTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (!showResult) {
            // Quiz Questions
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Progress
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = themeColor,
                    trackColor = themeColor.copy(alpha = 0.1f)
                )

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Dalam 2 minggu terakhir, seberapa sering Anda merasa terganggu oleh hal-hal berikut:",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    questions.forEachIndexed { index, question ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (answers.containsKey(question.id))
                                    themeColor.copy(alpha = 0.04f) else Color(0xFFF9FAFB)
                            ),
                            border = if (answers.containsKey(question.id))
                                BorderStroke(1.dp, themeColor.copy(alpha = 0.2f)) else null
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Surface(
                                        shape = CircleShape,
                                        color = themeColor.copy(alpha = 0.1f),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("${index + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = themeColor)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        question.text,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 20.sp
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
                                        color = if (isSelected) themeColor.copy(alpha = 0.1f) else Color.Transparent,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) themeColor else Color(0xFFE5E7EB)
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
                                                color = if (isSelected) themeColor else Color.DarkGray,
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
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        enabled = allAnswered
                    ) {
                        Text(
                            if (allAnswered) "Lihat Hasil" else "Jawab semua pertanyaan (${answers.size}/${questions.size})",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        } else {
            // Result Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    if (isGad7) "😰" else "😔",
                    fontSize = 56.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Hasil Tes Anda", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "$totalScore",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = severityColor
                )

                Text(
                    "dari ${questions.size * 3}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = severityColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        severity,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = severityColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Score Scale
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Skala Penilaian", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

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
                                Triple("15-19", "Cukup Berat", Color(0xFFFF6B35)),
                                Triple("20-27", "Berat", QuizRed)
                            )
                        }

                        scales.forEach { (range, label, color) ->
                            val isCurrent = label == severity
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(if (isCurrent) Modifier.background(color.copy(alpha = 0.08f), RoundedCornerShape(8.dp)) else Modifier)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(range, fontSize = 13.sp, modifier = Modifier.width(50.dp), fontWeight = FontWeight.Bold)
                                Text(label, fontSize = 13.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                                if (isCurrent) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("← Anda", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
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
                    border = BorderStroke(1.dp, severityColor.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💡 Rekomendasi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            when (severity) {
                                "Minimal" -> "Hasil kuis menunjukkan tingkat yang minimal. Ini adalah tanda positif. Tetap jaga kesehatan mental Anda dengan istirahat cukup, olahraga, dan hobi yang Anda sukai."
                                "Ringan" -> "Hasil menunjukkan tingkat ringan. Coba praktikkan teknik pernapasan dalam, bicarakan perasaan Anda dengan orang terdekat, dan meditasi 10 menit per hari."
                                "Sedang" -> "Hasil menunjukkan tingkat sedang. Kami menyarankan untuk bicara dengan orang yang Anda percaya dan pertimbangkan konsultasi dengan psikolog."
                                "Cukup Berat" -> "Hasil menunjukkan tingkat cukup berat. Sangat disarankan untuk berkonsultasi dengan psikolog atau psikiater. Anda tidak sendirian."
                                else -> "Hasil menunjukkan bahwa Anda mungkin membutuhkan bantuan profesional. Segera hubungi psikolog/psikiater atau hotline 119 ext. 8."
                            },
                            fontSize = 13.sp, lineHeight = 20.sp, color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Disclaimer
                Text(
                    "⚠️ Kuis ini adalah alat skrining awal, bukan diagnosis. Untuk diagnosis resmi, konsultasikan dengan profesional kesehatan mental.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) {
                    Text("Kembali ke Kesehatan Mental", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
