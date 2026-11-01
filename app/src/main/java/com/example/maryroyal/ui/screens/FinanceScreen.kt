package com.example.maryroyal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.maryroyal.data.model.*
import com.example.maryroyal.data.repository.MaryRoyalRepository
import com.example.maryroyal.ui.components.BentoCard
import com.example.maryroyal.ui.components.HeroBanner
import com.example.maryroyal.ui.components.StatCard
import com.example.maryroyal.ui.components.StatusBadge
import com.example.maryroyal.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun FinanceScreen(
    repository: MaryRoyalRepository,
    currentUser: User,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val learners by repository.getAllLearners().collectAsState(initial = emptyList())
    val invoices by repository.getAllInvoices().collectAsState(initial = emptyList())
    val payments by repository.getAllPayments().collectAsState(initial = emptyList())
    val feeStructures by repository.getFeeStructures().collectAsState(initial = emptyList())

    var showPaymentModal by remember { mutableStateOf(false) }
    var selectedPaymentForReceipt by remember { mutableStateOf<FeePayment?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Payments & Invoices, 1: Fee Structures

    val canRecord = currentUser.role in listOf(Role.ACCOUNTANT, Role.DIRECTOR, Role.HEAD_TEACHER, Role.SECRETARY)

    val totalBilled = invoices.sumOf { it.totalAmount }
    val totalCollected = payments.filter { it.status == "RECORDED" }.sumOf { it.amount }
    val totalBalance = totalBilled - totalCollected

    if (showPaymentModal) {
        RecordPaymentDialog(
            learners = learners,
            invoices = invoices,
            onDismiss = { showPaymentModal = false },
            onSavePayment = { invoiceId, learnerId, learnerName, amount, method, mpesaCode, ref ->
                coroutineScope.launch {
                    val p = repository.recordPayment(
                        invoiceId = invoiceId,
                        learnerId = learnerId,
                        learnerName = learnerName,
                        amount = amount,
                        method = method,
                        mpesaCode = mpesaCode,
                        reference = ref,
                        actorName = currentUser.name
                    )
                    showPaymentModal = false
                    selectedPaymentForReceipt = p
                }
            }
        )
    }

    if (selectedPaymentForReceipt != null) {
        ReceiptDialog(
            payment = selectedPaymentForReceipt!!,
            currentUser = currentUser,
            onDismiss = { selectedPaymentForReceipt = null },
            onVoid = { reason ->
                coroutineScope.launch {
                    repository.voidPayment(selectedPaymentForReceipt!!, reason, currentUser.name)
                    selectedPaymentForReceipt = null
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
                tag = "Accounts & Revenue Desk · Kisumu",
                title = "Financial Management",
                subtitle = "Manage fee structures, invoices, M-PESA/bank payments, and verifiable receipts.",
                actionButton = if (canRecord) {
                    {
                        Button(
                            onClick = { showPaymentModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_record_payment")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCard,
                                contentDescription = null,
                                tint = RoyalNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Record Fee Payment",
                                color = RoyalNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else null
            )
        }

        // Stats summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Billed",
                    value = "KES ${(totalBilled / 1000).toInt()}k",
                    subtitle = "Term invoices",
                    icon = Icons.Default.Receipt,
                    iconTint = RoyalBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Collected",
                    value = "KES ${(totalCollected / 1000).toInt()}k",
                    subtitle = "Recorded payments",
                    icon = Icons.Default.CheckCircle,
                    iconTint = AccentEmerald,
                    iconBg = AccentEmerald.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Balance",
                    value = "KES ${(totalBalance / 1000).toInt()}k",
                    subtitle = "Outstanding",
                    icon = Icons.Default.HourglassEmpty,
                    iconTint = AccentRose,
                    iconBg = AccentRose.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tab selection
        item {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Payments & Invoices", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Approved Fee Structures", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        if (selectedTab == 0) {
            item {
                Text(
                    text = "RECORDED FEE PAYMENTS (${payments.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(payments) { payment ->
                BentoCard(
                    onClick = { selectedPaymentForReceipt = payment },
                    modifier = Modifier.testTag("payment_row_${payment.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = payment.learnerName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                StatusBadge(
                                    text = payment.status,
                                    containerColor = if (payment.status == "VOID") AccentRoseLight else AccentEmeraldLight,
                                    contentColor = if (payment.status == "VOID") AccentRose else AccentEmerald
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Receipt: ${payment.receiptNo} · Method: ${payment.method.label} · ${payment.date}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            if (payment.mpesaCode.isNotBlank()) {
                                Text(
                                    text = "M-PESA Code: ${payment.mpesaCode}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentEmerald,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "KES ${payment.amount.toInt()}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = if (payment.status == "VOID") TextMuted else RoyalNavy
                            )
                            Text(
                                text = "Tap for receipt",
                                fontSize = 11.sp,
                                color = RoyalBlue
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Text(
                    text = "OFFICIALLY APPROVED FEE STRUCTURES",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(feeStructures) { structure ->
                BentoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = structure.grade,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy
                            )
                            Text(
                                text = structure.term,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "KES ${structure.total.toInt()} / Term",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = RoyalBlue
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        FeeLineItem("Tuition Fee", structure.tuitionFee)
                        FeeLineItem("Meals / Lunch Scheme", structure.mealFee)
                        FeeLineItem("Co-curricular Activities", structure.activityFee)
                        FeeLineItem("CBC Practical Materials", structure.cbcMaterialsFee)
                        FeeLineItem("School Transport (Optional)", structure.transportFee)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeeLineItem(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("KES ${amount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun RecordPaymentDialog(
    learners: List<Learner>,
    invoices: List<FeeInvoice>,
    onDismiss: () -> Unit,
    onSavePayment: (invoiceId: String, learnerId: String, learnerName: String, amount: Double, method: PaymentMethod, mpesaCode: String, ref: String) -> Unit
) {
    var selectedLearner by remember { mutableStateOf(learners.firstOrNull()) }
    var amountText by remember { mutableStateOf("15000") }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.MPESA) }
    var mpesaCode by remember { mutableStateOf("TBK" + (10000..99999).random() + "X") }
    var reference by remember { mutableStateOf("School fees installment") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Fee Payment", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Select Learner:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(learners) { l ->
                        FilterChip(
                            selected = selectedLearner?.id == l.id,
                            onClick = { selectedLearner = l },
                            label = { Text(l.name, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount Paid (KES) *") },
                    modifier = Modifier.fillMaxWidth().testTag("payment_input_amount")
                )

                Text("Payment Method:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PaymentMethod.values().forEach { m ->
                        FilterChip(
                            selected = selectedMethod == m,
                            onClick = { selectedMethod = m },
                            label = { Text(m.label, fontSize = 11.sp) }
                        )
                    }
                }

                if (selectedMethod == PaymentMethod.MPESA) {
                    OutlinedTextField(
                        value = mpesaCode,
                        onValueChange = { mpesaCode = it },
                        label = { Text("M-PESA Transaction Reference *") },
                        modifier = Modifier.fillMaxWidth().testTag("payment_input_mpesa")
                    )
                }

                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("Notes / Reference") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 10000.0
                    val learner = selectedLearner ?: learners.first()
                    val invoice = invoices.find { it.learnerId == learner.id } ?: invoices.firstOrNull()
                    val invId = invoice?.id ?: "inv_gen"
                    onSavePayment(invId, learner.id, learner.name, amt, selectedMethod, mpesaCode, reference)
                },
                modifier = Modifier.testTag("btn_confirm_payment")
            ) {
                Text("Record & Generate Receipt")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ReceiptDialog(
    payment: FeePayment,
    currentUser: User,
    onDismiss: () -> Unit,
    onVoid: (reason: String) -> Unit
) {
    var showVoidPrompt by remember { mutableStateOf(false) }
    var voidReason by remember { mutableStateOf("Duplicate entry") }

    val canVoid = (currentUser.role == Role.ACCOUNTANT || currentUser.role == Role.DIRECTOR) && payment.status != "VOID"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Mary Royal School", fontWeight = FontWeight.Black, fontSize = 18.sp, color = RoyalNavy)
                    Text("Obunga, Kisumu · Hard work pays", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(
                    text = payment.status,
                    containerColor = if (payment.status == "VOID") AccentRoseLight else AccentEmeraldLight,
                    contentColor = if (payment.status == "VOID") AccentRose else AccentEmerald
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Receipt Number:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(payment.receiptNo, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Learner Name:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(payment.learnerName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Payment Date:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(payment.date, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Payment Method:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(payment.method.label, fontSize = 12.sp)
                }
                if (payment.mpesaCode.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("M-PESA Code:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(payment.mpesaCode, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentEmerald)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(RoyalBlue.copy(alpha = 0.08f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AMOUNT RECEIVED", style = MaterialTheme.typography.labelSmall, color = RoyalBlueDark)
                        Text("KES ${payment.amount.toInt()}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = RoyalNavy)
                    }
                }

                if (payment.status == "VOID") {
                    Text("VOID REASON: ${payment.voidReason}", color = AccentRose, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                if (showVoidPrompt) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = voidReason,
                        onValueChange = { voidReason = it },
                        label = { Text("Reason for invalidating receipt") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { onVoid(voidReason) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRose),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Void Receipt")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        },
        dismissButton = {
            if (canVoid && !showVoidPrompt) {
                TextButton(onClick = { showVoidPrompt = true }) {
                    Text("Void Receipt", color = AccentRose)
                }
            }
        }
    )
}
