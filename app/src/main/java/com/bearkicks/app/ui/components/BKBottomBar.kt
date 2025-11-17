package com.bearkicks.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.bearkicks.app.ui.theme.BKTheme

data class BKNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null,
    val badgeCount: Int = 0
)

@Composable
fun BKBottomBar(
    currentRoute: String?,
    onItemSelected: (BKNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val spacing = BKTheme.spacing
    val elevations = BKTheme.elevations
    val pillColor = colors.primary
    val circleSelected = colors.primaryContainer
    val iconSelected = colors.onPrimaryContainer
    val circleUnselected = pillColor
    val iconUnselected = colors.onPrimary
    val noRipple = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg, vertical = spacing.xl),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.98f),
            color = pillColor,
            shape = RoundedCornerShape(32.dp),
            tonalElevation = elevations.level5
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 25.dp, vertical = 15.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BKDefaultNavItems.forEach { item ->
                    val selected = item.route == currentRoute
                    val iconVec = if (selected && item.selectedIcon != null) item.selectedIcon else item.icon

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (selected) circleSelected else circleUnselected)
                                .clickable(
                                    interactionSource = noRipple,
                                    indication = null,
                                    role = Role.Button
                                ) { onItemSelected(item) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVec,
                                contentDescription = item.label,
                                tint = if (selected) iconSelected else iconUnselected
                            )
                        }
                    }
                }
            }
        }
    }
}
val BKDefaultNavItems = listOf(
    BKNavItem(route = "shop", label = "Shop", icon = Icons.Filled.Store),
    BKNavItem(route = "wishlist", label = "Wishlist", icon = Icons.Filled.Favorite),
    BKNavItem(route = "home", label = "Home", icon = Icons.Filled.Home),
    BKNavItem(route = "cart", label = "Cart", icon = Icons.Filled.ShoppingCart),
    BKNavItem(route = "profile", label = "Profile", icon = Icons.Filled.Person)
)