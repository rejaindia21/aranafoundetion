package com.arnafoundation.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.arnafoundation.app.data.local.entities.BorrowerEntity
import com.arnafoundation.app.data.local.entities.EmiEntity
import com.arnafoundation.app.data.local.entities.InvestmentEntity
import com.arnafoundation.app.data.local.entities.LoanEntity
import com.arnafoundation.app.data.local.entities.TransactionEntity
import com.arnafoundation.app.data.local.entities.UserSessionEntity
import com.arnafoundation.app.data.model.EmiStatus
import com.arnafoundation.app.data.model.LoanStatus
import kotlinx.coroutines.flow.Flow

// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// BORROWER DAO
// ============================================================

@Dao
interface BorrowerDao {

    @Query("SELECT * FROM borrowers ORDER BY name ASC")
    fun getAllBorrowers(): Flow<List<BorrowerEntity>>

    @Query(
        """
        SELECT * FROM borrowers
        WHERE borrowerId = :id
        LIMIT 1
        """
    )
    fun observeBorrowerById(
        id: String
    ): Flow<BorrowerEntity?>

    @Query(
        """
        SELECT * FROM borrowers
        WHERE borrowerId = :id
        LIMIT 1
        """
    )
    fun observeCustomerById(
        id: String
    ): Flow<BorrowerEntity?>

    @Query(
        """
        SELECT * FROM borrowers
        WHERE borrowerId = :id
        LIMIT 1
        """
    )
    suspend fun getBorrowerById(
        id: String
    ): BorrowerEntity?

    @Query(
        """
        SELECT * FROM borrowers
        WHERE borrowerId = :id
        LIMIT 1
        """
    )
    suspend fun getCustomerById(
        id: String
    ): BorrowerEntity?

    @Query(
        """
        SELECT * FROM borrowers
        WHERE phone = :phone
        LIMIT 1
        """
    )
    suspend fun getBorrowerByPhone(
        phone: String
    ): BorrowerEntity?

    @Query(
        """
        SELECT * FROM borrowers
        WHERE assignedEmployeeId = :empId
        ORDER BY name ASC
        """
    )
    fun getBorrowersByEmployee(
        empId: String
    ): Flow<List<BorrowerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorrower(
        borrower: BorrowerEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBorrowers(
        borrowers: List<BorrowerEntity>
    )

    @Update
    suspend fun updateBorrower(
        borrower: BorrowerEntity
    )

    @Delete
    suspend fun deleteBorrower(
        borrower: BorrowerEntity
    )

    @Query(
        """
        DELETE FROM borrowers
        WHERE borrowerId = :id
        """
    )
    suspend fun deleteBorrowerById(
        id: String
    )

    @Query(
        """
        UPDATE borrowers
        SET walletBalance = walletBalance + :delta
        WHERE borrowerId = :borrowerId
        """
    )
    suspend fun updateWalletBalance(
        borrowerId: String,
        delta: Double
    )
}


// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// LOAN DAO
// ============================================================

@Dao
interface LoanDao {

    @Query(
        """
        SELECT * FROM loan_info
        ORDER BY applicationDate DESC
        """
    )
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Query(
        """
        SELECT * FROM loan_info
        WHERE borrowerId = :borrowerId
        ORDER BY applicationDate DESC
        """
    )
    fun getLoansByBorrower(
        borrowerId: String
    ): Flow<List<LoanEntity>>

    @Query(
        """
        SELECT * FROM loan_info
        WHERE borrowerId = :customerId
        ORDER BY applicationDate DESC
        """
    )
    fun observeLoansByCustomerId(
        customerId: String
    ): Flow<List<LoanEntity>>

    @Query(
        """
        SELECT * FROM loan_info
        WHERE borrowerId = :customerId
        ORDER BY applicationDate DESC
        """
    )
    suspend fun getLoansByCustomerId(
        customerId: String
    ): List<LoanEntity>

    @Query(
        """
        SELECT * FROM loan_info
        WHERE loanId = :loanId
        LIMIT 1
        """
    )
    suspend fun getLoanById(
        loanId: String
    ): LoanEntity?

    @Query(
        """
        SELECT * FROM loan_info
        WHERE loanId = :loanId
        LIMIT 1
        """
    )
    fun observeLoanById(
        loanId: String
    ): Flow<LoanEntity?>

    @Query(
        """
        SELECT * FROM loan_info
        WHERE status = :status
        ORDER BY applicationDate DESC
        """
    )
    fun getLoansByStatus(
        status: LoanStatus
    ): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(
        loan: LoanEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLoans(
        loans: List<LoanEntity>
    )

    @Update
    suspend fun updateLoan(
        loan: LoanEntity
    )

    @Delete
    suspend fun deleteLoan(
        loan: LoanEntity
    )

    @Query(
        """
        DELETE FROM loan_info
        WHERE loanId = :loanId
        """
    )
    suspend fun deleteLoanById(
        loanId: String
    )

    @Query(
        """
        DELETE FROM loan_info
        WHERE borrowerId = :customerId
        """
    )
    suspend fun deleteLoansByCustomerId(
        customerId: String
    )

    @Query(
        """
        UPDATE loan_info
        SET
            status = :status,
            disbursedDate = :disbursedDate
        WHERE loanId = :loanId
        """
    )
    suspend fun updateLoanStatus(
        loanId: String,
        status: LoanStatus,
        disbursedDate: String?
    )

    @Query(
        """
        UPDATE loan_info
        SET totalPaid = totalPaid + :amount
        WHERE loanId = :loanId
        """
    )
    suspend fun recordLoanPayment(
        loanId: String,
        amount: Double
    )
}


// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// EMI DAO
// ============================================================

@Dao
interface EmiDao {

    @Query(
        """
        SELECT * FROM loan_emi
        ORDER BY dueDate ASC
        """
    )
    fun getAllEmis(): Flow<List<EmiEntity>>

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE borrowerId = :borrowerId
        ORDER BY dueDate ASC
        """
    )
    fun getEmisByBorrower(
        borrowerId: String
    ): Flow<List<EmiEntity>>

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE borrowerId = :customerId
        ORDER BY dueDate ASC
        """
    )
    fun observeEmisByCustomerId(
        customerId: String
    ): Flow<List<EmiEntity>>

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE borrowerId = :customerId
        ORDER BY dueDate ASC
        """
    )
    fun observeCustomerEmis(
        customerId: String
    ): Flow<List<EmiEntity>>

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE borrowerId = :customerId
        ORDER BY dueDate ASC
        """
    )
    suspend fun getEmisByCustomerId(
        customerId: String
    ): List<EmiEntity>

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE borrowerId = :customerId
        ORDER BY dueDate ASC
        """
    )
    suspend fun getCustomerEmis(
        customerId: String
    ): List<EmiEntity>

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE loanId = :loanId
        ORDER BY installmentNo ASC
        """
    )
    fun getEmisByLoan(
        loanId: String
    ): Flow<List<EmiEntity>>

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE loanId = :loanId
        ORDER BY installmentNo ASC
        """
    )
    fun observeEmisByLoanId(
        loanId: String
    ): Flow<List<EmiEntity>>

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE loanId = :loanId
        ORDER BY installmentNo ASC
        """
    )
    suspend fun getEmisByLoanId(
        loanId: String
    ): List<EmiEntity>

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE status = :status
        ORDER BY dueDate ASC
        """
    )
    fun getEmisByStatus(
        status: EmiStatus
    ): Flow<List<EmiEntity>>

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE emiId = :emiId
        LIMIT 1
        """
    )
    suspend fun getEmiById(
        emiId: String
    ): EmiEntity?

    @Query(
        """
        SELECT * FROM loan_emi
        WHERE emiId = :emiId
        LIMIT 1
        """
    )
    fun observeEmiById(
        emiId: String
    ): Flow<EmiEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmi(
        emi: EmiEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEmis(
        emis: List<EmiEntity>
    )

    @Update
    suspend fun updateEmi(
        emi: EmiEntity
    )

    @Delete
    suspend fun deleteEmi(
        emi: EmiEntity
    )

    @Query(
        """
        DELETE FROM loan_emi
        WHERE emiId = :emiId
        """
    )
    suspend fun deleteEmiById(
        emiId: String
    )

    @Query(
        """
        DELETE FROM loan_emi
        WHERE borrowerId = :customerId
        """
    )
    suspend fun deleteEmisByCustomerId(
        customerId: String
    )
}


// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// INVESTMENT DAO
// ============================================================

@Dao
interface InvestmentDao {

    @Query(
        """
        SELECT * FROM investment_entries
        ORDER BY startDate DESC
        """
    )
    fun getAllInvestments(): Flow<List<InvestmentEntity>>

    @Query(
        """
        SELECT * FROM investment_entries
        WHERE borrowerId = :borrowerId
        ORDER BY startDate DESC
        """
    )
    fun getInvestmentsByBorrower(
        borrowerId: String
    ): Flow<List<InvestmentEntity>>

    @Query(
        """
        SELECT * FROM investment_entries
        WHERE borrowerId = :borrowerId
        ORDER BY startDate DESC
        """
    )
    suspend fun getInvestmentsByCustomerId(
        borrowerId: String
    ): List<InvestmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(
        investment: InvestmentEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllInvestments(
        investments: List<InvestmentEntity>
    )

    @Query(
        """
        DELETE FROM investment_entries
        WHERE borrowerId = :customerId
        """
    )
    suspend fun deleteInvestmentsByCustomerId(
        customerId: String
    )
}


// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// TRANSACTION DAO
// ============================================================

@Dao
interface TransactionDao {

    @Query(
        """
        SELECT * FROM wallet_transaction
        ORDER BY timestamp DESC
        """
    )
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM wallet_transaction
        WHERE borrowerId = :borrowerId
        ORDER BY timestamp DESC
        """
    )
    fun getTransactionsByBorrower(
        borrowerId: String
    ): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM wallet_transaction
        WHERE borrowerId = :borrowerId
        ORDER BY timestamp DESC
        """
    )
    suspend fun getTransactionsByCustomerId(
        borrowerId: String
    ): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(
        transaction: TransactionEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(
        transactions: List<TransactionEntity>
    )

    @Query(
        """
        DELETE FROM wallet_transaction
        WHERE borrowerId = :customerId
        """
    )
    suspend fun deleteTransactionsByCustomerId(
        customerId: String
    )
}


// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// SESSION DAO
// ============================================================

@Dao
interface SessionDao {

    @Query(
        """
        SELECT * FROM user_sessions
        ORDER BY lastLoginTime DESC
        LIMIT 1
        """
    )
    fun getCurrentSession(): Flow<UserSessionEntity?>

    @Query(
        """
        SELECT * FROM user_sessions
        ORDER BY lastLoginTime DESC
        LIMIT 1
        """
    )
    suspend fun getSessionSync(): UserSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(
        session: UserSessionEntity
    )

    @Query("DELETE FROM user_sessions")
    suspend fun clearSession()

    @Transaction
    suspend fun setSession(
        session: UserSessionEntity
    ) {
        clearSession()
        insertSession(session)
    }
}