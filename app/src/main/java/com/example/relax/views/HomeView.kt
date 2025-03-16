package com.example.relax.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

class HomeView {
    // Properties
    private val appName: String = "RelaX"

    // Methods
    @Preview(showBackground = true)
    @Composable
    fun StartScreen(){
        var startingLocation by remember { mutableStateOf("")}
        var destination by remember { mutableStateOf("")}
        Column(
            modifier = Modifier
                .fillMaxSize()
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                RenderAppName()
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                OutlinedTextField(
                    value = startingLocation,
                    onValueChange = { text ->
                        startingLocation = text
                    },
                    modifier = Modifier
                        .weight(1f)
                    )
            }
        }
    }
    @Composable
    fun RenderAppName(){
        Text(
            text = this.appName,
            fontSize = 30.sp,
            fontFamily = FontFamily.SansSerif,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

}