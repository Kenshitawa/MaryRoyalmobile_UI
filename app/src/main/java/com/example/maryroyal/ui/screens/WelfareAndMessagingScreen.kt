package com.example.maryroyal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maryroyal.data.model.ComplaintMessage
import com.example.maryroyal.data.model.Role
import com.example.maryroyal.data.model.User
import com.example.maryroyal.data.model.WelfareLoan
import com.example.maryroyal.data.repository.MaryRoyalRepository
import com.example.maryroyal.ui.components.BentoCard
import com.example.maryroyal.ui.components.HeroBanner
import com.example.maryroyal.ui.components.StatusBadge
import com.example.maryroyal.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun WelfareAndMessagingScreen(
    repository: MaryRoyalRepository,
    currentUser: User,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val complaints by repository.getAllComplaints().collectAsState(initial = emptyList())
    val welfareContribs by repository.getAllWelfareContributions().collectAsState(initial = emptyList())
    val welfareLoans by repository.getAllWelfareLoans().collectAsState(initial = emptyList())

    var activeTab by remember { mutableIntStateOf(if (currentUser.role == Role.PARENT) 1 else 0) } // 0: Staff Welfare, 1: Parent-School Messages
    var showNewComplaintDialog by remember { mutableStateOf(false) }
    var showNewLoanDialog by remember { mutableStateOf(false) }

    val totalWelfareSavings = welfareContribs.sumOf { it.amount }
    val totalLoansDisbursed = welfareLoans.filter { it.status == "APPROVED" }.sumOf { it.amount }

    if (showNewComplaintDialog) {
        NewComplaintDialog(
            parentName = currentUser.name,
            onDismiss = { showNewComplaintDialog = false },
            onSend = { subject, message, childName ->
                coroutineScope.launch {
                    repository.sendComplaint(
                        ComplaintMessage(
                            parentId = currentUser.id,
                            parentName = currentUser.name,
                            learnerName = childName,
                            subject = subject,
                            message = message
                        )
                    )
                    showNewComplaintDialog = false
                }
            }
        )
    }

    if (showNewLoanDialog) {
        NewLoanDialog(
            userName = currentUser.name,
            onDismiss = { showNewLoanDialog = false },
            onApply = { amount, reason ->
                coroutineScope.launch {
                    repository.applyWelfareLoan(
                        WelfareLoan(
                            staffId = currentUser.id,
                            staffName = currentUser.name,
                            amount = amount,
                            reason = reason
                        )
                    )
                    showNewLoanDialog = false
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HeroBanner(
                tag = "Community & Staff Support",
                title = "Welfare & Communications",
                subtitle = "Staff welfare scheme contributions, emergency loans, and direct Parent-to-Leadership dialogue.",
                actionButton = {
                    if (activeTab == 1) {
                        Button(
                            onClick = { showNewComplaintDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_new_message")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Message to Leadership", color = RoyalNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = { showNewLoanDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_apply_loan")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Apply for Welfare Loan", color = RoyalNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            )
        }

        item {
            TabRow(selectedTabIndex = activeTab) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Staff Welfare & Loans", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Parent Inquiries", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        if (activeTab == 0) {
            item {
                BentoCard {
                    Text("STAFF WELFARE POOL BALANCE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Savings Pool", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("KES ${totalWelfareSavings.toInt()}", fontWeight = FontWeight.Black, fontSize = 22.sp, color = AccentEmerald)
                        }
                        Column {
                            Text("Disbursed Loans", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("KES ${totalLoansDisbursed.toInt()}", fontWeight = FontWeight.Black, fontSize = 22.sp, color = RoyalNavy)
                        }
                    }
                }
            }

            item {
                Text("MONTHLY STAFF CONTRIBUTIONS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            items(welfareContribs) { c ->
                BentoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(c.staffName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${c.month} ${c.year} Contribution", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("KES ${c.amount.toInt()}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = RoyalBlue)
                    }
                }
            }
        } else {
            // Parent Inquiries
            item {
                Text(
                    text = "PARENT & GUARDIAN COMMUNICATIONS (${complaints.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (complaints.isEmpty()) {
                item {
                    BentoCard {
                        Text("No parent messages or feedback recorded.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(complaints) { msg ->
                    BentoCard(modifier = Modifier.testTag("complaint_item_${msg.id}")) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(msg.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    StatusBadge(
                                        text = msg.status,
                                        containerColor = if (msg.status == "RESOLVED") AccentEmeraldLight else RoyalGoldLight,
                                        contentColor = if (msg.status == "RESOLVED") AccentEmerald else RoyalGold
                                    )
                                }
                                Text(
                                    text = "From: ${msg.parentName} · Learner: ${msg.learnerName} · ${msg.createdAt}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(msg.message, fontSize = 12.sp)

                                if (msg.response.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFDBEAFE).copy(alpha = 0.5f))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "Head Teacher Response: ${msg.response}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = RoyalBlueDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewComplaintDialog(
    parentName: String,
    onDismiss: () -> Unit,
    onSend: (subject: String, message: String, childName: String) -> Unit
) {
    var subject by remember { mutableStateOf("Academic progress inquiry") }
    var childName by remember { mutableStateOf("Brian Ochieng") }
    var message by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Message School Leadership", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = childName,
                    onValueChange = { childName = it },
                    label = { Text("Learner's Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject / Reason") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message details for Head Teacher & Director") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(subject, message.ifBlank { "Requesting term review update." }, childName) },
                modifier = Modifier.testTag("btn_submit_complaint")
            ) {
                Text("Send Message")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun NewLoanDialog(
    userName: String,
    onDismiss: () -> Unit,
    onApply: (amount: Double, reason: String) -> Unit
) {
    var amountText by remember { mutableStateOf("10000") }
    var reason by remember { mutableStateOf("Emergency Medical Expense") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Staff Welfare Loan Application", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Loan Amount (KES)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Purpose of Loan") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onApply(amountText.toDoubleOrNull() ?: 5000.0, reason) }) {
                Text("Submit Application")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
