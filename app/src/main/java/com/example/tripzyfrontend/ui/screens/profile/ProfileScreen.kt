package com.example.tripzyfrontend.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.tripzyfrontend.navigation.Screen
import com.example.tripzyfrontend.ui.common.GlassCard
import com.example.tripzyfrontend.ui.common.StatCard
import com.example.tripzyfrontend.ui.theme.BalanceNegative
import com.example.tripzyfrontend.ui.theme.BalancePositive
import com.example.tripzyfrontend.ui.theme.BrandPrimary
import com.example.tripzyfrontend.ui.theme.DarkBackground
import com.example.tripzyfrontend.ui.theme.GlassBorder
import com.example.tripzyfrontend.ui.theme.GlassBorderTeal
import com.example.tripzyfrontend.ui.theme.NeonTeal
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val autoRemindEnabled by viewModel.autoRemindEnabled.collectAsState()
    val pushAlertsEnabled by viewModel.pushAlertsEnabled.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.logoutEvent.collectLatest {
            navController.navigate(Screen.Auth.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile & Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Edit profile */ }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = BrandPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Profile Card (Glassmorphic Hero)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = GlassBorderTeal,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.2f))
                            .border(2.dp, BrandPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.name ?: "Alex Morgan",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified Explorer",
                                tint = NeonTeal,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = currentUser?.email ?: "alex.morgan@tripzy.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Pro Traveler",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                ),
                                color = BrandPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3-Column Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Tours",
                    value = "14",
                    subtitle = "Active & Done",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Tracked",
                    value = "$18.4k",
                    subtitle = "Expenses",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Settlement",
                    value = "100%",
                    subtitle = "All Settled",
                    valueColor = BalancePositive,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Group: Financial & Payments
            Text(
                text = "Financial & Payments",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp)) {
                    SettingItem(
                        icon = Icons.Default.Paid,
                        title = "Default Currency",
                        subtitle = "USD ($) • Change currency",
                        onClick = { /* Change currency */ }
                    )
                    SettingItem(
                        icon = Icons.Default.AccountBalance,
                        title = "Linked Payment Methods",
                        subtitle = "Apple Pay, UPI & Bank connected",
                        onClick = { /* Payment methods */ }
                    )
                    SettingSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Auto-Remind Unsettled Dues",
                        subtitle = "Notify participants before tour ends",
                        checked = autoRemindEnabled,
                        onCheckedChange = { viewModel.toggleAutoRemind(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Group: Preferences & Security
            Text(
                text = "Preferences & Security",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp)) {
                    SettingItem(
                        icon = Icons.Default.DarkMode,
                        title = "App Appearance",
                        subtitle = "Dark Midnight Theme",
                        onClick = { /* Toggle theme */ }
                    )
                    SettingSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        subtitle = "Instant split and payment alerts",
                        checked = pushAlertsEnabled,
                        onCheckedChange = { viewModel.togglePushAlerts(it) }
                    )
                    SettingItem(
                        icon = Icons.Default.Fingerprint,
                        title = "Biometrics & Security",
                        subtitle = "Face ID enabled",
                        onClick = { /* Security settings */ }
                    )
                    SettingItem(
                        icon = Icons.Default.Download,
                        title = "Export Tour Data",
                        subtitle = "Download all tours as PDF / CSV",
                        onClick = { /* Export */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Danger Zone: Log Out
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BalanceNegative.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                onClick = { viewModel.logout() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = BalanceNegative,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Out of Tripzy",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = BalanceNegative
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BrandPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BrandPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandPrimary,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
