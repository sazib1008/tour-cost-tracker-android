package com.example.tripzyfrontend.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.model.TourSummary
import com.example.tripzyfrontend.navigation.Screen
import com.example.tripzyfrontend.ui.common.GlassCard
import com.example.tripzyfrontend.ui.common.StatusBadge
import com.example.tripzyfrontend.ui.theme.BalancePositive
import com.example.tripzyfrontend.ui.theme.BrandPrimary
import com.example.tripzyfrontend.ui.theme.GlassBorderTeal
import com.example.tripzyfrontend.ui.theme.MidnightSurfaceContainer
import com.example.tripzyfrontend.ui.theme.NeonTeal
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showJoinDialog by remember { mutableStateOf(false) }
    var joinCodeInput by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateTour.route) },
                containerColor = BrandPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Tour")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "Traveler"}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Manage your tour finances & splits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassCard(
                        shape = RoundedCornerShape(12.dp),
                        borderColor = GlassBorderTeal,
                        onClick = { showJoinDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Key,
                                contentDescription = null,
                                tint = NeonTeal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Join",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = NeonTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = { navController.navigate(Screen.Profile.route) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.15f))
                            .border(1.5.dp, BrandPrimary.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = BrandPrimary
                        )
                    }
                }
            }

            // Body content
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandPrimary)
                    }
                }
                is HomeUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.loadTours() }) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry")
                            }
                        }
                    }
                }
                is HomeUiState.Success -> {
                    if (state.tours.isEmpty()) {
                        EmptyToursView(
                            onCreateClick = { navController.navigate(Screen.CreateTour.route) },
                            onJoinClick = { showJoinDialog = true }
                        )
                    } else {
                        val activeTours = state.tours.filter { it.status == TourStatus.ACTIVE }
                        val featuredTour = activeTours.firstOrNull() ?: state.tours.first()

                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Quick Action Buttons Row
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    QuickActionItem(
                                        icon = Icons.Default.Add,
                                        label = "New Tour",
                                        onClick = { navController.navigate(Screen.CreateTour.route) }
                                    )
                                    QuickActionItem(
                                        icon = Icons.Default.Key,
                                        label = "Join Code",
                                        onClick = { showJoinDialog = true }
                                    )
                                    QuickActionItem(
                                        icon = Icons.Default.Receipt,
                                        label = "Expenses",
                                        onClick = { navController.navigate(Screen.TourDashboard.createRoute(featuredTour.id)) }
                                    )
                                    QuickActionItem(
                                        icon = Icons.Default.QrCodeScanner,
                                        label = "Scan Bill",
                                        onClick = { /* Scan receipt */ }
                                    )
                                }
                            }

                            // Active Tour Hero Card
                            item {
                                Text(
                                    text = "Active Trip",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                ActiveTourHeroCard(
                                    tour = featuredTour,
                                    onClick = { navController.navigate(Screen.TourDashboard.createRoute(featuredTour.id)) }
                                )
                            }

                            // All Tours List
                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "All Tours (${state.tours.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            items(state.tours, key = { it.id }) { tour ->
                                TourGlassCard(
                                    tour = tour,
                                    onClick = { navController.navigate(Screen.TourDashboard.createRoute(tour.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            containerColor = MidnightSurfaceContainer,
            title = {
                Text(
                    "Join a Tour",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        "Enter the 6-character invite code provided by your tour admin:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = joinCodeInput,
                        onValueChange = { if (it.length <= 6) joinCodeInput = it.uppercase() },
                        placeholder = { Text("e.g. BALI26") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (joinCodeInput.isNotBlank()) {
                            showJoinDialog = false
                            viewModel.joinTour(joinCodeInput) { joinedId ->
                                navController.navigate(Screen.TourDashboard.createRoute(joinedId))
                            }
                            joinCodeInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Join Tour")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MidnightSurfaceContainer)
                .border(1.dp, GlassBorderTeal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = NeonTeal,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActiveTourHeroCard(
    tour: TourSummary,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = GlassBorderTeal,
        shape = RoundedCornerShape(20.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Luggage,
                            contentDescription = null,
                            tint = NeonTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = tour.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                StatusBadge(status = tour.status)
            }

            if (!tour.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tour.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        tint = NeonTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${tour.memberCount} Members traveling",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "CODE: ${tour.inviteCode}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonTeal,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TourGlassCard(
    tour: TourSummary,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tour.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = tour.status)
            }

            if (!tour.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = tour.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tour.memberCount} members",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Invite: ${tour.inviteCode}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = NeonTeal
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = NeonTeal,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyToursView(
    onCreateClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                com.example.tripzyfrontend.ui.common.TripzyIcon(size = 44.dp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No tours yet",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Start a group trip or join an existing one to track shared costs.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCreateClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create a Tour")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onJoinClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Join with Invite Code")
            }
        }
    }
}
