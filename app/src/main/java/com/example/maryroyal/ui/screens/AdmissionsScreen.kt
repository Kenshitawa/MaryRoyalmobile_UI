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
import com.example.maryroyal.data.model.Learner
import com.example.maryroyal.data.model.User
import com.example.maryroyal.data.repository.MaryRoyalRepository
import com.example.maryroyal.ui.components.BentoCard
import com.example.maryroyal.ui.components.HeroBanner
import com.example.maryroyal.ui.components.StatusBadge
import com.example.maryroyal.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdmissionsScreen(
    repository: MaryRoyalRepository,
    currentUser: User,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allLearners by repository.getAllLearners().collectAsState(initial = emptyList())
    var showWizardModal by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGradeFilter by remember { mutableStateOf("All") }

    val grades = listOf("All", "PP1", "PP2", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6", "Grade 7", "Grade 8", "Grade 9")

    val filteredLearners = allLearners.filter { learner ->
        val matchesQuery = learner.name.contains(searchQuery, ignoreCase = true) ||
                learner.admissionNo.contains(searchQuery, ignoreCase = true)
        val matchesGrade = if (selectedGradeFilter == "All") true else learner.grade == selectedGradeFilter
        matchesQuery && matchesGrade
    }

    if (showWizardModal) {
        AdmissionWizardDialog(
            onDismiss = { showWizardModal = false },
            onSaveLearner = { newLearner ->
                coroutineScope.launch {
                    repository.insertLearner(newLearner)
                    repository.logAudit(
                        actorName = currentUser.name,
                        role = currentUser.role.name,
                        action = "ADMISSION_ENROLLED",
                        entity = "Learner",
                        entityId = newLearner.id
                    )
                    showWizardModal = false
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroBanner(
                tag = "Admissions & Enrollment Office",
                title = "Learner Admissions Desk",
                subtitle = "7-step verified CBE admissions packet, guardian data, safeguarding, and MOE UPI registry.",
                actionButton = {
                    Button(
                        onClick = { showWizardModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_new_admission")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = RoyalNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start 7-Step Admission Wizard",
                            color = RoyalNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }

        // Search & Filter
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by learner name or admission no...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_admissions"),
                shape = RoundedCornerShape(14.dp)
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(grades) { grade ->
                    FilterChip(
                        selected = selectedGradeFilter == grade,
                        onClick = { selectedGradeFilter = grade },
                        label = { Text(grade, fontSize = 12.sp) },
                        shape = CircleShape
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ENROLLED ADMISSION RECORDS (${filteredLearners.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(filteredLearners) { learner ->
            BentoCard(modifier = Modifier.testTag("learner_item_${learner.id}")) {
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
                                text = learner.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(
                                text = learner.admissionStatus,
                                containerColor = AccentEmeraldLight,
                                contentColor = AccentEmerald
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${learner.admissionNo} · ${learner.grade} (Stream ${learner.stream}) · ${learner.gender}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Guardian: ${learner.guardianName} (${learner.guardianRelation}) - ${learner.guardianPhone}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RoyalBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = RoyalBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdmissionWizardDialog(
    onDismiss: () -> Unit,
    onSaveLearner: (Learner) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    // Form states
    var name by remember { mutableStateOf("") }
    var admissionNo by remember { mutableStateOf("MRS-2026-${(100..999).random()}") }
    var grade by remember { mutableStateOf("Grade 1") }
    var stream by remember { mutableStateOf("Royal") }
    var gender by remember { mutableStateOf("Female") }
    var upiNumber by remember { mutableStateOf("KICD-${(100000..999999).random()}") }
    var dob by remember { mutableStateOf("2019-03-15") }

    var village by remember { mutableStateOf("Obunga") }
    var physicalAddress by remember { mutableStateOf("Obunga, Kisumu") }

    var conditions by remember { mutableStateOf("None") }
    var allergies by remember { mutableStateOf("None") }
    var bloodGroup by remember { mutableStateOf("O+") }

    var guardianName by remember { mutableStateOf("") }
    var guardianRelation by remember { mutableStateOf("Mother") }
    var guardianPhone by remember { mutableStateOf("+254 7") }

    var emergencyContactName by remember { mutableStateOf("") }
    var emergencyContactPhone by remember { mutableStateOf("+254 7") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Step $step of 7: " + when (step) {
                        1 -> "Learner Identity"
                        2 -> "Academic Placement"
                        3 -> "Residence & Location"
                        4 -> "Medical & Safeguarding"
                        5 -> "Primary Guardian"
                        6 -> "Emergency Contact"
                        else -> "Final Verification"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = RoyalNavy
                )
                LinearProgressIndicator(
                    progress = { step / 7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = RoyalBlue,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (step) {
                    1 -> {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Learner Full Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("wizard_input_name")
                        )
                        OutlinedTextField(
                            value = admissionNo,
                            onValueChange = { admissionNo = it },
                            label = { Text("Admission Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = upiNumber,
                            onValueChange = { upiNumber = it },
                            label = { Text("Nemis / KICD UPI Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    2 -> {
                        OutlinedTextField(
                            value = grade,
                            onValueChange = { grade = it },
                            label = { Text("Class / Grade (e.g. PP1 to Grade 9) *") },
                            modifier = Modifier.fillMaxWidth().testTag("wizard_input_grade")
                        )
                        OutlinedTextField(
                            value = stream,
                            onValueChange = { stream = it },
                            label = { Text("Stream (e.g. Royal, Crown, Gold)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = { Text("Gender (Male / Female)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    3 -> {
                        OutlinedTextField(
                            value = village,
                            onValueChange = { village = it },
                            label = { Text("Village / Estate (e.g. Obunga)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = physicalAddress,
                            onValueChange = { physicalAddress = it },
                            label = { Text("Physical Address / Landmark") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    4 -> {
                        OutlinedTextField(
                            value = conditions,
                            onValueChange = { conditions = it },
                            label = { Text("Known Medical Conditions (Restricted)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = allergies,
                            onValueChange = { allergies = it },
                            label = { Text("Allergies") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = { bloodGroup = it },
                            label = { Text("Blood Group") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    5 -> {
                        OutlinedTextField(
                            value = guardianName,
                            onValueChange = { guardianName = it },
                            label = { Text("Guardian Full Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("wizard_input_guardian")
                        )
                        OutlinedTextField(
                            value = guardianRelation,
                            onValueChange = { guardianRelation = it },
                            label = { Text("Relationship (Mother/Father/Guardian)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = guardianPhone,
                            onValueChange = { guardianPhone = it },
                            label = { Text("Guardian Phone Number *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    6 -> {
                        OutlinedTextField(
                            value = emergencyContactName,
                            onValueChange = { emergencyContactName = it },
                            label = { Text("Emergency Contact Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = emergencyContactPhone,
                            onValueChange = { emergencyContactPhone = it },
                            label = { Text("Emergency Contact Phone") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    7 -> {
                        Text(
                            text = "Please verify the learner admission details before final submission:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text("• Name: $name", style = MaterialTheme.typography.bodyMedium)
                        Text("• Admission No: $admissionNo", style = MaterialTheme.typography.bodyMedium)
                        Text("• Class: $grade ($stream)", style = MaterialTheme.typography.bodyMedium)
                        Text("• Guardian: $guardianName ($guardianRelation) - $guardianPhone", style = MaterialTheme.typography.bodyMedium)
                        Text("• Location: $village, $physicalAddress", style = MaterialTheme.typography.bodyMedium)
                        Text("• Medical: Blood $bloodGroup, Allergies: $allergies", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step < 7) {
                        step++
                    } else {
                        val newLearner = Learner(
                            admissionNo = admissionNo,
                            name = if (name.isBlank()) "New Learner" else name,
                            grade = grade,
                            stream = stream,
                            gender = gender,
                            upiNumber = upiNumber,
                            dateOfBirth = dob,
                            admissionStatus = "APPROVED",
                            village = village,
                            physicalAddress = physicalAddress,
                            knownConditions = conditions,
                            allergies = allergies,
                            bloodGroup = bloodGroup,
                            guardianName = if (guardianName.isBlank()) "Guardian" else guardianName,
                            guardianPhone = guardianPhone,
                            guardianRelation = guardianRelation,
                            emergencyContactName = emergencyContactName,
                            emergencyContactPhone = emergencyContactPhone
                        )
                        onSaveLearner(newLearner)
                    }
                },
                modifier = Modifier.testTag("wizard_btn_next")
            ) {
                Text(if (step < 7) "Next &rarr;" else "Enroll Learner")
            }
        },
        dismissButton = {
            if (step > 1) {
                TextButton(onClick = { step-- }) {
                    Text("&larr; Back")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
