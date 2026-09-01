package com.ecrinlabs.weara

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class MatchItem(
    val title:String,
    val shop:String,
    val price:String,
    val score:Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WearaApp() }
    }
}

@Composable
fun WearaApp() {
    val bg=Color(0xFF090A0C)
    val card=Color(0xFF15171B)
    val accent=Color(0xFFE8FF65)

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var screen by remember { mutableStateOf("home") }
    var status by remember { mutableStateOf("Find any outfit") }

    val gallery=rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ){ uri ->
        imageUri=uri
        if(uri!=null) {
            status="Photo ready"
            screen="preview"
        }
    }

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color=bg) {
            when(screen) {
                "results" -> ResultsScreen(
                    bg=bg, card=card, accent=accent,
                    imageUri=imageUri,
                    onBack={screen="preview"}
                )

                else -> LazyColumn(
                    modifier=Modifier
                        .fillMaxSize()
                        .padding(horizontal=20.dp),
                    verticalArrangement=Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(Modifier.height(20.dp)) }

                    item {
                        Text("WEARA", color=Color.White, fontSize=30.sp)
                        Text("by Ecrin Labs", color=Color.Gray, fontSize=12.sp)
                    }

                    item {
                        Text(
                            status,
                            color=Color(0xFFC7C8CC),
                            fontSize=17.sp
                        )
                    }

                    item {
                        Card(
                            modifier=Modifier
                                .fillMaxWidth()
                                .height(390.dp),
                            colors=CardDefaults.cardColors(containerColor=card),
                            shape=RoundedCornerShape(28.dp)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment=Alignment.Center) {
                                if(imageUri==null) {
                                    Text(
                                        "Point. Snap. Shop.",
                                        color=Color.White,
                                        fontSize=22.sp
                                    )
                                } else {
                                    AsyncImage(
                                        model=imageUri,
                                        contentDescription=null,
                                        modifier=Modifier.fillMaxSize(),
                                        contentScale=ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick={gallery.launch("image/*")},
                            modifier=Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            colors=ButtonDefaults.buttonColors(containerColor=accent),
                            shape=RoundedCornerShape(18.dp)
                        ){
                            Text(
                                if(imageUri==null) "Choose photo" else "Choose another photo",
                                color=Color.Black
                            )
                        }
                    }

                    if(imageUri!=null) {
                        item {
                            Button(
                                onClick={
                                    status="Searching visually…"
                                    screen="results"
                                },
                                modifier=Modifier
                                    .fillMaxWidth()
                                    .height(58.dp),
                                colors=ButtonDefaults.buttonColors(containerColor=Color.White),
                                shape=RoundedCornerShape(18.dp)
                            ){
                                Text("Find this outfit", color=Color.Black)
                            }
                        }
                    }

                    item {
                        Text("DISCOVER", color=Color.Gray, fontSize=12.sp)
                    }

                    item {
                        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                            Feature("Exact match", Modifier.weight(1f))
                            Feature("Similar", Modifier.weight(1f))
                            Feature("Cheaper", Modifier.weight(1f))
                        }
                    }

                    item { Spacer(Modifier.height(28.dp)) }
                }
            }
        }
    }
}

@Composable
fun ResultsScreen(
    bg:Color,
    card:Color,
    accent:Color,
    imageUri:Uri?,
    onBack:()->Unit
){
    val items=listOf(
        MatchItem("Black fitted top","Visual match","—",94),
        MatchItem("Wide-leg trousers","Visual match","—",91),
        MatchItem("Shoulder bag","Visual match","—",88),
        MatchItem("Cap","Visual match","—",84)
    )

    LazyColumn(
        modifier=Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal=20.dp),
        verticalArrangement=Arrangement.spacedBy(14.dp)
    ){
        item { Spacer(Modifier.height(18.dp)) }

        item {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Text(
                    "‹",
                    color=Color.White,
                    fontSize=38.sp,
                    modifier=Modifier.clickable{onBack()}
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Outfit detected", color=Color.White, fontSize=26.sp)
                    Text("Visual search results", color=Color.Gray, fontSize=13.sp)
                }
            }
        }

        if(imageUri!=null){
            item {
                Card(
                    modifier=Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    colors=CardDefaults.cardColors(containerColor=card),
                    shape=RoundedCornerShape(24.dp)
                ){
                    AsyncImage(
                        model=imageUri,
                        contentDescription=null,
                        modifier=Modifier.fillMaxSize(),
                        contentScale=ContentScale.Crop
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                FilterChip(selected=true,onClick={},label={Text("All")})
                FilterChip(selected=false,onClick={},label={Text("Exact")})
                FilterChip(selected=false,onClick={},label={Text("Similar")})
                FilterChip(selected=false,onClick={},label={Text("Cheaper")})
            }
        }

        items(items.size){ i ->
            val m=items[i]
            Card(
                modifier=Modifier.fillMaxWidth(),
                colors=CardDefaults.cardColors(containerColor=card),
                shape=RoundedCornerShape(20.dp)
            ){
                Row(
                    modifier=Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment=Alignment.CenterVertically
                ){
                    Box(
                        modifier=Modifier
                            .size(64.dp)
                            .background(Color(0xFF23262C),RoundedCornerShape(16.dp)),
                        contentAlignment=Alignment.Center
                    ){
                        Text("${m.score}%",color=accent,fontSize=16.sp)
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(Modifier.weight(1f)){
                        Text(m.title,color=Color.White,fontSize=16.sp)
                        Text(m.shop,color=Color.Gray,fontSize=12.sp)
                    }

                    Text("View",color=accent,fontSize=14.sp)
                }
            }
        }

        item {
            Text(
                "Weara compares visual embeddings locally on your device.",
                color=Color.DarkGray,
                fontSize=11.sp,
                modifier=Modifier.padding(vertical=20.dp)
            )
        }
    }
}

@Composable
private fun Feature(text:String, modifier:Modifier=Modifier){
    Card(
        modifier=modifier.height(80.dp),
        colors=CardDefaults.cardColors(containerColor=Color(0xFF15171B)),
        shape=RoundedCornerShape(18.dp)
    ){
        Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){
            Text(text,color=Color.White,fontSize=13.sp)
        }
    }
}
