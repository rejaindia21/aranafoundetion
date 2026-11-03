package com.arnafoundation.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arnafoundation.app.data.model.EmiStatus
import com.arnafoundation.app.data.model.InvestmentType
import com.arnafoundation.app.data.model.LoanStatus
import com.arnafoundation.app.data.model.PaymentMode
import com.arnafoundation.app.data.model.TransactionCategory
import com.arnafoundation.app.data.model.TransactionType
import com.arnafoundation.app.data.model.UserRole

// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// ROOM DATABASE ENTITIES
// ============================================================


// ============================================================
// BORROWER / CUSTOMER
// ============================================================

@Entity(tableName = "borrowers")
data class BorrowerEntity(

    @PrimaryKey
    val borrowerId: String,

    val name: String,

    val phone: String,

    val email: String,

    val village: String,

    val district: String,

    val aadhaarNo: String,

    val panNo: String,

    val occupation: String,

    val monthlyIncome: Double,

    val walletBalance: Double,

    val pinHash: String = "1234",

    val status: String = "ACTIVE",

    val joinedDate: String,

    val assignedEmployeeId: String = "EMP-001"
)


// ============================================================
// LOAN
// ============================================================

@Entity(tableName = "loan_info")
data class LoanEntity(

    @PrimaryKey
    val loanId: String,

    val borrowerId: String,

    val borrowerName: String,

    val borrowerVillage: String,

    val loanType: String,

    val principalAmount: Double,

    /**
     * Annual interest rate in percentage.
     * Example: 9.5 means 9.5% per year.
     */
    val interestRate: Double,

    val tenorMonths: Int,

    val totalRepayable: Double,

    val totalPaid: Double = 0.0,

    val emiAmount: Double,

    val status: LoanStatus,

    val applicationDate: String,

    val disbursedDate: String? = null,

    val purpose: String,

    val sanctionedBy: String = "Admin / Branch Mgr"
) {

    /**
     * Remaining amount payable for this loan.
     *
     * Never returns a negative outstanding value.
     */
    val outstandingAmount: Double
        get() = (totalRepayable - totalPaid)
            .coerceAtLeast(0.0)
}


// ============================================================
// LOAN EMI
// ============================================================

@Entity(tableName = "loan_emi")
data class EmiEntity(

    @PrimaryKey
    val emiId: String,

    val loanId: String,

    val borrowerId: String,

    val borrowerName: String,

    val installmentNo: Int,

    val totalInstallments: Int,

    val dueDate: String,

    val amount: Double,

    val principalPortion: Double,

    val interestPortion: Double,

    val paidAmount: Double = 0.0,

    val paidDate: String? = null,

    val paymentMode: PaymentMode? = null,

    val status: EmiStatus,

    val collectedBy: String? = null,

    val receiptNo: String? = null
)


// ============================================================
// INVESTMENT
// ============================================================

@Entity(tableName = "investment_entries")
data class InvestmentEntity(

    @PrimaryKey
    val investmentId: String,

    val borrowerId: String,

    val borrowerName: String,

    val schemeName: String,

    val schemeType: InvestmentType,

    val principalAmount: Double,

    /**
     * Annual return / interest rate in percentage.
     * Example: 7.5 means 7.5% per year.
     */
    val roiPercentage: Double,

    val tenorMonths: Int,

    val startDate: String,

    val maturityDate: String,

    val maturityAmount: Double,

    val accumulatedInterest: Double,

    val status: String = "ACTIVE",

    val certificateNo: String
)


// ============================================================
// WALLET TRANSACTION
// ============================================================

@Entity(tableName = "wallet_transaction")
data class TransactionEntity(

    @PrimaryKey
    val transactionId: String,

    val borrowerId: String,

    val borrowerName: String,

    val type: TransactionType,

    val category: TransactionCategory,

    val amount: Double,

    val balanceAfter: Double,

    val timestamp: Long = System.currentTimeMillis(),

    val formattedDate: String,

    val referenceId: String,

    val description: String,

    val receiptNo: String
)


// ============================================================
// USER SESSION
// ============================================================

@Entity(tableName = "user_sessions")
data class UserSessionEntity(

    /**
     * Only one authenticated session is maintained locally.
     */
    @PrimaryKey
    val sessionId: Int = 1,

    /**
     * Customer ID / Employee ID / Admin ID.
     *
     * Examples:
     * M00000001
     * E00001
     * E00000
     */
    val userId: String,

    val role: UserRole,

    val fullName: String,

    val phone: String,

    val email: String,

    /**
     * Customer ID, Employee ID or Admin Code.
     */
    val identifierCode: String,

    val lastLoginTime: Long = System.currentTimeMillis(),

    /**
     * Authentication token returned by the ARNA API.
     * Empty when the current backend does not provide a token.
     */
    val authToken: String = ""
)