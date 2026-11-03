package com.arnafoundation.app.data.repository

import com.arnafoundation.app.data.local.ArnaDatabase

/**
 * ============================================================
 * ARNA GRAM UNNAYA FOUNDATION
 * Database Initializer
 * ============================================================
 *
 * Purpose:
 * ------------------------------------------------------------
 * Initializes the local Room database without inserting any
 * fake, mock, demo, sample, or hardcoded business data.
 *
 * IMPORTANT:
 * ------------------------------------------------------------
 * 1. No fake customer data is created.
 * 2. No fake loan data is created.
 * 3. No fake EMI data is created.
 * 4. No fake transaction data is created.
 * 5. No fake investment data is created.
 * 6. Real customer information comes from the ARNA Foundation
 *    backend API after authenticated customer login/synchronization.
 * 7. Android does NOT connect directly to MySQL.
 *
 * ============================================================
 */
object DatabaseInitializer {

    /**
     * Seeds the local Room database only when required.
     *
     * Currently no seed data is inserted because the ARNA
     * application must use real backend data.
     *
     * @param database ARNA Foundation Room database instance.
     */
    suspend fun seedDatabaseIfEmpty(database: ArnaDatabase) {
        // --------------------------------------------------------
        // NO FAKE / MOCK / DEMO DATA
        // --------------------------------------------------------
        //
        // Do not insert hardcoded:
        // - Customers
        // - Loans
        // - EMI schedules
        // - Transactions
        // - Investments
        // - Wallet balances
        //
        // Real data is synchronized from the ARNA Foundation
        // backend after successful authentication.
        //
        // The database parameter is intentionally kept in the
        // method signature so existing application initialization
        // code remains compatible.
        //
        // No action is required here at present.
    }
}