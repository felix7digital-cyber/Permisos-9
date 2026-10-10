package com.forge.prueba_3_1
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaPermisos()
                }
            }
        }
    }
}

@Composable
fun PantallaPermisos() {
    val context = LocalContext.current
    var tick by remember { mutableStateOf(0) }

    // FIX: listOf<String>() en lugar de buildList { } con comas
    val permisosRuntime = remember {
        listOf<String>(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    val launcherMultiple = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> tick++ }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "prueba_3_1",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Gestor de permisos · Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace
        )

        HorizontalDivider(Modifier.padding(vertical = 6.dp))

        Text(
            text = "Paso 1 · Permisos estándar (diálogo del sistema)",
            style = MaterialTheme.typography.titleSmall
        )
        Button(
            onClick = {
                if (permisosRuntime.isEmpty()) {
                    tick++
                } else {
                    launcherMultiple.launch(permisosRuntime.toTypedArray())
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔐 Conceder ${permisosRuntime.size} permisos estándar")
        }

        if (permisosRuntime.isEmpty()) {
            Text(
                text = "(No se declararon permisos runtime para esta app)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = "Paso 2 · Permisos especiales (abren Ajustes)",
            style = MaterialTheme.typography.titleSmall
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BotonSettings(
                texto = "🪟 Ventana flotante (SYSTEM_ALERT_WINDOW)",
                accion = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                    )
                }
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BotonSettings(
                texto = "📂 Acceso a todos los archivos (MANAGE_EXTERNAL_STORAGE)",
                accion = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                    )
                }
            )
        }

        BotonSettings(
            texto = "🔋 Ignorar optimización de batería",
            accion = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:${context.packageName}")
                    )
                )
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            BotonSettings(
                texto = "📦 Instalar apps desconocidas",
                accion = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:${context.packageName}")
                        )
                    )
                }
            )
        }

        BotonSettings(
            texto = "⚙️  Ajustes generales de la app",
            accion = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")
                    )
                )
            }
        )

        Spacer(Modifier.height(10.dp))
        HorizontalDivider(Modifier.padding(vertical = 6.dp))

        Text(
            text = "Estado real de cada permiso",
            style = MaterialTheme.typography.titleSmall
        )

        OutlinedButton(
            onClick = { tick++ },
            modifier = Modifier.fillMaxWidth()
        ) { Text("🔄 Refrescar estado") }

        val _tickRefresh = tick

        permisosRuntime.forEach { perm ->
            val granted = ContextCompat.checkSelfPermission(context, perm) ==
                    PackageManager.PERMISSION_GRANTED
            FilaEstado(
                etiqueta = perm.substringAfterLast('.'),
                ok = granted
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val overlayOk = Settings.canDrawOverlays(context)
            FilaEstado("SYSTEM_ALERT_WINDOW", overlayOk)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val storageOk = android.os.Environment.isExternalStorageManager()
            FilaEstado("MANAGE_EXTERNAL_STORAGE", storageOk)
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(Modifier.padding(vertical = 6.dp))
        Text(
            text = "Acciones de la app original",
            style = MaterialTheme.typography.titleSmall
        )
            Button(
                onClick = { /* TODO: acción de "1. navigator.mediaDevices.getUserMedia (CAMERA / AUDIO)" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("1. navigator.mediaDevices.getUserMedia (CAMERA / AUDIO)") }

            Button(
                onClick = { /* TODO: acción de "2. window.AudioContext / MediaRecorder" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("2. window.AudioContext / MediaRecorder") }

            Button(
                onClick = { /* TODO: acción de "3. navigator.geolocation (FINE / COARSE LOCATION)" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("3. navigator.geolocation (FINE / COARSE LOCATION)") }

            Button(
                onClick = { /* TODO: acción de "4. window.addEventListener('deviceorientation')" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("4. window.addEventListener('deviceorientation')") }

            Button(
                onClick = { /* TODO: acción de "5. window.addEventListener('devicemotion')" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("5. window.addEventListener('devicemotion')") }

            Button(
                onClick = { /* TODO: acción de "6. navigator.vibrate (VIBRATE)" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("6. navigator.vibrate (VIBRATE)") }

            Button(
                onClick = { /* TODO: acción de "7. navigator.bluetooth.requestDevice" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("7. navigator.bluetooth.requestDevice") }

            Button(
                onClick = { /* TODO: acción de "8. navigator.usb.requestDevice" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("8. navigator.usb.requestDevice") }

            Button(
                onClick = { /* TODO: acción de "9. window.NDEFReader (NFC)" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("9. window.NDEFReader (NFC)") }

            Button(
                onClick = { /* TODO: acción de "10. navigator.serial.requestPort" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("10. navigator.serial.requestPort") }

            Button(
                onClick = { /* TODO: acción de "11. navigator.hid.requestDevice" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("11. navigator.hid.requestDevice") }

            Button(
                onClick = { /* TODO: acción de "12. navigator.connection / onLine" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("12. navigator.connection / onLine") }

            Button(
                onClick = { /* TODO: acción de "13. navigator.storage.estimate / persistence" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("13. navigator.storage.estimate / persistence") }

            Button(
                onClick = { /* TODO: acción de "14. window.showDirectoryPicker" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("14. window.showDirectoryPicker") }

            Button(
                onClick = { /* TODO: acción de "15. window.showOpenFilePicker" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("15. window.showOpenFilePicker") }

            Button(
                onClick = { /* TODO: acción de "16. window.showSaveFilePicker" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("16. window.showSaveFilePicker") }

            Button(
                onClick = { /* TODO: acción de "17. window.indexedDB" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("17. window.indexedDB") }

            Button(
                onClick = { /* TODO: acción de "18. navigator.contacts.select" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("18. navigator.contacts.select") }

            Button(
                onClick = { /* TODO: acción de "19. navigator.clipboard.readText" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("19. navigator.clipboard.readText") }

            Button(
                onClick = { /* TODO: acción de "20. navigator.clipboard.writeText" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("20. navigator.clipboard.writeText") }

            Button(
                onClick = { /* TODO: acción de "21. navigator.share" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("21. navigator.share") }

            Button(
                onClick = { /* TODO: acción de "22. navigator.credentials.get" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("22. navigator.credentials.get") }

            Button(
                onClick = { /* TODO: acción de "23. Notification.requestPermission" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("23. Notification.requestPermission") }

            Button(
                onClick = { /* TODO: acción de "24. navigator.serviceWorker.register" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("24. navigator.serviceWorker.register") }

            Button(
                onClick = { /* TODO: acción de "25. window.speechSynthesis" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("25. window.speechSynthesis") }

            Button(
                onClick = { /* TODO: acción de "26. window.webkitSpeechRecognition" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("26. window.webkitSpeechRecognition") }

            Button(
                onClick = { /* TODO: acción de "27. navigator.wakeLock.request" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("27. navigator.wakeLock.request") }

            Button(
                onClick = { /* TODO: acción de "28. document.documentElement.requestFullscreen" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("28. document.documentElement.requestFullscreen") }

            Button(
                onClick = { /* TODO: acción de "29. navigator.xr.requestSession" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("29. navigator.xr.requestSession") }

            Button(
                onClick = { /* TODO: acción de "30. navigator.presentation" */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("30. navigator.presentation") }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun BotonSettings(texto: String, accion: () -> Unit) {
    OutlinedButton(
        onClick = accion,
        modifier = Modifier.fillMaxWidth()
    ) { Text(texto, fontSize = 12.sp) }
}

@Composable
fun FilaEstado(etiqueta: String, ok: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (ok) "✓" else "✗",
            color = if (ok) Color(0xFF34D399) else Color(0xFFF87171),
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            modifier = Modifier.width(24.dp)
        )
        Text(
            text = etiqueta,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = if (ok) Color(0xFF94A3B8) else Color(0xFFE2E8F0)
        )
    }
}
