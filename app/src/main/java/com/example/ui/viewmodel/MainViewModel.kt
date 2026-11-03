package com.arnafoundation.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arnafoundation.app.data.local.entities.BorrowerEntity
import com.arnafoundation.app.data.local.entities.EmiEntity
import com.arnafoundation.app.data.local.entities.InvestmentEntity
import com.arnafoundation.app.data.local.entities.LoanEntity
import com.arnafoundation.app.data.local.entities.TransactionEntity
import com.arnafoundation.app.data.local.entities.UserSessionEntity
import com.arnafoundation.app.data.model.InvestmentType
import com.arnafoundation.app.data.model.PaymentMode
import com.arnafoundation.app.data.model.UserRole
import com.arnafoundation.app.data.repository.ArnaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CustomerProfileInfo(
    val memberId: String = "",
    val memberName: String = "",
    val phone: String = "",
    val email: String = "",
    val branchId: String = "",
    val accountStatus: String = "Active"
)

data class ReceiptData(
    val title: String,
    val receiptNo: String,
    val date: String,
    val amount: Double,
    val payerName: String,
    val borrowerId: String,
    val category: String,
    val referenceInfo: String,
    val paymentMode: String,
    val status: String = "SUCCESS / VERIFIED"
)

class MainViewModel(private val repository: ArnaRepository) : ViewModel() {

    // Current Session (Always active to prevent session drop during navigation/lifecycle transitions)
    val sessionState: StateFlow<UserSessionEntity?> = repository.currentSession
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Current Customer Data (dynamically observed from Room database via BorrowerDao)
    val allBorrowers: StateFlow<List<BorrowerEntity>> = repository.getAllBorrowers()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentBorrower: StateFlow<BorrowerEntity?> = sessionState.flatMapLatest { session ->
        if (session != null && session.role == UserRole.CUSTOMER && session.userId.isNotBlank()) {
            repository.observeCustomerById(session.userId)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val customerProfile: StateFlow<CustomerProfileInfo?> = currentBorrower.map { borrower ->
        if (borrower != null) {
            CustomerProfileInfo(
                memberId = borrower.borrowerId,
                memberName = borrower.name,
                phone = borrower.phone,
                email = borrower.email,
                branchId = borrower.district,
                accountStatus = borrower.status
            )
        } else null
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Loans & EMIs (Room LoanDao & EmiDao integration)
    val allLoans: StateFlow<List<LoanEntity>> = repository.getAllLoans()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val customerLoans: StateFlow<List<LoanEntity>> = sessionState.flatMapLatest { session ->
        if (session != null && session.role == UserRole.CUSTOMER && session.userId.isNotBlank()) {
            repository.observeCustomerLoans(session.userId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allEmis: StateFlow<List<EmiEntity>> = repository.getAllEmis()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val customerEmis: StateFlow<List<EmiEntity>> = sessionState.flatMapLatest { session ->
        if (session != null && session.role == UserRole.CUSTOMER && session.userId.isNotBlank()) {
            repository.observeCustomerEmis(session.userId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val customerTransactions: StateFlow<List<TransactionEntity>> = sessionState.flatMapLatest { session ->
        if (session != null && session.role == UserRole.CUSTOMER && session.userId.isNotBlank()) {
            repository.observeCustomerTransactions(session.userId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allInvestments: StateFlow<List<InvestmentEntity>> = repository.getAllInvestments()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val customerInvestments: StateFlow<List<InvestmentEntity>> = sessionState.flatMapLatest { session ->
        if (session != null && session.role == UserRole.CUSTOMER && session.userId.isNotBlank()) {
            repository.observeCustomerInvestments(session.userId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // UI Feedback & Dialogs
    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    private val _receiptToShow = MutableStateFlow<ReceiptData?>(null)
    val receiptToShow: StateFlow<ReceiptData?> = _receiptToShow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Navigation Tab state for Customer Portal (0 = Home, 1 = Loans, 2 = EMI, 3 = Investments, 4 = Passbook, 5 = Profile)
    private val _selectedCustomerTab = MutableStateFlow(0)
    val selectedCustomerTab: StateFlow<Int> = _selectedCustomerTab.asStateFlow()

    fun setCustomerTab(tab: Int) {
        _selectedCustomerTab.value = tab
    }

    // Selected Tab for Employee Portal (0 = Dashboard/Targets, 1 = Collect EMI, 2 = New Borrower KYC, 3 = Collection Logs)
    private val _selectedEmployeeTab = MutableStateFlow(0)
    val selectedEmployeeTab: StateFlow<Int> = _selectedEmployeeTab.asStateFlow()

    fun setEmployeeTab(tab: Int) {
        _selectedEmployeeTab.value = tab
    }

    // Selected Tab for Admin Portal (0 = Overview Analytics, 1 = Loan Sanctions, 2 = Borrower Master, 3 = Schemes/Audit)
    private val _selectedAdminTab = MutableStateFlow(0)
    val selectedAdminTab: StateFlow<Int> = _selectedAdminTab.asStateFlow()

    fun setAdminTab(tab: Int) {
        _selectedAdminTab.value = tab
    }

    init {
        viewModelScope.launch {
            sessionState.collect { session ->
                if (session != null && session.role == UserRole.CUSTOMER && session.userId.isNotBlank()) {
                    try {
                        repository.syncCustomerDashboard(session.userId)
                    } catch (e: Exception) {
                        Log.w("MainViewModel", "Background sync customer dashboard failed: ${e.message}")
                    }
                    try {
                        repository.syncCustomerLoans(session.userId)
                    } catch (e: Exception) {
                        Log.w("MainViewModel", "Background sync customer loans failed: ${e.message}")
                    }
                    try {
                        repository.syncCustomerEmis(session.userId)
                    } catch (e: Exception) {
                        Log.w("MainViewModel", "Background sync customer EMIs failed: ${e.message}")
                    }
                    try {
                        repository.syncCustomerTransactions(session.userId)
                    } catch (e: Exception) {
                        Log.w("MainViewModel", "Background sync customer transactions failed: ${e.message}")
                    }
                    try {
                        repository.syncCustomerInvestments(session.userId)
                    } catch (e: Exception) {
                        Log.w("MainViewModel", "Background sync customer investments failed: ${e.message}")
                    }
                }
            }
        }
    }

    fun refreshCustomerLoans() {
        val session = sessionState.value
        if (session != null && session.role == UserRole.CUSTOMER && session.userId.isNotBlank()) {
            viewModelScope.launch {
                try {
                    repository.syncCustomerLoans(session.userId)
                } catch (e: Exception) {
                    Log.w("MainViewModel", "Failed to sync customer loans: ${e.message}")
                }
            }
        }
    }

    fun refreshCustomerEmis() {
        val session = sessionState.value
        if (session != null && session.role == UserRole.CUSTOMER && session.userId.isNotBlank()) {
            viewModelScope.launch {
                try {
                    repository.syncCustomerEmis(session.userId)
                } catch (e: Exception) {
                    Log.w("MainViewModel", "Failed to sync customer EMIs: ${e.message}")
                }
            }
        }
    }

    fun refreshCustomerData() {
        val session = sessionState.value
        if (session != null && session.role == UserRole.CUSTOMER && session.userId.isNotBlank()) {
            viewModelScope.launch {
                try { repository.syncCustomerDashboard(session.userId) } catch (e: Exception) { Log.w("MainViewModel", "sync dashboard error: ${e.message}") }
                try { repository.syncCustomerLoans(session.userId) } catch (e: Exception) { Log.w("MainViewModel", "sync loans error: ${e.message}") }
                try { repository.syncCustomerEmis(session.userId) } catch (e: Exception) { Log.w("MainViewModel", "sync emis error: ${e.message}") }
                try { repository.syncCustomerTransactions(session.userId) } catch (e: Exception) { Log.w("MainViewModel", "sync txns error: ${e.message}") }
                try { repository.syncCustomerInvestments(session.userId) } catch (e: Exception) { Log.w("MainViewModel", "sync invs error: ${e.message}") }
            }
        }
    }

    fun showReceipt(receipt: ReceiptData) {
        _receiptToShow.value = receipt
    }

    fun dismissReceipt() {
        _receiptToShow.value = null
    }

    fun login(role: UserRole, phoneOrId: String, pin: String) {
        val trimmedId = phoneOrId.trim()
        val trimmedPin = pin.trim()

        if (trimmedId.isBlank()) {
            viewModelScope.launch {
                val label = when (role) {
                    UserRole.CUSTOMER -> "Customer / Membership ID"
                    UserRole.EMPLOYEE -> "Employee ID"
                    UserRole.ADMIN -> "Admin ID"
                }
                _uiMessage.emit("Please enter your $label.")
            }
            return
        }

        // If ID has customer prefix or customer role is chosen, authenticate via customer flow; otherwise use selected staff/admin role
        val effectiveRole = when {
            trimmedId.startsWith("M", ignoreCase = true) || trimmedId.startsWith("CUST", ignoreCase = true) || trimmedId.startsWith("AGU", ignoreCase = true) -> UserRole.CUSTOMER
            else -> role
        }

        if (effectiveRole == UserRole.CUSTOMER) {
            if (!trimmedPin.matches(Regex("^\\d{6}$"))) {
                viewModelScope.launch {
                    _uiMessage.emit("Customer Android Password must be exactly 6 numeric digits (0-9).")
                }
                return
            }
        } else if (trimmedPin.isBlank()) {
            viewModelScope.launch {
                _uiMessage.emit("Please enter your password / PIN.")
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.login(effectiveRole, trimmedId, trimmedPin)
            _isLoading.value = false
            result.onSuccess {
                _uiMessage.emit("Welcome to Arna Gram Unnayan Foundation, ${it.fullName}!")
            }.onFailure {
                _uiMessage.emit(it.message ?: "Authentication failed. Please verify credentials.")
            }
        }
    }

    fun switchRole(role: UserRole, targetCustomerId: String? = null) {
        viewModelScope.launch {
            repository.switchRoleQuick(role, targetCustomerId)
            _selectedCustomerTab.value = 0
            _selectedEmployeeTab.value = 0
            _selectedAdminTab.value = 0
            _uiMessage.emit("Switched workspace mode to ${role.name}")
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _selectedCustomerTab.value = 0
            _selectedEmployeeTab.value = 0
            _selectedAdminTab.value = 0
            _uiMessage.emit("Signed out of Arna Gram portal.")
        }
    }

    fun topUpWallet(borrowerId: String, amount: Double, note: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val txn = repository.addWalletBalance(borrowerId, amount, note)
                _isLoading.value = false
                _uiMessage.emit("Wallet loaded with ₹${String.format("%.2f", amount)} successfully!")
                showReceipt(
                    ReceiptData(
                        title = "Wallet Deposit Receipt",
                        receiptNo = txn.receiptNo,
                        date = txn.formattedDate,
                        amount = txn.amount,
                        payerName = txn.borrowerName,
                        borrowerId = txn.borrowerId,
                        category = "SAVINGS / WALLET TOP-UP",
                        referenceInfo = txn.referenceId,
                        paymentMode = "UPI / Banking",
                        status = "COMPLETED"
                    )
                )
            } catch (e: Exception) {
                _isLoading.value = false
                _uiMessage.emit("Deposit failed: ${e.message}")
            }
        }
    }

    fun applyLoan(
        borrowerId: String,
        loanType: String,
        principal: Double,
        tenorMonths: Int,
        purpose: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val loan = repository.applyLoan(borrowerId, loanType, principal, tenorMonths, purpose)
                _isLoading.value = false
                _uiMessage.emit("Loan Application #${loan.loanId} submitted for sanction review!")
            } catch (e: Exception) {
                _isLoading.value = false
                _uiMessage.emit("Application failed: ${e.message}")
            }
        }
    }

    fun approveLoan(loanId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val loan = repository.approveAndDisburseLoan(loanId)
                _isLoading.value = false
                _uiMessage.emit("Loan #${loan.loanId} sanctioned and ₹${String.format("%.2f", loan.principalAmount)} disbursed!")
            } catch (e: Exception) {
                _isLoading.value = false
                _uiMessage.emit("Approval failed: ${e.message}")
            }
        }
    }

    fun rejectLoan(loanId: String) {
        viewModelScope.launch {
            try {
                repository.rejectLoan(loanId)
                _uiMessage.emit("Loan application #$loanId marked as rejected.")
            } catch (e: Exception) {
                _uiMessage.emit("Error rejecting loan: ${e.message}")
            }
        }
    }

    fun payEmi(
        emi: EmiEntity,
        paymentMode: PaymentMode,
        collectedByAgent: String? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val updatedEmi = repository.payEmi(emi.emiId, paymentMode, collectedByAgent)
                _isLoading.value = false
                _uiMessage.emit("EMI Installment #${emi.installmentNo} paid successfully!")
                showReceipt(
                    ReceiptData(
                        title = "Arna Gram EMI Payment Voucher",
                        receiptNo = updatedEmi.receiptNo ?: "REC-EMI",
                        date = updatedEmi.paidDate ?: "",
                        amount = updatedEmi.paidAmount,
                        payerName = updatedEmi.borrowerName,
                        borrowerId = updatedEmi.borrowerId,
                        category = "LOAN REPAYMENT",
                        referenceInfo = "Loan #${updatedEmi.loanId} (Inst ${updatedEmi.installmentNo}/${updatedEmi.totalInstallments})",
                        paymentMode = paymentMode.name,
                        status = "PAID & VERIFIED"
                    )
                )
            } catch (e: Exception) {
                _isLoading.value = false
                _uiMessage.emit("Payment failed: ${e.message}")
            }
        }
    }

    fun createInvestment(
        borrowerId: String,
        schemeName: String,
        schemeType: InvestmentType,
        principalAmount: Double,
        tenorMonths: Int,
        roiPercentage: Double
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val inv = repository.createInvestment(
                    borrowerId,
                    schemeName,
                    schemeType,
                    principalAmount,
                    tenorMonths,
                    roiPercentage
                )
                _isLoading.value = false
                _uiMessage.emit("Scheme Certificate #${inv.certificateNo} issued successfully!")
                showReceipt(
                    ReceiptData(
                        title = "Gram Investment Certificate",
                        receiptNo = inv.certificateNo,
                        date = inv.startDate,
                        amount = inv.principalAmount,
                        payerName = inv.borrowerName,
                        borrowerId = inv.borrowerId,
                        category = "RURAL SCHEME DEPOSIT",
                        referenceInfo = "${inv.schemeName} (Maturity: ₹${String.format("%.2f", inv.maturityAmount)} on ${inv.maturityDate})",
                        paymentMode = "WALLET DEDUCTION",
                        status = "ACTIVE & SECURED"
                    )
                )
            } catch (e: Exception) {
                _isLoading.value = false
                _uiMessage.emit("Investment failed: ${e.message}")
            }
        }
    }

    fun onboardBorrower(
        name: String,
        phone: String,
        email: String,
        village: String,
        district: String,
        aadhaarNo: String,
        panNo: String,
        occupation: String,
        monthlyIncome: Double,
        initialDeposit: Double
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val borrower = repository.registerBorrower(
                    name, phone, email, village, district, aadhaarNo, panNo, occupation, monthlyIncome, initialDeposit
                )
                _isLoading.value = false
                _uiMessage.emit("Borrower ${borrower.name} registered with ID: ${borrower.borrowerId}")
            } catch (e: Exception) {
                _isLoading.value = false
                _uiMessage.emit("Registration failed: ${e.message}")
            }
        }
    }
}

class MainViewModelFactory(private val repository: ArnaRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
