package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@Composable
fun CameraXScannerView(
  modifier: Modifier = Modifier,
  onImageCaptured: (Bitmap?) -> Unit,
  onSimulatedCapture: () -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasCameraPermission = isGranted
    if (!isGranted) {
      Toast.makeText(context, "إذن الكاميرا مطلوب لمسح النقوش الأثرية", Toast.LENGTH_SHORT).show()
    }
  }

  var isTorchOn by remember { mutableStateOf(false) }
  var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
  var cameraInstance by remember { mutableStateOf<Camera?>(null) }
  var imageCaptureInstance by remember { mutableStateOf<ImageCapture?>(null) }
  var isCapturing by remember { mutableStateOf(false) }

  // Animated Scanner Laser Line
  val infiniteTransition = rememberInfiniteTransition(label = "LaserTransition")
  val scanLaserProgress by infiniteTransition.animateFloat(
    initialValue = 0.05f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "LaserProgress"
  )

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header Info
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.size(34.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              "الماسح الضوئي الميداني (CameraX OCR)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              "وجّه الكاميرا نحو النقش أو المسلة مع محاذاة السطور",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Live Badge
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (hasCameraPermission) Color(0xFF2E7D32).copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
        ) {
          Text(
            text = if (hasCameraPermission) "الكاميرا نشطة" else "الإذن مطلوب",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (hasCameraPermission) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Viewfinder Viewport
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(280.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF0F0F14))
          .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
      ) {
        if (hasCameraPermission) {
          AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
              val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
              }

              val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
              cameraProviderFuture.addListener({
                try {
                  val cameraProvider = cameraProviderFuture.get()
                  val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                  }

                  val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                  imageCaptureInstance = imageCapture
                  cameraProvider.unbindAll()
                  val cam = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                  )
                  cameraInstance = cam
                } catch (e: Exception) {
                  e.printStackTrace()
                }
              }, ContextCompat.getMainExecutor(ctx))

              previewView
            }
          )

          // Epigraphic Laser Overlay & Alignment Reticle
          Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val strokeWidth = 3.dp.toPx()
            val bracketLength = 28.dp.toPx()
            val margin = 24.dp.toPx()
            val primaryColor = Color(0xFFD4AF37)

            // Top-Left Corner
            drawLine(primaryColor, Offset(margin, margin), Offset(margin + bracketLength, margin), strokeWidth)
            drawLine(primaryColor, Offset(margin, margin), Offset(margin, margin + bracketLength), strokeWidth)

            // Top-Right Corner
            drawLine(primaryColor, Offset(width - margin, margin), Offset(width - margin - bracketLength, margin), strokeWidth)
            drawLine(primaryColor, Offset(width - margin, margin), Offset(width - margin, margin + bracketLength), strokeWidth)

            // Bottom-Left Corner
            drawLine(primaryColor, Offset(margin, height - margin), Offset(margin + bracketLength, height - margin), strokeWidth)
            drawLine(primaryColor, Offset(margin, height - margin), Offset(margin, height - margin - bracketLength), strokeWidth)

            // Bottom-Right Corner
            drawLine(primaryColor, Offset(width - margin, height - margin), Offset(width - margin - bracketLength, height - margin), strokeWidth)
            drawLine(primaryColor, Offset(width - margin, height - margin), Offset(width - margin, height - margin - bracketLength), strokeWidth)

            // Central Horizontal Epigraphic Baseline
            drawLine(
              color = Color(0x55D4AF37),
              start = Offset(margin + 16.dp.toPx(), height * 0.5f),
              end = Offset(width - margin - 16.dp.toPx(), height * 0.5f),
              strokeWidth = 1.5f,
              pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 15f))
            )

            // Animated Laser Line
            val laserY = height * scanLaserProgress
            drawLine(
              brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, Color(0xFF00FFCC), Color(0xFFD4AF37), Color(0xFF00FFCC), Color.Transparent)
              ),
              start = Offset(margin, laserY),
              end = Offset(width - margin, laserY),
              strokeWidth = 3.dp.toPx()
            )
          }

          // In-Viewfinder HUD Controls
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp)
              .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.Black.copy(alpha = 0.6f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00FFCC))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("مسح إبيغرافي نشط", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              // Torch Toggle
              Surface(
                shape = CircleShape,
                color = if (isTorchOn) Color(0xFFD4AF37) else Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                  .size(36.dp)
                  .clickable {
                    cameraInstance?.let { cam ->
                      if (cam.cameraInfo.hasFlashUnit()) {
                        isTorchOn = !isTorchOn
                        cam.cameraControl.enableTorch(isTorchOn)
                      } else {
                        Toast.makeText(context, "فلاش الكاميرا غير متوفر على هذا الجهاز", Toast.LENGTH_SHORT).show()
                      }
                    }
                  }
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "الفلاش",
                    tint = if (isTorchOn) Color.Black else Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }

              // Switch Lens
              Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                  .size(36.dp)
                  .clickable {
                    cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                      CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                      CameraSelector.DEFAULT_BACK_CAMERA
                    }
                  }
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    Icons.Default.FlipCameraAndroid,
                    contentDescription = "تبديل الكاميرا",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        } else {
          // Permission Request Placeholder UI
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              Icons.Default.CameraAlt,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              "يتطلب الماسح الضوئي إذن الكاميرا",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              "التقط صور المسلات والنقوش الأثرية لتحويلها فوراً إلى نص رقمي قابل للتحرير والتحليل",
              fontSize = 11.sp,
              color = Color.LightGray,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("منح إذن الكاميرا الآن")
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Shutter & Capture Action Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Main Capture Button
        Button(
          onClick = {
            if (hasCameraPermission && imageCaptureInstance != null) {
              isCapturing = true
              val executor = ContextCompat.getMainExecutor(context)
              imageCaptureInstance?.takePicture(
                executor,
                object : ImageCapture.OnImageCapturedCallback() {
                  override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = image.toBitmap()
                    image.close()
                    isCapturing = false
                    onImageCaptured(bitmap)
                    Toast.makeText(context, "تم التقاط الصورة بنجاح وجاري فك تشفير النص الإبيغرافي", Toast.LENGTH_SHORT).show()
                  }

                  override fun onError(exception: ImageCaptureException) {
                    isCapturing = false
                    Toast.makeText(context, "تعذر التقاط الصورة: ${exception.message}", Toast.LENGTH_SHORT).show()
                    // Fallback to simulated sample
                    onSimulatedCapture()
                  }
                }
              )
            } else {
              // Trigger simulated capture on emulators/browsers where camera is simulated
              onSimulatedCapture()
            }
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          shape = RoundedCornerShape(12.dp),
          enabled = !isCapturing
        ) {
          if (isCapturing) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("جاري معالجة الصورة...", fontSize = 12.sp)
          } else {
            Icon(Icons.Default.CenterFocusStrong, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("التقاط ومسح النقش الآن", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Quick Inscription Sample Picker (Simulation Fallback for testing)
        FilledTonalButton(
          onClick = {
            onSimulatedCapture()
          },
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("عينة نقش", fontSize = 11.sp)
        }
      }
    }
  }
}
