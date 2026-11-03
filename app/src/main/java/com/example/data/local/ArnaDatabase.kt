package com.arnafoundation.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arnafoundation.app.data.local.dao.BorrowerDao
import com.arnafoundation.app.data.local.dao.EmiDao
import com.arnafoundation.app.data.local.dao.InvestmentDao
import com.arnafoundation.app.data.local.dao.LoanDao
import com.arnafoundation.app.data.local.dao.SessionDao
import com.arnafoundation.app.data.local.dao.TransactionDao
import com.arnafoundation.app.data.local.entities.BorrowerEntity
import com.arnafoundation.app.data.local.entities.EmiEntity
import com.arnafoundation.app.data.local.entities.InvestmentEntity
import com.arnafoundation.app.data.local.entities.LoanEntity
import com.arnafoundation.app.data.local.entities.TransactionEntity
import com.arnafoundation.app.data.local.entities.UserSessionEntity

// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// ROOM DATABASE
// ============================================================

@Database(
    entities = [
        BorrowerEntity::class,
        LoanEntity::class,
        EmiEntity::class,
        InvestmentEntity::class,
        TransactionEntity::class,
        UserSessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ArnaDatabase : RoomDatabase() {

    // ========================================================
    // DAO ACCESS
    // ========================================================

    abstract fun borrowerDao(): BorrowerDao

    abstract fun loanDao(): LoanDao

    abstract fun emiDao(): EmiDao

    abstract fun investmentDao(): InvestmentDao

    abstract fun transactionDao(): TransactionDao

    abstract fun sessionDao(): SessionDao


    // ========================================================
    // DATABASE INSTANCE
    // ========================================================

    companion object {

        @Volatile
        private var INSTANCE: ArnaDatabase? = null


        // ====================================================
        // DATABASE MIGRATION 1 -> 2
        // ====================================================

        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(
                db: SupportSQLiteDatabase
            ) {

                /*
                 * Recreate the user_sessions table so that
                 * the current UserSessionEntity schema is used.
                 *
                 * Other application tables are preserved.
                 */

                db.execSQL(
                    "DROP TABLE IF EXISTS user_sessions"
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_sessions` (
                        `sessionId` INTEGER NOT NULL,
                        `userId` TEXT NOT NULL,
                        `role` TEXT NOT NULL,
                        `fullName` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `email` TEXT NOT NULL,
                        `identifierCode` TEXT NOT NULL,
                        `lastLoginTime` INTEGER NOT NULL,
                        `authToken` TEXT NOT NULL,
                        PRIMARY KEY(`sessionId`)
                    )
                    """.trimIndent()
                )
            }
        }


        // ====================================================
        // GET DATABASE
        // ====================================================

        fun getDatabase(
            context: Context
        ): ArnaDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ArnaDatabase::class.java,
                    "arna_gram_unnayan.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { database ->
                        INSTANCE = database
                    }
            }
        }
    }
}