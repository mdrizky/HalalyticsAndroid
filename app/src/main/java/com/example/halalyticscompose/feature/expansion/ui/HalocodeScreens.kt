package com.example.halalyticscompose.feature.expansion.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.halalyticscompose.feature.expansion.model.HalocodeConsultation
import com.example.halalyticscompose.feature.expansion.model.HalocodeExpert
import com.example.halalyticscompose.feature.expansion.model.HalocodeMessage
import com.example.halalyticscompose.feature.expansion.socket.ChatWebSocketManager
import com.example.halalyticscompose.feature.expansion.viewmodel.HalocodeViewModel
import com.example.halalyticscompose.utils.SessionManager
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HalocodeScreen(
    navController: NavController,
    viewModel: HalocodeViewModel = hiltViewModel(),
) {
    val experts by viewModel.experts.collectAsState()
    val queue by viewModel.queue.collectAsState()
    val currentConsultation by viewModel.currentConsultation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val isExpert = sessionManager.getRole()?.equals("expert", ignoreCase = true) == true

    LaunchedEffect(Unit) {
        viewModel.loadExperts()
        if (isExpert) {
            viewModel.loadQueue()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Halocode") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            if (currentConsultation != null) {
                ConsultationBanner(
                    consultation = currentConsultation!!,
                    onOpenChat = {
                        navController.navigate("halocode_chat/${currentConsultation!!.id}")
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (isExpert) {
                ExpertQueueCard(
                    totalQueue = queue.size,
                    onOpenDashboard = { navController.navigate("expert_dashboard") },
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = "Pilih pakar terpercaya untuk konsultasi real-time.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                experts.isEmpty() -> {
                    EmptyFeatureState("Belum ada pakar aktif saat ini.")
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        items(experts, key = { it.id }) { expert ->
                            HalocodeExpertCard(
                                expert = expert,
                                onConsult = {
                                    viewModel.createConsultation(expert.id) { consultation ->
                                        if (consultation.paymentStatus == "paid" || consultation.status == "active") {
                                            navController.navigate("halocode_chat/${consultation.id}")
                                        }
                                    }
                                },
                            )
                        }

                        if (!error.isNullOrBlank()) {
                            item {
                                Text(
                                    text = error.orEmpty(),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsultationBanner(
    consultation: HalocodeConsultation,
    onOpenChat: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Konsultasi Terakhir",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = consultation.expert?.name ?: "Pakar Halocode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Status: ${consultation.status.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (!consultation.paymentToken.isNullOrBlank() && consultation.paymentStatus != "paid") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Token pembayaran tersedia. Integrasi Midtrans tinggal memakai token ini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onOpenChat) {
                Text("Masuk Chat")
            }
        }
    }
}

@Composable
private fun ExpertQueueCard(
    totalQueue: Int,
    onOpenDashboard: () -> Unit,
) {
    Card(shape = RoundedCornerShape(18.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Dashboard Pakar", fontWeight = FontWeight.Bold)
                Text(
                    text = "Antrean aktif: $totalQueue sesi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onOpenDashboard) {
                Text("Buka")
            }
        }
    }
}

@Composable
private fun HalocodeExpertCard(
    expert: HalocodeExpert,
    onConsult: () -> Unit,
) {
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (expert.photoUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            } else {
                AsyncImage(
                    model = expert.photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(expert.name, fontWeight = FontWeight.Bold)
                    if (expert.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                Text(
                    text = expert.specialization,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rp ${rupiah(expert.pricePerSession)} / sesi",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Rating ${expert.rating} • ${expert.totalReviews} ulasan",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Circle,
                        contentDescription = null,
                        tint = if (expert.isOnline) Color(0xFF2E7D32) else Color(0xFF9E9E9E),
                        modifier = Modifier.size(10.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (expert.isOnline) "Online" else "Offline", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = onConsult) {
                    Text("Konsultasi")
                }
            }
        }
    }
}

@Composable
fun HalocodeChatScreen(
    consultationId: Int,
    navController: NavController,
    viewModel: HalocodeViewModel = hiltViewModel(),
) {
    val savedMessages by viewModel.messages.collectAsState()
    val wsMessages by viewModel.wsMessages.collectAsState()
    val connectionState by viewModel.wsConnectionState.collectAsState()
    val context = LocalContext.current
    val currentUserId = remember { SessionManager.getInstance(context).getUserId() }
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val allMessages = (savedMessages + wsMessages).distinctBy { it.id }

    LaunchedEffect(consultationId) {
        viewModel.connectToChat(consultationId)
    }

    LaunchedEffect(allMessages.size) {
        if (allMessages.isNotEmpty()) {
            listState.animateScrollToItem(allMessages.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat Konsultasi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.endConsultation(consultationId) {
                                navController.popBackStack()
                            }
                        },
                    ) {
                        Text("Akhiri", color = MaterialTheme.colorScheme.error)
                    }
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Tulis pesan untuk pakar...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(consultationId, messageText.trim())
                            messageText = ""
                        }
                    },
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            AnimatedVisibility(visible = connectionState != ChatWebSocketManager.ConnectionState.Connected) {
                Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(
                        text = when (connectionState) {
                            ChatWebSocketManager.ConnectionState.Connecting -> "Menghubungkan ke Reverb..."
                            ChatWebSocketManager.ConnectionState.Disconnected -> "Koneksi chat terputus"
                            is ChatWebSocketManager.ConnectionState.Error -> (connectionState as ChatWebSocketManager.ConnectionState.Error).message
                            else -> ""
                        },
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                items(allMessages, key = { it.id }) { message ->
                    ChatBubble(message = message, isMe = message.senderId == currentUserId)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: HalocodeMessage,
    isMe: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
    ) {
        Surface(
            color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMe) 18.dp else 6.dp,
                bottomEnd = if (isMe) 6.dp else 18.dp,
            ),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isMe && !message.senderName.isNullOrBlank()) {
                    Text(
                        text = message.senderName.orEmpty(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isMe) Color.White else MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = message.message,
                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.createdAt.substringAfter("T").take(5).ifBlank { message.createdAt.takeLast(5) },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMe) Color.White.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
fun ExpertDashboardScreen(
    navController: NavController,
    viewModel: HalocodeViewModel = hiltViewModel(),
) {
    val queue by viewModel.queue.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadQueue()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Pakar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            Card(shape = RoundedCornerShape(18.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text("Status Layanan", fontWeight = FontWeight.Bold)
                        Text(
                            text = if (isOnline) "Siap menerima konsultasi" else "Sedang offline",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = isOnline,
                        onCheckedChange = { viewModel.toggleOnline() },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Antrean Konsultasi", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            if (queue.isEmpty()) {
                EmptyFeatureState("Belum ada antrean konsultasi.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(queue, key = { it.id }) { consultation ->
                        Card(shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(consultation.expert?.name ?: "Sesi #${consultation.id}", fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Status ${consultation.status}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFeatureState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

private fun rupiah(amount: Int): String =
    NumberFormat.getInstance(Locale("id", "ID")).format(amount)
