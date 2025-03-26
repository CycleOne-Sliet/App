package com.cycleone.cycleoneapp.ui.components

import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cycleone.cycleoneapp.services.CachedNetworkClient
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.disableRotation
import ovh.plrapps.mapcompose.api.onTap
import ovh.plrapps.mapcompose.api.shouldLoopScale
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.MapUI
import ovh.plrapps.mapcompose.ui.state.MapState
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.tan

class MapDisplay {

    val levelCount = 20
    private val tileStreamProvider = TileStreamProvider { row, col, zoomLvl ->
        try {
            return@TileStreamProvider CachedNetworkClient.get("https://tile.openstreetmap.org/$zoomLvl/$col/$row.png").body?.byteStream()
        } catch (e: Error) {
            Log.e("tileStreamProvider", e.toString())
            null
        }
    }

    val state = MapState(
        levelCount, (128 * 2.pow(levelCount)),
        (128 * 2.pow(levelCount)), workerCount = 32
    ) {
        scroll(0.0, 0.0)
        scale(2.0f / 2.pow(levelCount - 1).toFloat())
    }.apply {
        addLayer(tileStreamProvider)
        shouldLoopScale = true
        disableRotation()
        onTap { x, y ->
            Log.d("TapOnMap", "$x, $y")
        }
    }

    fun locationToNormalizedCoords(location: Location): Pair<Double, Double> {

        return Pair(
            (location.longitude + 180) / (360),
            //https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Implementations
            (1 - ln(tan(Math.toRadians(location.latitude)) + 1 / cos(Math.toRadians(location.latitude))) / (Math.PI)) / 2

        )
    }


    @Composable
    @Preview
    fun Create(modifier: Modifier = Modifier) {
        MapUI(modifier, state = state)
    }
}

fun Int.pow(x: Int): Int {
    var result = 1
    for (i in 1..x) {
        result *= this
    }
    return result
}
