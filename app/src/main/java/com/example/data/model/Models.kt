package com.arnafoundation.app.data.model

// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// DATA MODELS / ENUMS
// ============================================================


// ============================================================
// USER ROLE
// ============================================================

enum class UserRole {

    CUSTOMER,

    EMPLOYEE,

    ADMIN
}


// ============================================================
// LOAN STATUS
// ============================================================

enum class LoanStatus {

    PENDING_APPROVAL,

    APPROVED,

    ACTIVE,

    CLOSED,

    REJECTED
}


// ============================================================
// EMI STATUS
// ============================================================

enum class EmiStatus {

    PENDING,

    PAID,

    OVERDUE
}


// ============================================================
// PAYMENT MODE
// ============================================================

enum class PaymentMode {

    WALLET,

    CASH,

    UPI,

    NET_BANKING
}


// ============================================================
// INVESTMENT TYPE
// ============================================================

enum class InvestmentType {

    FIXED_DEPOSIT,

    RECURRING_DEPOSIT,

    GRAM_SAMRIDDHI,

    MAHILA_VIKAS_BOND,

    FARMER_GREEN_DEPOSIT
}


// ============================================================
// TRANSACTION TYPE
// ============================================================

enum class TransactionType {

    CREDIT,

    DEBIT
}


// ============================================================
// TRANSACTION CATEGORY
// ============================================================

enum class TransactionCategory {

    LOAN_DISBURSEMENT,

    EMI_PAYMENT,

    INVESTMENT_DEPOSIT,

    INVESTMENT_MATURITY,

    WALLET_TOPUP,

    CASH_WITHDRAWAL,

    WELFARE_GRANT
}