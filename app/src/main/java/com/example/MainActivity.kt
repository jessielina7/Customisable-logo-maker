package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AiBrandingScreen
import com.example.ui.screens.CollectionScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.theme.LogoGeneratorTheme
import com.example.ui.viewmodel.LogoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LogoViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LogoGeneratorTheme {
                val uiState by viewModel.uiState.collectAsState()
                val context = LocalContext.current
                var currentTab by remember { mutableIntStateOf(0) }

                LaunchedEffect(uiState.toastMessage) {
                    uiState.toastMessage?.let { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        viewModel.clearToast()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Logo Generator Studio",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { currentTab = 0 },
                                icon = { Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(22.dp)) },
                                label = { Text("Studio") },
                                modifier = Modifier.testTag("nav_tab_studio")
                            )
                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { currentTab = 1 },
                                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(22.dp)) },
                                label = { Text("AI Studio") },
                                modifier = Modifier.testTag("nav_tab_ai")
                            )
                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { currentTab = 2 },
                                icon = { Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(22.dp)) },
                                label = { Text("Templates") },
                                modifier = Modifier.testTag("nav_tab_templates")
                            )
                            NavigationBarItem(
                                selected = currentTab == 3,
                                onClick = { currentTab = 3 },
                                icon = { Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(22.dp)) },
                                label = { Text("Saved") },
                                modifier = Modifier.testTag("nav_tab_saved")
                            )
                        }
                    }
                ) { innerPadding ->
                    val contentModifier = Modifier.padding(innerPadding)
                    when (currentTab) {
                        0 -> StudioScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            modifier = contentModifier
                        )
                        1 -> AiBrandingScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            onConceptApplied = { currentTab = 0 },
                            modifier = contentModifier
                        )
                        2 -> TemplatesScreen(
                            viewModel = viewModel,
                            onTemplateSelected = { currentTab = 0 },
                            modifier = contentModifier
                        )
                        3 -> CollectionScreen(
                            viewModel = viewModel,
                            onEditLogoSelected = { currentTab = 0 },
                            modifier = contentModifier
                        )
                    }
                }
            }
        }
    }
}
