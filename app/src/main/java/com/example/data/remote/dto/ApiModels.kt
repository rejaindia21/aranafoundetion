package com.arnafoundation.app.data.remote.dto

import com.arnafoundation.app.data.model.UserRole
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// REMOTE API DTO MODELS
// ============================================================


// ============================================================
// AUTHENTICATION
// PHP BACKEND: api-customer-login.php
// ============================================================

@JsonClass(generateAdapter = true)
data class CustomerLoginResponse(
    @Json(name = "success")
    val success: Boolean? = null,

    @Json(name = "message")
    val message: String? = null,

    @Json(name = "user")
    val user: CustomerLoginUser? = null
)


@JsonClass(generateAdapter = true)
data class CustomerLoginUser(

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "member_id")
    val memberId: String? = null,

    @Json(name = "name")
    val name: String? = null,

    @Json(name = "member_name")
    val memberName: String? = null,

    @Json(name = "email")
    val email: String? = null,

    @Json(name = "phone")
    val phone: String? = null,

    @Json(name = "mobile")
    val mobile: String? = null,

    @Json(name = "role")
    val role: String? = null,

    @Json(name = "branch_id")
    val branchId: String? = null,

    @Json(name = "branch")
    val branch: String? = null,

    @Json(name = "status")
    val status: String? = null
) {

    val resolvedMemberId: String
        get() = (memberId ?: id ?: "").trim()

    val resolvedName: String
        get() = (memberName ?: name ?: "").trim()

    val resolvedPhone: String
        get() = (phone ?: mobile ?: "").trim()

    val resolvedEmail: String
        get() = (email ?: "").trim()

    val resolvedBranchId: String
        get() = (branchId ?: branch ?: "").trim()

    val resolvedStatus: String
        get() = (status ?: "Active").trim()
}


// ============================================================
// EMPLOYEE LOGIN REQUEST
// ============================================================

@JsonClass(generateAdapter = true)
data class EmployeeLoginRequest(

    @Json(name = "employee_id")
    val employeeId: String,

    @Json(name = "password_pin")
    val passwordPin: String
)


// ============================================================
// ADMIN LOGIN REQUEST
// ============================================================

@JsonClass(generateAdapter = true)
data class AdminLoginRequest(

    @Json(name = "admin_id")
    val adminId: String,

    @Json(name = "password_pin")
    val passwordPin: String
)


// ============================================================
// GENERIC LOGIN RESPONSE
// ============================================================

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "success")
    val success: Boolean? = null,

    @Json(name = "status")
    val status: String? = null,

    @Json(name = "message")
    val message: String? = null,

    @Json(name = "token")
    val token: String? = null,

    @Json(name = "user")
    val user: UserDto? = null
) {
    val isSuccessfulLogin: Boolean
        get() = success == true || status.equals("success", ignoreCase = true)
}


// ============================================================
// USER DTO (ARNA MySQL user table)
// ============================================================

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id")
    val id: String? = null,

    @Json(name = "user_id")
    val userId: String? = null,

    @Json(name = "employee_id")
    val employeeId: String? = null,

    @Json(name = "username")
    val username: String? = null,

    @Json(name = "role")
    val role: String? = null,

    @Json(name = "name")
    val name: String? = null,

    @Json(name = "full_name")
    val fullName: String? = null,

    @Json(name = "employee_name")
    val employeeName: String? = null,

    @Json(name = "fname")
    val fname: String? = null,

    @Json(name = "phone")
    val phone: String? = null,

    @Json(name = "mobile")
    val mobile: String? = null,

    @Json(name = "email")
    val email: String? = null,

    @Json(name = "branchs")
    val branchs: String? = null,

    @Json(name = "branch")
    val branch: String? = null,

    @Json(name = "branch_id")
    val branchId: String? = null,

    @Json(name = "branch_gram")
    val branchGram: String? = null,

    @Json(name = "permissions")
    val permissions: String? = null,

    @Json(name = "stetus")
    val stetus: String? = null,

    @Json(name = "status")
    val status: String? = null,

    @Json(name = "original_role")
    val originalRole: String? = null,

    @Json(name = "designation")
    val designation: String? = null,

    @Json(name = "wallet_balance")
    val walletBalance: Double? = null,

    @Json(name = "token")
    val token: String? = null
) {
    val resolvedId: String
        get() = (employeeId ?: username ?: id ?: userId ?: "").trim()

    val resolvedName: String
        get() = (fullName ?: employeeName ?: name ?: fname ?: username ?: "ARNA User").trim()

    val resolvedRole: String
        get() = (originalRole?.takeIf { it.isNotBlank() } ?: role ?: designation ?: "").trim()

    val resolvedDesignation: String
        get() = (designation?.takeIf { it.isNotBlank() } ?: originalRole?.takeIf { it.isNotBlank() } ?: role ?: "").trim()

    val resolvedPhone: String
        get() = (phone ?: mobile ?: "").trim()

    val resolvedEmail: String
        get() = (email ?: "").trim()

    val resolvedStatus: String
        get() = (stetus ?: status ?: "Unblock").trim()

    val isBlocked: Boolean
        get() {
            val s = resolvedStatus.lowercase()
            return s == "block" || s == "blocked" || s == "inactive" || s == "deactivated" || s == "disabled"
        }
}

/**
 * Normalizes backend database role to the application's three UI access categories:
 * - Admin (Admin, Administrator) -> ADMIN portal
 * - Non-admin employees (M.D, C.E.O, Employee, Staff, Field Officer, etc.) -> EMPLOYEE portal
 * - Customer (Customer, Borrower, Member) -> CUSTOMER portal
 *
 * This preserves the original database role in the user profile while routing correctly.
 */
fun normalizeBackendRole(rawRole: String?): UserRole {
    val clean = (rawRole ?: "").trim().lowercase()
    return when {
        clean == "admin" || clean == "administrator" -> UserRole.ADMIN
        clean == "customer" || clean == "borrower" || clean == "member" -> UserRole.CUSTOMER
        else -> UserRole.EMPLOYEE
    }
}


// ============================================================
// CUSTOMER DASHBOARD DTO
// ============================================================

@JsonClass(generateAdapter = true)
data class CustomerDashboardDto(

    @Json(name = "customer_id")
    val customerId: String,

    @Json(name = "customer_name")
    val customerName: String,

    @Json(name = "branch")
    val branch: String,

    @Json(name = "available_balance")
    val availableBalance: Double,

    @Json(name = "active_loan")
    val activeLoan: LoanDto?,

    @Json(name = "total_loans_count")
    val totalLoansCount: Int = 0,

    @Json(name = "upcoming_emi")
    val upcomingEmi: EmiDto?,

    @Json(name = "total_investments_amount")
    val totalInvestmentsAmount: Double = 0.0,

    @Json(name = "recent_transactions")
    val recentTransactions: List<TransactionDto>? = null
)


// ============================================================
// LOAN DTO
// ============================================================

@JsonClass(generateAdapter = true)
data class LoanDto(

    @Json(name = "loan_id")
    val loanId: String,

    @Json(name = "borrower_id")
    val borrowerId: String,

    @Json(name = "borrower_name")
    val borrowerName: String,

    @Json(name = "loan_type")
    val loanType: String,

    @Json(name = "principal_amount")
    val principalAmount: Double,

    @Json(name = "interest_rate")
    val interestRate: Double,

    @Json(name = "tenor_months")
    val tenorMonths: Int,

    @Json(name = "total_repayable")
    val totalRepayable: Double,

    @Json(name = "total_paid")
    val totalPaid: Double = 0.0,

    @Json(name = "emi_amount")
    val emiAmount: Double,

    @Json(name = "status")
    val status: String,

    @Json(name = "application_date")
    val applicationDate: String,

    @Json(name = "disbursed_date")
    val disbursedDate: String?,

    @Json(name = "purpose")
    val purpose: String
)


// ============================================================
// EMI DTO
// ============================================================

@JsonClass(generateAdapter = true)
data class EmiDto(

    @Json(name = "emi_id")
    val emiId: String,

    @Json(name = "loan_id")
    val loanId: String,

    @Json(name = "borrower_id")
    val borrowerId: String,

    @Json(name = "installment_no")
    val installmentNo: Int,

    @Json(name = "total_installments")
    val totalInstallments: Int,

    @Json(name = "due_date")
    val dueDate: String,

    @Json(name = "amount")
    val amount: Double,

    @Json(name = "principal_portion")
    val principalPortion: Double = 0.0,

    @Json(name = "interest_portion")
    val interestPortion: Double = 0.0,

    @Json(name = "paid_amount")
    val paidAmount: Double = 0.0,

    @Json(name = "paid_date")
    val paidDate: String?,

    @Json(name = "payment_mode")
    val paymentMode: String?,

    @Json(name = "status")
    val status: String,

    @Json(name = "receipt_no")
    val receiptNo: String?
)


// ============================================================
// INVESTMENT DTO
// ============================================================

@JsonClass(generateAdapter = true)
data class InvestmentDto(

    @Json(name = "investment_id")
    val investmentId: String,

    @Json(name = "borrower_id")
    val borrowerId: String,

    @Json(name = "borrower_name")
    val borrowerName: String,

    @Json(name = "scheme_name")
    val schemeName: String,

    @Json(name = "scheme_type")
    val schemeType: String,

    @Json(name = "principal_amount")
    val principalAmount: Double,

    @Json(name = "roi_percentage")
    val roiPercentage: Double,

    @Json(name = "tenor_months")
    val tenorMonths: Int,

    @Json(name = "start_date")
    val startDate: String,

    @Json(name = "maturity_date")
    val maturityDate: String,

    @Json(name = "maturity_amount")
    val maturityAmount: Double,

    @Json(name = "accumulated_interest")
    val accumulatedInterest: Double = 0.0,

    @Json(name = "status")
    val status: String,

    @Json(name = "certificate_no")
    val certificateNo: String
)


// ============================================================
// TRANSACTION DTO
// ============================================================

@JsonClass(generateAdapter = true)
data class TransactionDto(

    @Json(name = "transaction_id")
    val transactionId: String,

    @Json(name = "borrower_id")
    val borrowerId: String,

    @Json(name = "borrower_name")
    val borrowerName: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "category")
    val category: String,

    @Json(name = "amount")
    val amount: Double,

    @Json(name = "balance_after")
    val balanceAfter: Double,

    @Json(name = "formatted_date")
    val formattedDate: String,

    @Json(name = "reference_id")
    val referenceId: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "receipt_no")
    val receiptNo: String
)


// ============================================================
// GENERIC ACTION REQUESTS
// ============================================================


// ------------------------------------------------------------
// PAY EMI
// ------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class PayEmiRequest(

    @Json(name = "emi_id")
    val emiId: String,

    @Json(name = "borrower_id")
    val borrowerId: String,

    @Json(name = "payment_mode")
    val paymentMode: String,

    @Json(name = "amount")
    val amount: Double,

    @Json(name = "collected_by")
    val collectedBy: String? = null
)


// ------------------------------------------------------------
// APPLY LOAN
// ------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class ApplyLoanRequest(

    @Json(name = "borrower_id")
    val borrowerId: String,

    @Json(name = "loan_type")
    val loanType: String,

    @Json(name = "principal_amount")
    val principalAmount: Double,

    @Json(name = "tenor_months")
    val tenorMonths: Int,

    @Json(name = "purpose")
    val purpose: String
)


// ------------------------------------------------------------
// CREATE INVESTMENT
// ------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class CreateInvestmentRequest(

    @Json(name = "borrower_id")
    val borrowerId: String,

    @Json(name = "scheme_name")
    val schemeName: String,

    @Json(name = "scheme_type")
    val schemeType: String,

    @Json(name = "principal_amount")
    val principalAmount: Double,

    @Json(name = "tenor_months")
    val tenorMonths: Int,

    @Json(name = "roi_percentage")
    val roiPercentage: Double
)


// ------------------------------------------------------------
// TOP-UP WALLET
// ------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class TopUpWalletRequest(

    @Json(name = "borrower_id")
    val borrowerId: String,

    @Json(name = "amount")
    val amount: Double,

    @Json(name = "note")
    val note: String
)


// ============================================================
// GENERIC API RESPONSE
// ============================================================

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(

    @Json(name = "status")
    val status: String,

    @Json(name = "message")
    val message: String?,

    @Json(name = "data")
    val data: T?
)


// ============================================================
// LIVE ARNA BACKEND DTOs
// admin-customers.php / admin-loans.php
// ============================================================


// ============================================================
// REAL CUSTOMER LIST RESPONSE
// ============================================================

@JsonClass(generateAdapter = true)
data class RealCustomerListResponse(

    @Json(name = "success")
    val success: Boolean? = null,

    @Json(name = "message")
    val message: String? = null,

    @Json(name = "customers")
    val customers: List<RealCustomerDto>? = null,

    @Json(name = "data")
    val data: List<RealCustomerDto>? = null
)


// ============================================================
// REAL CUSTOMER DTO
// ============================================================

@JsonClass(generateAdapter = true)
data class RealCustomerDto(

    @Json(name = "id")
    val id: Long? = null,

    @Json(name = "database_id")
    val databaseId: Long? = null,

    @Json(name = "borrower_id")
    val borrowerId: String? = null,

    @Json(name = "name")
    val name: String? = null,

    @Json(name = "first_name")
    val firstName: String? = null,

    @Json(name = "last_name")
    val lastName: String? = null,

    @Json(name = "mobile")
    val mobile: String? = null,

    @Json(name = "phone")
    val phone: String? = null,

    @Json(name = "branch")
    val branch: String? = null,

    @Json(name = "email")
    val email: String? = null,

    @Json(name = "status")
    val status: String? = null
)


// ============================================================
// REAL LOAN LIST RESPONSE
// ============================================================

@JsonClass(generateAdapter = true)
data class RealLoanListResponse(

    @Json(name = "success")
    val success: Boolean? = null,

    @Json(name = "action")
    val action: String? = null,

    @Json(name = "total")
    val total: Int? = null,

    @Json(name = "loans")
    val loans: List<RealLoanDto>? = null
)


// ============================================================
// REAL LOAN DTO
// ============================================================

@JsonClass(generateAdapter = true)
data class RealLoanDto(

    @Json(name = "id")
    val id: String? = null,

    @Json(name = "loan_id")
    val loanId: String? = null,

    @Json(name = "borrower_id")
    val borrowerId: String? = null,

    @Json(name = "Branch")
    val branch: String? = null,

    @Json(name = "baccount")
    val baccount: String? = null,

    @Json(name = "name")
    val name: String? = null,

    @Json(name = "Phone")
    val phone: String? = null,

    @Json(name = "Address")
    val address: String? = null,

    @Json(name = "Loan_Type")
    val loanType: String? = null,

    @Json(name = "l_terms")
    val terms: String? = null,

    @Json(name = "L_Duration")
    val duration: String? = null,

    @Json(name = "amount")
    val amount: String? = null,

    @Json(name = "Total_Amount")
    val totalAmount: String? = null,

    @Json(name = "T_Int_Amount")
    val interestAmount: String? = null,

    @Json(name = "Emi_Amount")
    val emiAmount: String? = null,

    @Json(name = "Loan_Fee")
    val loanFee: String? = null,

    @Json(name = "Inc_Fee")
    val incFee: String? = null,

    @Json(name = "admincharge")
    val adminCharge: String? = null,

    @Json(name = "gstfee")
    val gstFee: String? = null,

    @Json(name = "T_pay_Amount")
    val totalPayAmount: String? = null,

    @Json(name = "Start_Date")
    val startDate: String? = null,

    @Json(name = "pay_date")
    val payDate: String? = null,

    @Json(name = "amount_topay")
    val amountToPay: String? = null,

    @Json(name = "teller")
    val teller: String? = null,

    @Json(name = "employee_code")
    val employeeCode: String? = null,

    @Json(name = "status")
    val status: String? = null,

    @Json(name = "Active_stetus")
    val activeStatus: String? = null,

    @Json(name = "apply_date")
    val applyDate: String? = null,

    @Json(name = "display_status")
    val displayStatus: String? = null,

    @Json(name = "amount_numeric")
    val amountNumeric: Double? = null,

    @Json(name = "emi_numeric")
    val emiNumeric: Double? = null
)


// ============================================================
// REAL EMI LIST RESPONSE
// ============================================================

@JsonClass(generateAdapter = true)
data class RealEmiListResponse(

    @Json(name = "success")
    val success: Boolean? = null,

    @Json(name = "action")
    val action: String? = null,

    @Json(name = "emis")
    val emis: List<RealEmiDto>? = null
)


// ============================================================
// REAL EMI DTO
// ============================================================

@JsonClass(generateAdapter = true)
data class RealEmiDto(

    @Json(name = "id")
    val id: Long? = null,

    @Json(name = "loan_id")
    val loanId: String? = null,

    @Json(name = "emi_number")
    val emiNumber: String? = null,

    @Json(name = "emi_date")
    val emiDate: String? = null,

    @Json(name = "Emi_status")
    val emiStatus: String? = null,

    @Json(name = "Branch")
    val branch: String? = null,

    @Json(name = "payment_branch")
    val paymentBranch: String? = null,

    @Json(name = "payment_branch_code")
    val paymentBranchCode: String? = null,

    @Json(name = "payment_branch_name")
    val paymentBranchName: String? = null,

    @Json(name = "name")
    val name: String? = null,

    @Json(name = "mobile")
    val mobile: String? = null,

    @Json(name = "brower_id")
    val borrowerId: String? = null,

    @Json(name = "L_amnt")
    val loanAmount: String? = null,

    @Json(name = "Emi_amnt")
    val emiAmount: String? = null,

    @Json(name = "Principal_amnt")
    val principalAmount: String? = null,

    @Json(name = "Interest_amnt")
    val interestAmount: String? = null,

    @Json(name = "emi_paid_date")
    val emiPaidDate: String? = null,

    @Json(name = "Payment_Mathot")
    val paymentMethod: String? = null,

    @Json(name = "employee_code")
    val employeeCode: String? = null,

    @Json(name = "rec_emp")
    val recEmp: String? = null,

    @Json(name = "paid_amount")
    val paidAmount: String? = null,

    @Json(name = "payment_ref")
    val paymentRef: String? = null,

    @Json(name = "created_at")
    val createdAt: String? = null,

    @Json(name = "updated_at")
    val updatedAt: String? = null
)