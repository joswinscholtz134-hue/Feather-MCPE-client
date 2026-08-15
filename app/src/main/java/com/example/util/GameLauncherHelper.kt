package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.AddonEntity
import java.io.File

object GameLauncherHelper {
    const val MINECRAFT_PACKAGE = "com.mojang.minecraftpe"
    const val MINECRAFT_PREVIEW_PACKAGE = "com.mojang.minecraftpreview"

    fun isMinecraftInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return runCatching {
            pm.getPackageInfo(MINECRAFT_PACKAGE, 0)
            true
        }.getOrElse {
            runCatching {
                pm.getPackageInfo(MINECRAFT_PREVIEW_PACKAGE, 0)
                true
            }.getOrDefault(false)
        }
    }

    fun launchMinecraft(context: Context): Boolean {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(MINECRAFT_PACKAGE)
            ?: pm.getLaunchIntentForPackage(MINECRAFT_PREVIEW_PACKAGE)

        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return try {
                context.startActivity(launchIntent)
                true
            } catch (e: Exception) {
                Toast.makeText(context, "Please install Minecraft from the official store.", Toast.LENGTH_LONG).show()
                false
            }
        }

        Toast.makeText(context, "Please install Minecraft from the official store.", Toast.LENGTH_LONG).show()
        return false
    }

    fun connectToServer(context: Context, serverName: String, address: String, port: Int) {
        if (!isMinecraftInstalled(context)) {
            Toast.makeText(context, "Please install Minecraft from the official store.", Toast.LENGTH_LONG).show()
            copyToClipboard(context, "$address:$port")
            return
        }

        val sanitizedName = Uri.encode(serverName)
        val uriString = "minecraft://?addExternalServer=$sanitizedName:$address:$port"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setPackage(MINECRAFT_PACKAGE)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            if (!launchMinecraft(context)) {
                Toast.makeText(context, "Please install Minecraft from the official store.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun installAddon(context: Context, addon: AddonEntity) {
        if (!isMinecraftInstalled(context)) {
            Toast.makeText(context, "Please install Minecraft from the official store.", Toast.LENGTH_LONG).show()
            return
        }

        val storageDir = File(context.filesDir, "imported_addons")
        val file = File(storageDir, addon.fileName)

        if (!file.exists()) {
            Toast.makeText(context, "Pack file ready for Bedrock auto-import: ${addon.name}", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/octet-stream")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage(MINECRAFT_PACKAGE)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val uri = Uri.fromFile(file)
                val chooser = Intent.createChooser(Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "*/*")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }, "Import into Minecraft Bedrock")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            } catch (ex: Exception) {
                Toast.makeText(context, "Please install Minecraft from the official store.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Feather Client Server", text)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "Copied '$text' to clipboard!", Toast.LENGTH_SHORT).show()
    }
}
