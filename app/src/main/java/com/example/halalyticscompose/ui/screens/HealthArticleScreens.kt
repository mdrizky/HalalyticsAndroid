package com.example.halalyticscompose.ui.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.halalyticscompose.Data.Model.HealthArticleItem
import com.example.halalyticscompose.ui.viewmodel.HealthArticleViewModel

private data class HealthArticleLocal(
    val id: String,
    val title: String,
    val excerpt: String,
    val content: String,
    val category: String,
    val imageUrl: String
)

private val fallbackArticles = listOf(
    HealthArticleLocal(
        id = "hidrasi-sehat",
        title = "Pentingnya Hidrasi: Kapan Tubuh Mulai Kekurangan Cairan?",
        excerpt = "Dehidrasi ringan bisa menurunkan fokus, energi, dan performa harian.",
        content = "Tubuh memerlukan cairan cukup untuk menjaga tekanan darah, suhu tubuh, dan fungsi organ. Tanda awal dehidrasi antara lain bibir kering, urine pekat, pusing, dan lemas. Pola sederhana: minum rutin sepanjang hari, bukan menunggu haus.",
        category = "Nutrisi",
        imageUrl = "https://images.unsplash.com/photo-1550505095-81378a674395?auto=format&fit=crop&w=800&q=80"
    ),
    HealthArticleLocal(
        id = "gula-tersembunyi",
        title = "Gula Tersembunyi di Produk Harian dan Cara Membacanya",
        excerpt = "Banyak produk kemasan tampak sehat namun tinggi gula tambahan.",
        content = "Periksa label nutrition facts dan ingredients. Nama gula bisa muncul sebagai sucrose, glucose syrup, fructose, maltodextrin, atau corn syrup. Prioritaskan produk dengan gula lebih rendah per 100g/100ml.",
        category = "Fakta Kesehatan",
        imageUrl = "https://images.unsplash.com/photo-1551024601-bec78aea704b?auto=format&fit=crop&w=800&q=80"
    ),
    HealthArticleLocal(
        id = "tidur-berkualitas",
        title = "Tidur Berkualitas: Pondasi Imunitas dan Kesehatan Metabolik",
        excerpt = "Kurang tidur berdampak pada hormon lapar, mood, dan daya tahan tubuh.",
        content = "Tidur 7-9 jam untuk dewasa membantu pemulihan jaringan, fungsi kognitif, dan stabilitas hormon. Biasakan jadwal tidur konsisten dan batasi layar sebelum tidur.",
        category = "Gaya Hidup",
        imageUrl = "https://images.unsplash.com/photo-1541781774459-bb2af2f05b55?auto=format&fit=crop&w=800&q=80"
    ),
    HealthArticleLocal(
        id = "cek-tekanan-darah",
        title = "Cek Tekanan Darah Mandiri: Angka yang Perlu Diwaspadai",
        excerpt = "Pemantauan rutin membantu deteksi dini risiko kardiovaskular.",
        content = "Tekanan darah tinggi sering tanpa gejala. Lakukan pengukuran pada waktu yang sama, posisi duduk tenang, dan alat tervalidasi.",
        category = "Pantauan Tubuh",
        imageUrl = "https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=800&q=80"
    ),
    HealthArticleLocal(
        id = "aman-pilih-obat",
        title = "Tips Memilih Obat OTC dengan Aman dan Tepat",
        excerpt = "Pahami kandungan aktif, dosis, dan interaksi obat-makanan.",
        content = "Baca label indikasi, kontraindikasi, serta dosis maksimal harian. Hindari penggunaan ganda bahan aktif yang sama dari dua produk berbeda.",
        category = "Obat",
        imageUrl = "https://images.unsplash.com/photo-1587370560942-ad2a04eabb6d?auto=format&fit=crop&w=800&q=80"
    )
)

private fun HealthArticleItem.toUiKey(): String = slug ?: id

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthArticleListScreen(
    navController: NavController,
    viewModel: HealthArticleViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val remoteArticles by viewModel.articles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadArticles(includeExternal = true)
    }

    val displayRemote = remember(remoteArticles, query) {
        val q = query.trim().lowercase()
        val source = if (remoteArticles.isNotEmpty()) remoteArticles else emptyList()
        if (q.isBlank()) source else source.filter {
            it.title.lowercase().contains(q) ||
                (it.excerpt ?: "").lowercase().contains(q) ||
                (it.category ?: "").lowercase().contains(q)
        }
    }

    val displayFallback = remember(query) {
        val q = query.trim().lowercase()
        if (q.isBlank()) fallbackArticles
        else fallbackArticles.filter {
            it.title.lowercase().contains(q) || it.excerpt.lowercase().contains(q) || it.category.lowercase().contains(q)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Artikel Kesehatan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari artikel kesehatan...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading && remoteArticles.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Memuat artikel...")
                    }
                }
                remoteArticles.isEmpty() -> {
                    if (!error.isNullOrBlank()) {
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(displayFallback) { article ->
                            ArticleCard(
                                category = article.category,
                                title = article.title,
                                excerpt = article.excerpt,
                                imageUrl = article.imageUrl,
                                onClick = { navController.navigate("health_article_detail/${Uri.encode(article.id)}") }
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(displayRemote) { article ->
                            ArticleCard(
                                category = article.category ?: "Kesehatan",
                                title = article.title,
                                excerpt = article.excerpt ?: "",
                                imageUrl = article.imageUrl,
                                onClick = {
                                    viewModel.setSelectedArticle(article)
                                    navController.navigate("health_article_detail/${Uri.encode(article.toUiKey())}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(
    category: String,
    title: String,
    excerpt: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(90.dp)
                    .padding(end = 10.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Text(title, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    excerpt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthArticleDetailScreen(
    navController: NavController,
    articleId: String,
    viewModel: HealthArticleViewModel = hiltViewModel()
) {
    val selected by viewModel.selectedArticle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val decodedArticleId = remember(articleId) { Uri.decode(articleId) }
    val fallback = remember(decodedArticleId) { fallbackArticles.firstOrNull { it.id == decodedArticleId } }

    LaunchedEffect(decodedArticleId) {
        val current = selected
        if (current == null || (current.toUiKey() != decodedArticleId && current.id != decodedArticleId)) {
            if (fallback == null) viewModel.loadArticleDetail(decodedArticleId)
        }
    }

    val remoteArticle = selected?.takeIf { it.toUiKey() == decodedArticleId || it.id == decodedArticleId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Artikel", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when {
            fallback != null -> {
                ArticleDetailContent(
                    modifier = Modifier.padding(padding),
                    category = fallback.category,
                    title = fallback.title,
                    content = fallback.content,
                    imageUrl = fallback.imageUrl,
                )
            }
            isLoading && remoteArticle == null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Memuat detail artikel...")
                }
            }
            remoteArticle != null -> {
                ArticleDetailContent(
                    modifier = Modifier.padding(padding),
                    category = remoteArticle.category ?: "Kesehatan",
                    title = remoteArticle.title,
                    content = remoteArticle.content ?: (remoteArticle.excerpt ?: "-"),
                    imageUrl = remoteArticle.imageUrl,
                )
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(error ?: "Artikel tidak ditemukan")
                }
            }
        }
    }
}

@Composable
private fun ArticleDetailContent(
    modifier: Modifier = Modifier,
    category: String,
    title: String,
    content: String,
    imageUrl: String?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().then(modifier),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(category, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
        }
        item { Spacer(modifier = Modifier.height(18.dp)) }
    }
}
