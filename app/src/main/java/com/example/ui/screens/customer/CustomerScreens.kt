package com.arnafoundation.app.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arnafoundation.app.R
import com.arnafoundation.app.data.local.entities.BorrowerEntity
import com.arnafoundation.app.data.local.entities.EmiEntity
import com.arnafoundation.app.data.local.entities.InvestmentEntity
import com.arnafoundation.app.data.local.entities.LoanEntity
import com.arnafoundation.app.data.local.entities.TransactionEntity
import com.arnafoundation.app.data.model.EmiStatus
import com.arnafoundation.app.data.model.InvestmentType
import com.arnafoundation.app.data.model.LoanStatus
import com.arnafoundation.app.data.model.PaymentMode
import com.arnafoundation.app.data.model.TransactionCategory
import com.arnafoundation.app.data.model.TransactionType
import com.arnafoundation.app.data.model.UserRole
import com.arnafoundation.app.ui.components.AppButton
import com.arnafoundation.app.ui.components.AppCard
import com.arnafoundation.app.ui.components.LoanCalculatorDialog
import com.arnafoundation.app.ui.components.StatusBadge
import com.arnafoundation.app.ui.theme.*
import com.arnafoundation.app.ui.viewmodel.CustomerProfileInfo
import com.arnafoundation.app.ui.viewmodel.MainViewModel
import com.arnafoundation.app.ui.viewmodel.ReceiptData
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedCustomerTab.collectAsStateWithLifecycle()
    val borrower by viewModel.currentBorrower.collectAsStateWithLifecycle()
    val customerProfile by viewModel.customerProfile.collectAsStateWithLifecycle()
    val loans by viewModel.customerLoans.collectAsStateWithLifecycle()
    val transactions by viewModel.customerTransactions.collectAsStateWithLifecycle()
    val investments by viewModel.customerInvestments.collectAsStateWithLifecycle()
    val customerEmis by viewModel.customerEmis.collectAsStateWithLifecycle()
    val session by viewModel.sessionState.collectAsStateWithLifecycle()

    var showTopUpDialog by remember { mutableStateOf(false) }
    var showLoanCalculator by remember { mutableStateOf(false) }
    var showApplyLoanSheet by remember { mutableStateOf(false) }
    var showNewDepositSheet by remember { mutableStateOf(false) }

    if (showTopUpDialog && borrower != null) {
        TopUpWalletDialog(
            borrower = borrower!!,
            onDismiss = { showTopUpDialog = false },
            onConfirm = { amount, note ->
                viewModel.topUpWallet(borrower!!.borrowerId, amount, note)
                showTopUpDialog = false
            }
        )
    }

    if (showLoanCalculator) {
        LoanCalculatorDialog(
            onDismiss = { showLoanCalculator = false },
            onApplyWithValues = { _, _ ->
                showLoanCalculator = false
                showApplyLoanSheet = true
            }
        )
    }

    if (showApplyLoanSheet && borrower != null) {
        ApplyLoanBottomSheet(
            borrower = borrower!!,
            onDismiss = { showApplyLoanSheet = false },
            onSubmit = { loanType, principal, tenor, purpose ->
                viewModel.applyLoan(borrower!!.borrowerId, loanType, principal, tenor, purpose)
                showApplyLoanSheet = false
            }
        )
    }

    if (showNewDepositSheet && borrower != null) {
        NewDepositBottomSheet(
            borrower = borrower!!,
            onDismiss = { showNewDepositSheet = false },
            onSubmit = { schemeName, type, principal, tenor, roi ->
                viewModel.createInvestment(borrower!!.borrowerId, schemeName, type, principal, tenor, roi)
                showNewDepositSheet = false
            }
        )
    }

    Scaffold(
        containerColor = BentoBg,
        topBar = {
            Surface(
                color = Color.White.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "ARNA GRAM UNNAYA FOUNDATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ArnaGreenPrimary,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            "CUSTOMER PORTAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ArnaGreenDark,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val displayName = customerProfile?.memberName?.ifBlank { null } ?: borrower?.name ?: session?.fullName?.ifBlank { null } ?: "Customer"
                        val customerId = customerProfile?.memberId?.ifBlank { null } ?: borrower?.borrowerId ?: session?.userId ?: "—"
                        Text(
                            "Welcome, $displayName",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            "Customer ID: $customerId",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = BentoTealPrimary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val displayName = customerProfile?.memberName?.ifBlank { null } ?: borrower?.name ?: session?.fullName?.ifBlank { null } ?: "Customer"
                        val initials = remember(displayName) {
                            val parts = displayName.trim().split(" ")
                            if (parts.size >= 2 && parts[0].isNotEmpty() && parts[1].isNotEmpty()) {
                                "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                            } else {
                                displayName.trim().take(2).uppercase()
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ArnaGreenPrimary)
                                .clickable { viewModel.setCustomerTab(4) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                initials,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2))
                                .testTag("customer_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(76.dp)
                ) {
                    val navItems = listOf(
                        Triple(0, "Home", Icons.Default.Home),
                        Triple(1, "Loans", Icons.Default.MonetizationOn),
                        Triple(2, "EMI", Icons.Default.Payment),
                        Triple(3, "Transactions", Icons.Default.ReceiptLong),
                        Triple(4, "Profile", Icons.Default.Person)
                    )
                    navItems.forEach { (tabIndex, label, icon) ->
                        val isSelected = selectedTab == tabIndex
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setCustomerTab(tabIndex) },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) BentoTealLight else Color.Transparent)
                                        .padding(horizontal = 14.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = label,
                                        tint = if (isSelected) BentoTealPrimary else BentoTextSecondary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) BentoTealPrimary else BentoTextSecondary.copy(alpha = 0.7f)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = BentoTealPrimary,
                                unselectedIconColor = BentoTextSecondary.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BentoBg)
        ) {
            when (selectedTab) {
                0 -> CustomerDashboardScreen(
                    borrower = borrower,
                    customerProfile = customerProfile,
                    loans = loans,
                    transactions = transactions,
                    investments = investments,
                    customerEmis = customerEmis,
                    onTopUp = { showTopUpDialog = true },
                    onApplyLoan = { showApplyLoanSheet = true },
                    onNewInvestment = { showNewDepositSheet = true },
                    onNavigateLoans = { viewModel.setCustomerTab(1) },
                    onNavigateEmis = { viewModel.setCustomerTab(2) },
                    onNavigatePassbook = { viewModel.setCustomerTab(3) },
                    onOpenCalculator = { showLoanCalculator = true }
                )
                1 -> CustomerLoansScreen(
                    loans = loans,
                    onApplyLoan = { showApplyLoanSheet = true },
                    onOpenCalculator = { showLoanCalculator = true },
                    onViewEmis = { viewModel.setCustomerTab(2) }
                )
                2 -> CustomerEmiScreen(
                    borrower = borrower,
                    viewModel = viewModel
                )
                3 -> CustomerPassbookScreen(
                    borrower = borrower,
                    transactions = transactions,
                    onViewReceipt = { txn ->
                        viewModel.showReceipt(
                            ReceiptData(
                                title = "Passbook Transaction Voucher",
                                receiptNo = txn.receiptNo,
                                date = txn.formattedDate,
                                amount = txn.amount,
                                payerName = txn.borrowerName,
                                borrowerId = txn.borrowerId,
                                category = txn.category.name.replace("_", " "),
                                referenceInfo = txn.referenceId,
                                paymentMode = if (txn.type == TransactionType.CREDIT) "CREDIT INFLOW" else "DEBIT OUTFLOW",
                                status = "SETTLED"
                            )
                        )
                    }
                )
                4 -> CustomerProfileScreen(
                    borrower = borrower,
                    customerProfile = customerProfile,
                    onLogout = { viewModel.logout() }
                )
                else -> CustomerProfileScreen(
                    borrower = borrower,
                    customerProfile = customerProfile,
                    onLogout = { viewModel.logout() }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. DASHBOARD SCREEN (Bento Grid)
// -------------------------------------------------------------
@Composable
fun CustomerDashboardScreen(
    borrower: BorrowerEntity?,
    customerProfile: CustomerProfileInfo? = null,
    loans: List<LoanEntity>,
    transactions: List<TransactionEntity>,
    investments: List<InvestmentEntity> = emptyList(),
    customerEmis: List<EmiEntity> = emptyList(),
    onTopUp: () -> Unit,
    onApplyLoan: () -> Unit,
    onNewInvestment: () -> Unit,
    onNavigateLoans: () -> Unit,
    onNavigateEmis: () -> Unit,
    onNavigateInvest: () -> Unit = {},
    onNavigatePassbook: () -> Unit,
    onOpenCalculator: () -> Unit
) {
    val scrollState = rememberScrollState()
    val activeLoans = loans.filter { it.status == LoanStatus.ACTIVE || it.status == LoanStatus.APPROVED }
    val activeLoansCount = activeLoans.size
    val totalLoanAmount = loans.sumOf { it.principalAmount }
    val outstandingAmount = loans.filter { it.status == LoanStatus.ACTIVE }.sumOf { it.outstandingAmount }

    val activeLoan = loans.firstOrNull { it.status == LoanStatus.ACTIVE } ?: loans.firstOrNull { it.status == LoanStatus.APPROVED }
    val activeSavingsAmount = investments.sumOf { it.principalAmount }

    val totalEmisCount = customerEmis.size
    val paidEmisCount = customerEmis.count { it.status == EmiStatus.PAID }
    val dueEmisCount = customerEmis.count { it.status == EmiStatus.OVERDUE || it.status == EmiStatus.PENDING }

    val memberId = customerProfile?.memberId?.ifBlank { null } ?: borrower?.borrowerId ?: "—"
    val memberName = customerProfile?.memberName?.ifBlank { null } ?: borrower?.name ?: "Customer"
    val phone = customerProfile?.phone?.ifBlank { null } ?: borrower?.phone?.ifBlank { null } ?: "Not Available"
    val branchId = customerProfile?.branchId?.ifBlank { null } ?: borrower?.district?.ifBlank { null } ?: "Main Branch"
    val accountStatus = customerProfile?.accountStatus?.ifBlank { null } ?: borrower?.status?.ifBlank { null } ?: "ACTIVE"
    val isActive = accountStatus.equals("ACTIVE", ignoreCase = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "MY ARNA ACCOUNT",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = BentoTextPrimary,
            letterSpacing = (-0.3).sp
        )

        // Bento Card 1: MY ACCOUNT
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("customer_my_account_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BentoBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MY ACCOUNT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTealPrimary,
                        letterSpacing = 1.sp
                    )

                    // Account Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isActive) Color(0xFFDCFCE7) else BentoCoralContainer)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) Color(0xFF16A34A) else BentoCoralText)
                            )
                            Text(
                                text = accountStatus.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color(0xFF15803D) else BentoCoralText,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = BentoBorder.copy(alpha = 0.6f),
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoProfileDetailItem(
                        icon = Icons.Default.Person,
                        label = "Customer Name",
                        value = memberName,
                        modifier = Modifier.weight(1f)
                    )
                    BentoProfileDetailItem(
                        icon = Icons.Default.Badge,
                        label = "Customer ID",
                        value = memberId,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoProfileDetailItem(
                        icon = Icons.Default.Phone,
                        label = "Mobile",
                        value = phone,
                        modifier = Modifier.weight(1f)
                    )
                    BentoProfileDetailItem(
                        icon = Icons.Default.LocationOn,
                        label = "Branch",
                        value = branchId,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Bento Card 2: FINANCIAL OVERVIEW
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("customer_financial_overview_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BentoBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FINANCIAL OVERVIEW",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArnaGreenDark,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ArnaGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = ArnaGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Metric Row 1: Active Loans, Total Loan Amount, Outstanding Amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FinancialMetricBox(
                        label = "Active Loans",
                        value = "$activeLoansCount",
                        modifier = Modifier.weight(1f),
                        containerColor = ArnaGreenLight,
                        valueColor = ArnaGreenDark
                    )
                    FinancialMetricBox(
                        label = "Total Loan Amount",
                        value = "₹${String.format(Locale.getDefault(), "%,.0f", totalLoanAmount)}",
                        modifier = Modifier.weight(1.3f),
                        containerColor = Color(0xFFF8FAFC),
                        valueColor = BentoTextPrimary
                    )
                    FinancialMetricBox(
                        label = "Outstanding Amount",
                        value = "₹${String.format(Locale.getDefault(), "%,.0f", outstandingAmount)}",
                        modifier = Modifier.weight(1.3f),
                        containerColor = Color(0xFFFEF2F2),
                        valueColor = Color(0xFFDC2626)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Metric Row 2: Total EMI, Paid EMI, Due EMI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FinancialMetricBox(
                        label = "Total EMI",
                        value = "$totalEmisCount",
                        modifier = Modifier.weight(1f),
                        containerColor = Color(0xFFF8FAFC),
                        valueColor = BentoTextPrimary
                    )
                    FinancialMetricBox(
                        label = "Paid EMI",
                        value = "$paidEmisCount",
                        modifier = Modifier.weight(1f),
                        containerColor = Color(0xFFDCFCE7),
                        valueColor = Color(0xFF15803D)
                    )
                    FinancialMetricBox(
                        label = "Due EMI",
                        value = "$dueEmisCount",
                        modifier = Modifier.weight(1f),
                        containerColor = if (dueEmisCount > 0) Color(0xFFFEF2F2) else Color(0xFFF1F5F9),
                        valueColor = if (dueEmisCount > 0) Color(0xFFDC2626) else BentoTextPrimary
                    )
                }
            }
        }

        // Bento Card 1: Total Wallet Balance Hero Card (Col-Span 2)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = BentoTealLight),
            border = BorderStroke(1.dp, Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            "Total Wallet Balance",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = BentoTealOnLight.copy(alpha = 0.75f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val balance = borrower?.walletBalance ?: 0.0
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "₹${String.format(Locale.getDefault(), "%,.0f", balance)}",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTealOnLight
                            )
                            Text(
                                ".00",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                color = BentoTealOnLight.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color.White.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            memberId,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTealPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Micro badge info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color.White.copy(alpha = 0.5f))
                            .clickable { onTopUp() }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = BentoTealPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            "Add Money",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTealPrimary
                        )
                    }

                    // Transfer / Action Button
                    Button(
                        onClick = onNavigateEmis,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoTealPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp)
                    ) {
                        Text(
                            "Transfer / Pay",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Bento 2-Column Grid (Active Loan & Next EMI)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bento Card 2: Active Loan (Col 1)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateLoans() }
                    .testTag("customer_active_loan_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BentoMintSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = BentoTealPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        "Active Loan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = BentoTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        if (activeLoan != null) "₹${String.format(Locale.getDefault(), "%,.0f", activeLoan.principalAmount)}" else "—",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        activeLoan?.let { "#${it.loanId}" } ?: "No Active Loan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeLoan != null) BentoTealPrimary else BentoTextMuted
                    )
                }
            }

            // Bento Card 3: Next EMI (Col 2)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateEmis() }
                    .testTag("customer_next_emi_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCoralContainer),
                border = BorderStroke(1.dp, Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BentoCoralIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = BentoCoralText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        "Next EMI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = BentoTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        if (activeLoan != null) "₹${String.format(Locale.getDefault(), "%,.0f", activeLoan.emiAmount)}" else "—",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoCoralText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (activeLoan != null) "Due Scheduled" else "No Pending EMI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = BentoTextSecondary
                    )
                }
            }
        }

        // Bento Card 4: Fixed Savings (Span 2)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateInvest() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BentoBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(BentoPurpleContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = BentoPurpleText,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            "Fixed Savings & Gram Schemes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = BentoTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            if (activeSavingsAmount > 0) "₹${String.format(Locale.getDefault(), "%,.2f", activeSavingsAmount)}" else "—",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BentoTealLight)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        if (activeSavingsAmount > 0) "+8.5% p.a." else "API Pending",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTealPrimary
                    )
                }
            }
        }

        // Bento 4-Item Quick Services Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BentoQuickServiceTile(
                icon = Icons.Default.Payment,
                label = "Pay",
                modifier = Modifier.weight(1f),
                onClick = onNavigateEmis
            )
            BentoQuickServiceTile(
                icon = Icons.Default.ReceiptLong,
                label = "History",
                modifier = Modifier.weight(1f),
                onClick = onNavigatePassbook
            )
            BentoQuickServiceTile(
                icon = Icons.Default.MonetizationOn,
                label = "Apply",
                modifier = Modifier.weight(1f),
                onClick = onApplyLoan
            )
            BentoQuickServiceTile(
                icon = Icons.Default.Info,
                label = "Support",
                modifier = Modifier.weight(1f),
                onClick = onOpenCalculator
            )
        }

        // Recent Passbook Section (Bento Activity)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "RECENT PASSBOOK ACTIVITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextMuted,
                letterSpacing = 1.2.sp
            )
            TextButton(onClick = onNavigatePassbook) {
                Text(
                    "View Full",
                    fontSize = 12.sp,
                    color = BentoTealPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (transactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions recorded yet.", fontSize = 13.sp, color = BentoTextMuted)
                }
            }
        } else {
            transactions.take(3).forEach { txn ->
                PassbookRowItem(txn = txn, onClick = onNavigatePassbook)
            }
        }
    }
}

@Composable
private fun BentoQuickServiceTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArnaGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = ArnaGreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )
        }
    }
}

@Composable
fun PassbookRowItem(
    txn: TransactionEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (txn.type == TransactionType.CREDIT) BentoMintSoft else BentoCoralIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (txn.type == TransactionType.CREDIT) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (txn.type == TransactionType.CREDIT) BentoTealPrimary else BentoCoralText,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        txn.description,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        txn.formattedDate,
                        fontSize = 11.sp,
                        color = BentoTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (txn.type == TransactionType.CREDIT) "+" else "-"}₹${String.format(Locale.getDefault(), "%,.2f", txn.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (txn.type == TransactionType.CREDIT) BentoTealPrimary else BentoCoralText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Bal: ₹${String.format(Locale.getDefault(), "%,.0f", txn.balanceAfter)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = BentoTextMuted
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. LOANS SCREEN
// -------------------------------------------------------------
@Composable
fun CustomerLoansScreen(
    loans: List<LoanEntity>,
    onApplyLoan: () -> Unit,
    onOpenCalculator: () -> Unit,
    onViewEmis: () -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    val filters = listOf("All Loans", "Active", "Pending", "Closed")

    val filteredLoans = when (selectedFilter) {
        1 -> loans.filter { it.status == LoanStatus.ACTIVE }
        2 -> loans.filter { it.status == LoanStatus.PENDING_APPROVAL }
        3 -> loans.filter { it.status == LoanStatus.CLOSED }
        else -> loans
    }

    val totalSanctioned = loans.filter { it.status == LoanStatus.ACTIVE || it.status == LoanStatus.CLOSED }.sumOf { it.principalAmount }
    val totalPaid = loans.sumOf { it.totalPaid }
    val totalOutstanding = loans.filter { it.status == LoanStatus.ACTIVE }.sumOf { it.outstandingAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Microfinance Loans", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                Text("Low-interest rural enterprise credit", fontSize = 12.sp, color = BentoTextSecondary)
            }

            Button(
                onClick = onApplyLoan,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoTealPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Loan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bento Top Stat Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoTealLight),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("SANCTIONED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoTealDark, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("₹${String.format(Locale.getDefault(), "%,.0f", totalSanctioned)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BentoTealDark)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${loans.count { it.status == LoanStatus.ACTIVE }} Active Facility", fontSize = 10.sp, color = BentoTextSecondary)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCoralContainer),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("OUTSTANDING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoCoralText, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("₹${String.format(Locale.getDefault(), "%,.0f", totalOutstanding)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BentoCoralText)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Paid ₹${String.format(Locale.getDefault(), "%,.0f", totalPaid)}", fontSize = 10.sp, color = BentoTextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Tabs
        TabRow(
            selectedTabIndex = selectedFilter,
            containerColor = Color.White,
            contentColor = BentoTealPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BentoBorder, RoundedCornerShape(16.dp))
        ) {
            filters.forEachIndexed { index, title ->
                Tab(
                    selected = selectedFilter == index,
                    onClick = { selectedFilter = index },
                    text = {
                        Text(
                            title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedFilter == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedFilter == index) BentoTealPrimary else BentoTextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredLoans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = BentoTextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No loans in this category.", fontSize = 14.sp, color = BentoTextMuted)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onApplyLoan,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text("Apply for a Microloan", color = BentoTealPrimary)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredLoans, key = { it.loanId }) { loan ->
                    val totalEmis = loan.tenorMonths
                    val paidEmis = if (loan.emiAmount > 0) ((loan.totalPaid / loan.emiAmount).toInt()).coerceAtMost(totalEmis) else 0
                    val dueEmis = (totalEmis - paidEmis).coerceAtLeast(0)
                    val nextEmiDate = loan.disbursedDate?.ifBlank { null } ?: loan.applicationDate

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewEmis() }
                            .testTag("customer_loan_card_${loan.loanId}"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BentoBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(loan.loanType, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                    Text("Loan ID: ${loan.loanId}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BentoTealPrimary)
                                }
                                StatusBadge(statusText = loan.status.name)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Purpose: ${loan.purpose}", fontSize = 12.sp, color = BentoTextSecondary)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Bento Metric Cards (2x2 Grid)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SurfaceCream)
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("Loan Amount", fontSize = 10.sp, color = BentoTextMuted, fontWeight = FontWeight.Medium)
                                        Text("₹${String.format(Locale.getDefault(), "%,.0f", loan.principalAmount)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTealPrimary)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SurfaceCream)
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("Monthly EMI", fontSize = 10.sp, color = BentoTextMuted, fontWeight = FontWeight.Medium)
                                        Text("₹${String.format(Locale.getDefault(), "%,.0f", loan.emiAmount)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoCoralText)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // EMI Breakdown Row
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Text("Total EMI", fontSize = 10.sp, color = BentoTextMuted)
                                        Text("$totalEmis", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BentoMintSoft)
                                        .padding(8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Text("Paid EMI", fontSize = 10.sp, color = BentoTealDark)
                                        Text("$paidEmis", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BentoTealPrimary)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BentoCoralContainer)
                                        .padding(8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Text("Due EMI", fontSize = 10.sp, color = BentoCoralText)
                                        Text("$dueEmis", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BentoCoralText)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Text("Next EMI Date", fontSize = 10.sp, color = BentoTextMuted)
                                        Text(nextEmiDate, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                    }
                                }
                            }

                            if (loan.status == LoanStatus.ACTIVE) {
                                Spacer(modifier = Modifier.height(12.dp))
                                val prog = if (loan.totalRepayable > 0) (loan.totalPaid / loan.totalRepayable).toFloat() else 0f
                                LinearProgressIndicator(
                                    progress = { prog },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = BentoTealPrimary,
                                    trackColor = BentoBorder
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Paid: ₹${String.format(Locale.getDefault(), "%,.0f", loan.totalPaid)}", fontSize = 10.sp, color = BentoTealPrimary, fontWeight = FontWeight.SemiBold)
                                    Text("Outstanding: ₹${String.format(Locale.getDefault(), "%,.0f", loan.outstandingAmount)}", fontSize = 10.sp, color = BentoTextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. EMI SCREEN (REPAYMENTS / READ-ONLY EMI SCHEDULE)
// -------------------------------------------------------------
@Composable
fun CustomerEmiScreen(
    borrower: BorrowerEntity?,
    viewModel: MainViewModel
) {
    val customerEmis by viewModel.customerEmis.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val totalEmis = customerEmis.size
    val paidEmis = customerEmis.count { it.status == EmiStatus.PAID }
    val dueEmis = customerEmis.count { it.status == EmiStatus.OVERDUE }
    val upcomingEmis = customerEmis.count { it.status == EmiStatus.PENDING }

    val totalDueAmount = customerEmis.filter { it.status != EmiStatus.PAID }.sumOf { it.amount }
    val totalPaidAmount = customerEmis.filter { it.status == EmiStatus.PAID }.sumOf { if (it.paidAmount > 0) it.paidAmount else it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("customer_emi_screen")
    ) {
        // Top App Bar / Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "EMI Schedule",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Loan Installments",
                    fontSize = 12.sp,
                    color = BentoTextSecondary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { viewModel.refreshCustomerEmis() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh EMIs",
                        tint = BentoTealPrimary
                    )
                }
                if (totalEmis > 0) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = BentoTealLight,
                        border = BorderStroke(1.dp, BentoTealPrimary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "$totalEmis Installments",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoTealPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Summary Section: 4 Summary Cards (2x2 Grid)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Total EMI
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("customer_emi_summary_total"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "TOTAL EMI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalEmis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "All Tenors",
                        fontSize = 10.sp,
                        color = BentoTextSecondary
                    )
                }
            }

            // Card 2: Paid EMI
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("customer_emi_summary_paid"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoMintSoft),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PAID EMI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTealDark,
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BentoTealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$paidEmis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTealPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%,.0f", totalPaidAmount)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = BentoTealDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 3: Due EMI
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("customer_emi_summary_due"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCoralContainer),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DUE EMI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoCoralText,
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BentoCoralText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$dueEmis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoCoralText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (dueEmis > 0) "Immediate Attention" else "Clear",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = BentoCoralText.copy(alpha = 0.8f)
                    )
                }
            }

            // Card 4: Upcoming EMI
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("customer_emi_summary_upcoming"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                border = BorderStroke(1.dp, Color(0xFFBAE6FD))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "UPCOMING EMI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0369A1),
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$upcomingEmis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0369A1)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%,.0f", totalDueAmount)} Total Due",
                        fontSize = 10.sp,
                        color = Color(0xFF0284C7)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // EMI List Section or Empty/Loading State
        if (isLoading && customerEmis.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = BentoTealPrimary,
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Loading EMI schedule...", fontSize = 13.sp, color = BentoTextMuted)
                }
            }
        } else if (customerEmis.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .testTag("customer_emi_empty_state"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SurfaceCream),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = BentoTealPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No EMI Schedule Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No loan installments are currently available for your account.",
                        fontSize = 13.sp,
                        color = BentoTextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            Text(
                text = "INSTALLMENT TIMELINE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextMuted,
                letterSpacing = 0.6.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("customer_emi_list")
            ) {
                items(customerEmis, key = { it.emiId }) { emi ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_emi_card_${emi.emiId}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BentoBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top Row: Installment #, Loan ID, Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (emi.status) {
                                                    EmiStatus.PAID -> BentoMintSoft
                                                    EmiStatus.OVERDUE -> BentoCoralContainer
                                                    EmiStatus.PENDING -> Color(0xFFE0F2FE)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "#${emi.installmentNo}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = when (emi.status) {
                                                EmiStatus.PAID -> BentoTealPrimary
                                                EmiStatus.OVERDUE -> BentoCoralText
                                                EmiStatus.PENDING -> Color(0xFF0369A1)
                                            }
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Installment ${emi.installmentNo} of ${emi.totalInstallments}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BentoTextPrimary
                                        )
                                        if (emi.loanId.isNotBlank()) {
                                            Text(
                                                text = "Loan ID: ${emi.loanId}",
                                                fontSize = 11.sp,
                                                color = BentoTextSecondary
                                            )
                                        }
                                    }
                                }

                                // Status Badge
                                EmiStatusBadge(status = emi.status)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Middle Box: Due Date & Amount
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceCream)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Due Date",
                                        fontSize = 10.sp,
                                        color = BentoTextMuted
                                    )
                                    Text(
                                        text = emi.dueDate,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BentoTextPrimary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "EMI Amount",
                                        fontSize = 10.sp,
                                        color = BentoTextMuted
                                    )
                                    Text(
                                        text = "₹${String.format(Locale.getDefault(), "%,.2f", emi.amount)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (emi.status == EmiStatus.PAID) BentoTealPrimary else BentoTextPrimary
                                    )
                                }
                            }

                            // Optional Breakdown or Payment Info
                            if (emi.principalPortion > 0 || emi.interestPortion > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Principal: ₹${String.format(Locale.getDefault(), "%,.0f", emi.principalPortion)}",
                                        fontSize = 11.sp,
                                        color = BentoTextSecondary
                                    )
                                    Text(
                                        text = "Interest: ₹${String.format(Locale.getDefault(), "%,.0f", emi.interestPortion)}",
                                        fontSize = 11.sp,
                                        color = BentoTextSecondary
                                    )
                                }
                            }

                            if (emi.status == EmiStatus.PAID && !emi.paidDate.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Paid on: ${emi.paidDate}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BentoTealPrimary
                                    )
                                    if (!emi.receiptNo.isNullOrBlank()) {
                                        Text(
                                            text = "Slip: ${emi.receiptNo}",
                                            fontSize = 10.sp,
                                            color = BentoTextMuted
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
private fun EmiStatusBadge(status: EmiStatus) {
    val (bgColor, textColor, label) = when (status) {
        EmiStatus.PAID -> Triple(BentoMintSoft, BentoTealPrimary, "PAID")
        EmiStatus.PENDING -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), "UPCOMING")
        EmiStatus.OVERDUE -> Triple(BentoCoralContainer, BentoCoralText, "DUE")
    }

    Surface(
        shape = RoundedCornerShape(100.dp),
        color = bgColor,
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.2f))
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun PaymentModeSelector(
    mode: PaymentMode,
    title: String,
    subtitle: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) BentoTealLight.copy(alpha = 0.5f) else Color(0xFFF8FAFC))
            .border(1.dp, if (selected) BentoTealPrimary else BentoBorder, RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = BentoTealPrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
            Text(subtitle, fontSize = 11.sp, color = BentoTextSecondary)
        }
    }
}

// -------------------------------------------------------------
// 4. INVESTMENTS SCREEN
// -------------------------------------------------------------
@Composable
fun CustomerInvestmentsScreen(
    borrower: BorrowerEntity?,
    investments: List<InvestmentEntity>,
    onNewInvestment: () -> Unit
) {
    val totalInvested = investments.sumOf { it.principalAmount }
    val totalMaturity = investments.sumOf { it.maturityAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Gram Savings & Schemes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                Text("High-yield rural deposit bonds", fontSize = 12.sp, color = BentoTextSecondary)
            }

            Button(
                onClick = onNewInvestment,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoTealPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Deposit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bento Portfolio Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = BentoPurpleContainer),
            border = BorderStroke(1.dp, Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "TOTAL SCHEME SAVINGS PORTFOLIO",
                    fontSize = 11.sp,
                    color = BentoPurpleText.copy(alpha = 0.75f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "₹${String.format(Locale.getDefault(), "%,.2f", if (totalInvested > 0) totalInvested else 12000.0)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BentoPurpleText
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Projected Maturity Value", fontSize = 12.sp, color = BentoPurpleText.copy(alpha = 0.8f))
                    Text(
                        "₹${String.format(Locale.getDefault(), "%,.2f", if (totalMaturity > 0) totalMaturity else 14200.0)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoPurpleText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("ACTIVE DEPOSIT CERTIFICATES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTextMuted, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        if (investments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active deposit schemes. Start saving today!", fontSize = 13.sp, color = BentoTextMuted)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(investments, key = { it.investmentId }) { inv ->
                    AppCard(backgroundColor = Color.White) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(inv.schemeName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                Text("Cert: ${inv.certificateNo}", fontSize = 11.sp, color = BentoTextSecondary)
                            }
                            StatusBadge(statusText = inv.status)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Principal", fontSize = 11.sp, color = BentoTextMuted)
                                Text("₹${String.format(Locale.getDefault(), "%,.0f", inv.principalAmount)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTealPrimary)
                            }
                            Column {
                                Text("Interest Rate", fontSize = 11.sp, color = BentoTextMuted)
                                Text("${inv.roiPercentage}% p.a.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                            }
                            Column {
                                Text("Maturity Date", fontSize = 11.sp, color = BentoTextMuted)
                                Text(inv.maturityDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                            }
                            Column {
                                Text("Maturity Val", fontSize = 11.sp, color = BentoTextMuted)
                                Text("₹${String.format(Locale.getDefault(), "%,.0f", inv.maturityAmount)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTealPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. PASSBOOK SCREEN
// -------------------------------------------------------------
@Composable
fun CustomerPassbookScreen(
    borrower: BorrowerEntity?,
    transactions: List<TransactionEntity>,
    onViewReceipt: (TransactionEntity) -> Unit
) {
    var selectedFilter by remember { mutableStateOf<TransactionCategory?>(null) }

    val filteredList = if (selectedFilter == null) transactions else transactions.filter { it.category == selectedFilter }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Digital Passbook", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                Text("Official ledger of all debits & credits", fontSize = 12.sp, color = BentoTextSecondary)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(BentoTealLight)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("A/C: ${borrower?.borrowerId ?: "—"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTealPrimary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { selectedFilter = null },
                label = { Text("All", fontSize = 11.sp) },
                shape = RoundedCornerShape(100.dp)
            )
            FilterChip(
                selected = selectedFilter == TransactionCategory.EMI_PAYMENT,
                onClick = { selectedFilter = TransactionCategory.EMI_PAYMENT },
                label = { Text("EMI Paid", fontSize = 11.sp) },
                shape = RoundedCornerShape(100.dp)
            )
            FilterChip(
                selected = selectedFilter == TransactionCategory.LOAN_DISBURSEMENT,
                onClick = { selectedFilter = TransactionCategory.LOAN_DISBURSEMENT },
                label = { Text("Disbursed", fontSize = 11.sp) },
                shape = RoundedCornerShape(100.dp)
            )
            FilterChip(
                selected = selectedFilter == TransactionCategory.WALLET_TOPUP,
                onClick = { selectedFilter = TransactionCategory.WALLET_TOPUP },
                label = { Text("Deposits", fontSize = 11.sp) },
                shape = RoundedCornerShape(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No payment history found", fontSize = 13.sp, color = BentoTextMuted)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList, key = { it.transactionId }) { txn ->
                    PassbookRowItem(txn = txn, onClick = { onViewReceipt(txn) })
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. PROFILE & KYC SCREEN
// -------------------------------------------------------------
@Composable
fun CustomerProfileScreen(
    borrower: BorrowerEntity?,
    customerProfile: CustomerProfileInfo? = null,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    val memberId = customerProfile?.memberId?.ifBlank { null } ?: borrower?.borrowerId ?: "—"
    val memberName = customerProfile?.memberName?.ifBlank { null } ?: borrower?.name ?: "Customer"
    val phone = customerProfile?.phone?.ifBlank { null } ?: borrower?.phone?.ifBlank { null } ?: "Not Available"
    val email = customerProfile?.email?.ifBlank { null } ?: borrower?.email?.ifBlank { null } ?: "Not Available"
    val branchId = customerProfile?.branchId?.ifBlank { null } ?: borrower?.district?.ifBlank { null } ?: "Not Assigned"
    val accountStatus = customerProfile?.accountStatus?.ifBlank { null } ?: borrower?.status?.ifBlank { null } ?: "ACTIVE"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BentoBorder)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(BentoTealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (memberName.take(2)).uppercase(),
                        color = BentoTealPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(memberName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                Text("Member ID: $memberId", fontSize = 12.sp, color = BentoTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                StatusBadge(statusText = accountStatus.uppercase())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("ACCOUNT & REGISTRATION DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTextMuted, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        AppCard(backgroundColor = Color.White) {
            ProfileDetailRow(label = "Member ID", value = memberId)
            ProfileDetailRow(label = "Member Name", value = memberName)
            ProfileDetailRow(label = "Mobile Number", value = phone)
            ProfileDetailRow(label = "Email Address", value = email)
            ProfileDetailRow(label = "Branch ID", value = branchId)
            ProfileDetailRow(label = "Account Status", value = accountStatus.uppercase())
            ProfileDetailRow(label = "Occupation", value = borrower?.occupation ?: "Member / Entrepreneur")
            ProfileDetailRow(label = "Joined Date", value = borrower?.joinedDate ?: "—")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = BentoCoralContainer, contentColor = BentoCoralText),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("customer_profile_signout_button"),
            shape = RoundedCornerShape(100.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out of Member Portal", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextMuted)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

// -------------------------------------------------------------
// DIALOGS & BOTTOM SHEETS
// -------------------------------------------------------------
@Composable
fun TopUpWalletDialog(
    borrower: BorrowerEntity,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, note: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Add Money to Wallet", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Emerald900)
                Text("Member: ${borrower.name} (${borrower.borrowerId})", fontSize = 12.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Deposit Amount (₹)") },
                    placeholder = { Text("Enter amount") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Source / Note") },
                    placeholder = { Text("e.g. UPI / Branch Deposit") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("500", "1000", "2000", "5000").forEach { preset ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Emerald50)
                                .clickable { amountText = preset }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("₹$preset", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald800)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AppButton(
                    text = if (amountText.isNotBlank()) "Deposit ₹$amountText" else "Enter Amount",
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            onConfirm(amt, noteText.ifBlank { "Wallet Top-up" })
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyLoanBottomSheet(
    borrower: BorrowerEntity,
    onDismiss: () -> Unit,
    onSubmit: (loanType: String, principal: Double, tenor: Int, purpose: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedType by remember { mutableStateOf("Mahila Dairy & Cattle Scheme") }
    var principalText by remember { mutableStateOf("40000") }
    var tenorText by remember { mutableStateOf("12") }
    var purposeText by remember { mutableStateOf("Purchase of dairy equipment and fodder storage") }

    val loanTypes = listOf(
        "Mahila Dairy & Cattle Scheme",
        "Handloom Artisan Micro Loan",
        "Solar Bio-Gas Micro Grant",
        "Women SHG Spice Processing",
        "Agricultural Crop Input Loan",
        "Rural Kirana Enterprise Loan"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Apply for Microfinance Loan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Emerald900)
            Text("Subsidized rural empowerment loan with easy monthly installments", fontSize = 12.sp, color = TextMuted)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Scheme", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))

            loanTypes.forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedType == type) Emerald50 else Color(0xFFF8FAFC))
                        .clickable { selectedType = type }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selectedType == type, onClick = { selectedType = type })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(type, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = principalText,
                onValueChange = { principalText = it },
                label = { Text("Required Principal Amount (₹)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = tenorText,
                onValueChange = { tenorText = it },
                label = { Text("Tenure (Months, e.g., 6, 12, 18, 24)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = purposeText,
                onValueChange = { purposeText = it },
                label = { Text("Business Purpose / Proposal") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                text = "Submit Application for Sanction",
                onClick = {
                    val p = principalText.toDoubleOrNull() ?: 0.0
                    val t = tenorText.toIntOrNull() ?: 12
                    if (p > 0 && t > 0) {
                        onSubmit(selectedType, p, t, purposeText)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDepositBottomSheet(
    borrower: BorrowerEntity,
    onDismiss: () -> Unit,
    onSubmit: (schemeName: String, type: InvestmentType, principal: Double, tenor: Int, roi: Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedScheme by remember { mutableStateOf("Gram Samriddhi Fixed Deposit") }
    var principalText by remember { mutableStateOf("10000") }
    var tenorText by remember { mutableStateOf("12") }

    val schemes = listOf(
        Triple("Gram Samriddhi Fixed Deposit", InvestmentType.GRAM_SAMRIDDHI, 8.5),
        Triple("Mahila Vikas Recurring Deposit", InvestmentType.MAHILA_VIKAS_BOND, 9.0),
        Triple("Farmer Green Security Deposit", InvestmentType.FARMER_GREEN_DEPOSIT, 8.0)
    )

    val currentScheme = schemes.find { it.first == selectedScheme } ?: schemes[0]

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Open Gram Savings Scheme", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Emerald900)
            Text("Invest in rural cooperative bonds with guaranteed returns", fontSize = 12.sp, color = TextMuted)

            Spacer(modifier = Modifier.height(16.dp))

            schemes.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedScheme == item.first) Amber50 else Color(0xFFF8FAFC))
                        .border(1.dp, if (selectedScheme == item.first) Amber600 else OutlineSoft, RoundedCornerShape(10.dp))
                        .clickable { selectedScheme = item.first }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selectedScheme == item.first, onClick = { selectedScheme = item.first })
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(item.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Annual Return: ${item.third}% p.a. • Guaranteed", fontSize = 11.sp, color = Amber800)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = principalText,
                onValueChange = { principalText = it },
                label = { Text("Deposit Amount (₹)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = tenorText,
                onValueChange = { tenorText = it },
                label = { Text("Tenure (Months, e.g. 12, 24, 36)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                text = "Deposit & Issue Certificate",
                onClick = {
                    val p = principalText.toDoubleOrNull() ?: 0.0
                    val t = tenorText.toIntOrNull() ?: 12
                    if (p > 0 && t > 0) {
                        onSubmit(currentScheme.first, currentScheme.second, p, t, currentScheme.third)
                    }
                },
                containerColor = Amber600
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BentoProfileDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BentoBg)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BentoTealPrimary,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = BentoTextSecondary
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BentoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FinancialMetricBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFF1F5F9),
    valueColor: Color = BentoTextPrimary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = BentoTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

