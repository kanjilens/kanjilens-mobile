package com.example.kanjilens.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kanjilens.ui.theme.AppSecondary
import com.example.kanjilens.ui.theme.AppTextMuted

enum class AppTab {
    HOME,
    SETTINGS,
}

@Composable
fun AppBottomBar(
    selectedTab: AppTab,
    onHome: () -> Unit,
    onCamera: () -> Unit,
    onSettings: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color.Transparent)
                ) {
                    val indicatorOffset = if (selectedTab == AppTab.HOME) 34.dp else (-34).dp
                    Box(
                        modifier = Modifier
                            .align(if (selectedTab == AppTab.HOME) Alignment.CenterStart else Alignment.CenterEnd)
                            .offset(x = indicatorOffset)
                            .width(96.dp)
                            .height(3.dp)
                            .background(AppSecondary)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavItem(
                        icon = { Icon(Icons.Outlined.Home, null) },
                        label = "Home",
                        selected = selectedTab == AppTab.HOME,
                        onClick = onHome
                    )
                    NavItem(
                        icon = { Icon(Icons.Outlined.AutoStories, null) },
                        label = "Descobertos",
                        onClick = {}
                    )
                    Spacer(modifier = Modifier.width(72.dp))
                    NavItem(
                        icon = { Icon(Icons.Outlined.Search, null) },
                        label = "Enciclopedia",
                        onClick = {}
                    )
                    NavItem(
                        icon = { Icon(Icons.Outlined.Settings, null) },
                        label = "Config.",
                        selected = selectedTab == AppTab.SETTINGS,
                        onClick = onSettings
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-26).dp)
                .size(74.dp)
                .clip(CircleShape)
                .background(AppSecondary)
                .clickable(onClick = onCamera),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CenterFocusStrong,
                contentDescription = "Abrir camera",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: @Composable () -> Unit,
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    val tint = if (selected) AppSecondary else AppTextMuted
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box {
            CompositionLocalProvider(LocalContentColor provides tint, content = icon)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = tint
        )
    }
}
