package com.example.skycast.notifications

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.skycast.R
import com.example.skycast.model.pojo.MyAlert
import com.example.skycast.screens.notifications.WeatherAlertBottomSheet
import com.example.skycast.ui.theme.PrimaryColor
import com.example.skycast.ui.theme.TertiaryColor
import com.example.skycast.viewmodel.NotificationsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: NotificationsViewModel
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val scheduledAlerts by viewModel.scheduledAlerts.collectAsState()


    LaunchedEffect(Unit) {
        while(true) {
            viewModel.updateAlerts() // Add this method to NotificationsViewModel
            kotlinx.coroutines.delay(1000) // Refresh every second
        }
    }
    // Check for overlay permission
    CheckOverlayPermission()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(PrimaryColor.value),
                            Color(TertiaryColor.value)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Text(text = "Weather Alerts", color = Color.White,style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier.fillMaxSize(),

            ) {
                if (scheduledAlerts.isNotEmpty()) {
                    viewModel.updateAlerts()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(scheduledAlerts) { index ->
                            val alert = index
                            SwipeableNotificationCard(
                                message = formatTimeRemainingMessage(alert),
                                timestamp = formatTimestamp(alert.startTime ?: 0),
                                soundEnabled = alert.useDefaultSound ?: false,
                                onDelete = { viewModel.cancelAlert(alert.id.toString()) },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End
                    ) {
                        IconButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier
                                .size(90.dp)
                                .padding(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.location),
                                contentDescription = stringResource(id = R.string.go_to_location_screen),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    EmptyNotificationsMessage({ showBottomSheet =true})
                }
            }
        }

    }

    if (showBottomSheet) {
        WeatherAlertBottomSheet(
            onDismiss = { showBottomSheet = false },
            viewModel = viewModel
        )
    }
}

private fun formatTimeRemainingMessage(alert: MyAlert): String {
    val remaining = (alert.startTime?:0 + alert?.duration!!) - System.currentTimeMillis()
    val hours = remaining / (1000 * 60 * 60)
    val minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60)

    return when {
        hours > 0 -> "Alert in ${hours}h ${minutes}m"
        minutes > 0 -> "Alert in ${minutes}m"
        else -> "Alert due now"
    }
}

private fun formatTimestamp(time: Long): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(time))
}

@Composable
private fun EmptyNotificationsMessage(  showBottomSheet: () -> Unit) {
   Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(PrimaryColor.value),
                        Color(TertiaryColor.value)
                    )
                )
            )
            .padding(16.dp),

    ) {
        Image(
            painter = painterResource(id = R.drawable.bell),
            contentDescription = "Weather House",
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.Text(
            text = "No active notifications",
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                onClick = { showBottomSheet() },
                modifier = Modifier
                    .size(80.dp)
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = stringResource(id = R.string.go_to_location_screen),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableNotificationCard(
    message: String,
    timestamp: String,
    soundEnabled: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                showDeleteDialog = true
            }
            false
        }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { androidx.compose.material3.Text("Delete Alert") },
            text = { androidx.compose.material3.Text("Are you sure you want to delete this alert?") },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                ) {
                    androidx.compose.material3.Text("Delete")
                }
            },
            dismissButton = {
                androidx.compose.material3.Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                    )
                ) {
                    androidx.compose.material3.Text("Cancel")
                }
            }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {},)
    {
        NotificationCard(
            message = message,
            timestamp = timestamp,
            soundEnabled = soundEnabled,
            modifier = modifier
        )
    }

}

@Composable
private fun NotificationCard(
    message: String,
    timestamp: String,
    soundEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    text = "Weather Alert",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = if (soundEnabled)
                        Icons.Default.Notifications
                    else
                        Icons.Default.Notifications,
                    contentDescription = if (soundEnabled)
                        "Sound Enabled"
                    else
                        "Sound Disabled",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            androidx.compose.material3.Text(
                text = message,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            androidx.compose.material3.Text(
                text = timestamp,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }

}
@Composable
fun CheckOverlayPermission() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var permissionRequested by remember { mutableStateOf(false) }

    // Launch intent to settings if permission not granted
    LaunchedEffect(Unit) {
        if (!Settings.canDrawOverlays(context) && !permissionRequested) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            permissionRequested = true
        }
    }

    // Observe lifecycle to re-check permission on resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (Settings.canDrawOverlays(context)) {

                    Log.d("OverlayPermission", "Permission granted")
                } else {

                    Log.d("OverlayPermission", "Permission still denied")
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


