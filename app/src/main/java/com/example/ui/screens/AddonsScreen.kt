package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AddonEntity
import com.example.data.model.AddonType
import com.example.ui.components.FeatherSwitch
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowingButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.FeatherCyan
import com.example.ui.theme.FeatherPurple
import com.example.ui.theme.FeatherPurpleBright
import com.example.ui.theme.PvpGreen
import com.example.ui.theme.PvpRed
import com.example.ui.theme.PvpYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.GameLauncherHelper

@Composable
fun AddonsScreen(
    addons: List<AddonEntity>,
    onImportAddon: (uri: Uri, fileName: String, fileSize: Long) -> Unit,
    onToggleAddon: (AddonEntity) -> Unit,
    onDeleteAddon: (AddonEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var addonToDelete by remember { mutableStateOf<AddonEntity?>(null) }

    // System file picker for .mcpack, .mcaddon, .mcworld, .zip
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val (name, size) = queryFileInfo(context, uri)
            onImportAddon(uri, name, size)
        }
    }

    val enabledCount = addons.count { it.isEnabled }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SectionHeader(
                title = "Add-ons & Packs",
                subtitle = "Manage .mcpack, .mcaddon, and texture packs",
                trailingBadge = "$enabledCount ACTIVE"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Import Action Banner Card
            GlassCard(modifier = Modifier.fillMaxWidth(), glowEffect = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📥", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Import Pack or World",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Supports .mcpack, .mcaddon, .mcworld and .zip files",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    GlowingButton(
                        text = "BROWSE",
                        icon = Icons.Default.FolderZip,
                        onClick = {
                            filePickerLauncher.launch(
                                arrayOf(
                                    "application/*",
                                    "application/zip",
                                    "application/octet-stream",
                                    "*/*"
                                )
                            )
                        },
                        modifier = Modifier.height(42.dp),
                        testTag = "browse_addon_button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Addons list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(addons, key = { it.id }) { addon ->
                    AddonCardItem(
                        addon = addon,
                        onToggle = { onToggleAddon(addon) },
                        onInstall = {
                            GameLauncherHelper.installAddon(context, addon)
                        },
                        onDelete = { addonToDelete = addon }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Delete dialog
    addonToDelete?.let { addon ->
        AlertDialog(
            onDismissRequest = { addonToDelete = null },
            title = { Text("Delete Add-on", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete '${addon.name}'?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAddon(addon)
                        addonToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { addonToDelete = null }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF181B29)
        )
    }
}

@Composable
private fun AddonCardItem(
    addon: AddonEntity,
    onToggle: () -> Unit,
    onInstall: () -> Unit,
    onDelete: () -> Unit
) {
    val formattedSize = remember(addon.fileSizeBytes) {
        val mb = addon.fileSizeBytes / (1024f * 1024f)
        if (mb >= 1f) "%.1f MB".format(mb) else "${(addon.fileSizeBytes / 1024)} KB"
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("addon_card_${addon.id}"),
        glowEffect = addon.isEnabled
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (addon.isEnabled) FeatherPurple.copy(alpha = 0.25f)
                                else Color(0xFF161928)
                            )
                            .border(
                                1.dp,
                                if (addon.isEnabled) FeatherPurple.copy(alpha = 0.6f) else BorderSubtle,
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = addon.iconEmoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = addon.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF222638))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = addon.type.name.replace("_", " "),
                                    fontSize = 9.sp,
                                    color = FeatherCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "v${addon.version} • $formattedSize",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                FeatherSwitch(
                    checked = addon.isEnabled,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.testTag("toggle_addon_${addon.id}")
                )
            }

            Text(
                text = addon.description,
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!addon.isCurated) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = PvpRed, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FeatherPurple.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("⭐ CURATED", fontSize = 9.sp, color = FeatherPurpleBright, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onInstall,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp).testTag("install_addon_${addon.id}")
                ) {
                    Icon(imageVector = Icons.Default.InstallMobile, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (addon.isCurated) "Apply to Bedrock" else "Open in MCPE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun queryFileInfo(context: Context, uri: Uri): Pair<String, Long> {
    var name = "imported_pack.mcpack"
    var size = 0L

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (nameIndex != -1) name = cursor.getString(nameIndex) ?: name
            if (sizeIndex != -1) size = cursor.getLong(sizeIndex)
        }
    }
    return Pair(name, size)
}
