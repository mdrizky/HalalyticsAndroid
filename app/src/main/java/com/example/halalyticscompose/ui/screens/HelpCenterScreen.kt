package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val MintHelp = Color(0xFF00BFA6)
private val RedAccent = Color(0xFFE5173F)

data class FaqCategory(
    val name: String,
    val emoji: String,
    val color: Color
)

data class FaqItem(
    val question: String,
    val answer: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var expandedFaq by remember { mutableStateOf<String?>(null) }
    var showContactSheet by remember { mutableStateOf(false) }

    val categories = listOf(
        FaqCategory("Panduan Pengguna", "📖", Color(0xFF3B82F6)),
        FaqCategory("Fitur Kesehatan", "❤️", Color(0xFFEF4444)),
        FaqCategory("Akun & Keamanan", "🔐", Color(0xFF8B5CF6)),
        FaqCategory("Teknis", "🔧", Color(0xFFF59E0B)),
        FaqCategory("Pembayaran", "💰", Color(0xFF10B981)),
        FaqCategory("Lainnya", "📋", Color.Gray),
    )

    val faqs = listOf(
        FaqItem("Bagaimana cara scan barcode produk?", "Buka aplikasi → tap tombol Scan di menu bawah → arahkan kamera ke barcode produk → hasil akan muncul otomatis.", "Panduan Pengguna"),
        FaqItem("Bagaimana cara mengecek status halal?", "Anda bisa scan barcode, ketik nama produk di pencarian, atau gunakan fitur Verifikasi Sertifikat Halal.", "Panduan Pengguna"),
        FaqItem("Apakah data scan saya tersimpan?", "Ya, semua riwayat scan tersimpan di menu Riwayat. Anda bisa melihat produk yang pernah di-scan kapan saja.", "Panduan Pengguna"),
        FaqItem("Bagaimana cara menggunakan Pengingat Obat?", "Masuk ke Health Suite → Pengingat Obat → Buat Pengingat Baru → Cari obat → Isi jadwal → Simpan.", "Fitur Kesehatan"),
        FaqItem("Apakah hasil kuis kesehatan mental akurat?", "Kuis GAD-7 dan PHQ-9 adalah alat skrining standar internasional. Hasilnya bersifat panduan awal, bukan diagnosis resmi.", "Fitur Kesehatan"),
        FaqItem("Bagaimana AI Health Assistant bekerja?", "AI menggunakan teknologi Gemini untuk informasi kesehatan. AI mempertimbangkan profil medis Anda untuk jawaban yang lebih personal.", "Fitur Kesehatan"),
        FaqItem("Bagaimana cara mengubah kata sandi?", "Masuk ke Pengaturan → Pengaturan Akun → Ubah Kata Sandi → Masukkan kata sandi lama dan baru → Simpan.", "Akun & Keamanan"),
        FaqItem("Apakah data medis saya aman?", "Ya, semua data medis dienkripsi dan hanya bisa diakses oleh Anda. Kami tidak membagikan data Anda ke pihak ketiga.", "Akun & Keamanan"),
        FaqItem("Kenapa kamera scan tidak berfungsi?", "Pastikan izin kamera sudah diberikan. Buka Pengaturan HP → Aplikasi → Halalytics → Izin → aktifkan Kamera.", "Teknis"),
        FaqItem("Aplikasi terasa lambat?", "Coba clear cache aplikasi, pastikan koneksi internet stabil, dan update ke versi terbaru.", "Teknis"),
    )

    val filteredFaqs = faqs.filter { faq ->
        val matchesSearch = searchQuery.isBlank() || faq.question.contains(searchQuery, ignoreCase = true) || faq.answer.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || faq.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusat Bantuan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Text(
                    "Apa yang bisa kami bantu? 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Search
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari topik atau ketik pertanyaanmu") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // Category Grid
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedCategory = if (selectedCategory == cat.name) null else cat.name
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCategory == cat.name) cat.color.copy(alpha = 0.1f) else Color(0xFFF5F5F5)
                            ),
                            border = if (selectedCategory == cat.name) BorderStroke(1.dp, cat.color) else null
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(cat.emoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(cat.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.drop(3).forEach { cat ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedCategory = if (selectedCategory == cat.name) null else cat.name
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCategory == cat.name) cat.color.copy(alpha = 0.1f) else Color(0xFFF5F5F5)
                            ),
                            border = if (selectedCategory == cat.name) BorderStroke(1.dp, cat.color) else null
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(cat.emoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(cat.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }

            // FAQ Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    if (selectedCategory != null) "FAQ: $selectedCategory" else "Pertanyaan Populer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // FAQ Accordion List
            items(filteredFaqs) { faq ->
                val isExpanded = expandedFaq == faq.question
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedFaq = if (isExpanded) null else faq.question
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded) MintHelp.copy(alpha = 0.04f) else Color(0xFFFAFAFA)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                faq.question,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f),
                                lineHeight = 20.sp
                            )
                            Icon(
                                if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFFEEEEEE))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(faq.answer, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 20.sp)
                            }
                        }
                    }
                }
            }

            // Contact Support
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Hubungi Customer Support", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Kami siap 24 jam untuk membantu", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Chat
                            Button(
                                onClick = { /* Open chat */ },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MintHelp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Text("Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            // Email
                            Button(
                                onClick = { /* Open email */ },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MintHelp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Text("Email", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            // Phone
                            Button(
                                onClick = { /* Open phone */ },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MintHelp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Text("Telepon", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Legal Links
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { }) {
                        Text("Ketentuan Penggunaan", fontSize = 12.sp, color = MintHelp)
                    }
                    Text("•", modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp), color = Color.Gray)
                    TextButton(onClick = { }) {
                        Text("Kebijakan Privasi", fontSize = 12.sp, color = MintHelp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
