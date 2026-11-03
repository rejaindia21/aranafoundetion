package com.arnafoundation.app.ui.screens.employee

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arnafoundation.app.data.local.entities.BorrowerEntity
import com.arnafoundation.app.data.local.entities.EmiEntity
import com.arnafoundation.app.data.local.entities.UserSessionEntity
import com.arnafoundation.app.data.model.EmiStatus
import com.arnafoundation.app.data.model.PaymentMode
import com.arnafoundation.app.data.model.UserRole
import com.arnafoundation.app.ui.components.AppButton
import com.arnafoundation.app.ui.components.AppCard
import com.arnafoundation.app.ui.components.StatusBadge
import com.arnafoundation.app.ui.theme.*
import com.arnafoundation.app.ui.viewmodel.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeMainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedEmployeeTab.collectAsStateWithLifecycle()
    val borrowers by viewModel.allBorrowers.collectAsStateWithLifecycle()
    val allLoans by viewModel.allLoans.collectAsStateWithLifecycle()
    val session by viewModel.sessionState.collectAsStateWithLifecycle()

    var activeDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(ArnaGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = ArnaGreenPrimary, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "ARNA GRAM UNNAYA FOUNDATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ArnaGreenPrimary,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                "FIELD OFFICER PORTAL",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ArnaGreenDark
                            )
                            val officerName = session?.fullName?.ifBlank { null } ?: session?.userId ?: "Field Officer"
                            val officerId = session?.userId ?: "EMP-01"
                            Text(
                                "Welcome, $officerName",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                "Employee ID: $officerId",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("employee_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color(0xFFBA1A1A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setEmployeeTab(0) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Emerald800, indicatorColor = Emerald100)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setEmployeeTab(1) },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Customers") },
                    label = { Text("Customers", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Emerald800, indicatorColor = Emerald100)
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setEmployeeTab(2) },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Reports") },
                    label = { Text("Reports", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Emerald800, indicatorColor = Emerald100)
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.setEmployeeTab(3) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Emerald800, indicatorColor = Emerald100)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SurfaceCream)
        ) {
            when (selectedTab) {
                0 -> EmployeeDashboardTab(
                    borrowers = borrowers,
                    onNavigateTab = { viewModel.setEmployeeTab(it) },
                    onOpenAction = { activeDialog = it },
                    onLogout = { viewModel.logout() }
                )
                1 -> EmployeeCustomersTab(
                    borrowers = borrowers,
                    viewModel = viewModel
                )
                2 -> EmployeeReportsTab(borrowers = borrowers)
                3 -> EmployeeProfileTab(
                    session = session,
                    onLogout = { viewModel.logout() }
                )
            }

            activeDialog?.let { dialogType ->
                EmployeeActionDialog(
                    title = dialogType,
                    borrowers = borrowers,
                    onDismiss = { activeDialog = null }
                )
            }
        }
    }
}

@Composable
fun EmployeeDashboardTab(
    borrowers: List<BorrowerEntity>,
    onNavigateTab: (Int) -> Unit,
    onOpenAction: (String) -> Unit,
    onLogout: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Target Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Emerald900)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("TODAY'S VILLAGE COLLECTION TARGET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald100, letterSpacing = 1.sp)
                Text("₹38,500 / ₹45,000", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { 38500f / 45000f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = Amber400,
                    trackColor = Emerald800
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Achievement: 85.5%", fontSize = 12.sp, color = Amber400, fontWeight = FontWeight.Bold)
                    Text("Remaining: ₹6,500", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        // DAILY WORK SECTION
        Text("DAILY WORK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald900, letterSpacing = 1.sp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EmployeeActionTile(
                icon = Icons.Default.EventAvailable,
                label = "Attendance",
                modifier = Modifier.weight(1f),
                onClick = { onOpenAction("Daily Attendance") }
            )
            EmployeeActionTile(
                icon = Icons.Default.PersonAdd,
                label = "New Customers",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(1) }
            )
            EmployeeActionTile(
                icon = Icons.Default.Payment,
                label = "Loan EMI Payment",
                modifier = Modifier.weight(1f),
                onClick = { onOpenAction("Loan EMI Payment") }
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EmployeeActionTile(
                icon = Icons.Default.Refresh,
                label = "Renewal Investments",
                modifier = Modifier.weight(1f),
                onClick = { onOpenAction("Renewal Investments") }
            )
            EmployeeActionTile(
                icon = Icons.Default.Warning,
                label = "Due EMI List",
                modifier = Modifier.weight(1f),
                onClick = { onOpenAction("Due EMI List") }
            )
            EmployeeActionTile(
                icon = Icons.Default.Assessment,
                label = "Collection Reports",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(2) }
            )
        }

        // EMPLOYEE SERVICES SECTION
        Text("EMPLOYEE SERVICES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald900, letterSpacing = 1.sp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EmployeeActionTile(
                icon = Icons.Default.AccountBalanceWallet,
                label = "My Wallet",
                modifier = Modifier.weight(1f),
                onClick = { onOpenAction("My Field Wallet") }
            )
            EmployeeActionTile(
                icon = Icons.Default.Savings,
                label = "DRD/RD Statement",
                modifier = Modifier.weight(1f),
                onClick = { onOpenAction("DRD/RD Statement") }
            )
            EmployeeActionTile(
                icon = Icons.Default.ReceiptLong,
                label = "EMI Statement",
                modifier = Modifier.weight(1f),
                onClick = { onOpenAction("EMI Statement") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun EmployeeCustomersTab(
    borrowers: List<BorrowerEntity>,
    viewModel: MainViewModel
) {
    var selectedSubTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { selectedSubTab = 0 },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedSubTab == 0) Emerald900 else Color.White,
                    contentColor = if (selectedSubTab == 0) Color.White else Emerald900
                ),
                border = BorderStroke(1.dp, if (selectedSubTab == 0) Emerald900 else OutlineSoft)
            ) {
                Text("Center Members (${borrowers.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { selectedSubTab = 1 },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedSubTab == 1) Emerald900 else Color.White,
                    contentColor = if (selectedSubTab == 1) Color.White else Emerald900
                ),
                border = BorderStroke(1.dp, if (selectedSubTab == 1) Emerald900 else OutlineSoft)
            ) {
                Text("New Member KYC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedSubTab == 0) {
                EmployeeBorrowerListTab(borrowers = borrowers)
            } else {
                EmployeeOnboardTab(
                    onRegister = { name, phone, email, village, district, aadhaar, pan, occ, inc, dep ->
                        viewModel.onboardBorrower(name, phone, email, village, district, aadhaar, pan, occ, inc, dep)
                        selectedSubTab = 0
                    }
                )
            }
        }
    }
}

@Composable
fun EmployeeReportsTab(borrowers: List<BorrowerEntity>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("FIELD COLLECTION & AUDIT REPORTS", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Emerald900)
        Text("Daily village route progress & recovery performance", fontSize = 12.sp, color = TextMuted)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, OutlineSoft)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Today's Collections", fontSize = 11.sp, color = TextMuted)
                    Text("₹38,500.00", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                }
                Column {
                    Text("Vouchers Issued", fontSize = 11.sp, color = TextMuted)
                    Text("8 Borrowers", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column {
                    Text("Pending Route", fontSize = 11.sp, color = TextMuted)
                    Text("2 Villages", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Amber800)
                }
            }
        }

        Text("TODAY'S FIELD COLLECTION ROUTE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 1.sp)

        listOf(
            Triple("Nabagram Center #04", "Sunita Mondal (Dairy Loan)", "₹4,541.67 • Collected"),
            Triple("Sonarpur South Village", "Ramesh Chandra Das (Handloom)", "₹4,380.00 • Due Today"),
            Triple("Ranaghat East SHG", "Anjali Barman (Spice SHG)", "KYC Verification Pending"),
            Triple("Krishnanagar Block 1", "Bikram Halder (Agri Solar)", "₹3,900.00 • Scheduled 4 PM")
        ).forEach { item ->
            AppCard(backgroundColor = Color.White) {
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
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Emerald800, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(item.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(item.second, fontSize = 11.sp, color = TextMuted)
                        }
                    }
                    Text(item.third, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (item.third.contains("Collected")) SuccessGreen else Amber800)
                }
            }
        }
    }
}

@Composable
fun EmployeeProfileTab(
    session: UserSessionEntity?,
    onLogout: () -> Unit
) {
    val officerName = session?.fullName?.ifBlank { null } ?: session?.userId ?: "Field Officer"
    val officerId = session?.userId ?: "EMP-01"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("OFFICER PROFILE", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Emerald900)
        Text("Authorized Field Representative Information", fontSize = 12.sp, color = TextMuted)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, OutlineSoft)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Amber100),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = Amber800, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(officerName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Employee ID: $officerId", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Emerald800)
                Spacer(modifier = Modifier.height(6.dp))
                StatusBadge(statusText = "FIELD OFFICER")
            }
        }

        Text("DUTY DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald900, letterSpacing = 1.sp)

        AppCard(backgroundColor = Color.White) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                EmployeeDetailRow(label = "Branch", value = "Nadia Central District Hub")
                EmployeeDetailRow(label = "Assigned Route", value = "Nabagram & Sonarpur Cluster")
                EmployeeDetailRow(label = "Daily Shift Status", value = "Checked-In (08:30 AM)")
                EmployeeDetailRow(label = "Authority", value = "Spot EMI Collection & KYC Entry")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFEBEA),
                contentColor = Color(0xFFBA1A1A)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("employee_signout_button"),
            shape = RoundedCornerShape(100.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out of Field Officer Portal", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmployeeDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextMuted)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

@Composable
private fun EmployeeActionTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, OutlineSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ArnaGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = ArnaGreenPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun EmployeeActionDialog(
    title: String,
    borrowers: List<BorrowerEntity>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Emerald900)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                when (title) {
                    "Daily Attendance" -> {
                        Text("Shift Status: ACTIVE (Present)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        Text("Check-In Time: 08:30 AM • Location: Nadia Field Office", fontSize = 12.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Attendance recorded on ARNA Foundation GPS biometric server.", fontSize = 11.sp, color = TextMuted)
                    }
                    "My Field Wallet" -> {
                        Text("Cash-in-Hand Float: ₹38,500.00", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Emerald900)
                        Text("Collected from 8 beneficiaries today. Ready for end-of-day branch deposit.", fontSize = 12.sp, color = TextMuted)
                    }
                    "Due EMI List" -> {
                        Text("Beneficiaries with Due EMIs on today's route:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        borrowers.take(3).forEach { b ->
                            Text("• ${b.name} (${b.borrowerId}) - ₹4,541.67 Due", fontSize = 12.sp, color = Amber800)
                        }
                    }
                    "DRD/RD Statement" -> {
                        Text("Daily Recurring Deposit Collections:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("• Total Collected Today: ₹6,200.00 across 12 members", fontSize = 12.sp, color = TextMuted)
                    }
                    "EMI Statement" -> {
                        Text("EMI Receipts Issued Today:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("• Total Vouchers: 8 • Total Amount: ₹32,300.00", fontSize = 12.sp, color = TextMuted)
                    }
                    else -> {
                        Text(
                            "Service synchronized with ARNA Foundation Field Operations Gateway.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmployeeSpotCollectionTab(
    borrowers: List<BorrowerEntity>,
    viewModel: MainViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBorrower by remember { mutableStateOf<BorrowerEntity?>(borrowers.firstOrNull()) }
    var amountText by remember { mutableStateOf("4541.67") }
    var selectedMode by remember { mutableStateOf(PaymentMode.CASH) }
    var collectionSuccess by remember { mutableStateOf<String?>(null) }

    val filteredBorrowers = borrowers.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.borrowerId.contains(searchQuery, ignoreCase = true) || it.village.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("On-Spot EMI Field Collection", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Emerald900)
        Text("Collect cash or QR payments and generate instant voucher", fontSize = 12.sp, color = TextMuted)

        Spacer(modifier = Modifier.height(16.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Borrower by Name / ID / Village") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Emerald800) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Borrower selection list
        Text("SELECT BORROWER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))

        filteredBorrowers.take(3).forEach { b ->
            val isSelected = selectedBorrower?.borrowerId == b.borrowerId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Emerald50 else Color.White)
                    .border(1.dp, if (isSelected) Emerald600 else OutlineSoft, RoundedCornerShape(10.dp))
                    .clickable {
                        selectedBorrower = b
                        amountText = "4541.67"
                    }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(b.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("${b.borrowerId} • ${b.village}", fontSize = 11.sp, color = TextMuted)
                }
                RadioButton(selected = isSelected, onClick = { selectedBorrower = b })
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Collection Form Card
        if (selectedBorrower != null) {
            AppCard(backgroundColor = Color.White) {
                Text("COLLECTION DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Collection Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Payment Channel Received:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(PaymentMode.CASH, PaymentMode.UPI, PaymentMode.WALLET).forEach { mode ->
                        val isModeSelected = selectedMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isModeSelected) Emerald800 else Color(0xFFF1F5F9))
                                .clickable { selectedMode = mode }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                mode.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isModeSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                AppButton(
                    text = "Record Spot Collection & Issue Slip",
                    onClick = {
                        val dummyEmi = EmiEntity(
                            emiId = "AGU-SPOT-${System.currentTimeMillis() % 10000}",
                            loanId = "AGU-LN-2024-089",
                            borrowerId = selectedBorrower!!.borrowerId,
                            borrowerName = selectedBorrower!!.name,
                            installmentNo = 5,
                            totalInstallments = 12,
                            dueDate = "15 Sep 2026",
                            amount = amountText.toDoubleOrNull() ?: 4541.67,
                            principalPortion = 4166.67,
                            interestPortion = 375.0,
                            status = EmiStatus.PENDING
                        )
                        viewModel.payEmi(dummyEmi, selectedMode, "EMP-001 (Partha B.)")
                    }
                )
            }
        }
    }
}

@Composable
fun EmployeeOnboardTab(
    onRegister: (name: String, phone: String, email: String, village: String, district: String, aadhaar: String, pan: String, occ: String, inc: Double, dep: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("Nadia") }
    var aadhaar by remember { mutableStateOf("") }
    var pan by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("Handloom & Agriculture") }
    var incomeText by remember { mutableStateOf("22000") }
    var depositText by remember { mutableStateOf("1000") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("New Member KYC Onboarding", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Emerald900)
        Text("Enroll village borrower into Arna Gram cooperative network", fontSize = 12.sp, color = TextMuted)

        Spacer(modifier = Modifier.height(16.dp))

        AppCard(backgroundColor = Color.White) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Borrower Full Name *") },
                modifier = Modifier.fillMaxWidth().testTag("onboard_name"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Primary Mobile Number *") },
                modifier = Modifier.fillMaxWidth().testTag("onboard_phone"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = village,
                onValueChange = { village = it },
                label = { Text("Village / Gram Panchayat *") },
                modifier = Modifier.fillMaxWidth().testTag("onboard_village"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = aadhaar,
                onValueChange = { aadhaar = it },
                label = { Text("Aadhaar Number (12 Digits)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = occupation,
                onValueChange = { occupation = it },
                label = { Text("Trade / Occupation") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = incomeText,
                onValueChange = { incomeText = it },
                label = { Text("Monthly Household Income (₹)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = depositText,
                onValueChange = { depositText = it },
                label = { Text("Initial Savings Account Deposit (₹)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                text = "Enroll Member & Open Account",
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        val inc = incomeText.toDoubleOrNull() ?: 20000.0
                        val dep = depositText.toDoubleOrNull() ?: 500.0
                        onRegister(name, phone, "", village, district, aadhaar, pan, occupation, inc, dep)
                    }
                },
                testTag = "onboard_submit"
            )
        }
    }
}

@Composable
fun EmployeeBorrowerListTab(borrowers: List<BorrowerEntity>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Village Center Members (${borrowers.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Emerald900)
        Text("Registered self-help group borrowers & account holders", fontSize = 12.sp, color = TextMuted)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(borrowers, key = { it.borrowerId }) { b ->
                AppCard(backgroundColor = Color.White) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(b.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${b.borrowerId} • ${b.phone}", fontSize = 11.sp, color = TextMuted)
                            Text(b.village, fontSize = 11.sp, color = Emerald800)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Wallet: ₹${String.format(Locale.getDefault(), "%,.0f", b.walletBalance)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            StatusBadge(statusText = b.status)
                        }
                    }
                }
            }
        }
    }
}
