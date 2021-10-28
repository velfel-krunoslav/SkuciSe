package com.blackbyte.skucise.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.blackbyte.skucise.components.DatePicker
import com.blackbyte.skucise.components.DropdownButton
import com.blackbyte.skucise.components.NavTopBar
import com.blackbyte.skucise.components.OutlinedInputField
import com.blackbyte.skucise.ui.theme.SkuciSeTheme


@Preview
@Composable
fun SignUpScreen(returnToPreviousScreen: () -> Unit = {}) {

    var showCalendar: Boolean by remember { mutableStateOf(false) }

    SkuciSeTheme {
        Scaffold(
            backgroundColor = MaterialTheme.colors.background,
            topBar = { NavTopBar("Registracija", returnToPreviousScreen = returnToPreviousScreen) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(all = 20.dp)
            ) {
                OutlinedInputField("Ime", modifier = Modifier.fillMaxWidth())
                Box(modifier = Modifier.size(size = 18.dp))
                OutlinedInputField("Prezime", modifier = Modifier.fillMaxWidth())
                Box(modifier = Modifier.size(size = 20.dp))
                OutlinedInputField("E-adresa", modifier = Modifier.fillMaxWidth())
                Box(modifier = Modifier.size(size = 20.dp))
                OutlinedButton(
                    onClick = {
                        showCalendar = true
                    },
                    modifier = Modifier
                        .height(46.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Datum rođenja")
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "registration icon"
                        )
                    }


                }
                Box(modifier = Modifier.size(size = 20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Box(modifier = Modifier.size(size = 6.dp))
                        DropdownButton(
                            hintText = "\uD83C\uDDF7\uD83C\uDDF8 Srbija (+381)",
                            items = listOf("\uD83C\uDDF7\uD83C\uDDF8 Srbija (+381)"),
                            disabled = listOf(),
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )
                    }
                    Box(modifier = Modifier.size(size = 10.dp))
                    OutlinedInputField("Broj telefona", modifier = Modifier.fillMaxWidth())


                }
                Box(modifier = Modifier.size(size = 20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Box(modifier = Modifier.size(size = 6.dp))
                        DropdownButton(
                            hintText = "Dokument",
                            items = listOf("Lična karta", "Pasoš", "Vozačka dozvola"),
                            disabled = listOf(),
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )
                    }
                    Box(modifier = Modifier.size(size = 10.dp))
                    OutlinedInputField("Broj isprave", modifier = Modifier.fillMaxWidth())
                }
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Više informacija", color = MaterialTheme.colors.primary)
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "registration icon",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colors.primary
                    )
                }

                if (showCalendar)
                    Popup(onDismissRequest = { showCalendar = false }) {
                        Surface(color = MaterialTheme.colors.primaryVariant) {
                            DatePicker()
                        }
                    }
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Button(
                        onClick = {
                            // do something here
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Registrujte se")
                    }
                    Box(modifier = Modifier.size(size = 10.dp))
                    Text(
                        text = "Imate nalog? Prijavite se.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
