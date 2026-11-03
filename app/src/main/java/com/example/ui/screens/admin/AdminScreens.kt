package com.arnafoundation.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arnafoundation.app.data.local.entities.BorrowerEntity
import com.arnafoundation.app.data.local.entities.InvestmentEntity
import com.arnafoundation.app.data.local.entities.LoanEntity
import com.arnafoundation.app.data.local.entities.UserSessionEntity
import com.arnafoundation.app.data.model.LoanStatus
import com.arnafoundation.app.ui.components.AppCard
import com.arnafoundation.app.ui.components.StatusBadge
import com.arnafoundation.app.ui.theme.*
import com.arnafoundation.app.ui.viewmodel.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedAdminTab.collectAsStateWithLifecycle()
    val borrowers by viewModel.allBorrowers.collectAsStateWithLifecycle()
    val loans by viewModel.allLoans.collectAsStateWithLifecycle()
    val investments by viewModel.allInvestments.collectAsStateWithLifecycle()
    val session by viewModel.sessionState.collectAsStateWithLifecycle()

    var activeDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(ArnaGreenDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "ARNA GRAM UNNAYA FOUNDATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ArnaGreenPrimary,
                                letterSpacing = 1.2.sp
                            )

                            Text(
                                text = "DIGITAL ADMIN PORTAL",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ArnaGreenDark
                            )

                            val adminName =
                                session?.fullName
                                    ?.ifBlank { null }
                                    ?: "ARNA Foundation Admin"

                            Text(
                                text = "Welcome, $adminName",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.logout()
                        },
                        modifier = Modifier.testTag("admin_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color(0xFFBA1A1A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        viewModel.setAdminTab(0)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald800,
                        indicatorColor = Emerald100
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        viewModel.setAdminTab(1)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Branch Wallet"
                        )
                    },
                    label = {
                        Text(
                            text = "Branch Wallet",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald800,
                        indicatorColor = Emerald100
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        viewModel.setAdminTab(2)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "Reports"
                        )
                    },
                    label = {
                        Text(
                            text = "Reports",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald800,
                        indicatorColor = Emerald100
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = {
                        viewModel.setAdminTab(3)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text(
                            text = "Profile",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald800,
                        indicatorColor = Emerald100
                    )
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

                0 -> {
                    AdminOverviewTab(
                        borrowers = borrowers,
                        loans = loans,
                        investments = investments,
                        onNavigateTab = {
                            viewModel.setAdminTab(it)
                        },
                        onOpenAction = {
                            activeDialog = it
                        }
                    )
                }

                1 -> {
                    AdminBranchWalletTab(
                        borrowers = borrowers,
                        loans = loans
                    )
                }

                2 -> {
                    AdminReportsTab(
                        loans = loans,
                        borrowers = borrowers,
                        investments = investments,
                        onApproveLoan = {
                            viewModel.approveLoan(it)
                        },
                        onRejectLoan = {
                            viewModel.rejectLoan(it)
                        }
                    )
                }

                3 -> {
                    AdminProfileTab(
                        session = session,
                        onLogout = {
                            viewModel.logout()
                        }
                    )
                }
            }

            activeDialog?.let { dialogTitle ->

                AdminActionDialog(
                    title = dialogTitle,
                    onDismiss = {
                        activeDialog = null
                    }
                )
            }
        }
    }
}

@Composable
fun AdminOverviewTab(
    borrowers: List<BorrowerEntity>,
    loans: List<LoanEntity>,
    investments: List<InvestmentEntity>,
    onNavigateTab: (Int) -> Unit = {},
    onOpenAction: (String) -> Unit = {}
) {

    val totalDisbursed =
        loans
            .filter {
                it.status != LoanStatus.PENDING_APPROVAL &&
                    it.status != LoanStatus.REJECTED
            }
            .sumOf {
                it.principalAmount
            }

    val totalOutstanding =
        loans
            .filter {
                it.status == LoanStatus.ACTIVE
            }
            .sumOf {
                it.outstandingAmount
            }

    val totalInvested =
        investments.sumOf {
            it.principalAmount
        }

    val activeLoansCount =
        loans.count {
            it.status == LoanStatus.ACTIVE
        }

    val totalCustomerWallet =
        borrowers.sumOf {
            it.walletBalance
        }

    val branchWalletTotal =
        (totalInvested + totalCustomerWallet)
            .coerceAtLeast(0.0)

    val totalWalletBalance =
        branchWalletTotal + totalCustomerWallet

    val depositWallet =
        totalInvested + totalCustomerWallet

    val withdrawWallet =
        loans
            .filter {
                it.status == LoanStatus.ACTIVE ||
                    it.status == LoanStatus.CLOSED
            }
            .sumOf {
                it.totalPaid
            }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "FOUNDATION WALLETS & PORTFOLIO",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AdminMetricCard(
                title = "Branch Wallet",
                value = "₹${
                    String.format(
                        Locale.getDefault(),
                        "%,.0f",
                        branchWalletTotal
                    )
                }",
                subtext = "Vault & Cash",
                color = Emerald800,
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigateTab(1)
                }
            )

            AdminMetricCard(
                title = "Total Wallet Balance",
                value = "₹${
                    String.format(
                        Locale.getDefault(),
                        "%,.0f",
                        totalWalletBalance
                    )
                }",
                subtext = "Treasury Total",
                color = Emerald900,
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigateTab(1)
                }
            )

            AdminMetricCard(
                title = "Deposit Wallet",
                value = "₹${
                    String.format(
                        Locale.getDefault(),
                        "%,.0f",
                        depositWallet
                    )
                }",
                subtext = "Bonds & Savings",
                color = InfoBlue,
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Deposit Wallet Ledger")
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AdminMetricCard(
                title = "Withdraw Wallet",
                value = "₹${
                    String.format(
                        Locale.getDefault(),
                        "%,.0f",
                        withdrawWallet
                    )
                }",
                subtext = "Disbursement Fund",
                color = Amber800,
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Withdraw Wallet Ledger")
                }
            )

            AdminMetricCard(
                title = "Branches",
                value = "4 Active",
                subtext = "Nadia & 24 Pgs",
                color = Emerald800,
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Branch Management")
                }
            )

            AdminMetricCard(
                title = "Customers",
                value = "${borrowers.size} Members",
                subtext = "Verified SHG KYC",
                color = Emerald900,
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigateTab(2)
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AdminMetricCard(
                title = "Employees",
                value = "6 Officers",
                subtext = "Field Collection",
                color = InfoBlue,
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Employee Location")
                }
            )

            AdminMetricCard(
                title = "Active Loans",
                value = "$activeLoansCount Loans",
                subtext = "₹${
                    String.format(
                        Locale.getDefault(),
                        "%,.0f",
                        totalOutstanding
                    )
                } Out",
                color = Amber800,
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigateTab(2)
                }
            )

            AdminMetricCard(
                title = "Investments",
                value = "₹${
                    String.format(
                        Locale.getDefault(),
                        "%,.0f",
                        totalInvested
                    )
                }",
                subtext = "${investments.size} Active Bonds",
                color = Emerald800,
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigateTab(2)
                }
            )
        }

        Text(
            text = "QUICK ACTIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AdminActionTile(
                icon = Icons.Default.PersonAdd,
                label = "New Customers",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("New Customer Registration")
                }
            )

            AdminActionTile(
                icon = Icons.Default.MonetizationOn,
                label = "New Loans",
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigateTab(2)
                }
            )

            AdminActionTile(
                icon = Icons.Default.Savings,
                label = "New Investments",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("New Investment Issuance")
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AdminActionTile(
                icon = Icons.Default.Payment,
                label = "Loan EMI Pay",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Loan EMI Payment Gateway")
                }
            )

            AdminActionTile(
                icon = Icons.Default.Refresh,
                label = "Renew Investment",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Renew Investment Certificate")
                }
            )

            AdminActionTile(
                icon = Icons.Default.DoneAll,
                label = "Maturity Payment",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Maturity Settlement & Payout")
                }
            )
        }

        Text(
            text = "ADMIN CENTER",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AdminActionTile(
                icon = Icons.Default.LocationOn,
                label = "Employee Location",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Employee Live GPS Location")
                }
            )

            AdminActionTile(
                icon = Icons.Default.Sms,
                label = "SMS Balance",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("SMS Gateway Credits: 14,850 SMS")
                }
            )

            AdminActionTile(
                icon = Icons.Default.PhoneAndroid,
                label = "Recharge Balance",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Recharge Gateway Balance: ₹12,400.00")
                }
            )

            AdminActionTile(
                icon = Icons.Default.Store,
                label = "Collection Center",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Village Collection Centers (4 Centers)")
                }
            )
        }

        Text(
            text = "MANAGEMENT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AdminActionTile(
                icon = Icons.Default.Payment,
                label = "EMI Collection",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Daily EMI Collection Summary")
                }
            )

            AdminActionTile(
                icon = Icons.Default.TrendingUp,
                label = "Investment Collection",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenAction("Daily RD / DRD Bond Collections")
                }
            )

            AdminActionTile(
                icon = Icons.Default.Assessment,
                label = "Collection Reports",
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigateTab(2)
                }
            )
        }

        Text(
            text = "ORGANIZATION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900,
            letterSpacing = 1.sp
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onOpenAction("Branch Management & Network")
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(
                1.dp,
                OutlineSoft
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Emerald100),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = Emerald800,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "Branch Management",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "4 Active Branches • Nadia, Sonarpur, Ranaghat, Krishnanagar",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun AdminBranchWalletTab(
    borrowers: List<BorrowerEntity>,
    loans: List<LoanEntity>
) {

    val totalDisbursed =
        loans
            .filter {
                it.status != LoanStatus.PENDING_APPROVAL
            }
            .sumOf {
                it.principalAmount
            }

    val totalRecovered =
        loans.sumOf {
            it.totalPaid
        }

    val branchVaults =
        if (borrowers.isNotEmpty()) {

            borrowers
                .groupBy {
                    it.district.ifBlank {
                        "Nadia Central"
                    }
                }
                .map { (district, members) ->

                    val districtDisbursed =
                        loans
                            .filter { loan ->
                                members.any { member ->
                                    member.borrowerId == loan.borrowerId
                                }
                            }
                            .sumOf {
                                it.principalAmount
                            }

                    val districtVault =
                        (
                            members.sumOf {
                                it.walletBalance
                            } + districtDisbursed
                        )
                            .coerceAtLeast(10000.0)

                    Pair(
                        "$district Branch Hub",
                        districtVault
                    )
                }

        } else {

            listOf(
                Pair(
                    "Nadia Central Head Office",
                    0.0
                )
            )
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "BRANCH WALLET & TREASURY",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
        )

        Text(
            text = "Foundation Level Vault & Field Float Management",
            fontSize = 12.sp,
            color = TextMuted
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Emerald900
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "TOTAL TREASURY VAULT RESERVES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald100,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "₹${
                        String.format(
                            Locale.getDefault(),
                            "%,.0f",
                            branchVaults.sumOf { it.second }
                        )
                    }",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(
                    color = Emerald800
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            text = "Disbursed Loans",
                            fontSize = 10.sp,
                            color = Emerald100
                        )

                        Text(
                            text = "₹${
                                String.format(
                                    Locale.getDefault(),
                                    "%,.0f",
                                    totalDisbursed
                                )
                            }",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column {

                        Text(
                            text = "Total Recovery",
                            fontSize = 10.sp,
                            color = Emerald100
                        )

                        Text(
                            text = "₹${
                                String.format(
                                    Locale.getDefault(),
                                    "%,.0f",
                                    totalRecovered
                                )
                            }",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Amber400
                        )
                    }

                    Column {

                        Text(
                            text = "Customer Floats",
                            fontSize = 10.sp,
                            color = Emerald100
                        )

                        Text(
                            text = "₹${
                                String.format(
                                    Locale.getDefault(),
                                    "%,.0f",
                                    borrowers.sumOf {
                                        it.walletBalance
                                    }
                                )
                            }",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Text(
            text = "BRANCH-WISE ALLOCATION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900,
            letterSpacing = 1.sp
        )

        branchVaults.forEach { (branchName, amount) ->

            AppCard(
                backgroundColor = Color.White
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Emerald100),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Emerald800,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {

                            Text(
                                text = branchName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Text(
                                text = "Operational Vault Status: Active",
                                fontSize = 11.sp,
                                color = SuccessGreen
                            )
                        }
                    }

                    Text(
                        text = "₹${
                            String.format(
                                Locale.getDefault(),
                                "%,.0f",
                                amount
                            )
                        }",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )
                }
            }
        }
    }
}

@Composable
fun AdminReportsTab(
    loans: List<LoanEntity>,
    borrowers: List<BorrowerEntity>,
    investments: List<InvestmentEntity>,
    onApproveLoan: (String) -> Unit,
    onRejectLoan: (String) -> Unit
) {

    var selectedSubTab by remember {
        mutableStateOf(0)
    }

    val subTabs =
        listOf(
            "Sanctions",
            "Borrowers",
            "Schemes & Bonds"
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "AUDIT & GOVERNANCE REPORTS",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
        )

        Text(
            text = "Foundation Level Audits, Sanctions & Registries",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            subTabs.forEachIndexed { index, title ->

                val isSelected =
                    selectedSubTab == index

                OutlinedButton(
                    onClick = {
                        selectedSubTab = index
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor =
                            if (isSelected) {
                                Emerald900
                            } else {
                                Color.White
                            },
                        contentColor =
                            if (isSelected) {
                                Color.White
                            } else {
                                Emerald900
                            }
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) {
                            Emerald900
                        } else {
                            OutlineSoft
                        }
                    )
                ) {

                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight =
                            if (isSelected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            when (selectedSubTab) {

                0 -> {
                    AdminSanctionsTab(
                        loans = loans,
                        onApprove = onApproveLoan,
                        onReject = onRejectLoan
                    )
                }

                1 -> {
                    AdminBorrowersMasterTab(
                        borrowers = borrowers
                    )
                }

                2 -> {
                    AdminAuditTab(
                        investments = investments
                    )
                }
            }
        }
    }
}

@Composable
fun AdminProfileTab(
    session: UserSessionEntity?,
    onLogout: () -> Unit
) {

    val adminName =
        session?.fullName
            ?.ifBlank { null }
            ?: "ARNA Foundation Admin"

    val adminId =
        session?.userId
            ?: "ADMIN-01"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "ADMINISTRATOR PROFILE",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
        )

        Text(
            text = "System Administrator Authority & Credentials",
            fontSize = 12.sp,
            color = TextMuted
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(
                1.dp,
                OutlineSoft
            )
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
                        .background(Emerald900),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = adminName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Admin ID: $adminId",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Emerald800
                )

                Spacer(modifier = Modifier.height(6.dp))

                StatusBadge(
                    statusText = "EXECUTIVE ROLE"
                )
            }
        }

        Text(
            text = "AUTHORIZATION DETAILS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900,
            letterSpacing = 1.sp
        )

        AppCard(
            backgroundColor = Color.White
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                AdminDetailRow(
                    label = "System Role",
                    value = "Primary Executive Administrator"
                )

                AdminDetailRow(
                    label = "Access Scope",
                    value = "Foundation Treasury, Portfolios & Disbursals"
                )

                AdminDetailRow(
                    label = "Organization",
                    value = "ARNA GRAM UNNAYA FOUNDATION"
                )

                AdminDetailRow(
                    label = "Security Protocol",
                    value = "Isolated Admin Portal Active"
                )
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
                .testTag("admin_signout_button"),
            shape = RoundedCornerShape(100.dp)
        ) {

            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Sign Out of Admin Console",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminDetailRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            fontSize = 12.sp,
            color = TextMuted
        )

        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            OutlineSoft
        )
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = title,
                fontSize = 10.sp,
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtext,
                fontSize = 9.sp,
                color = TextMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AdminActionTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            OutlineSoft
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 12.dp,
                    horizontal = 4.dp
                ),
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

                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = ArnaGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun AdminActionDialog(
    title: String,
    onDismiss: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Connected to ARNA FOUNDATION Digital Management Server. Action verified under ARNA Foundation management protocol.",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald800
                    )
                ) {

                    Text(
                        text = "Acknowledge & Close",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AdminSanctionsTab(
    loans: List<LoanEntity>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Loan Sanctions & Approvals",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
        )

        Text(
            text = "Review credit proposals, sanction loans & disburse directly",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            items(
                items = loans,
                key = {
                    it.loanId
                }
            ) { loan ->

                AppCard(
                    backgroundColor = Color.White
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = loan.borrowerName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Text(
                                text = "${loan.loanType} • ID: ${loan.loanId}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        StatusBadge(
                            statusText = loan.status.name
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Purpose: ${loan.purpose}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                text = "Principal",
                                fontSize = 11.sp,
                                color = TextMuted
                            )

                            Text(
                                text = "₹${
                                    String.format(
                                        Locale.getDefault(),
                                        "%,.0f",
                                        loan.principalAmount
                                    )
                                }",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald800
                            )
                        }

                        Column {

                            Text(
                                text = "Tenor",
                                fontSize = 11.sp,
                                color = TextMuted
                            )

                            Text(
                                text = "${loan.tenorMonths} Mo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Column {

                            Text(
                                text = "Monthly EMI",
                                fontSize = 11.sp,
                                color = TextMuted
                            )

                            Text(
                                text = "₹${
                                    String.format(
                                        Locale.getDefault(),
                                        "%,.0f",
                                        loan.emiAmount
                                    )
                                }",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Amber700
                            )
                        }

                        Column {

                            Text(
                                text = "Interest",
                                fontSize = 11.sp,
                                color = TextMuted
                            )

                            Text(
                                text = "${loan.interestRate}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    if (
                        loan.status ==
                        LoanStatus.PENDING_APPROVAL
                    ) {

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            OutlinedButton(
                                onClick = {
                                    onReject(loan.loanId)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = DangerRed
                                )
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(
                                    modifier = Modifier.width(4.dp)
                                )

                                Text(
                                    text = "Reject",
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = {
                                    onApprove(loan.loanId)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Emerald800
                                )
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(
                                    modifier = Modifier.width(4.dp)
                                )

                                Text(
                                    text = "Sanction & Disburse",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminBorrowersMasterTab(
    borrowers: List<BorrowerEntity>
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Master Borrower Registry (${borrowers.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
        )

        Text(
            text = "Consolidated database of all registered beneficiaries",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            items(
                items = borrowers,
                key = {
                    it.borrowerId
                }
            ) { borrower ->

                AppCard(
                    backgroundColor = Color.White
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = borrower.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Text(
                                text = "ID: ${borrower.borrowerId} • ${borrower.phone}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )

                            Text(
                                text = "${borrower.village}, ${borrower.district} • ${borrower.occupation}",
                                fontSize = 11.sp,
                                color = Emerald800
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {

                            Text(
                                text = "Wallet: ₹${
                                    String.format(
                                        Locale.getDefault(),
                                        "%,.0f",
                                        borrower.walletBalance
                                    )
                                }",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )

                            StatusBadge(
                                statusText = borrower.status
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAuditTab(
    investments: List<InvestmentEntity>
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Schemes & Rural Bonds Registry",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
        )

        Text(
            text = "Fixed deposits and recurring savings bonds issued",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        investments.forEach { investment ->

            AppCard(
                backgroundColor = Color.White
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = investment.schemeName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "Cert #${investment.certificateNo} • Holder: ${investment.borrowerName}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    StatusBadge(
                        statusText = investment.status
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            text = "Principal",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Text(
                            text = "₹${
                                String.format(
                                    Locale.getDefault(),
                                    "%,.0f",
                                    investment.principalAmount
                                )
                            }",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald800
                        )
                    }

                    Column {

                        Text(
                            text = "ROI",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Text(
                            text = "${investment.roiPercentage}% p.a.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Column {

                        Text(
                            text = "Maturity Date",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Text(
                            text = investment.maturityDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Column {

                        Text(
                            text = "Maturity Value",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Text(
                            text = "₹${
                                String.format(
                                    Locale.getDefault(),
                                    "%,.0f",
                                    investment.maturityAmount
                                )
                            }",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Amber700
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}