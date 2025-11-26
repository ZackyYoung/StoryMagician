package com.bytedance.storymagician.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ShotDetailActivity(
    shotId: String?,
    onBack: () -> Unit
) {

    var description by remember { mutableStateOf("A misty forest at dawn with a tent") }
    var transition by remember { mutableStateOf("Ken Burns Effect") }
    var narration by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {

        TextButton(onClick = { onBack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Back", fontSize = 20.sp)
        }

        Text(
            "Shot Detail (ID = $shotId)",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color.DarkGray, RoundedCornerShape(12.dp))
        ) { }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Shot description", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text("Video Transition", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        var expanded by remember { mutableStateOf(false) }

        Box {
            OutlinedTextField(
                value = transition,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Ken Burns Effect") },
                    onClick = {
                        transition = "Ken Burns Effect"
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Fade In") },
                    onClick = {
                        transition = "Fade In"
                        expanded = false
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(text = "Narration text", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = narration,
            onValueChange = { narration = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Generate Image", fontSize = 25.sp)
        }
    }
}


@Preview
@Composable
fun ShotDetailActivityPreview() {
    ShotDetailActivity(onBack = {}, shotId = "123")
}
