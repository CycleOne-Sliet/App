package com.cycleone.cycleoneapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.MenuItem
import kotlinx.coroutines.launch

@Composable
fun NavHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth().size(320.dp)
            .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "CycleOne", fontSize = 64.sp,
            color = Color.Black)
    }
}

@Composable
fun DrawerBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp, color = Color(0xffff6b35)),
    onItemClick: (MenuItem) -> Unit,

    ) {
    LazyColumn(
        modifier = modifier // Explicitly apply the modifier to LazyColumn
    ) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) } // Move clickable up in the chain
                    .padding(16.dp) // Keep padding at the end
            ) {
                Icon(imageVector = item.icon, contentDescription = item.title,
                    tint = Color(0xffff6b35)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MyNav(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit

    ){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    NavProvider.drawerController = drawerState

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .requiredWidth(320.dp)
                        .requiredHeight(200.dp)
                        .background(Color(0xffff6b35), RoundedCornerShape(topEnd = 16.dp))
                ){
                    NavHeader()
                    HorizontalDivider(thickness = 3.dp)
                }
                Column(
                    modifier = Modifier
                        .requiredWidth(320.dp)
                        .fillMaxHeight()
                        .background(color = Color.Black, RoundedCornerShape( bottomEnd = 16.dp))
                ) {
                    DrawerBody(items = listOf(
                        MenuItem(
                            id = "Home",
                            title = "Home",
                            icon = Icons.Default.Home
                        ),
                        MenuItem(
                            id = "Profile",
                            title = "Profile",
                            icon = Icons.Default.Person
                        )
                    ),
                        modifier = Modifier.padding(8.dp),
                        onItemClick = {
                            if (it.id == "Profile") {
                                navController.navigate("/profilePage")
                            }

                        }
                    )

                }
            }
        },
drawerState = drawerState
    ) {
        Scaffold(

        ) {
            innerPadding ->
        content(innerPadding)
        }

    }
}
