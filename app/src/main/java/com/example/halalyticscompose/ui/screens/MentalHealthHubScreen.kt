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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.MentalHealthViewModel
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import com.example.halalyticscompose.R

// ═══════════════════════════════════════════════════════════════════
// COLOR CONSTANTS — Emerald Forest Premium + Semantic Accents
// ═══════════════════════════════════════════════════════════════════
// Color constants moved into theme-aware components
val CalmBlue = Color(0xFFE3F2FD)
val WarmPurple = Color(0xFFF3E5F5)

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
fun MentalHealthHubScreen(
    navController: NavController,
    viewModel: MentalHealthViewModel = hiltViewModel()
) {
    var selectedTopic by remember { mutableStateOf<MentalHealthTopic?>(null) }
    var selectedArticleCategory by remember { mutableStateOf("Kesehatan Mental") }

    val news by viewModel.news.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNews()
    }

    val topics = listOf(
        MentalHealthTopic(stringResource(R.string.mental_health_topic_stress), "😤", Color(0xFFF57C00), stringResource(R.string.mental_health_topic_stress_desc)),
        MentalHealthTopic(stringResource(R.string.mental_health_topic_anxiety), "😰", Color(0xFF1565C0), stringResource(R.string.mental_health_topic_anxiety_desc)),
        MentalHealthTopic(stringResource(R.string.mental_health_topic_depression), "😔", Color(0xFF6A1B9A), stringResource(R.string.mental_health_topic_depression_desc)),
        MentalHealthTopic(stringResource(R.string.mental_health_topic_family), "👨‍👩‍👧‍👦", Color(0xFFAD1457), stringResource(R.string.mental_health_topic_family_desc)),
        MentalHealthTopic(stringResource(R.string.mental_health_topic_lonely), "🏠", Color(0xFF00695C), stringResource(R.string.mental_health_topic_lonely_desc)),
    )

    val articles = listOf(
        MentalHealthArticle("5 Langkah Sederhana untuk Mengatasi Stres di Tempat Kerja", "Stres", "💼"),
        MentalHealthArticle("Mengenal Tanda-Tanda Kecemasan yang Sering Diabaikan", "Kecemasan", "⚠️"),
        MentalHealthArticle("Tips Move On Setelah Putus Cinta dari Para Psikolog", "Hubungan", "💔"),
        MentalHealthArticle("Pentingnya Tidur yang Cukup untuk Kesehatan Mental", "Kesehatan Mental", "😴"),
        MentalHealthArticle("Teknik Pernapasan 4-7-8 untuk Mengurangi Kecemasan", "Kecemasan", "🌬️"),
        MentalHealthArticle("Cara Membangun Rutinitas Pagi yang Positif", "Kesehatan Mental", "🌅"),
    )

    val articleCategories = listOf(stringResource(R.string.search_hub_all), "Stres", "Kecemasan", "Hubungan")

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
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
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 28.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            stringResource(R.string.mental_health_title),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            stringResource(R.string.mental_health_subtitle),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.primary)
                    }
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
                    stringResource(R.string.mental_health_topics),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { navController.navigate("halocode") }) {
                    Text(
                        stringResource(R.string.home_see_all),
                        color = MaterialTheme.colorScheme.primary,
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
                            containerColor = if (selectedTopic == topic) topic.color.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant
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
                                color = MaterialTheme.colorScheme.onSurface
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { navController.navigate("halocode") },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = topic.color)
                        ) {
                            Text(stringResource(R.string.mental_health_carikan_ahli), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── PAHAMI PERBEDAAN ANTAR AHLI ──
            Text(
                stringResource(R.string.mental_health_experts),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground,
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
                    modifier = Modifier.weight(1f).clickable { navController.navigate("halocode") },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧑‍⚕️", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            stringResource(R.string.mental_health_psychiatrist),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            stringResource(R.string.mental_health_psychiatrist_desc),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Psikolog Card
                Card(
                    modifier = Modifier.weight(1f).clickable { navController.navigate("halocode") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧠", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            stringResource(R.string.mental_health_psychologist),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            stringResource(R.string.mental_health_psychologist_desc),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── KUIS KESEHATAN MENTAL ──
            Text(
                stringResource(R.string.mental_health_quiz_title),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                stringResource(R.string.mental_health_quiz_subtitle),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                stringResource(R.string.mental_health_articles_title),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground,
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
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
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
                            .clickable { navController.navigate("health_articles") },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
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
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                 Text(
                                    article.category,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                             Icon(
                                Icons.AutoMirrored.Filled.ArrowForward, null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
             Text(
                description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
             Text(
                stringResource(R.string.mental_health_quiz_start),
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 13.sp
            )
        }
    }
}
