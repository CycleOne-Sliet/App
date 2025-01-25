package com.cycleone.cycleoneapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cycleone.cycleoneapp.services.CachedNetworkClient
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.enableRotation
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.MapUI
import ovh.plrapps.mapcompose.ui.state.MapState

class MapDisplay {
    @Composable
    @Preview
    fun Create(modifier: Modifier = Modifier, initialX: Float = 0.5f, initialY: Float = 0.5f) {
        val tileStreamProvider = TileStreamProvider { row, col, zoomLvl ->
            CachedNetworkClient.get("https://tile.openstreetmap.org/$zoomLvl/$col/$row.png").body?.byteStream()
        }
        val state = MapState(4, 4096, 4096).apply {
            addLayer(tileStreamProvider)
            enableRotation()
        }
        MapUI(modifier, state = state)
    }
}