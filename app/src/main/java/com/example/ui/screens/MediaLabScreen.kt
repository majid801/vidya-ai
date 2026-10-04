package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.EduMentorViewModel

@Composable
fun MediaLabScreen(
    viewModel: EduMentorViewModel,
    modifier: Modifier = Modifier
) {
    val prompt by viewModel.mediaPrompt.collectAsState()
    val isGenerating by viewModel.isGeneratingMedia.collectAsState()
    val statusMsg by viewModel.mediaStatusMessage.collectAsState()
    val resultImageB64 by viewModel.mediaResultImageBase64.collectAsState()
    val selectedRes by viewModel.selectedImageResolution.collectAsState()
    val selectedAspect by viewModel.selectedVideoAspect.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Create/Edit Diagram, 1: High-Res Image (1K/2K/4K), 2: Veo Video (16:9/9:16)
    var uploadedPhotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    uploadedPhotoBitmap = BitmapFactory.decodeStream(stream)
                }
            } catch (_: Exception) {}
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "AI Educational Media Lab",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Generate visual diagrams, 4K scientific illustrations, and Veo video concept simulations.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Diagrams", style = MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("High-Res (4K)", style = MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Veo Video", style = MaterialTheme.typography.labelMedium) }
                )
            }
        }

        // Configuration Card based on selected tab
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (selectedTab) {
                        0 -> {
                            Text(
                                text = "Create & Edit Diagrams (gemini-3.1-flash-image-preview)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create educational diagrams (e.g. human heart, ray optics, cell mitosis) or edit an uploaded sketch.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        1 -> {
                            Text(
                                text = "High-Quality Image (gemini-3-pro-image-preview)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Choose Output Resolution Affordance:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("1K", "2K", "4K").forEach { res ->
                                    val isSel = selectedRes == res
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { viewModel.selectedImageResolution.value = res },
                                        label = { Text("$res Ultra", style = MaterialTheme.typography.labelSmall) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                        ),
                                        modifier = Modifier.testTag("res_chip_$res")
                                    )
                                }
                            }
                        }
                        2 -> {
                            Text(
                                text = "Veo Concept Video (veo-3.1-fast-generate-preview)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aspect Ratio (Mandatory 16:9 or 9:16):",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("16:9", "9:16").forEach { aspect ->
                                    val isSel = selectedAspect == aspect
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { viewModel.selectedVideoAspect.value = aspect },
                                        label = {
                                            Text(
                                                if (aspect == "16:9") "16:9 (Landscape)" else "9:16 (Portrait)",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                        ),
                                        modifier = Modifier.testTag("aspect_chip_${aspect.replace(":", "_")}")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Upload optional photo reference
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uploadedPhotoBitmap != null) "Photo attached ✓" else "Attach source photo (optional)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uploadedPhotoBitmap != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = {
                                photoPicker.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier.testTag("media_upload_photo_button")
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (uploadedPhotoBitmap != null) "Change Photo" else "Pick Photo")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Prompt Input
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.mediaPrompt.value = it },
                        placeholder = {
                            Text(
                                when (selectedTab) {
                                    0 -> "e.g. Detailed labeled diagram of a chloroplast during photosynthesis"
                                    1 -> "e.g. 4K high resolution realistic scientific illustration of the Bohr model of atom"
                                    else -> "e.g. Animated physics simulation of parabolic projectile motion with velocity vectors"
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("media_prompt_input"),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Button
                    Button(
                        onClick = {
                            if (prompt.isNotBlank()) {
                                when (selectedTab) {
                                    0 -> viewModel.createOrEditConceptImage(prompt, uploadedPhotoBitmap)
                                    1 -> viewModel.generateHighQualityConceptArt(prompt)
                                    2 -> viewModel.generateVeoConceptVideo(prompt, uploadedPhotoBitmap)
                                }
                            }
                        },
                        enabled = !isGenerating && prompt.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("media_generate_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...")
                        } else {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Default.Videocam else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                when (selectedTab) {
                                    0 -> "Generate Diagram"
                                    1 -> "Render High-Res ($selectedRes)"
                                    else -> "Generate Veo Video ($selectedAspect)"
                                }
                            )
                        }
                    }
                }
            }
        }

        // Status or Result Card
        if (statusMsg.isNotBlank()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = statusMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }

        // Rendered Image Display (if available)
        if (resultImageB64 != null) {
            item {
                val decodedBitmap = remember(resultImageB64) {
                    try {
                        val bytes = Base64.decode(resultImageB64, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    } catch (_: Exception) {
                        null
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("media_result_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Generated AI Visual Asset",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (decodedBitmap != null) {
                            Image(
                                bitmap = decodedBitmap.asImageBitmap(),
                                contentDescription = "Generated Diagram",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            Text(
                                text = resultImageB64 ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Interactive Educational Diagrams Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive Syllabus Concept Visualizer",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time vector rendered scientific diagrams and mathematical coordinate graphs.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    var currentDiagram by remember { mutableStateOf(com.example.ui.components.DiagramType.RAY_OPTICS) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = currentDiagram == com.example.ui.components.DiagramType.RAY_OPTICS,
                            onClick = { currentDiagram = com.example.ui.components.DiagramType.RAY_OPTICS },
                            label = { Text("Ray Optics", style = MaterialTheme.typography.labelSmall) }
                        )
                        FilterChip(
                            selected = currentDiagram == com.example.ui.components.DiagramType.ELECTRIC_CIRCUIT,
                            onClick = { currentDiagram = com.example.ui.components.DiagramType.ELECTRIC_CIRCUIT },
                            label = { Text("Circuit", style = MaterialTheme.typography.labelSmall) }
                        )
                        FilterChip(
                            selected = currentDiagram == com.example.ui.components.DiagramType.PARABOLA_GRAPH,
                            onClick = { currentDiagram = com.example.ui.components.DiagramType.PARABOLA_GRAPH },
                            label = { Text("Graph", style = MaterialTheme.typography.labelSmall) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    com.example.ui.components.EducationalDiagramCanvas(diagramType = currentDiagram)
                }
            }
        }
    }
}
