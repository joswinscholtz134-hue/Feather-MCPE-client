package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ModCategory
import com.example.data.model.ModEntity
import com.example.ui.components.FeatherSwitch
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.FeatherCyan
import com.example.ui.theme.FeatherPurple
import com.example.ui.theme.PvpGreen
import com.example.ui.theme.PvpYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModManagerScreen(
    mods: List<ModEntity>,
    searchQuery: String,
    selectedCategory: ModCategory?,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (ModCategory?) -> Unit,
    onToggleMod: (ModEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredMods = remember(mods, searchQuery, selectedCategory) {
        mods.filter { mod ->
            val matchesCategory = selectedCategory == null || mod.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                mod.name.contains(searchQuery, ignoreCase = true) ||
                mod.description.contains(searchQuery, ignoreCase = true) ||
                mod.badgeText.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val enabledCount = mods.count { it.isEnabled }
    val perfCount = mods.count { it.category == ModCategory.PERFORMANCE && it.isEnabled }
    val pvpCount = mods.count { it.category == ModCategory.PVP && it.isEnabled }
    val utilCount = mods.count { it.category == ModCategory.UTILITY && it.isEnabled }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header stats
        SectionHeader(
            title = "Feather Mod Manager",
            subtitle = "$enabledCount of ${mods.size} client enhancements active",
            trailingBadge = "$enabledCount ACTIVE"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text("Search Performance, PvP, Utility mods...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF151826),
                unfocusedContainerColor = Color(0xFF131522),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mod_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryChip(
                title = "All",
                badge = "${mods.size}",
                isSelected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                modifier = Modifier.weight(1f)
            )
            CategoryChip(
                title = "Performance",
                badge = "$perfCount",
                icon = "⚡",
                isSelected = selectedCategory == ModCategory.PERFORMANCE,
                onClick = { onCategorySelected(ModCategory.PERFORMANCE) },
                modifier = Modifier.weight(1.3f)
            )
            CategoryChip(
                title = "PvP",
                badge = "$pvpCount",
                icon = "⚔️",
                isSelected = selectedCategory == ModCategory.PVP,
                onClick = { onCategorySelected(ModCategory.PVP) },
                modifier = Modifier.weight(1f)
            )
            CategoryChip(
                title = "Utility",
                badge = "$utilCount",
                icon = "🛠️",
                isSelected = selectedCategory == ModCategory.UTILITY,
                onClick = { onCategorySelected(ModCategory.UTILITY) },
                modifier = Modifier.weight(1.1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mod Cards List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredMods, key = { it.id }) { mod ->
                ModCardItem(
                    mod = mod,
                    onToggle = { onToggleMod(mod) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    badge: String,
    icon: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                else Color(0xFF141724)
            )
            .border(
                BorderStroke(
                    1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else BorderSubtle
                ),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Text(text = icon, fontSize = 11.sp)
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else TextSecondary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = badge,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) FeatherCyan else TextMuted
            )
        }
    }
}

@Composable
private fun ModCardItem(
    mod: ModEntity,
    onToggle: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableStateOf(mod.customValue) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mod_card_${mod.id}"),
        glowEffect = mod.isEnabled
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (mod.isEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else Color(0xFF1F2335)
                            )
                            .border(
                                1.dp,
                                if (mod.isEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                else BorderSubtle,
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = mod.iconName, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.padding(end = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mod.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        if (mod.badgeText.isNotEmpty()) {
                            Text(
                                text = mod.badgeText,
                                fontSize = 10.sp,
                                color = if (mod.isEnabled) FeatherCyan else TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FeatherSwitch(
                        checked = mod.isEnabled,
                        onCheckedChange = { onToggle() },
                        modifier = Modifier.testTag("toggle_${mod.id}")
                    )

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand options",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = mod.description,
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp)
            )

            // Expanded Settings Sub-sheet
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF121420))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Intensity / HUD Opacity",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${(sliderValue * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FeatherCyan
                        )
                    }

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = FeatherCyan,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color(0xFF222638)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Category: ${mod.category.name.lowercase().capitalize()}",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Auto-synced with Bedrock Options",
                            fontSize = 9.sp,
                            color = PvpGreen
                        )
                    }
                }
            }
        }
    }
}
