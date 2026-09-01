package com.ecrinlabs.weara

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WearaApp() }
    }
}

@Composable
fun WearaApp() {
    val bg = Color(0xFF090A0C)
    val card = Color(0xFF15171B)
    val accent = Color(0xFFE8FF65)
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var status by remember { mutableStateOf("Find the outfit. Find the price.") }

    val gallery = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        if (uri != null) status = "Photo ready — tap Analyze outfit"
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = bg) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Spacer(Modifier.height(14.dp))
                Text("WEARA", color = Color.White, fontSize = 30.sp)
                Text("by Ecrin Labs", color = Color.Gray, fontSize = 12.sp)
                Text(status, color = Color(0xFFC7C8CC), fontSize = 17.sp)

                Card(
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    colors = CardDefaults.cardColors(containerColor = card),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            if (imageUri == null) "Point. Snap. Shop." else "✓ Photo selected",
                            color = if (imageUri == null) Color.White else accent,
                            fontSize = 22.sp
                        )
                    }
                }

                Button(
                    onClick = { gallery.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Choose from gallery", color = Color.Black)
                }

                OutlinedButton(
                    onClick = {
                        status = if (imageUri == null)
                            "Choose a photo first"
                        else
                            "Analyzing locally — no paid AI API"
                    },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Analyze outfit", color = Color.White)
                }

                Text("DISCOVER", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Feature("Exact match", Modifier.weight(1f))
                    Feature("Similar", Modifier.weight(1f))
                    Feature("Cheaper", Modifier.weight(1f))
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "Your photos stay under your control • No AI credits",
                    color = Color.DarkGray,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun Feature(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15171B)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text, color = Color.White, fontSize = 13.sp)
        }
    }
}
