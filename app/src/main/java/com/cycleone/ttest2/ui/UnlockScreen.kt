package com.cycleone.ttest2.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cycleone.ttest2.R

class UnlockScreen(private val controller: NavController) {
    @Composable
    fun StandAreas(modifier: Modifier = Modifier) {
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { controller.popBackStack() }, modifier = Modifier.offset(x = 20.dp)) {
                Image(painter = painterResource(id = R.drawable.left_arrow), "Back", modifier = Modifier.requiredWidth(20.dp))
            }
            Image(
                painter = painterResource(id = R.drawable.cycle2),
                contentDescription = "bike parking-pana 2",
                modifier = Modifier
                    .requiredSize(size = 300.dp).align(Alignment.CenterHorizontally))
            Text(
                text = "Select Cycle To Unlock",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally))
            LazyVerticalGrid(
                verticalArrangement = Arrangement.spacedBy(32.dp),
                columns = GridCells.Fixed(2)

            ) {
                items(8) {
                        index -> CycleButton(num = index + 1)
                }
        }
        }
    }
    @Composable
    fun CycleButton(num: Int) {
        val context = LocalContext.current

        Button(
            modifier = Modifier
                .requiredWidth(width = 120.dp)
                .requiredHeight(height = 54.dp),
            onClick = {
                try {
                    val queue = Volley.newRequestQueue(context)
                    val url = "http://10.10.10.10/?cycleNum=$num"
                    val stringRequest = StringRequest(Request.Method.GET, url,
                        { response -> Log.d("Info", "Success: $response")},
                        { Log.e("ERROR", "Probably not connected to the stand wifi") })
                    queue.add(stringRequest)
                } catch (e: Error) {
                    Log.e("Error", e.toString())
                }
            }
        ) {
            Text(
                text = "Cycle $num",
                style = TextStyle(
                    fontSize = 16.sp
                ),
                modifier = Modifier
            )
        }
    }

}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun StandAreasPreview() {
    UnlockScreen(rememberNavController()).StandAreas(Modifier)
}