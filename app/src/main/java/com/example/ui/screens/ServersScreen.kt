package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.ServerEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowingButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.FeatherCyan
import com.example.ui.theme.FeatherPurple
import com.example.ui.theme.FeatherPurpleBright
import com.example.ui.theme.PvpGreen
import com.example.ui.theme.PvpOrange
import com.example.ui.theme.PvpRed
import com.example.ui.theme.PvpYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.GameLauncherHelper

@Composable
fun ServersScreen(
    servers: List<ServerEntity>,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onAddServer: (name: String, address: String, port: Int, version: String, motd: String) -> Unit,
    onUpdateServer: (ServerEntity) -> Unit,
    onDeleteServer: (ServerEntity) -> Unit,
    onToggleFavorite: (ServerEntity) -> Unit,
    onRefreshPings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var serverToEdit by remember { mutableStateOf<ServerEntity?>(null) }
    var serverToDelete by remember { mutableStateOf<ServerEntity?>(null) }

    val filteredServers = remember(servers, searchQuery) {
        if (searchQuery.isBlank()) servers
        else {
            servers.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.address.contains(searchQuery, ignoreCase = true) ||
                    it.motd.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Bedrock Servers",
                    subtitle = "Manage & ping Minecraft Bedrock networks",
                    trailingBadge = "${servers.size} SAVED",
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onRefreshPings,
                    modifier = Modifier.testTag("refresh_pings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Pings",
                        tint = FeatherCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChanged,
                placeholder = { Text("Search servers by name or address...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChanged("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
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
                    .testTag("server_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredServers, key = { it.id }) { server ->
                    ServerCardItem(
                        server = server,
                        onPlay = {
                            GameLauncherHelper.connectToServer(
                                context,
                                server.name,
                                server.address,
                                server.port
                            )
                        },
                        onCopy = {
                            GameLauncherHelper.copyToClipboard(context, "${server.address}:${server.port}")
                        },
                        onFavorite = { onToggleFavorite(server) },
                        onEdit = { serverToEdit = server },
                        onDelete = { serverToDelete = server }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Floating Action Button to Add Server
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = FeatherPurple,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_server_fab")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Server")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Server", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    // Add Server Dialog
    if (showAddDialog) {
        ServerEditorDialog(
            title = "Add Bedrock Server",
            initialName = "",
            initialAddress = "",
            initialPort = 19132,
            initialVersion = "1.21.x",
            initialMotd = "Bedrock Network",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, address, port, version, motd ->
                onAddServer(name, address, port, version, motd)
                showAddDialog = false
            }
        )
    }

    // Edit Server Dialog
    serverToEdit?.let { server ->
        ServerEditorDialog(
            title = "Edit Server: ${server.name}",
            initialName = server.name,
            initialAddress = server.address,
            initialPort = server.port,
            initialVersion = server.version,
            initialMotd = server.motd,
            onDismiss = { serverToEdit = null },
            onConfirm = { name, address, port, version, motd ->
                onUpdateServer(
                    server.copy(
                        name = name,
                        address = address,
                        port = port,
                        version = version,
                        motd = motd
                    )
                )
                serverToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    serverToDelete?.let { server ->
        AlertDialog(
            onDismissRequest = { serverToDelete = null },
            title = { Text("Remove Server", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete '${server.name}' (${server.address}:${server.port}) from your saved servers?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteServer(server)
                        serverToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { serverToDelete = null }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF181B29)
        )
    }
}

@Composable
private fun ServerCardItem(
    server: ServerEntity,
    onPlay: () -> Unit,
    onCopy: () -> Unit,
    onFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("server_card_${server.id}"),
        glowEffect = server.isFavorite
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onFavorite,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (server.isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = "Favorite",
                            tint = if (server.isFavorite) PvpYellow else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Text(
                            text = server.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${server.address}:${server.port}",
                            fontSize = 11.sp,
                            color = FeatherCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Ping Latency Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF141724))
                        .border(
                            1.dp,
                            when {
                                server.pingMs in 1..40 -> PvpGreen
                                server.pingMs in 41..90 -> PvpYellow
                                else -> PvpOrange
                            },
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        server.pingMs in 1..40 -> PvpGreen
                                        server.pingMs in 41..90 -> PvpYellow
                                        else -> PvpOrange
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (server.pingMs > 0) "${server.pingMs}ms" else "ONLINE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            if (server.motd.isNotEmpty()) {
                Text(
                    text = server.motd,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy IP", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                    if (!server.isDefault) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = PvpRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                GlowingButton(
                    text = "PLAY",
                    icon = Icons.Default.PlayArrow,
                    onClick = onPlay,
                    modifier = Modifier.height(38.dp),
                    testTag = "play_server_${server.id}"
                )
            }
        }
    }
}

@Composable
private fun ServerEditorDialog(
    title: String,
    initialName: String,
    initialAddress: String,
    initialPort: Int,
    initialVersion: String,
    initialMotd: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, address: String, port: Int, version: String, motd: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var address by remember { mutableStateOf(initialAddress) }
    var portStr by remember { mutableStateOf(initialPort.toString()) }
    var version by remember { mutableStateOf(initialVersion) }
    var motd by remember { mutableStateOf(initialMotd) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Server Name") },
                    placeholder = { Text("e.g. My Survival Bedrock") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Server Address / IP") },
                    placeholder = { Text("e.g. play.myserver.net") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = portStr,
                        onValueChange = { portStr = it },
                        label = { Text("Port") },
                        placeholder = { Text("19132") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = version,
                        onValueChange = { version = it },
                        label = { Text("Version") },
                        placeholder = { Text("1.21.x") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = motd,
                    onValueChange = { motd = it },
                    label = { Text("Description / MOTD") },
                    placeholder = { Text("Bedrock custom server") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val port = portStr.toIntOrNull() ?: 19132
                    if (address.isNotBlank()) {
                        onConfirm(name, address, port, version, motd)
                    }
                }
            ) {
                Text("Save Server")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color(0xFF181B29)
    )
}
