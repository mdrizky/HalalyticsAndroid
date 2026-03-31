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

private val MintAccent = Color(0xFF00BFA6)
private val NavyDeep = Color(0xFF0A1929)
private val WarmPurple = Color(0xFF7C3AED)
private val SoftPink = Color(0xFFEC4899)
private val CalmBlue = Color(0xFF3B82F6)
private val SoftTeal = Color(0xFF14B8A6)

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
        MentalHealthTopic("Stres", "😤", Color(0xFFF59E0B), "Stres adalah respons alami tubuh terhadap tekanan atau tuntutan. Stres yang berkepanjangan dapat memengaruhi kesehatan fisik dan mental Anda. Pelajari cara mengelola stres dengan efektif."),
        MentalHealthTopic("Gangguan\nKecemasan", "😰", CalmBlue, "Gangguan kecemasan melibatkan rasa khawatir berlebihan yang sulit dikendalikan. Ini bisa memengaruhi aktivitas sehari-hari. Penanganan yang tepat dapat membantu Anda kembali beraktivitas normal."),
        MentalHealthTopic("Depresi", "😔", WarmPurple, "Depresi lebih dari sekadar rasa sedih biasa. Ini adalah kondisi medis yang memengaruhi cara Anda berpikir, merasa, dan bertindak. Dengan penanganan yang tepat, depresi bisa diatasi."),
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kesehatan Mental", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Tagline
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(WarmPurple.copy(alpha = 0.08f), Color.Transparent))
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        "Jangan abaikan\nkesehatan mentalmu.",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDeep,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Dapatkan bantuan dari ahli yang tepat.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // ── Jelajahi Topik Konseling Umum ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Jelajahi Topik Konseling", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                TextButton(onClick = { }) {
                    Text("Lihat Semua", color = MintAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                        colors = CardDefaults.cardColors(containerColor = topic.color.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(topic.emoji, fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                topic.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // Selected topic description
            selectedTopic?.let { topic ->
                Card(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = topic.color.copy(alpha = 0.06f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(topic.description, fontSize = 13.sp, lineHeight = 20.sp, color = Color.DarkGray)
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

            // ── Pahami Perbedaan Antar Ahli ──
            Text(
                "Pahami Perbedaan Antar Ahli",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Psikiater Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmPurple.copy(alpha = 0.06f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🧑‍⚕️", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Pilih Psikiater", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Jika kamu butuh penanganan lebih intensif atau terapi pengobatan",
                            fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp
                        )
                    }
                }

                // Psikolog Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CalmBlue.copy(alpha = 0.06f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🧠", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Pilih Psikolog", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Jika kamu perlu asesmen kesehatan mental dan bantuan konseling",
                            fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Kuis Kesehatan Mental ──
            Text(
                "Kuis Kesehatan Mental",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                "Dirancang oleh ahli. Hasil instan.",
                fontSize = 12.sp, color = Color.Gray,
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

            // ── Baca Tentang Kesehatan Mental ──
            Text(
                "Baca Tentang Kesehatan Mental",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
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
                            selectedContainerColor = MintAccent,
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
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(article.emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    article.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(article.category, fontSize = 11.sp, color = MintAccent, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
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
        modifier = Modifier.width(200.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = color)
            Text(subtitle, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Mulai →", fontWeight = FontWeight.Bold, color = color, fontSize = 13.sp)
        }
    }
}
