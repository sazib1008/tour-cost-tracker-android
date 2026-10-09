package com.example.tripzyfrontend.ui.screens.tour.members

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.domain.model.TourRole
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.ui.common.GlassCard
import com.example.tripzyfrontend.ui.common.StatusBadge
import com.example.tripzyfrontend.ui.theme.BrandPrimary
import com.example.tripzyfrontend.ui.theme.GlassBorderTeal
import com.example.tripzyfrontend.ui.theme.MidnightSurfaceContainer
import com.example.tripzyfrontend.ui.theme.NeonTeal
import com.example.tripzyfrontend.ui.theme.StatusArchivedBg
import com.example.tripzyfrontend.ui.theme.StatusArchivedText
import kotlinx.coroutines.flow.collectLatest

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    navController: NavController,
    tourId: String,
    viewModel: MembersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteEmailInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("MEMBER") }
    var searchQuery by remember { mutableStateOf("") }

    var memberToRemove by remember { mutableStateOf<TourMember?>(null) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadMembers(isSilent = true)
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
                            "Tour Members",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (uiState is MembersUiState.Success) {
                            val count = (uiState as MembersUiState.Success).members.size
                            Text(
                                "$count Travelers connected",
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
                    if (uiState is MembersUiState.Success) {
                        val tour = (uiState as MembersUiState.Success).tour
                        StatusBadge(status = tour.status, modifier = Modifier.padding(end = 12.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (uiState is MembersUiState.Success) {
                val state = uiState as MembersUiState.Success
                val isArchived = state.tour.status == TourStatus.ARCHIVED
                if (state.isAdmin && !isArchived) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = { showInviteDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("+ Invite Member via Email", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is MembersUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPrimary)
                }
            }
            is MembersUiState.Error -> {
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
                        Button(onClick = { viewModel.loadMembers() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry")
                        }
                    }
                }
            }
            is MembersUiState.Success -> {
                val isArchived = state.tour.status == TourStatus.ARCHIVED
                val filteredMembers = if (searchQuery.isBlank()) {
                    state.members
                } else {
                    state.members.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                                it.email.contains(searchQuery, ignoreCase = true)
                    }
                }

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
                                    text = "Tour is archived. Inviting and removing members is disabled.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StatusArchivedText
                                )
                            }
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Invite Hero Glass Card
                        item {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = GlassBorderTeal,
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                "TOUR INVITE CODE",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                state.tour.inviteCode,
                                                style = MaterialTheme.typography.headlineMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 3.sp,
                                                    fontFamily = FontFamily.Monospace
                                                ),
                                                color = NeonTeal
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(BrandPrimary.copy(alpha = 0.15f))
                                                .border(1.dp, GlassBorderTeal, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.QrCode,
                                                contentDescription = "QR Code",
                                                tint = NeonTeal,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(state.tour.inviteCode))
                                                Toast.makeText(context, "Invite code copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MidnightSurfaceContainer
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .border(1.dp, GlassBorderTeal, RoundedCornerShape(10.dp))
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = NeonTeal, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Copy Code", color = NeonTeal, style = MaterialTheme.typography.labelMedium)
                                        }

                                        Button(
                                            onClick = {
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        "Join my tour '${state.tour.title}' on Tripzy! Use code: ${state.tour.inviteCode}"
                                                    )
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Share Tour Invite"))
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Share Link", color = Color.White, style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                }
                            }
                        }

                        // Search Filter Input
                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search members by name...") },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Members Roster
                        item {
                            Text(
                                text = "Members (${filteredMembers.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        items(filteredMembers, key = { it.userId }) { member ->
                            MemberGlassCard(
                                member = member,
                                isCurrentUser = member.userId == state.currentUserId,
                                canRemove = state.isAdmin && !isArchived && (member.userId != state.currentUserId || state.members.count { it.role == TourRole.ADMIN } > 1),
                                onRemoveClick = { memberToRemove = member }
                            )
                        }
                    }
                }
            }
        }
    }

    // Invite Member Dialog
    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            containerColor = MidnightSurfaceContainer,
            title = {
                Text(
                    "Invite Member",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter the user's registered Google email address:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = inviteEmailInput,
                        onValueChange = { inviteEmailInput = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("friend@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        "Tour Role:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedRole = "MEMBER" },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedRole == "MEMBER") BrandPrimary.copy(alpha = 0.15f) else Color.Transparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedRole == "MEMBER") BrandPrimary else MaterialTheme.colorScheme.outline),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Member", color = if (selectedRole == "MEMBER") NeonTeal else MaterialTheme.colorScheme.onSurface)
                        }
                        OutlinedButton(
                            onClick = { selectedRole = "ADMIN" },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedRole == "ADMIN") BrandPrimary.copy(alpha = 0.15f) else Color.Transparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedRole == "ADMIN") BrandPrimary else MaterialTheme.colorScheme.outline),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Co-Admin", color = if (selectedRole == "ADMIN") NeonTeal else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inviteEmailInput.isNotBlank()) {
                            showInviteDialog = false
                            viewModel.inviteMember(inviteEmailInput, selectedRole)
                            inviteEmailInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Send Invite")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInviteDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // Remove Confirmation Dialog
    memberToRemove?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToRemove = null },
            containerColor = MidnightSurfaceContainer,
            title = {
                Text(
                    "Remove Member",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "Are you sure you want to remove ${member.name} (${member.email}) from this tour?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeMember(member.userId)
                        memberToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { memberToRemove = null }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun MemberGlassCard(
    member: TourMember,
    isCurrentUser: Boolean,
    canRemove: Boolean,
    onRemoveClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary.copy(alpha = 0.15f))
                    .border(1.dp, GlassBorderTeal, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = NeonTeal, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isCurrentUser) "${member.name} (You)" else member.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    RoleBadge(role = member.role)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = member.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (canRemove) {
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(role: TourRole) {
    val isAdmin = role == TourRole.ADMIN
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isAdmin) BrandPrimary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isAdmin) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = NeonTeal, modifier = Modifier.size(10.dp))
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = role.name,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                color = if (isAdmin) NeonTeal else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
