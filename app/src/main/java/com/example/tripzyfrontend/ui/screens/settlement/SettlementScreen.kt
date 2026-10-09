package com.example.tripzyfrontend.ui.screens.settlement

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tripzyfrontend.domain.model.PaymentMethod
import com.example.tripzyfrontend.domain.model.SettlementTransaction
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.ui.common.AmountText
import com.example.tripzyfrontend.ui.common.GlassCard
import com.example.tripzyfrontend.ui.common.StatusBadge
import com.example.tripzyfrontend.ui.theme.BalancePositive
import com.example.tripzyfrontend.ui.theme.BrandPrimary
import com.example.tripzyfrontend.ui.theme.GlassBorderTeal
import com.example.tripzyfrontend.ui.theme.MidnightSurfaceContainer
import com.example.tripzyfrontend.ui.theme.NeonTeal
import com.example.tripzyfrontend.ui.theme.StatusArchivedBg
import com.example.tripzyfrontend.ui.theme.StatusArchivedText
import com.example.tripzyfrontend.ui.theme.StatusSettledBg
import com.example.tripzyfrontend.ui.theme.StatusSettledText
import kotlinx.coroutines.flow.collectLatest

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlementScreen(
    navController: NavController,
    tourId: String,
    viewModel: SettlementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showRecordDialog by remember { mutableStateOf(false) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadSettlements(isSilent = true)
    }

    LaunchedEffect(viewModel) {
        viewModel.snackbarEvent.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Settlements",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (uiState is SettlementUiState.Success) {
                            Text(
                                (uiState as SettlementUiState.Success).tour.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
                    if (uiState is SettlementUiState.Success) {
                        val tour = (uiState as SettlementUiState.Success).tour
                        StatusBadge(status = tour.status, modifier = Modifier.padding(end = 12.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            val isArchived = (uiState as? SettlementUiState.Success)?.tour?.status == TourStatus.ARCHIVED
            if (!isArchived) {
                FloatingActionButton(
                    onClick = { showRecordDialog = true },
                    containerColor = BrandPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Record Settlement")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when (val state = uiState) {
            is SettlementUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPrimary)
                }
            }
            is SettlementUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
                        Button(onClick = { viewModel.loadSettlements() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry")
                        }
                    }
                }
            }
            is SettlementUiState.Success -> {
                val isArchived = state.tour.status == TourStatus.ARCHIVED

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (isArchived) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(StatusArchivedBg)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = StatusArchivedText, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tour is archived. Recording new settlements is disabled.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StatusArchivedText
                                )
                            }
                        }
                    }

                    if (state.tour.status == TourStatus.SETTLED) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(StatusSettledBg)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSettledText, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "All balances are settled for this tour!",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = StatusSettledText
                                )
                            }
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Smart Debt Optimization Insight Banner
                        item {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = GlassBorderTeal,
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(BrandPrimary.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = NeonTeal,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                "SMART DEBT OPTIMIZATION",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = NeonTeal
                                            )
                                            Text(
                                                "Active: Pairwise transfers simplified",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    if (!isArchived) {
                                        Button(
                                            onClick = { showRecordDialog = true },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Paid, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Record a Settlement Payment", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        if (state.settlements.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.Paid,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "No settlements recorded yet",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isArchived) "No settlement payments were made." else "When members pay each other, record payments here.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            item {
                                Text(
                                    text = "Completed Payments (${state.settlements.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            items(state.settlements, key = { it.id }) { settlement ->
                                SettlementGlassCard(settlement = settlement)
                            }
                        }
                    }
                }

                // Record Settlement Dialog
                if (showRecordDialog) {
                    RecordSettlementDialog(
                        members = state.members,
                        currentUserId = currentUser?.id,
                        isRecording = state.isRecording,
                        onDismiss = { showRecordDialog = false },
                        onSubmit = { fromId, toId, amount, method, note, ref ->
                            viewModel.recordSettlement(fromId, toId, amount, method, note, ref) {
                                showRecordDialog = false
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettlementGlassCard(settlement: SettlementTransaction) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MethodBadge(method = settlement.paymentMethod)
                val dateStr = try { settlement.settledAt.substring(0, 10) } catch (e: Exception) { settlement.settledAt }
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = settlement.fromUserName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = NeonTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = settlement.toUserName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                AmountText(
                    money = settlement.amount,
                    style = MaterialTheme.typography.titleLarge,
                    color = BalancePositive
                )
            }

            if (!settlement.note.isNullOrBlank() || !settlement.transactionRef.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                if (!settlement.note.isNullOrBlank()) {
                    Text(
                        text = "Note: ${settlement.note}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!settlement.transactionRef.isNullOrBlank()) {
                    Text(
                        text = "Ref: ${settlement.transactionRef}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MethodBadge(method: PaymentMethod) {
    val label = when (method) {
        PaymentMethod.CASH -> "Cash"
        PaymentMethod.BKASH -> "bKash"
        PaymentMethod.NAGAD -> "Nagad"
        PaymentMethod.BANK_TRANSFER -> "Bank Transfer"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(BrandPrimary.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Payment, contentDescription = null, tint = NeonTeal, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NeonTeal)
            )
        }
    }
}

@Composable
private fun RecordSettlementDialog(
    members: List<TourMember>,
    currentUserId: String?,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (fromId: String, toId: String, amount: Double, method: PaymentMethod, note: String?, ref: String?) -> Unit
) {
    var fromUserId by remember { mutableStateOf(currentUserId ?: members.firstOrNull()?.userId ?: "") }
    var toUserId by remember { mutableStateOf(members.firstOrNull { it.userId != fromUserId }?.userId ?: "") }
    var amountInput by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.BKASH) }
    var noteInput by remember { mutableStateOf("") }
    var refInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightSurfaceContainer,
        title = {
            Text(
                "Record Settlement Payment",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Payer (From) Selector
                Text("Who Paid? (Debtor):", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    members.forEach { m ->
                        val isSelected = fromUserId == m.userId
                        OutlinedCard(
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth().clickable { fromUserId = m.userId }
                        ) {
                            Text(
                                text = if (m.userId == currentUserId) "${m.name} (You)" else m.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) NeonTeal else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                // Receiver (To) Selector
                Text("Paid To? (Creditor):", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    members.filter { it.userId != fromUserId }.forEach { m ->
                        val isSelected = toUserId == m.userId
                        OutlinedCard(
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth().clickable { toUserId = m.userId }
                        ) {
                            Text(
                                text = m.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) NeonTeal else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) amountInput = it },
                    label = { Text("Amount (৳)") },
                    placeholder = { Text("500.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Method
                Text("Payment Method:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PaymentMethod.values().forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            label = { Text(method.name.replace("_", " "), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = NeonTeal
                            )
                        )
                    }
                }

                // Optional note & ref
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Note (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = refInput,
                    onValueChange = { refInput = it },
                    label = { Text("TrxID / Reference (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && fromUserId.isNotBlank() && toUserId.isNotBlank()) {
                        onSubmit(fromUserId, toUserId, amount, selectedMethod, noteInput.ifBlank { null }, refInput.ifBlank { null })
                    }
                },
                enabled = (amountInput.toDoubleOrNull() ?: 0.0) > 0 && !isRecording,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                if (isRecording) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                } else {
                    Text("Record Settlement")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
