package com.blackbyte.skucise.components


import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.blackbyte.skucise.data.Filter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackbyte.skucise.components.OutlinedInputField
import com.blackbyte.skucise.components.OutlinedPasswordField
import com.blackbyte.skucise.components.SearchField
import com.blackbyte.skucise.data.listOfCities
import com.blackbyte.skucise.ui.theme.Purple200
import com.blackbyte.skucise.ui.theme.Purple500
import com.blackbyte.skucise.ui.theme.SkuciSeTheme
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.blackbyte.skucise.ui.theme.LightPurple
import kotlin.math.ln

@Composable
fun FilterChipAmenities(
    filters:List<Filter>
){
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ){

        Column(
            modifier = Modifier
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            FlowRow(
                // position of chips
                mainAxisAlignment = FlowMainAxisAlignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 16.dp)
                    //.padding(horizontal = 5.dp)
            ){
                filters.forEach{filter ->
                    ChipAmenity(
                        filter = filter,
                        modifier = Modifier.padding(end = 4.dp,bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.size(13.dp))

                }
            }
        }
    }
}

@Composable
fun ChipAmenity(
    filter: Filter,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium
){
    val  (selected, setSelected) = filter.enabled
    val backgroundColor by  animateColorAsState(
        if(selected) MaterialTheme.colors.primary else Color.White
    )
    val border = Modifier.fadeInDiagonalGradientBorder(
        showBorder = !selected,
        colors = listOf(
            Color.LightGray,  Color.LightGray
        ),
        shape = shape,

        )
    val textColor by animateColorAsState(
        if(selected) Color.White else
            Color.Black
    )

    // surface
    ChipSurface(
        modifier = modifier.height(40.dp).width(150.dp),
        color = backgroundColor,
        contentColor = textColor,
        shape = shape,
        elevation = 2.dp
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        val pressed by interactionSource.collectIsPressedAsState()
        val backgroundPressed =
            if (pressed) {
                Modifier.offsetGradientBackground(
                    listOf(
                        Color.LightGray, Color.LightGray
                    ),
                    200f,
                    0f
                )
            } else {
                Modifier.background(Color.Transparent)
            }
        Box(
            modifier = Modifier
                .toggleable(
                    value = selected,
                    onValueChange = setSelected,
                    interactionSource = interactionSource,
                    indication = null
                )
                .then(backgroundPressed)
                .then(border)
                .height(40.dp)
                .width(150.dp) // size of box
        ) {
            Text(
                text = filter.name,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .fillMaxHeight()
            )
        }

    }
}

