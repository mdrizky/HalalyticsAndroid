package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val BmiBlue = Color(0xFF3B82F6)
private val BmiGreen = Color(0xFF10B981)
private val BmiYellow = Color(0xFFF59E0B)
private val BmiRed = Color(0xFFEF4444)
private val MintAccent = Color(0xFF00BFA6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BMICalculatorScreen(navController: NavController) {
    var weightInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf<Float?>(null) }
    var bmiCategory by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(true) }

    fun calculateBmi() {
        val weight = weightInput.toFloatOrNull() ?: return
        val height = heightInput.toFloatOrNull() ?: return
        if (height <= 0 || weight <= 0) return
        val heightM = height / 100f
        val bmi = weight / (heightM * heightM)
        bmiResult = bmi
        bmiCategory = when {
            bmi < 18.5f -> "underweight"
            bmi < 23f -> "normal"
            bmi < 25f -> "overweight"
            else -> "obese"
        }
        showResult = true
        showInfo = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kalkulator BMI", fontWeight = FontWeight.Bold) },
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
                .padding(20.dp)
        ) {
            // Info Section
            AnimatedVisibility(visible = showInfo) {
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("📊 Apa itu BMI?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "BMI (Body Mass Index) adalah cara menghitung berat badan ideal berdasarkan tinggi dan berat badan. " +
                                        "BMI hanya berlaku untuk usia 20 tahun ke atas dan tidak untuk ibu hamil.",
                                fontSize = 13.sp, color = Color.DarkGray, lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Kategori BMI:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            listOf(
                                Triple("Di bawah 18.5", "Berat badan rendah", BmiBlue),
                                Triple("18.5 – 22.9", "BMI Normal", BmiGreen),
                                Triple("23.0 – 24.9", "Berat badan berlebih", BmiYellow),
                                Triple("Di atas 25.0", "Perlu penanganan", BmiRed)
                            ).forEach { (range, label, color) ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("$range → $label", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Input Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Hitung BMI Anda", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Berat Badan (kg)") },
                        placeholder = { Text("Contoh: 55") },
                        leadingIcon = { Text("⚖️", fontSize = 20.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = { heightInput = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Tinggi Badan (cm)") },
                        placeholder = { Text("Contoh: 165") },
                        leadingIcon = { Text("📏", fontSize = 20.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { calculateBmi() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                        enabled = weightInput.isNotBlank() && heightInput.isNotBlank()
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("HITUNG", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }

            // Result Section
            AnimatedVisibility(
                visible = showResult && bmiResult != null,
                enter = slideInVertically() + fadeIn()
            ) {
                val bmi = bmiResult ?: 0f
                val resultColor = when (bmiCategory) {
                    "underweight" -> BmiBlue
                    "normal" -> BmiGreen
                    "overweight" -> BmiYellow
                    "obese" -> BmiRed
                    else -> Color.Gray
                }
                val categoryLabel = when (bmiCategory) {
                    "underweight" -> "Berat Badan Rendah"
                    "normal" -> "Normal ✅"
                    "overweight" -> "Berat Badan Berlebih"
                    "obese" -> "Obesitas ⚠️"
                    else -> ""
                }
                val description = when (bmiCategory) {
                    "underweight" -> "Berat badan Anda di bawah normal. Konsultasikan dengan dokter atau ahli gizi untuk pola makan sehat."
                    "normal" -> "Selamat! BMI Anda dalam rentang normal. Pertahankan pola hidup sehat dengan makan bergizi dan olahraga teratur."
                    "overweight" -> "Berat badan Anda sedikit berlebih. Pertimbangkan diet seimbang dan olahraga 30 menit per hari."
                    "obese" -> "BMI Anda menunjukkan obesitas. Segera konsultasikan dengan dokter untuk penanganan yang tepat."
                    else -> ""
                }

                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = resultColor.copy(alpha = 0.08f)),
                        border = BorderStroke(2.dp, resultColor.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Hasil BMI Anda", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                String.format("%.1f", bmi),
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = resultColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = resultColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    categoryLabel,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = resultColor,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                description,
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            // BMI Scale Bar
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                            ) {
                                Box(modifier = Modifier.weight(18.5f).fillMaxHeight().background(BmiBlue))
                                Box(modifier = Modifier.weight(4.5f).fillMaxHeight().background(BmiGreen))
                                Box(modifier = Modifier.weight(2f).fillMaxHeight().background(BmiYellow))
                                Box(modifier = Modifier.weight(25f).fillMaxHeight().background(BmiRed))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("0", fontSize = 10.sp, color = Color.Gray)
                                Text("18.5", fontSize = 10.sp, color = Color.Gray)
                                Text("23", fontSize = 10.sp, color = Color.Gray)
                                Text("25", fontSize = 10.sp, color = Color.Gray)
                                Text("50", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            showResult = false
                            showInfo = true
                            bmiResult = null
                            weightInput = ""
                            heightInput = ""
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hitung Ulang", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
