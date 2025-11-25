package com.bytedance.storymagician.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi


@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun FrontPageActivity(onGenerateStoryboard: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp)
    ) {
        Text(
            text = "StoryMagician",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Create",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        TextField(
            value = "",
            onValueChange = { },
            placeholder = { Text("Write your story...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Chip(
                onClick = { },
                label = { Text("Movie", fontSize = 20.sp) },
                colors = ChipDefaults.chipColors(
                    backgroundColor = Color.White
                )
            )
            Chip(
                onClick = { },
                label = { Text("Animation", fontSize = 20.sp) },
                colors = ChipDefaults.chipColors(
                    backgroundColor = Color.White
                )
            )
            Chip(
                onClick = { },
                label = { Text("Realistic", fontSize = 20.sp) },
                colors = ChipDefaults.chipColors(
                    backgroundColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onGenerateStoryboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "Generate Storyboard",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "The storyboard will open automatically after generation.",
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        )
    }
}


@Preview
@Composable
fun FrontPageActivityPreview() {
    FrontPageActivity(onGenerateStoryboard = {})
}
