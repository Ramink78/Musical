package rk.musical.ui

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import rk.musical.ui.theme.MusicalTheme

val mediaPermission =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

@Composable
fun RationaleWarning(
    onRequest: () -> Unit,
    buttonText: String,
    icon: ImageVector,
    rationaleTitle: String,
    rationaleText: String
) {
    Surface(
        color = AlertDialogDefaults.containerColor,
        tonalElevation = AlertDialogDefaults.TonalElevation,
        shape = AlertDialogDefaults.shape,
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier =
            Modifier
                .padding(24.dp)
        ) {
            Box(
                modifier =
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = rationaleTitle,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = rationaleText,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRequest) {
                Text(text = buttonText, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequiredMediaPermission(
    permissionState: PermissionState,
    rationalContent: @Composable () -> Unit,
    grantedContent: @Composable () -> Unit,
    deniedContent: @Composable () -> Unit
) {
    when {
        permissionState.status.isGranted -> grantedContent()
        permissionState.status.shouldShowRationale -> rationalContent()
        else -> {
            LaunchedEffect(key1 = Unit) {
                permissionState.launchPermissionRequest()
            }
            deniedContent()
        }
    }
}

@Preview
@Composable
fun RationaleWarningPreview() {
    MusicalTheme(darkTheme = true) {
        RationaleWarning(
            onRequest = {},
            buttonText = "Grant Access",
            rationaleText = "Rationale text",
            icon = Icons.Rounded.MusicNote,
            rationaleTitle = "Media Access"
        )
    }
}
