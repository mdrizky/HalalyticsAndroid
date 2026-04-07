package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ═══════════════════════════════════════════════════════════════════
// COLOR CONSTANTS — Emerald Forest Premium + Semantic Accents
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

// Accent colors for topics (kept for semantic purpose)
private val WarmPurple = Color(0xFF6A1B9A)
private val SoftPink = Color(0xFFAD1457)
private val CalmBlue = Color(0xFF1565C0)
private val SoftTeal = Color(0xFF00695C)

data class MentalHealthTopic(
    val title: String,
    val emoji: String,
    val color: Color,
    val description: String
)

data class MentalHealthArticle(
    val title: String,
    val category: String,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentalHealthHubScreen(navController: NavController) {
    var selectedTopic by remember { mutableStateOf<MentalHealthTopic?>(null) }
    var selectedArticleCategory by remember { mutableStateOf("Kesehatan Mental") }

    val topics = listOf(
        MentalHealthTopic("Stres", "😤", Color(0xFFF57C00), "Stres adalah respons alami tubuh terhadap tekanan. Stres berkepanjangan dapat memengaruhi kesehatan fisik dan mental Anda. Pelajari cara mengelola stres dengan efektif."),
        MentalHealthTopic("Gangguan\nKecemasan", "😰", CalmBlue, "Gangguan kecemasan melibatkan rasa khawatir berlebihan yang sulit dikendalikan. Penanganan yang tepat dapat membantu Anda kembali beraktivitas normal."),
        MentalHealthTopic("Depresi", "😔", WarmPurple, "Depresi lebih dari sekadar rasa sedih biasa. Ini adalah kondisi medis yang memengaruhi cara Anda berpikir, merasa, dan bertindak. Dengan penanganan tepat, depresi bisa diatasi."),
        MentalHealthTopic("Keluarga &\nHubungan", "👨‍👩‍👧‍👦", SoftPink, "Masalah dalam keluarga atau hubungan bisa menjadi sumber stres yang signifikan. Konseling dapat membantu memperbaiki komunikasi dan membangun hubungan yang lebih sehat."),
        MentalHealthTopic("Kesepian", "🏠", SoftTeal, "Kesepian adalah perasaan terisolasi meskipun dikelilingi orang lain. Ini umum terjadi dan bisa diatasi dengan langkah-langkah kecil menuju koneksi sosial."),
    )

    val articles = listOf(
        MentalHealthArticle("5 Langkah Sederhana untuk Mengatasi Stres di Tempat Kerja", "Stres", "💼"),
        MentalHealthArticle("Mengenal Tanda-Tanda Kecemasan yang Sering Diabaikan", "Kecemasan", "⚠️"),
        MentalHealthArticle("Tips Move On Setelah Putus Cinta dari Para Psikolog", "Hubungan", "💔"),
        MentalHealthArticle("Pentingnya Tidur yang Cukup untuk Kesehatan Mental", "Kesehatan Mental", "😴"),
        MentalHealthArticle("Teknik Pernapasan 4-7-8 untuk Mengurangi Kecemasan", "Kecemasan", "🌬️"),
        MentalHealthArticle("Cara Membangun Rutinitas Pagi yang Positif", "Kesehatan Mental", "🌅"),
    )

    val articleCategories = listOf("Kesehatan Mental", "Stres", "Kecemasan", "Hubungan")

    Scaffold(containerColor = SageBg) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── EMERALD GRADIENT HEADER ──
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
                Column {
                    // Back + Title
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
                            "Kesehatan Mental",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Hero tagline
                    Text(
                        "Jangan abaikan\nkesehatan mentalmu.",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Dapatkan bantuan dari ahli yang tepat.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── JELAJAHI TOPIK KONSELING ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Jelajahi Topik Konseling",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextDark
                )
                TextButton(onClick = { }) {
                    Text(
                        "Lihat Semua",
                        color = EmeraldDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topics) { topic ->
                    Card(
                        modifier = Modifier
                            .width(130.dp)
                            .clickable { selectedTopic = if (selectedTopic == topic) null else topic },
                        shape = RoundedCornerShape(16.dp),
                        border = if (selectedTopic == topic) BorderStroke(2.dp, topic.color) else null,
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTopic == topic) topic.color.copy(alpha = 0.08f) else CardBg
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(topic.emoji, fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                topic.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                color = TextDark
                            )
                        }
                    }
                }
            }

            // Selected topic description
            selectedTopic?.let { topic ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = topic.color.copy(alpha = 0.06f)),
                    border = BorderStroke(1.dp, topic.color.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            topic.description,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { /* Cari psikolog */ },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = topic.color)
                        ) {
                            Text("Carikan Ahli", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── PAHAMI PERBEDAAN ANTAR AHLI ──
            Text(
                "Pahami Perbedaan Antar Ahli",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Psikiater Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(WarmPurple.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧑‍⚕️", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Pilih Psikiater",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Penanganan intensif atau terapi pengobatan",
                            fontSize = 11.sp,
                            color = TextMedium,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Psikolog Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CalmBlue.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧠", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Pilih Psikolog",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Asesmen kesehatan mental dan konseling",
                            fontSize = 11.sp,
                            color = TextMedium,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── KUIS KESEHATAN MENTAL ──
            Text(
                "Kuis Kesehatan Mental",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                "Dirancang oleh ahli. Hasil instan.",
                fontSize = 12.sp,
                color = TextMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuizCard(
                        title = "GAD-7",
                        subtitle = "Tes Gangguan Kecemasan",
                        emoji = "😰",
                        color = CalmBlue,
                        description = "7 pertanyaan · 2 menit",
                        onClick = { navController.navigate("mental_health_quiz/gad7") }
                    )
                }
                item {
                    QuizCard(
                        title = "PHQ-9",
                        subtitle = "Tes Depresi",
                        emoji = "😔",
                        color = WarmPurple,
                        description = "9 pertanyaan · 3 menit",
                        onClick = { navController.navigate("mental_health_quiz/phq9") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── BACA TENTANG KESEHATAN MENTAL ──
            Text(
                "Baca Tentang Kesehatan Mental",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Category filters
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(articleCategories) { cat ->
                    FilterChip(
                        selected = selectedArticleCategory == cat,
                        onClick = { selectedArticleCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Article list
            val filteredArticles = articles.filter {
                selectedArticleCategory == "Kesehatan Mental" || it.category == selectedArticleCategory
            }
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                filteredArticles.forEach { article ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { /* navigate to article */ },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SoftSage),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(article.emoji, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    article.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = TextDark,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    article.category,
                                    fontSize = 11.sp,
                                    color = EmeraldDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward, null,
                                tint = TextLight,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuizCard(
    title: String,
    subtitle: String,
    emoji: String,
    color: Color,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = color
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                description,
                fontSize = 11.sp,
                color = TextMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Mulai →",
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 13.sp
            )
        }
    }
}
