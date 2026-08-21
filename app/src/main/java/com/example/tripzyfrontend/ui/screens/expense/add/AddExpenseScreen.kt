package com.example.tripzyfrontend.ui.screens.expense.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tripzyfrontend.domain.model.ExpenseCategory
import com.example.tripzyfrontend.domain.model.SplitType
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.ui.theme.BrandPrimary
import com.example.tripzyfrontend.ui.theme.TagPersonalText
import com.example.tripzyfrontend.ui.theme.TagSharedText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseScreen(
    navController: NavController,
    tourId: String,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var selectedFlowType by remember { mutableStateOf(ExpenseFlowType.SHARED) }
    var selectedSplitType by remember { mutableStateOf(SplitType.EQUAL) }

    var selectedPayerId by remember { mutableStateOf("") }
    var selectedParticipantIds by remember { mutableStateOf(setOf<String>()) }
    var paidForTargetUserId by remember { mutableStateOf("") }

    val exactAmountInputs = remember { mutableStateMapOf<String, String>() }
    val percentageInputs = remember { mutableStateMapOf<String, String>() }
    val shareInputs = remember { mutableStateMapOf<String, String>() }

    // Initialize defaults when content loads
    LaunchedEffect(uiState, currentUser) {
        if (uiState is AddExpenseUiState.Content) {
            val content = uiState as AddExpenseUiState.Content
            if (selectedPayerId.isBlank()) {
                selectedPayerId = currentUser?.id ?: content.members.firstOrNull()?.userId ?: ""
            }
            if (selectedParticipantIds.isEmpty() && content.members.isNotEmpty()) {
                selectedParticipantIds = content.members.map { it.userId }.toSet()
            }
            if (paidForTargetUserId.isBlank() && content.members.isNotEmpty()) {
                paidForTargetUserId = content.members.firstOrNull { it.userId != selectedPayerId }?.userId ?: ""
            }
            content.members.forEach { m ->
                if (!shareInputs.containsKey(m.userId)) shareInputs[m.userId] = "1"
            }
        }
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddExpenseUiState.Success -> {
                navController.popBackStack()
            }
            is AddExpenseUiState.Content -> {
                state.errorMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when (val state = uiState) {
            is AddExpenseUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPrimary)
                }
            }
            is AddExpenseUiState.Success -> {}
            is AddExpenseUiState.Content -> {
                val totalAmount = amountInput.toDoubleOrNull() ?: 0.0

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Amount Input Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Amount (৳)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "৳",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = amountInput,
                                    onValueChange = { input ->
                                        if (input.isEmpty() || input.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                            amountInput = input
                                        }
                                    },
                                    placeholder = { Text("0.00", fontSize = 28.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Title & Description
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Expense Title *") },
                        placeholder = { Text("e.g. Resort Booking / Dinner / Fuel") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (Optional)") },
                        placeholder = { Text("Receipt details, notes...") },
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Expense Type Selection
                    Column {
                        Text(
                            text = "Expense Type",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TypeChip(
                                label = "Shared",
                                selected = selectedFlowType == ExpenseFlowType.SHARED,
                                onClick = { selectedFlowType = ExpenseFlowType.SHARED },
                                activeColor = TagSharedText,
                                modifier = Modifier.weight(1f)
                            )
                            TypeChip(
                                label = "Personal",
                                selected = selectedFlowType == ExpenseFlowType.PERSONAL,
                                onClick = { selectedFlowType = ExpenseFlowType.PERSONAL },
                                activeColor = TagPersonalText,
                                modifier = Modifier.weight(1f)
                            )
                            TypeChip(
                                label = "For Someone",
                                selected = selectedFlowType == ExpenseFlowType.PAID_FOR_SOMEONE,
                                onClick = { selectedFlowType = ExpenseFlowType.PAID_FOR_SOMEONE },
                                activeColor = BrandPrimary,
                                modifier = Modifier.weight(1.2f)
                            )
                        }
                    }

                    // Category Selector
                    Column {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ExpenseCategory.values().forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat.name, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandPrimary.copy(alpha = 0.15f),
                                        selectedLabelColor = BrandPrimary
                                    )
                                )
                            }
                        }
                    }

                    // Payer Selector
                    Column {
                        Text(
                            text = "Paid By",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            state.members.forEach { member ->
                                val isSelected = selectedPayerId == member.userId
                                OutlinedCard(
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPayerId = member.userId }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = if (member.userId == currentUser?.id) "${member.name} (You)" else member.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isSelected) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SHARED Split Method Options
                    if (selectedFlowType == ExpenseFlowType.SHARED) {
                        Column {
                            Text(
                                text = "Split Method",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SplitMethodChip("Equal", selectedSplitType == SplitType.EQUAL, { selectedSplitType = SplitType.EQUAL }, Modifier.weight(1f))
                                SplitMethodChip("Exact", selectedSplitType == SplitType.EXACT, { selectedSplitType = SplitType.EXACT }, Modifier.weight(1f))
                                SplitMethodChip("%", selectedSplitType == SplitType.PERCENTAGE, { selectedSplitType = SplitType.PERCENTAGE }, Modifier.weight(0.8f))
                                SplitMethodChip("Shares", selectedSplitType == SplitType.SHARES, { selectedSplitType = SplitType.SHARES }, Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 1. EQUAL SPLIT
                            if (selectedSplitType == SplitType.EQUAL) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Split Equally (${selectedParticipantIds.size} members)",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = if (selectedParticipantIds.size == state.members.size) "Select None" else "Select All",
                                        style = MaterialTheme.typography.labelMedium.copy(color = BrandPrimary, fontWeight = FontWeight.SemiBold),
                                        modifier = Modifier.clickable {
                                            selectedParticipantIds = if (selectedParticipantIds.size == state.members.size) emptySet() else state.members.map { it.userId }.toSet()
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                state.members.forEach { member ->
                                    val isChecked = selectedParticipantIds.contains(member.userId)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedParticipantIds = if (isChecked) selectedParticipantIds - member.userId else selectedParticipantIds + member.userId
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                selectedParticipantIds = if (checked) selectedParticipantIds + member.userId else selectedParticipantIds - member.userId
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = member.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }

                            // 2. EXACT AMOUNTS SPLIT
                            if (selectedSplitType == SplitType.EXACT) {
                                val currentExactSum = exactAmountInputs.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
                                val remaining = totalAmount - currentExactSum

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Enter Exact ৳ per member:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                                    Text(
                                        text = "Remaining: ৳${"%.2f".format(remaining)}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (Math.abs(remaining) < 0.01) BrandPrimary else MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                state.members.forEach { member ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(member.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                        OutlinedTextField(
                                            value = exactAmountInputs[member.userId] ?: "",
                                            onValueChange = { input ->
                                                if (input.isEmpty() || input.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                                    exactAmountInputs[member.userId] = input
                                                }
                                            },
                                            prefix = { Text("৳") },
                                            placeholder = { Text("0.00") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            singleLine = true,
                                            modifier = Modifier.width(130.dp)
                                        )
                                    }
                                }
                            }

                            // 3. PERCENTAGE SPLIT
                            if (selectedSplitType == SplitType.PERCENTAGE) {
                                val currentPercentSum = percentageInputs.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
                                val remainingPercent = 100.0 - currentPercentSum

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Enter Percentage (%) per member:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                                    Text(
                                        text = "Remaining: ${"%.1f".format(remainingPercent)}%",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (Math.abs(remainingPercent) < 0.01) BrandPrimary else MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                state.members.forEach { member ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(member.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                        OutlinedTextField(
                                            value = percentageInputs[member.userId] ?: "",
                                            onValueChange = { input ->
                                                if (input.isEmpty() || input.matches(Regex("^\\d*(\\.\\d{0,1})?$"))) {
                                                    percentageInputs[member.userId] = input
                                                }
                                            },
                                            suffix = { Text("%") },
                                            placeholder = { Text("0.0") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            singleLine = true,
                                            modifier = Modifier.width(110.dp)
                                        )
                                    }
                                }
                            }

                            // 4. SHARES / WEIGHTS SPLIT
                            if (selectedSplitType == SplitType.SHARES) {
                                val totalSharesCount = shareInputs.values.sumOf { it.toIntOrNull() ?: 0 }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Enter shares/weight per member:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                                    Text("Total Shares: $totalSharesCount", style = MaterialTheme.typography.bodySmall.copy(color = BrandPrimary, fontWeight = FontWeight.Bold))
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                state.members.forEach { member ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(member.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                        OutlinedTextField(
                                            value = shareInputs[member.userId] ?: "1",
                                            onValueChange = { input ->
                                                if (input.isEmpty() || input.matches(Regex("^\\d*$"))) {
                                                    shareInputs[member.userId] = input
                                                }
                                            },
                                            suffix = { Text("shares") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.width(130.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Target Member Selection (For PAID_FOR_SOMEONE)
                    if (selectedFlowType == ExpenseFlowType.PAID_FOR_SOMEONE) {
                        Column {
                            Text(
                                text = "Who was this paid for?",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            state.members.filter { it.userId != selectedPayerId }.forEach { member ->
                                val isSelected = paidForTargetUserId == member.userId
                                OutlinedCard(
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clickable { paidForTargetUserId = member.userId }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = member.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isSelected) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            val exactMap = exactAmountInputs.mapValues { it.value.toDoubleOrNull() ?: 0.0 }
                            val percentMap = percentageInputs.mapValues { it.value.toDoubleOrNull() ?: 0.0 }
                            val sharesMap = shareInputs.mapValues { it.value.toIntOrNull() ?: 0 }

                            viewModel.submitExpense(
                                title = title,
                                description = description,
                                amountMajor = totalAmount,
                                category = selectedCategory,
                                flowType = selectedFlowType,
                                splitType = selectedSplitType,
                                selectedPayerId = selectedPayerId,
                                selectedParticipantIds = selectedParticipantIds,
                                paidForTargetUserId = paidForTargetUserId,
                                exactAmounts = exactMap,
                                percentages = percentMap,
                                shares = sharesMap
                            )
                        },
                        enabled = title.isNotBlank() && totalAmount > 0.0 && !state.isSaving,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Text("Save Expense", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun TypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) activeColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) activeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (selected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SplitMethodChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) BrandPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) BrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (selected) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
