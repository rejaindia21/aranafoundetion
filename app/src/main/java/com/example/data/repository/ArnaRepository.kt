package com.arnafoundation.app.data.repository

import android.util.Log

import com.arnafoundation.app.data.local.ArnaDatabase
import com.arnafoundation.app.data.local.entities.BorrowerEntity
import com.arnafoundation.app.data.local.entities.EmiEntity
import com.arnafoundation.app.data.local.entities.InvestmentEntity
import com.arnafoundation.app.data.local.entities.LoanEntity
import com.arnafoundation.app.data.local.entities.TransactionEntity
import com.arnafoundation.app.data.local.entities.UserSessionEntity

import com.arnafoundation.app.data.model.EmiStatus
import com.arnafoundation.app.data.model.InvestmentType
import com.arnafoundation.app.data.model.LoanStatus
import com.arnafoundation.app.data.model.PaymentMode
import com.arnafoundation.app.data.model.TransactionCategory
import com.arnafoundation.app.data.model.TransactionType
import com.arnafoundation.app.data.model.UserRole

import com.arnafoundation.app.data.remote.ApiClient
import com.arnafoundation.app.data.remote.ApiConfigManager

import com.arnafoundation.app.data.remote.dto.AdminLoginRequest
import com.arnafoundation.app.data.remote.dto.ApplyLoanRequest
import com.arnafoundation.app.data.remote.dto.CreateInvestmentRequest
import com.arnafoundation.app.data.remote.dto.CustomerDashboardDto
import com.arnafoundation.app.data.remote.dto.CustomerLoginResponse
import com.arnafoundation.app.data.remote.dto.EmployeeLoginRequest
import com.arnafoundation.app.data.remote.dto.LoginResponse
import com.arnafoundation.app.data.remote.dto.PayEmiRequest
import com.arnafoundation.app.data.remote.dto.TopUpWalletRequest
import com.arnafoundation.app.data.remote.dto.normalizeBackendRole

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * ============================================================
 * ARNA GRAM UNNAYA FOUNDATION
 * REPOSITORY
 * ============================================================
 *
 * Main data layer between:
 *
 * UI
 *   ↓
 * ViewModel
 *   ↓
 * ArnaRepository
 *   ↓
 * ARNA PHP REST API + Room Database
 *
 * IMPORTANT:
 * ------------------------------------------------------------
 * 1. Android does NOT connect directly to MySQL.
 * 2. Live authentication must come from ARNA backend.
 * 3. Customer records are scoped by borrower/customer ID.
 * 4. No fake employee/admin authentication is performed.
 * 5. Mock mode remains available only through the explicit
 *    ApiConfig setting and should remain FALSE in production.
 * ============================================================
 */
class ArnaRepository(
    private val database: ArnaDatabase
) {

    private val borrowerDao = database.borrowerDao()
    private val loanDao = database.loanDao()
    private val emiDao = database.emiDao()
    private val investmentDao = database.investmentDao()
    private val transactionDao = database.transactionDao()
    private val sessionDao = database.sessionDao()

    private val apiService
        get() = ApiClient.getApiService()


    // =========================================================
    // CURRENT SESSION
    // =========================================================

    /**
     * Reactive current authenticated session.
     */
    val currentSession: Flow<UserSessionEntity?> =
        sessionDao.getCurrentSession()


    // =========================================================
    // AUTHENTICATION & LOGIN
    // =========================================================

    /**
     * Authenticate Customer / Employee / Admin.
     *
     * Live mode:
     * - Customer -> ARNA customer login API
     * - Employee -> ARNA employee login API
     * - Admin -> ARNA admin login API
     *
     * No fake local authentication is performed when the
     * live server rejects or cannot verify credentials.
     */
    suspend fun login(
        role: UserRole,
        phoneOrId: String,
        pinOrPassword: String
    ): Result<UserSessionEntity> {

        val trimmedId = phoneOrId.trim()
        val trimmedPin = pinOrPassword.trim()

        if (trimmedId.isBlank()) {
            return Result.failure(
                Exception("Please enter your User ID / Customer ID.")
            )
        }

        if (trimmedPin.isBlank()) {
            return Result.failure(
                Exception("Please enter your password or PIN.")
            )
        }

        return try {

            if (ApiConfigManager.currentConfig.isMockMode) {

                return loginLocalCacheFallback(
                    role = role,
                    phoneOrId = trimmedId,
                    pin = trimmedPin
                )
            }

            when (role) {

                // =================================================
                // CUSTOMER
                // =================================================

                UserRole.CUSTOMER -> {

                    val response = apiService.customerLogin(
                        id = trimmedId,
                        password = trimmedPin
                    )

                    val body = response.body()
                    Log.d(
                        "ArnaAuth",
                        "Customer login result: path=../api/api-customer-login.php, method=POST, status=${response.code()}, success=${body?.success == true}, message=${body?.message ?: response.message()}"
                    )

                    if (!response.isSuccessful) {

                        val message =
                            parseCustomerLoginError(
                                response.errorBody()?.string(),
                                response.code()
                            )

                        return Result.failure(
                            Exception(message)
                        )
                    }

                    if (
                        body == null ||
                        body.success != true ||
                        body.user == null
                    ) {
                        return Result.failure(
                            Exception(
                                body?.message
                                    ?: "Incorrect Customer ID or password."
                            )
                        )
                    }

                    val user = body.user

                    val memberId = (
                        user.memberId
                            ?: user.id
                            ?: trimmedId
                        )
                        .trim()

                    val memberName = (
                        user.memberName
                            ?: user.name
                            ?: "Customer"
                        )
                        .trim()

                    val phone =
                        user.phone
                            ?.trim()
                            ?: ""

                    val email =
                        user.email
                            ?.trim()
                            ?: ""

                    val branchId =
                        user.branchId
                            ?.trim()
                            ?: ""

                    val status =
                        user.status
                            ?.trim()
                            ?: "Active"


                    /*
                     * The current customer login API does not
                     * return a JWT/session token.
                     *
                     * Therefore we intentionally store an
                     * empty token instead of inventing one.
                     */
                    val session = UserSessionEntity(
                        userId = memberId,
                        role = UserRole.CUSTOMER,
                        fullName = memberName,
                        phone = phone,
                        email = email,
                        identifierCode = memberId,
                        authToken = ""
                    )

                    sessionDao.setSession(session)


                    // ---------------------------------------------
                    // Save authenticated customer profile locally
                    // ---------------------------------------------

                    val existing =
                        borrowerDao.getBorrowerById(memberId)

                    val borrower = BorrowerEntity(
                        borrowerId = memberId,

                        name = memberName.ifBlank {
                            existing?.name ?: "Customer"
                        },

                        phone = phone.ifBlank {
                            existing?.phone ?: ""
                        },

                        email = email.ifBlank {
                            existing?.email ?: ""
                        },

                        village = if (branchId.isNotBlank()) {
                            "Branch: $branchId"
                        } else {
                            existing?.village ?: ""
                        },

                        district = if (branchId.isNotBlank()) {
                            branchId
                        } else {
                            existing?.district ?: ""
                        },

                        aadhaarNo =
                            existing?.aadhaarNo ?: "",

                        panNo =
                            existing?.panNo ?: "",

                        occupation =
                            existing?.occupation ?: "Member",

                        monthlyIncome =
                            existing?.monthlyIncome ?: 0.0,

                        walletBalance =
                            existing?.walletBalance ?: 0.0,

                        status =
                            status.ifBlank {
                                existing?.status ?: "ACTIVE"
                            },

                        joinedDate =
                            existing?.joinedDate
                                ?: SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                ).format(Date()),

                        assignedEmployeeId =
                            if (branchId.isNotBlank()) {
                                branchId
                            } else {
                                existing?.assignedEmployeeId ?: ""
                            }
                    )

                    borrowerDao.insertBorrower(borrower)

                    Result.success(session)
                }


                // =================================================
                // EMPLOYEE & ADMIN (ARNA MySQL user table)
                // =================================================

                UserRole.EMPLOYEE,
                UserRole.ADMIN -> {

                    // Query the ARNA user table authentication endpoint
                    // Form fields: id (employee_id, username, email, or numeric id), password
                    var response = apiService.userLogin(
                        id = trimmedId,
                        password = trimmedPin
                    )

                    // Fallback to appUserLogin if relative unified login returns 404
                    if (!response.isSuccessful && response.code() == 404) {
                        response = apiService.appUserLogin(
                            id = trimmedId,
                            password = trimmedPin
                        )
                    }

                    // Fallback to role-specific endpoint if unified login returns 404
                    if (!response.isSuccessful && response.code() == 404) {
                        response = if (role == UserRole.ADMIN) {
                            apiService.adminLogin(
                                id = trimmedId,
                                password = trimmedPin
                            )
                        } else {
                            apiService.employeeLogin(
                                id = trimmedId,
                                password = trimmedPin
                            )
                        }
                    }

                    if (!response.isSuccessful) {
                        val message = parseLoginError(
                            response.errorBody()?.string(),
                            response.code()
                        )
                        return Result.failure(Exception(message))
                    }

                    val body = response.body()
                    val userDto = body?.user
                    val rawRole = userDto?.resolvedRole ?: ""
                    val normalizedRole = normalizeBackendRole(rawRole)

                    Log.d(
                        "ArnaAuth",
                        "User login result: path=../api/api-login.php, method=POST, status=${response.code()}, success=${body?.isSuccessfulLogin == true}, role=${normalizedRole.name}, message=${body?.message ?: response.message()}"
                    )

                    if (body == null || !body.isSuccessfulLogin || userDto == null) {
                        val message = body?.message
                            ?.takeIf { it.isNotBlank() }
                            ?: "Authentication failed. Please verify your credentials."
                        return Result.failure(Exception(message))
                    }

                    // Verify account status (MySQL user table stetus column)
                    if (userDto.isBlocked) {
                        return Result.failure(
                            Exception("Account is currently blocked or inactive. Please contact the administrator.")
                        )
                    }

                    val resolvedId = userDto.resolvedId.ifBlank { trimmedId }
                    val resolvedName = userDto.resolvedName
                    val resolvedPhone = userDto.resolvedPhone
                    val resolvedEmail = userDto.resolvedEmail
                    val authToken = body.token ?: userDto.token ?: ""

                    if (authToken.isNotBlank()) {
                        ApiConfigManager.setToken(authToken)
                    }

                    val displayRole = when {
                        userDto.resolvedDesignation.isNotBlank() -> userDto.resolvedDesignation
                        rawRole.isNotBlank() -> rawRole
                        else -> normalizedRole.name
                    }

                    val identifierCode = if (displayRole.isNotBlank()) {
                        "$resolvedId • $displayRole"
                    } else {
                        resolvedId
                    }

                    val session = UserSessionEntity(
                        userId = resolvedId,
                        role = normalizedRole,
                        fullName = resolvedName,
                        phone = resolvedPhone,
                        email = resolvedEmail,
                        identifierCode = identifierCode,
                        authToken = authToken
                    )

                    sessionDao.setSession(session)
                    Result.success(session)
                }
            }

        } catch (e: java.net.UnknownHostException) {

            Log.w(
                "ArnaRepository",
                "ARNA server host unreachable: ${e.message}"
            )

            Result.failure(
                Exception(
                    "Unable to connect to the ARNA server. Please check your internet connection."
                )
            )

        } catch (e: java.net.SocketTimeoutException) {

            Log.w(
                "ArnaRepository",
                "ARNA server request timed out: ${e.message}"
            )

            Result.failure(
                Exception(
                    "Connection timed out. Please try again."
                )
            )

        } catch (e: java.io.IOException) {

            Log.w(
                "ArnaRepository",
                "Network I/O error during login: ${e.message}"
            )

            Result.failure(
                Exception(
                    "Network error. Unable to communicate with the ARNA server."
                )
            )

        } catch (e: Exception) {

            Log.e(
                "ArnaRepository",
                "Login error",
                e
            )

            Result.failure(
                Exception(
                    e.message
                        ?.takeIf { it.isNotBlank() }
                        ?: "Authentication error occurred."
                )
            )
        }
    }


    // =========================================================
    // CUSTOMER LOGIN ERROR PARSER
    // =========================================================

    private fun parseCustomerLoginError(
        errorBody: String?,
        httpCode: Int
    ): String {

        if (errorBody.isNullOrBlank()) {
            return "Authentication failed (HTTP $httpCode). Please check your credentials."
        }

        return try {

            val moshi =
                com.squareup.moshi.Moshi.Builder()
                    .addLast(
                        com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()
                    )
                    .build()

            val adapter =
                moshi.adapter(
                    CustomerLoginResponse::class.java
                )

            adapter
                .fromJson(errorBody)
                ?.message
                ?.takeIf { it.isNotBlank() }
                ?: "Authentication failed (HTTP $httpCode). Please check your credentials."

        } catch (_: Exception) {

            "Authentication failed (HTTP $httpCode). Please check your credentials."
        }
    }


    // =========================================================
    // USER LOGIN ERROR PARSER (EMPLOYEE & ADMIN)
    // =========================================================

    private fun parseLoginError(
        errorBody: String?,
        httpCode: Int
    ): String {

        if (errorBody.isNullOrBlank()) {
            return "Authentication failed (HTTP $httpCode). Please check your credentials."
        }

        return try {

            val moshi =
                com.squareup.moshi.Moshi.Builder()
                    .addLast(
                        com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()
                    )
                    .build()

            val adapter =
                moshi.adapter(
                    LoginResponse::class.java
                )

            adapter
                .fromJson(errorBody)
                ?.message
                ?.takeIf { it.isNotBlank() }
                ?: "Authentication failed (HTTP $httpCode). Please check your credentials."

        } catch (_: Exception) {

            "Authentication failed (HTTP $httpCode). Please check your credentials."
        }
    }


    // =========================================================
    // LOCAL CACHE FALLBACK
    // =========================================================
    //
    // This is reached ONLY when ApiConfigManager.isMockMode
    // is explicitly TRUE.
    //
    // Production configuration remains:
    // isMockMode = false
    // =========================================================

    private suspend fun loginLocalCacheFallback(
        role: UserRole,
        phoneOrId: String,
        pin: String
    ): Result<UserSessionEntity> {

        /*
         * The PIN is intentionally not used as a fake
         * production authentication mechanism.
         *
         * Mock/local mode is only for development/testing.
         */

        return when (role) {

            UserRole.CUSTOMER -> {

                val borrower =
                    borrowerDao.getBorrowerByPhone(phoneOrId)
                        ?: borrowerDao.getBorrowerById(phoneOrId)

                if (borrower != null) {

                    val session = UserSessionEntity(
                        userId = borrower.borrowerId,
                        role = UserRole.CUSTOMER,
                        fullName = borrower.name,
                        phone = borrower.phone,
                        email = borrower.email,
                        identifierCode = borrower.borrowerId,
                        authToken = ""
                    )

                    sessionDao.setSession(session)

                    Result.success(session)

                } else {

                    Result.failure(
                        Exception(
                            "Customer not found for '$phoneOrId'."
                        )
                    )
                }
            }


            UserRole.EMPLOYEE -> {

                Result.failure(
                    Exception(
                        "Local employee authentication is disabled. Please use the live ARNA server."
                    )
                )
            }


            UserRole.ADMIN -> {

                Result.failure(
                    Exception(
                        "Local administrator authentication is disabled. Please use the live ARNA server."
                    )
                )
            }
        }
    }


    // =========================================================
    // ROLE QUICK SWITCH
    // =========================================================
    //
    // Development helper only.
    // It does NOT authenticate against the live server.
    //
    // Do not use this as a production login mechanism.
    // =========================================================

    suspend fun switchRoleQuick(
        role: UserRole,
        targetCustomerId: String? = null
    ) {

        if (!ApiConfigManager.currentConfig.isMockMode) {
            Log.w(
                "ArnaRepository",
                "switchRoleQuick ignored because live mode is enabled."
            )
            return
        }

        when (role) {

            UserRole.CUSTOMER -> {

                val targetId =
                    targetCustomerId?.trim()

                if (targetId.isNullOrBlank()) {
                    return
                }

                val borrower =
                    borrowerDao.getBorrowerById(targetId)

                if (borrower != null) {

                    sessionDao.setSession(
                        UserSessionEntity(
                            userId = borrower.borrowerId,
                            role = UserRole.CUSTOMER,
                            fullName = borrower.name,
                            phone = borrower.phone,
                            email = borrower.email,
                            identifierCode = borrower.borrowerId,
                            authToken = ""
                        )
                    )
                }
            }

            UserRole.EMPLOYEE -> {
                Log.w(
                    "ArnaRepository",
                    "Quick employee role switching is disabled."
                )
            }

            UserRole.ADMIN -> {
                Log.w(
                    "ArnaRepository",
                    "Quick admin role switching is disabled."
                )
            }
        }
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    suspend fun logout() {

        ApiConfigManager.clearToken()

        sessionDao.clearSession()
    }


    // =========================================================
    // BORROWERS / CUSTOMER PROFILE
    // =========================================================

    fun getAllBorrowers():
            Flow<List<BorrowerEntity>> =
        borrowerDao.getAllBorrowers()


    fun observeCustomerById(
        id: String
    ): Flow<BorrowerEntity?> =
        borrowerDao.observeCustomerById(id)


    suspend fun getBorrower(
        id: String
    ): BorrowerEntity? =
        borrowerDao.getBorrowerById(id)


    suspend fun getCustomerById(
        id: String
    ): BorrowerEntity? =
        borrowerDao.getCustomerById(id)


    suspend fun saveCustomerProfile(
        customer: BorrowerEntity
    ) =
        borrowerDao.insertBorrower(customer)


    suspend fun updateCustomerProfile(
        customer: BorrowerEntity
    ) =
        borrowerDao.updateBorrower(customer)


    suspend fun deleteCustomerProfile(
        customerId: String
    ) =
        borrowerDao.deleteBorrowerById(customerId)


    suspend fun registerBorrower(
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
    ): BorrowerEntity {

        val newId =
            "AGU-CUST-${System.currentTimeMillis() % 10000}"

        val dateFormat =
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )

        val borrower = BorrowerEntity(

            borrowerId = newId,

            name = name,

            phone = phone,

            email =
                email.ifBlank {
                    "${name.lowercase(Locale.getDefault())
                        .replace(" ", ".")}@arnagram.org"
                },

            village = village,

            district = district,

            aadhaarNo = aadhaarNo,

            panNo = panNo,

            occupation = occupation,

            monthlyIncome = monthlyIncome,

            walletBalance = initialDeposit,

            joinedDate =
                dateFormat.format(Date()),

            assignedEmployeeId = "EMP-001"
        )

        borrowerDao.insertBorrower(borrower)


        if (initialDeposit > 0) {

            val txnId =
                "TXN-${System.currentTimeMillis() % 100000}"

            val receipt =
                "REC-DEP-${System.currentTimeMillis() % 10000}"

            val fullDateFormat =
                SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
                )

            transactionDao.insertTransaction(
                TransactionEntity(

                    transactionId = txnId,

                    borrowerId = newId,

                    borrowerName = name,

                    type = TransactionType.CREDIT,

                    category =
                        TransactionCategory.WALLET_TOPUP,

                    amount = initialDeposit,

                    balanceAfter = initialDeposit,

                    formattedDate =
                        fullDateFormat.format(Date()),

                    referenceId =
                        "ONBOARDING-DEPOSIT",

                    description =
                        "Initial Member Savings Account Opening Deposit",

                    receiptNo = receipt
                )
            )
        }

        return borrower
    }


    // =========================================================
    // WALLET
    // =========================================================

    suspend fun addWalletBalance(
        borrowerId: String,
        amount: Double,
        note: String
    ): TransactionEntity {

        require(amount > 0) {
            "Wallet top-up amount must be greater than zero."
        }

        try {

            if (!ApiConfigManager.currentConfig.isMockMode) {

                apiService.topUpWallet(
                    TopUpWalletRequest(
                        borrowerId = borrowerId,
                        amount = amount,
                        note = note
                    )
                )
            }

        } catch (e: Exception) {

            Log.w(
                "ArnaRepository",
                "API wallet top-up error: ${e.message}"
            )
        }


        val borrower =
            borrowerDao.getBorrowerById(borrowerId)
                ?: throw Exception("Borrower not found")


        val newBalance =
            borrower.walletBalance + amount


        borrowerDao.updateWalletBalance(
            borrowerId,
            amount
        )


        val txnId =
            "TXN-${System.currentTimeMillis() % 100000}"

        val receipt =
            "REC-TOPUP-${System.currentTimeMillis() % 10000}"

        val fullDateFormat =
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )


        val txn = TransactionEntity(

            transactionId = txnId,

            borrowerId = borrowerId,

            borrowerName = borrower.name,

            type = TransactionType.CREDIT,

            category =
                TransactionCategory.WALLET_TOPUP,

            amount = amount,

            balanceAfter = newBalance,

            formattedDate =
                fullDateFormat.format(Date()),

            referenceId =
                "UPI/DIRECT_DEPOSIT",

            description =
                note.ifBlank {
                    "Wallet Top-up Deposit via UPI/Bank"
                },

            receiptNo = receipt
        )


        transactionDao.insertTransaction(txn)

        return txn
    }


    // =========================================================
    // LOANS
    // =========================================================

    fun getLoansForBorrower(
        borrowerId: String
    ): Flow<List<LoanEntity>> =
        loanDao.getLoansByBorrower(borrowerId)


    fun observeCustomerLoans(
        customerId: String
    ): Flow<List<LoanEntity>> =
        loanDao.observeLoansByCustomerId(customerId)


    suspend fun getCustomerLoans(
        customerId: String
    ): List<LoanEntity> =
        loanDao.getLoansByCustomerId(customerId)


    suspend fun getLoanById(
        loanId: String
    ): LoanEntity? =
        loanDao.getLoanById(loanId)


    fun observeLoanById(
        loanId: String
    ): Flow<LoanEntity?> =
        loanDao.observeLoanById(loanId)


    suspend fun saveLoan(
        loan: LoanEntity
    ) =
        loanDao.insertLoan(loan)


    suspend fun updateLoan(
        loan: LoanEntity
    ) =
        loanDao.updateLoan(loan)


    suspend fun deleteLoan(
        loanId: String
    ) =
        loanDao.deleteLoanById(loanId)


    fun getAllLoans():
            Flow<List<LoanEntity>> =
        loanDao.getAllLoans()


    // =========================================================
    // CUSTOMER LOAN SYNC
    // =========================================================

    suspend fun syncCustomerLoans(
        customerId: String
    ): Result<List<LoanEntity>> {

        val trimmedId =
            customerId.trim()

        if (trimmedId.isBlank()) {
            return Result.success(emptyList())
        }

        return try {

            if (!ApiConfigManager.currentConfig.isMockMode) {

                val response =
                    apiService.getRealLoans(
                        action = "list",
                        tid = "E00000",
                        search = trimmedId
                    )


                if (response.isSuccessful) {

                    val apiResponse =
                        response.body()

                    val loanDtos =
                        apiResponse?.loans
                            ?: emptyList()


                    /*
                     * Keep only records belonging to the
                     * authenticated customer.
                     */
                    val filteredDtos =
                        loanDtos.filter { dto ->

                            dto.borrowerId.equals(
                                trimmedId,
                                ignoreCase = true
                            ) ||

                            dto.loanId?.contains(
                                trimmedId,
                                ignoreCase = true
                            ) == true
                        }


                    /*
                     * Replace this customer's cached loans
                     * with fresh server records.
                     */
                    loanDao.deleteLoansByCustomerId(
                        trimmedId
                    )


                    val entities =
                        filteredDtos.map { dto ->

                            val loanId =
                                (
                                    dto.loanId
                                        ?: "LN-${dto.id ?: System.currentTimeMillis()}"
                                    ).trim()


                            val principalAmount =
                                dto.amountNumeric
                                    ?: dto.amount
                                        ?.replace(",", "")
                                        ?.toDoubleOrNull()
                                    ?: 0.0


                            val emiAmount =
                                dto.emiNumeric
                                    ?: dto.emiAmount
                                        ?.replace(",", "")
                                        ?.toDoubleOrNull()
                                    ?: 0.0


                            val totalAmount =
                                dto.totalAmount
                                    ?.replace(",", "")
                                    ?.toDoubleOrNull()
                                    ?: principalAmount


                            val tenor =
                                dto.duration
                                    ?.toIntOrNull()
                                    ?: 12


                            val displayStatus =
                                dto.displayStatus
                                    ?: dto.status
                                    ?: "Active"


                            val loanStatus =
                                when {

                                    displayStatus.contains(
                                        "closed",
                                        ignoreCase = true
                                    ) ->
                                        LoanStatus.CLOSED

                                    displayStatus.contains(
                                        "rejected",
                                        ignoreCase = true
                                    ) ->
                                        LoanStatus.REJECTED

                                    displayStatus.contains(
                                        "approved",
                                        ignoreCase = true
                                    ) ||

                                    displayStatus.contains(
                                        "active",
                                        ignoreCase = true
                                    ) ->
                                        LoanStatus.ACTIVE

                                    else ->
                                        LoanStatus.PENDING_APPROVAL
                                }


                            LoanEntity(

                                loanId = loanId,

                                borrowerId =
                                    dto.borrowerId
                                        ?.trim()
                                        ?: trimmedId,

                                borrowerName =
                                    dto.name
                                        ?.trim()
                                        ?: "Customer",

                                borrowerVillage =
                                    dto.address
                                        ?.trim()
                                        ?: dto.branch
                                            ?.trim()
                                        ?: "",

                                loanType =
                                    dto.loanType
                                        ?.trim()
                                        ?: "Personal Loan",

                                principalAmount =
                                    principalAmount,

                                interestRate =
                                    dto.interestAmount
                                        ?.replace(",", "")
                                        ?.toDoubleOrNull()
                                        ?: 0.0,

                                tenorMonths =
                                    tenor,

                                totalRepayable =
                                    totalAmount,

                                totalPaid =
                                    dto.totalPayAmount
                                        ?.replace(",", "")
                                        ?.toDoubleOrNull()
                                        ?: 0.0,

                                emiAmount =
                                    emiAmount,

                                status =
                                    loanStatus,

                                applicationDate =
                                    dto.applyDate
                                        ?: dto.startDate
                                        ?: "",

                                disbursedDate =
                                    dto.payDate
                                        ?: dto.startDate
                                        ?: "",

                                purpose =
                                    dto.loanType
                                        ?: "Personal Loan"
                            )
                        }


                    if (entities.isNotEmpty()) {

                        loanDao.insertAllLoans(
                            entities
                        )
                    }


                    val localLoans =
                        loanDao.getLoansByCustomerId(
                            trimmedId
                        )

                    Result.success(localLoans)

                } else {

                    Result.success(
                        loanDao.getLoansByCustomerId(
                            trimmedId
                        )
                    )
                }

            } else {

                Result.success(
                    loanDao.getLoansByCustomerId(
                        trimmedId
                    )
                )
            }

        } catch (e: Exception) {

            Log.w(
                "ArnaRepository",
                "Customer loan sync failed: ${e.message}"
            )

            Result.success(
                loanDao.getLoansByCustomerId(
                    trimmedId
                )
            )
        }
    }


    // =========================================================
    // EMIs
    // =========================================================

    fun observeCustomerEmis(
        customerId: String
    ): Flow<List<EmiEntity>> =
        emiDao.observeEmisByCustomerId(customerId)


    suspend fun getCustomerEmis(
        customerId: String
    ): List<EmiEntity> =
        emiDao.getEmisByCustomerId(customerId)


    fun observeEmisForLoan(
        loanId: String
    ): Flow<List<EmiEntity>> =
        emiDao.observeEmisByLoanId(loanId)


    suspend fun getEmisForLoan(
        loanId: String
    ): List<EmiEntity> =
        emiDao.getEmisByLoanId(loanId)


    suspend fun getEmiById(
        emiId: String
    ): EmiEntity? =
        emiDao.getEmiById(emiId)


    fun observeEmiById(
        emiId: String
    ): Flow<EmiEntity?> =
        emiDao.observeEmiById(emiId)


    suspend fun saveEmi(
        emi: EmiEntity
    ) =
        emiDao.insertEmi(emi)


    suspend fun saveAllEmis(
        emis: List<EmiEntity>
    ) =
        emiDao.insertAllEmis(emis)


    suspend fun updateEmi(
        emi: EmiEntity
    ) =
        emiDao.updateEmi(emi)


    suspend fun deleteEmi(
        emiId: String
    ) =
        emiDao.deleteEmiById(emiId)


    fun getAllEmis():
            Flow<List<EmiEntity>> =
        emiDao.getAllEmis()


    fun getEmisForBorrower(
        borrowerId: String
    ): Flow<List<EmiEntity>> =
        emiDao.getEmisByBorrower(borrowerId)


    // =========================================================
    // CUSTOMER EMI SYNC
    // =========================================================

    suspend fun syncCustomerEmis(
        customerId: String
    ): Result<List<EmiEntity>> {

        val trimmedId =
            customerId.trim()

        if (trimmedId.isBlank()) {
            return Result.success(emptyList())
        }

        return try {

            if (!ApiConfigManager.currentConfig.isMockMode) {

                var loans =
                    loanDao.getLoansByCustomerId(
                        trimmedId
                    )


                if (loans.isEmpty()) {

                    syncCustomerLoans(
                        trimmedId
                    )

                    loans =
                        loanDao.getLoansByCustomerId(
                            trimmedId
                        )
                }


                val allFetchedEmis =
                    mutableListOf<EmiEntity>()

                val generatedTxns =
                    mutableListOf<TransactionEntity>()


                for (loan in loans) {

                    val response =
                        apiService.getRealEmis(
                            action = "emi_list",
                            tid = "E00000",
                            loanId = loan.loanId
                        )


                    if (!response.isSuccessful) {
                        continue
                    }


                    val body =
                        response.body()

                    val dtoList =
                        body?.emis
                            ?: emptyList()


                    var loanTotalPaid = 0.0


                    val entities =
                        dtoList.map { dto ->

                            val isPaid =
                                dto.emiStatus
                                    ?.trim()
                                    ?.equals(
                                        "Paid",
                                        ignoreCase = true
                                    ) == true


                            val parsedStatus =
                                if (isPaid) {
                                    EmiStatus.PAID
                                } else {
                                    EmiStatus.PENDING
                                }


                            val emiAmount =
                                dto.emiAmount
                                    ?.replace(",", "")
                                    ?.toDoubleOrNull()
                                    ?: loan.emiAmount


                            val paidAmount =
                                dto.paidAmount
                                    ?.replace(",", "")
                                    ?.toDoubleOrNull()
                                    ?: if (isPaid) {
                                        emiAmount
                                    } else {
                                        0.0
                                    }


                            val installmentNo =
                                dto.emiNumber
                                    ?.toIntOrNull()
                                    ?: 1


                            val receiptNo =
                                dto.paymentRef
                                    ?.ifBlank { null }
                                    ?: "REC-${loan.loanId}-$installmentNo"


                            if (isPaid) {

                                loanTotalPaid +=
                                    paidAmount


                                generatedTxns.add(
                                    TransactionEntity(

                                        transactionId =
                                            dto.paymentRef
                                                ?.ifBlank {
                                                    null
                                                }
                                                ?: "TXN-${loan.loanId}-$installmentNo",

                                        borrowerId =
                                            trimmedId,

                                        borrowerName =
                                            dto.name
                                                ?.ifBlank {
                                                    loan.borrowerName
                                                }
                                                ?: loan.borrowerName,

                                        type =
                                            TransactionType.DEBIT,

                                        category =
                                            TransactionCategory.EMI_PAYMENT,

                                        amount =
                                            paidAmount,

                                        balanceAfter =
                                            0.0,

                                        formattedDate =
                                            dto.emiPaidDate
                                                ?.ifBlank {
                                                    dto.createdAt
                                                        ?: dto.emiDate
                                                        ?: ""
                                                }
                                                ?: (
                                                    dto.emiDate
                                                        ?: ""
                                                    ),

                                        referenceId =
                                            "${loan.loanId} #Installment $installmentNo",

                                        description =
                                            "EMI Repayment - ${loan.loanType} (Installment $installmentNo)",

                                        receiptNo =
                                            receiptNo
                                    )
                                )
                            }


                            EmiEntity(

                                emiId =
                                    "${loan.loanId}-EMI-$installmentNo",

                                loanId =
                                    loan.loanId,

                                borrowerId =
                                    trimmedId,

                                borrowerName =
                                    dto.name
                                        ?.ifBlank {
                                            loan.borrowerName
                                        }
                                        ?: loan.borrowerName,

                                installmentNo =
                                    installmentNo,

                                totalInstallments =
                                    loan.tenorMonths,

                                dueDate =
                                    dto.emiDate
                                        ?: "",

                                amount =
                                    emiAmount,

                                principalPortion =
                                    dto.principalAmount
                                        ?.replace(",", "")
                                        ?.toDoubleOrNull()
                                        ?: 0.0,

                                interestPortion =
                                    dto.interestAmount
                                        ?.replace(",", "")
                                        ?.toDoubleOrNull()
                                        ?: 0.0,

                                paidAmount =
                                    paidAmount,

                                paidDate =
                                    dto.emiPaidDate,

                                paymentMode =
                                    if (isPaid) {
                                        PaymentMode.CASH
                                    } else {
                                        null
                                    },

                                status =
                                    parsedStatus,

                                collectedBy =
                                    dto.recEmp
                                        ?: dto.employeeCode,

                                receiptNo =
                                    receiptNo
                            )
                        }


                    if (entities.isNotEmpty()) {
                        allFetchedEmis.addAll(
                            entities
                        )
                    }


                    if (loanTotalPaid > 0) {

                        loanDao.insertLoan(
                            loan.copy(
                                totalPaid =
                                    loanTotalPaid
                            )
                        )
                    }
                }


                if (allFetchedEmis.isNotEmpty()) {

                    emiDao.deleteEmisByCustomerId(
                        trimmedId
                    )

                    emiDao.insertAllEmis(
                        allFetchedEmis
                    )
                }


                if (generatedTxns.isNotEmpty()) {

                    transactionDao.insertAllTransactions(
                        generatedTxns
                    )
                }


                val localEmis =
                    emiDao.getEmisByCustomerId(
                        trimmedId
                    )

                Result.success(localEmis)

            } else {

                Result.success(
                    emiDao.getEmisByCustomerId(
                        trimmedId
                    )
                )
            }

        } catch (e: Exception) {

            Log.w(
                "ArnaRepository",
                "Customer EMI sync failed: ${e.message}"
            )

            Result.success(
                emiDao.getEmisByCustomerId(
                    trimmedId
                )
            )
        }
    }


    // =========================================================
    // APPLY LOAN
    // =========================================================

    suspend fun applyLoan(
        borrowerId: String,
        loanType: String,
        principal: Double,
        tenorMonths: Int,
        purpose: String
    ): LoanEntity {

        require(principal > 0) {
            "Loan amount must be greater than zero."
        }

        require(tenorMonths > 0) {
            "Loan tenure must be greater than zero."
        }


        try {

            if (!ApiConfigManager.currentConfig.isMockMode) {

                apiService.applyLoan(
                    ApplyLoanRequest(
                        borrowerId = borrowerId,
                        loanType = loanType,
                        principalAmount = principal,
                        tenorMonths = tenorMonths,
                        purpose = purpose
                    )
                )
            }

        } catch (e: Exception) {

            Log.w(
                "ArnaRepository",
                "API loan application error: ${e.message}"
            )
        }


        val borrower =
            borrowerDao.getBorrowerById(borrowerId)
                ?: throw Exception(
                    "Borrower not found"
                )


        val interestRate =
            when {

                principal <= 30000 ->
                    7.5

                principal <= 75000 ->
                    8.5

                else ->
                    9.5
            }


        val totalInterest =
            principal *
                (interestRate / 100.0) *
                (tenorMonths / 12.0)


        val totalRepayable =
            principal + totalInterest


        val emiAmount =
            totalRepayable / tenorMonths


        val loanId =
            "AGU-LN-${System.currentTimeMillis() % 100000}"


        val dateFormat =
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )


        val applicationDate =
            dateFormat.format(Date())


        val loan = LoanEntity(

            loanId = loanId,

            borrowerId = borrowerId,

            borrowerName = borrower.name,

            borrowerVillage = borrower.village,

            loanType = loanType,

            principalAmount = principal,

            interestRate = interestRate,

            tenorMonths = tenorMonths,

            totalRepayable = totalRepayable,

            totalPaid = 0.0,

            emiAmount = emiAmount,

            status =
                LoanStatus.PENDING_APPROVAL,

            applicationDate =
                applicationDate,

            disbursedDate = null,

            purpose = purpose,

            sanctionedBy =
                "Awaiting Verification"
        )


        loanDao.insertLoan(loan)

        return loan
    }


    // =========================================================
    // APPROVE & DISBURSE LOAN
    // =========================================================

    suspend fun approveAndDisburseLoan(
        loanId: String
    ): LoanEntity {

        val loan =
            loanDao.getLoanById(loanId)
                ?: throw Exception(
                    "Loan not found"
                )


        val dateFormat =
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )


        val fullDateFormat =
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )


        val disbursedDate =
            dateFormat.format(Date())


        // -----------------------------------------------------
        // 1. Update loan status
        // -----------------------------------------------------

        loanDao.updateLoanStatus(
            loanId,
            LoanStatus.ACTIVE,
            disbursedDate
        )


        // -----------------------------------------------------
        // 2. Credit principal to customer wallet
        // -----------------------------------------------------

        val borrower =
            borrowerDao.getBorrowerById(
                loan.borrowerId
            )


        val newBalance =
            (borrower?.walletBalance ?: 0.0) +
                loan.principalAmount


        borrowerDao.updateWalletBalance(
            loan.borrowerId,
            loan.principalAmount
        )


        // -----------------------------------------------------
        // 3. Record disbursement transaction
        // -----------------------------------------------------

        val txnId =
            "TXN-DISB-${System.currentTimeMillis() % 100000}"


        val receipt =
            "REC-DISB-${System.currentTimeMillis() % 10000}"


        transactionDao.insertTransaction(
            TransactionEntity(

                transactionId = txnId,

                borrowerId =
                    loan.borrowerId,

                borrowerName =
                    loan.borrowerName,

                type =
                    TransactionType.CREDIT,

                category =
                    TransactionCategory.LOAN_DISBURSEMENT,

                amount =
                    loan.principalAmount,

                balanceAfter =
                    newBalance,

                formattedDate =
                    fullDateFormat.format(Date()),

                referenceId =
                    loanId,

                description =
                    "Loan Disbursal Credit: ${loan.loanType}",

                receiptNo =
                    receipt
            )
        )


        // -----------------------------------------------------
        // 4. Generate EMI schedule
        // -----------------------------------------------------

        val emis =
            mutableListOf<EmiEntity>()


        val principalPerMonth =
            loan.principalAmount /
                loan.tenorMonths


        val interestPerMonth =
            (
                loan.totalRepayable -
                    loan.principalAmount
                ) /
                loan.tenorMonths


        for (i in 1..loan.tenorMonths) {

            val dueCal =
                Calendar.getInstance()

            dueCal.add(
                Calendar.MONTH,
                i
            )


            val dueDateStr =
                dateFormat.format(
                    dueCal.time
                )


            emis.add(
                EmiEntity(

                    emiId =
                        "AGU-EMI-${loanId.takeLast(4)}-$i",

                    loanId =
                        loanId,

                    borrowerId =
                        loan.borrowerId,

                    borrowerName =
                        loan.borrowerName,

                    installmentNo =
                        i,

                    totalInstallments =
                        loan.tenorMonths,

                    dueDate =
                        dueDateStr,

                    amount =
                        loan.emiAmount,

                    principalPortion =
                        principalPerMonth,

                    interestPortion =
                        interestPerMonth,

                    paidAmount =
                        0.0,

                    paidDate =
                        null,

                    paymentMode =
                        null,

                    status =
                        EmiStatus.PENDING,

                    collectedBy =
                        null,

                    receiptNo =
                        null
                )
            )
        }


        emiDao.insertAllEmis(emis)


        return loan.copy(
            status = LoanStatus.ACTIVE,
            disbursedDate = disbursedDate
        )
    }


    // =========================================================
    // REJECT LOAN
    // =========================================================

    suspend fun rejectLoan(
        loanId: String
    ) {

        loanDao.updateLoanStatus(
            loanId,
            LoanStatus.REJECTED,
            null
        )
    }


    // =========================================================
    // PAY EMI
    // =========================================================

    suspend fun payEmi(
        emiId: String,
        paymentMode: PaymentMode,
        collectedByAgent: String? = null
    ): EmiEntity {

        val emi =
            emiDao.getEmiById(emiId)
                ?: throw Exception(
                    "EMI installment not found"
                )


        val borrower =
            borrowerDao.getBorrowerById(
                emi.borrowerId
            )
                ?: throw Exception(
                    "Borrower not found"
                )


        if (emi.status == EmiStatus.PAID) {
            throw Exception(
                "This EMI installment has already been paid."
            )
        }


        if (
            paymentMode == PaymentMode.WALLET &&
            borrower.walletBalance < emi.amount
        ) {

            throw Exception(
                "Insufficient wallet balance " +
                    "(₹${
                        String.format(
                            Locale.getDefault(),
                            "%.2f",
                            borrower.walletBalance
                        )
                    }). " +
                    "Please top up or choose Cash/UPI."
            )
        }


        try {

            if (!ApiConfigManager.currentConfig.isMockMode) {

                apiService.payEmi(
                    PayEmiRequest(
                        emiId = emiId,
                        borrowerId = emi.borrowerId,
                        paymentMode = paymentMode.name,
                        amount = emi.amount,
                        collectedBy = collectedByAgent
                    )
                )
            }

        } catch (e: Exception) {

            Log.w(
                "ArnaRepository",
                "API pay EMI error: ${e.message}"
            )
        }


        val fullDateFormat =
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )


        val dateNow =
            fullDateFormat.format(Date())


        val receiptNo =
            "REC-EMI-${System.currentTimeMillis() % 100000}"


        var balanceAfter =
            borrower.walletBalance


        if (paymentMode == PaymentMode.WALLET) {

            balanceAfter -=
                emi.amount

            borrowerDao.updateWalletBalance(
                emi.borrowerId,
                -emi.amount
            )
        }


        val updatedEmi =
            emi.copy(

                status =
                    EmiStatus.PAID,

                paidAmount =
                    emi.amount,

                paidDate =
                    dateNow,

                paymentMode =
                    paymentMode,

                collectedBy =
                    collectedByAgent
                        ?: "Direct Online",

                receiptNo =
                    receiptNo
            )


        emiDao.updateEmi(
            updatedEmi
        )


        loanDao.recordLoanPayment(
            emi.loanId,
            emi.amount
        )


        val txnId =
            "TXN-EMI-${System.currentTimeMillis() % 100000}"


        transactionDao.insertTransaction(
            TransactionEntity(

                transactionId =
                    txnId,

                borrowerId =
                    emi.borrowerId,

                borrowerName =
                    emi.borrowerName,

                type =
                    TransactionType.DEBIT,

                category =
                    TransactionCategory.EMI_PAYMENT,

                amount =
                    emi.amount,

                balanceAfter =
                    balanceAfter,

                formattedDate =
                    dateNow,

                referenceId =
                    "${emi.loanId} #Installment ${emi.installmentNo}/${emi.totalInstallments}",

                description =
                    "Loan Repayment via $paymentMode [${emi.receiptNo ?: receiptNo}]",

                receiptNo =
                    receiptNo
            )
        )


        val loan =
            loanDao.getLoanById(
                emi.loanId
            )


        if (
            loan != null &&
            (loan.totalPaid + emi.amount) >=
            (loan.totalRepayable - 1.0)
        ) {

            loanDao.updateLoanStatus(
                emi.loanId,
                LoanStatus.CLOSED,
                loan.disbursedDate
            )
        }


        return updatedEmi
    }


    // =========================================================
    // INVESTMENTS
    // =========================================================

    fun getInvestmentsForBorrower(
        borrowerId: String
    ): Flow<List<InvestmentEntity>> =
        investmentDao.getInvestmentsByBorrower(
            borrowerId
        )


    fun getAllInvestments():
            Flow<List<InvestmentEntity>> =
        investmentDao.getAllInvestments()


    suspend fun createInvestment(
        borrowerId: String,
        schemeName: String,
        schemeType: InvestmentType,
        principalAmount: Double,
        tenorMonths: Int,
        roiPercentage: Double
    ): InvestmentEntity {

        val borrower =
            borrowerDao.getBorrowerById(
                borrowerId
            )
                ?: throw Exception(
                    "Borrower not found"
                )


        if (
            borrower.walletBalance <
            principalAmount
        ) {

            throw Exception(
                "Insufficient wallet balance for investment deposit. Please top up first."
            )
        }


        try {

            if (!ApiConfigManager.currentConfig.isMockMode) {

                apiService.createInvestment(
                    CreateInvestmentRequest(
                        borrowerId = borrowerId,
                        schemeName = schemeName,
                        schemeType = schemeType.name,
                        principalAmount = principalAmount,
                        tenorMonths = tenorMonths,
                        roiPercentage = roiPercentage
                    )
                )
            }

        } catch (e: Exception) {

            Log.w(
                "ArnaRepository",
                "API investment create error: ${e.message}"
            )
        }


        val newBalance =
            borrower.walletBalance -
                principalAmount


        borrowerDao.updateWalletBalance(
            borrowerId,
            -principalAmount
        )


        val dateFormat =
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )


        val fullDateFormat =
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )


        val startDate =
            dateFormat.format(Date())


        val cal =
            Calendar.getInstance()

        cal.add(
            Calendar.MONTH,
            tenorMonths
        )


        val maturityDate =
            dateFormat.format(cal.time)


        val totalInterest =
            principalAmount *
                (roiPercentage / 100.0) *
                (tenorMonths / 12.0)


        val maturityAmount =
            principalAmount +
                totalInterest


        val invId =
            "AGU-INV-${System.currentTimeMillis() % 100000}"


        val certNo =
            "AGU-CERT-${System.currentTimeMillis() % 100000}"


        val investment =
            InvestmentEntity(

                investmentId =
                    invId,

                borrowerId =
                    borrowerId,

                borrowerName =
                    borrower.name,

                schemeName =
                    schemeName,

                schemeType =
                    schemeType,

                principalAmount =
                    principalAmount,

                roiPercentage =
                    roiPercentage,

                tenorMonths =
                    tenorMonths,

                startDate =
                    startDate,

                maturityDate =
                    maturityDate,

                maturityAmount =
                    maturityAmount,

                accumulatedInterest =
                    0.0,

                status =
                    "ACTIVE",

                certificateNo =
                    certNo
            )


        investmentDao.insertInvestment(
            investment
        )


        val txnId =
            "TXN-INV-${System.currentTimeMillis() % 100000}"


        transactionDao.insertTransaction(
            TransactionEntity(

                transactionId =
                    txnId,

                borrowerId =
                    borrowerId,

                borrowerName =
                    borrower.name,

                type =
                    TransactionType.DEBIT,

                category =
                    TransactionCategory.INVESTMENT_DEPOSIT,

                amount =
                    principalAmount,

                balanceAfter =
                    newBalance,

                formattedDate =
                    fullDateFormat.format(Date()),

                referenceId =
                    invId,

                description =
                    "Deposit into Scheme: $schemeName ($tenorMonths M @ $roiPercentage% p.a.)",

                receiptNo =
                    certNo
            )
        )


        return investment
    }


    // =========================================================
    // TRANSACTIONS / PASSBOOK
    // =========================================================

    fun getTransactionsForBorrower(
        borrowerId: String
    ): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByBorrower(
            borrowerId
        )


    fun observeCustomerTransactions(
        customerId: String
    ): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByBorrower(
            customerId
        )


    fun getAllTransactions():
            Flow<List<TransactionEntity>> =
        transactionDao.getAllTransactions()


    fun observeCustomerInvestments(
        customerId: String
    ): Flow<List<InvestmentEntity>> =
        investmentDao.getInvestmentsByBorrower(
            customerId
        )


    // =========================================================
    // CUSTOMER DASHBOARD SYNC
    // =========================================================

    suspend fun syncCustomerDashboard(
        customerId: String
    ): Result<CustomerDashboardDto?> {

        val trimmedId =
            customerId.trim()

        if (trimmedId.isBlank()) {
            return Result.success(null)
        }


        return try {

            if (!ApiConfigManager.currentConfig.isMockMode) {

                val response =
                    apiService.getRealCustomers(
                        action = "list",
                        search = trimmedId
                    )


                if (response.isSuccessful) {

                    val body =
                        response.body()


                    val customers =
                        body?.customers
                            ?: body?.data
                            ?: emptyList()


                    val matchedCustomer =
                        customers.firstOrNull {

                            it.borrowerId.equals(
                                trimmedId,
                                ignoreCase = true
                            ) ||

                            it.id?.toString() ==
                            trimmedId ||

                            it.mobile ==
                            trimmedId
                        }


                    if (matchedCustomer != null) {

                        val fullName =
                            matchedCustomer.name
                                ?.trim()
                                ?.ifBlank { null }
                                ?: listOfNotNull(
                                    matchedCustomer.firstName
                                        ?.trim(),

                                    matchedCustomer.lastName
                                        ?.trim()
                                )
                                    .joinToString(" ")
                                    .trim()


                        val existing =
                            borrowerDao.getCustomerById(
                                trimmedId
                            )


                        val updated =
                            (
                                existing
                                    ?: BorrowerEntity(

                                        borrowerId =
                                            trimmedId,

                                        name =
                                            fullName.ifBlank {
                                                "Customer"
                                            },

                                        phone =
                                            matchedCustomer.phone
                                                ?: matchedCustomer.mobile
                                                ?: "",

                                        email =
                                            matchedCustomer.email
                                                ?: "",

                                        village =
                                            matchedCustomer.branch
                                                ?: "",

                                        district =
                                            matchedCustomer.branch
                                                ?: "",

                                        aadhaarNo =
                                            "",

                                        panNo =
                                            "",

                                        occupation =
                                            "Member",

                                        monthlyIncome =
                                            0.0,

                                        walletBalance =
                                            0.0,

                                        status =
                                            matchedCustomer.status
                                                ?: "Active",

                                        joinedDate =
                                            SimpleDateFormat(
                                                "dd MMM yyyy",
                                                Locale.getDefault()
                                            ).format(Date()),

                                        assignedEmployeeId =
                                            matchedCustomer.branch
                                                ?: ""
                                    )
                                )
                                .copy(

                                    name =
                                        if (fullName.isNotBlank()) {
                                            fullName
                                        } else {
                                            existing?.name
                                                ?: "Customer"
                                        },

                                    phone =
                                        matchedCustomer.phone
                                            ?: matchedCustomer.mobile
                                            ?: existing?.phone
                                            ?: "",

                                    email =
                                        matchedCustomer.email
                                            ?: existing?.email
                                            ?: "",

                                    status =
                                        matchedCustomer.status
                                            ?: existing?.status
                                            ?: "Active",

                                    district =
                                        matchedCustomer.branch
                                            ?: existing?.district
                                            ?: ""
                                )


                        borrowerDao.insertBorrower(
                            updated
                        )
                    }
                }


                // -------------------------------------------------
                // Sync customer-specific data
                // -------------------------------------------------

                syncCustomerLoans(
                    trimmedId
                )

                syncCustomerInvestments(
                    trimmedId
                )


                Result.success(null)

            } else {

                Result.success(null)
            }

        } catch (e: Exception) {

            Log.w(
                "ArnaRepository",
                "Customer dashboard sync failed: ${e.message}"
            )

            Result.success(null)
        }
    }


    // =========================================================
    // CUSTOMER TRANSACTION SYNC
    // =========================================================

    suspend fun syncCustomerTransactions(
        customerId: String
    ): Result<List<TransactionEntity>> {

        val trimmedId =
            customerId.trim()

        if (trimmedId.isBlank()) {
            return Result.success(emptyList())
        }


        return try {

            val localTxns =
                transactionDao.getTransactionsByCustomerId(
                    trimmedId
                )


            if (localTxns.isEmpty()) {

                /*
                 * EMI records can generate authentic
                 * server-derived EMI transactions.
                 */
                syncCustomerEmis(
                    trimmedId
                )
            }


            val refreshed =
                transactionDao.getTransactionsByCustomerId(
                    trimmedId
                )


            Result.success(refreshed)

        } catch (e: Exception) {

            Log.w(
                "ArnaRepository",
                "Customer transaction sync failed: ${e.message}"
            )


            val local =
                transactionDao.getTransactionsByCustomerId(
                    trimmedId
                )


            Result.success(local)
        }
    }


    // =========================================================
    // CUSTOMER INVESTMENT SYNC
    // =========================================================
    //
    // Existing ARNA endpoint retained exactly as supplied:
    //
    // /api/app/admin/admin-investments.php
    //
    // This section parses the existing server HTML because
    // the supplied backend currently exposes investment data
    // through that page rather than a confirmed JSON endpoint.
    //
    // Do NOT replace this URL with an invented API endpoint.
    // =========================================================

    suspend fun syncCustomerInvestments(
        customerId: String
    ): Result<List<InvestmentEntity>> =
        withContext(Dispatchers.IO) {

            val trimmedId =
                customerId.trim()


            if (trimmedId.isBlank()) {
                return@withContext Result.success(
                    emptyList()
                )
            }


            try {

                if (!ApiConfigManager.currentConfig.isMockMode) {

                    val encodedCustomerId =
                        java.net.URLEncoder.encode(
                            trimmedId,
                            "UTF-8"
                        )


                    val url =
                        "https://arnafoundation.com/api/app/admin/admin-investments.php?search=$encodedCustomerId"


                    val request =
                        okhttp3.Request.Builder()
                            .url(url)
                            .get()
                            .build()


                    val client =
                        ApiClient.getRawOkHttpClient()


                    client.newCall(request)
                        .execute()
                        .use { response ->

                            if (response.isSuccessful) {

                                val html =
                                    response.body
                                        ?.string()
                                        ?: ""


                                /*
                                 * Replace previous cached investment
                                 * records for this customer only.
                                 */
                                investmentDao.deleteInvestmentsByCustomerId(
                                    trimmedId
                                )


                                val customer =
                                    borrowerDao.getCustomerById(
                                        trimmedId
                                    )


                                val customerName =
                                    customer?.name
                                        ?: "Customer"


                                val rowRegex =
                                    Regex(
                                        """<strong\s+style="[^"]*"\s*>\s*(RD\d+|DRD\d+|FD\d+|MIS\d+|[A-Z0-9]+)\s*</strong>.*?ID:\s*($trimmedId).*?Type:\s*([A-Za-z0-9]+)\s*Period:\s*(\d+\s*(?:Months|Days)?).*?Rs\.\s*([\d,.]+).*?Rs\.\s*([\d,.]+).*?Rs\.\s*([\d,.]+)\s*(\d{2}-\d{2}-\d{4})\s*(\d{2}-\d{2}-\d{4})""",
                                        RegexOption.DOT_MATCHES_ALL
                                    )


                                val matches =
                                    rowRegex.findAll(html)


                                val entities =
                                    matches.map { match ->

                                        val invId =
                                            match.groupValues[1]
                                                .trim()


                                        val typeStr =
                                            match.groupValues[3]
                                                .trim()
                                                .uppercase(
                                                    Locale.getDefault()
                                                )


                                        val periodStr =
                                            match.groupValues[4]
                                                .trim()


                                        val depositStr =
                                            match.groupValues[5]
                                                .replace(",", "")
                                                .trim()


                                        val interestStr =
                                            match.groupValues[6]
                                                .replace(",", "")
                                                .trim()


                                        val maturityStr =
                                            match.groupValues[7]
                                                .replace(",", "")
                                                .trim()


                                        val startDate =
                                            match.groupValues[8]
                                                .trim()


                                        val maturityDate =
                                            match.groupValues[9]
                                                .trim()


                                        val tenor =
                                            periodStr
                                                .filter {
                                                    it.isDigit()
                                                }
                                                .toIntOrNull()
                                                ?: 12


                                        val depositAmt =
                                            depositStr
                                                .toDoubleOrNull()
                                                ?: 0.0


                                        val interestAmt =
                                            interestStr
                                                .toDoubleOrNull()
                                                ?: 0.0


                                        val maturityAmt =
                                            maturityStr
                                                .toDoubleOrNull()
                                                ?: (
                                                    depositAmt +
                                                        interestAmt
                                                    )


                                        val investmentType =
                                            when {

                                                typeStr.contains(
                                                    "RD"
                                                ) ||
                                                typeStr.contains(
                                                    "DRD"
                                                ) ->
                                                    InvestmentType.RECURRING_DEPOSIT

                                                typeStr.contains(
                                                    "FD"
                                                ) ->
                                                    InvestmentType.FIXED_DEPOSIT

                                                typeStr.contains(
                                                    "BOND"
                                                ) ->
                                                    InvestmentType.MAHILA_VIKAS_BOND

                                                typeStr.contains(
                                                    "GREEN"
                                                ) ->
                                                    InvestmentType.FARMER_GREEN_DEPOSIT

                                                else ->
                                                    InvestmentType.GRAM_SAMRIDDHI
                                            }


                                        val schemeName =
                                            when (
                                                investmentType
                                            ) {

                                                InvestmentType.RECURRING_DEPOSIT ->
                                                    "Recurring Deposit (RD)"

                                                InvestmentType.FIXED_DEPOSIT ->
                                                    "Fixed Deposit (FD)"

                                                InvestmentType.GRAM_SAMRIDDHI ->
                                                    "Gram Samriddhi Yojana"

                                                InvestmentType.MAHILA_VIKAS_BOND ->
                                                    "Mahila Vikas Bond"

                                                InvestmentType.FARMER_GREEN_DEPOSIT ->
                                                    "Farmer Green Deposit"
                                            }


                                        InvestmentEntity(

                                            investmentId =
                                                invId,

                                            borrowerId =
                                                trimmedId,

                                            borrowerName =
                                                customerName,

                                            schemeName =
                                                schemeName,

                                            schemeType =
                                                investmentType,

                                            principalAmount =
                                                depositAmt,

                                            roiPercentage =
                                                if (depositAmt > 0) {
                                                    (
                                                        interestAmt /
                                                            depositAmt
                                                        ) * 100.0
                                                } else {
                                                    10.0
                                                },

                                            tenorMonths =
                                                if (
                                                    periodStr.contains(
                                                        "Day",
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    tenor / 30
                                                } else {
                                                    tenor
                                                },

                                            startDate =
                                                startDate,

                                            maturityDate =
                                                maturityDate,

                                            maturityAmount =
                                                maturityAmt,

                                            accumulatedInterest =
                                                interestAmt,

                                            status =
                                                "Approved",

                                            certificateNo =
                                                "CERT-$invId"
                                        )
                                    }
                                    .toList()


                                if (entities.isNotEmpty()) {

                                    investmentDao.insertAllInvestments(
                                        entities
                                    )
                                }
                            }
                        }
                }


                val local =
                    investmentDao.getInvestmentsByCustomerId(
                        trimmedId
                    )


                Result.success(local)

            } catch (e: Exception) {

                Log.w(
                    "ArnaRepository",
                    "Customer investment sync failed: ${e.message}"
                )


                val local =
                    investmentDao.getInvestmentsByCustomerId(
                        trimmedId
                    )


                Result.success(local)
            }
        }
}