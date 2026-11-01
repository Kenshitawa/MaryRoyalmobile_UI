package com.example.maryroyal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.lifecycleScope
import com.example.maryroyal.data.local.MaryRoyalDatabase
import com.example.maryroyal.data.model.Role
import com.example.maryroyal.data.model.User
import com.example.maryroyal.data.repository.MaryRoyalRepository
import com.example.maryroyal.ui.screens.*
import com.example.maryroyal.ui.theme.MaryRoyalTheme
import com.example.maryroyal.ui.theme.RoyalNavy

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = MaryRoyalDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = MaryRoyalRepository(db.dao())

        setContent {
            MaryRoyalTheme {
                MaryRoyalApp(repository = repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaryRoyalApp(repository: MaryRoyalRepository) {
    val users by repository.getAllUsers().collectAsState(initial = emptyList())
    var currentUser by remember {
        mutableStateOf(
            User(
                id = "user_dir",
                name = "Dr. Mary Otieno",
                email = "director@maryroyal.sc.ke",
                role = Role.DIRECTOR,
                phone = "+254 712 345 678",
                staffNumber = "MRS-001",
                employmentTitle = "School Director"
            )
        )
    }

    LaunchedEffect(users) {
        if (users.isNotEmpty() && users.none { it.id == currentUser.id }) {
            currentUser = users.first()
        }
    }

    var currentScreen by remember { mutableStateOf("dashboard") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "MR",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = RoyalNavy
                            )
                        }
                        Column {
                            Text(
                                text = "MARY ROYAL",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Obunga, Kisumu · ${currentUser.role.title}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFBAE6FD),
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RoyalNavy,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(
                        onClick = {
                            // Cycle to next role
                            if (users.isNotEmpty()) {
                                val nextIndex = (users.indexOfFirst { it.id == currentUser.id } + 1) % users.size
                                currentUser = users[nextIndex]
                            }
                        },
                        modifier = Modifier.testTag("btn_switch_role")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwitchAccount,
                            contentDescription = "Switch Active Role",
                            tint = Color(0xFFFBBF24)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == "dashboard",
                    onClick = { currentScreen = "dashboard" },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Overview", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_item_dashboard")
                )
                NavigationBarItem(
                    selected = currentScreen == "admissions",
                    onClick = { currentScreen = "admissions" },
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Admissions") },
                    label = { Text("Admit", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_item_admissions")
                )
                NavigationBarItem(
                    selected = currentScreen == "learners",
                    onClick = { currentScreen = "learners" },
                    icon = { Icon(Icons.Default.People, contentDescription = "Learners") },
                    label = { Text("Learners", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_item_learners")
                )
                NavigationBarItem(
                    selected = currentScreen == "attendance",
                    onClick = { currentScreen = "attendance" },
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Attendance") },
                    label = { Text("Roll Call", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_item_attendance")
                )
                NavigationBarItem(
                    selected = currentScreen == "assessments",
                    onClick = { currentScreen = "assessments" },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "CBE Grades") },
                    label = { Text("CBE", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_item_assessments")
                )
                NavigationBarItem(
                    selected = currentScreen == "finance",
                    onClick = { currentScreen = "finance" },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Finance") },
                    label = { Text("Finance", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_item_finance")
                )
                NavigationBarItem(
                    selected = currentScreen == "reports",
                    onClick = { currentScreen = "reports" },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Reports") },
                    label = { Text("Reports", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_item_reports")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "dashboard" -> DashboardScreen(
                    repository = repository,
                    currentUser = currentUser,
                    onSwitchUser = { currentUser = it },
                    onNavigate = { screen -> currentScreen = screen }
                )
                "admissions" -> AdmissionsScreen(
                    repository = repository,
                    currentUser = currentUser,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
                "learners" -> LearnersScreen(
                    repository = repository,
                    currentUser = currentUser,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
                "attendance" -> AttendanceScreen(
                    repository = repository,
                    currentUser = currentUser,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
                "assessments" -> AssessmentsScreen(
                    repository = repository,
                    currentUser = currentUser,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
                "finance" -> FinanceScreen(
                    repository = repository,
                    currentUser = currentUser,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
                "timetable" -> TimetableScreen(
                    repository = repository,
                    currentUser = currentUser,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
                "reports" -> ReportsAndAuditScreen(
                    repository = repository,
                    currentUser = currentUser,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
                "welfare" -> WelfareAndMessagingScreen(
                    repository = repository,
                    currentUser = currentUser,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
                else -> DashboardScreen(
                    repository = repository,
                    currentUser = currentUser,
                    onSwitchUser = { currentUser = it },
                    onNavigate = { screen -> currentScreen = screen }
                )
            }
        }
    }
}
