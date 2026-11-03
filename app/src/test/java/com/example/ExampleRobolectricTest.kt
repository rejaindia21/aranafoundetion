package com.arnafoundation.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.arnafoundation.app.data.local.ArnaDatabase
import com.arnafoundation.app.data.local.entities.BorrowerEntity
import com.arnafoundation.app.data.local.entities.EmiEntity
import com.arnafoundation.app.data.local.entities.LoanEntity
import com.arnafoundation.app.data.local.entities.UserSessionEntity
import com.arnafoundation.app.data.model.EmiStatus
import com.arnafoundation.app.data.model.LoanStatus
import com.arnafoundation.app.data.model.PaymentMode
import com.arnafoundation.app.data.model.UserRole
import com.arnafoundation.app.data.repository.ArnaRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val appName = context.getString(R.string.app_name)

        assertEquals(
            "ARNA GRAM UNNAYA FOUNDATION",
            appName
        )
    }

    @Test
    fun `verify database session insertion and retrieval`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val inMemoryDb = Room
            .inMemoryDatabaseBuilder(
                context,
                ArnaDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()

        try {
            val sessionDao = inMemoryDb.sessionDao()

            val testSession = UserSessionEntity(
                sessionId = 1,
                userId = "M00000002",
                role = UserRole.CUSTOMER,
                fullName = "Test Customer",
                phone = "9876543210",
                email = "test@arnagram.org",
                identifierCode = "M00000002",
                lastLoginTime = System.currentTimeMillis(),
                authToken = ""
            )

            sessionDao.setSession(testSession)

            val retrieved = sessionDao.getSessionSync()

            assertNotNull(retrieved)
            assertEquals("M00000002", retrieved?.userId)
            assertEquals(UserRole.CUSTOMER, retrieved?.role)
        } finally {
            inMemoryDb.close()
        }
    }

    @Test
    fun `verify Customer M00000001 session creation and clear on logout`() =
        runBlocking {
            val context =
                ApplicationProvider.getApplicationContext<Context>()

            val inMemoryDb = Room
                .inMemoryDatabaseBuilder(
                    context,
                    ArnaDatabase::class.java
                )
                .allowMainThreadQueries()
                .build()

            try {
                val sessionDao = inMemoryDb.sessionDao()

                val customerSession = UserSessionEntity(
                    sessionId = 1,
                    userId = "M00000001",
                    role = UserRole.CUSTOMER,
                    fullName = "Arna Customer",
                    phone = "9876543210",
                    email = "customer@arnagram.org",
                    identifierCode = "M00000001",
                    lastLoginTime = System.currentTimeMillis(),
                    authToken = ""
                )

                sessionDao.setSession(customerSession)

                var retrieved = sessionDao.getSessionSync()

                assertNotNull(retrieved)
                assertEquals("M00000001", retrieved?.userId)
                assertEquals(UserRole.CUSTOMER, retrieved?.role)

                sessionDao.clearSession()

                retrieved = sessionDao.getSessionSync()

                assertNull(retrieved)
            } finally {
                inMemoryDb.close()
            }
        }

    @Test
    fun `verify Employee E00001 session creation and clear on logout`() =
        runBlocking {
            val context =
                ApplicationProvider.getApplicationContext<Context>()

            val inMemoryDb = Room
                .inMemoryDatabaseBuilder(
                    context,
                    ArnaDatabase::class.java
                )
                .allowMainThreadQueries()
                .build()

            try {
                val sessionDao = inMemoryDb.sessionDao()

                val employeeSession = UserSessionEntity(
                    sessionId = 1,
                    userId = "E00001",
                    role = UserRole.EMPLOYEE,
                    fullName = "Partha Banerjee (Field Officer)",
                    phone = "9800112233",
                    email = "partha.b@arnagram.org",
                    identifierCode = "E00001 • Field Operations",
                    lastLoginTime = System.currentTimeMillis(),
                    authToken = "jwt_employee_session"
                )

                sessionDao.setSession(employeeSession)

                var retrieved = sessionDao.getSessionSync()

                assertNotNull(retrieved)
                assertEquals("E00001", retrieved?.userId)
                assertEquals(UserRole.EMPLOYEE, retrieved?.role)

                sessionDao.clearSession()

                retrieved = sessionDao.getSessionSync()

                assertNull(retrieved)
            } finally {
                inMemoryDb.close()
            }
        }

    @Test
    fun `verify Admin E00000 session creation and clear on logout`() =
        runBlocking {
            val context =
                ApplicationProvider.getApplicationContext<Context>()

            val inMemoryDb = Room
                .inMemoryDatabaseBuilder(
                    context,
                    ArnaDatabase::class.java
                )
                .allowMainThreadQueries()
                .build()

            try {
                val sessionDao = inMemoryDb.sessionDao()

                val adminSession = UserSessionEntity(
                    sessionId = 1,
                    userId = "E00000",
                    role = UserRole.ADMIN,
                    fullName = "Branch Director (Admin)",
                    phone = "9900990011",
                    email = "admin@arnagram.org",
                    identifierCode = "E00000 • HQ Kolkata",
                    lastLoginTime = System.currentTimeMillis(),
                    authToken = "jwt_admin_session"
                )

                sessionDao.setSession(adminSession)

                var retrieved = sessionDao.getSessionSync()

                assertNotNull(retrieved)
                assertEquals("E00000", retrieved?.userId)
                assertEquals(UserRole.ADMIN, retrieved?.role)

                sessionDao.clearSession()

                retrieved = sessionDao.getSessionSync()

                assertNull(retrieved)
            } finally {
                inMemoryDb.close()
            }
        }

    @Test
    fun `verify Customer profile observation and persistence across logout`() =
        runBlocking {
            val context =
                ApplicationProvider.getApplicationContext<Context>()

            val inMemoryDb = Room
                .inMemoryDatabaseBuilder(
                    context,
                    ArnaDatabase::class.java
                )
                .allowMainThreadQueries()
                .build()

            try {
                val sessionDao = inMemoryDb.sessionDao()
                val borrowerDao = inMemoryDb.borrowerDao()

                val testBorrower = BorrowerEntity(
                    borrowerId = "M00000001",
                    name = "Sujata Mondal",
                    phone = "9830012345",
                    email = "sujata@example.com",
                    village = "Branch: B001",
                    district = "B001",
                    aadhaarNo = "XXXX-XXXX-1234",
                    panNo = "ABCDE1234F",
                    occupation = "Self-Employed",
                    monthlyIncome = 25000.0,
                    walletBalance = 5000.0,
                    status = "Active",
                    joinedDate = "01 Jan 2024",
                    assignedEmployeeId = "B001"
                )

                borrowerDao.insertBorrower(testBorrower)

                val fetched =
                    borrowerDao.getCustomerById("M00000001")

                assertNotNull(fetched)
                assertEquals("Sujata Mondal", fetched?.name)
                assertEquals("9830012345", fetched?.phone)
                assertEquals("Active", fetched?.status)

                val customerSession = UserSessionEntity(
                    sessionId = 1,
                    userId = "M00000001",
                    role = UserRole.CUSTOMER,
                    fullName = "Sujata Mondal",
                    phone = "9830012345",
                    email = "sujata@example.com",
                    identifierCode = "M00000001",
                    lastLoginTime = System.currentTimeMillis(),
                    authToken = ""
                )

                sessionDao.setSession(customerSession)

                assertNotNull(
                    sessionDao.getSessionSync()
                )

                sessionDao.clearSession()

                assertNull(
                    sessionDao.getSessionSync()
                )

                val borrowerAfterLogout =
                    borrowerDao.getCustomerById("M00000001")

                assertNotNull(borrowerAfterLogout)
                assertEquals(
                    "Sujata Mondal",
                    borrowerAfterLogout?.name
                )
            } finally {
                inMemoryDb.close()
            }
        }

    @Test
    fun `verify LoanDao customer filtering and multi-customer isolation`() =
        runBlocking {
            val context =
                ApplicationProvider.getApplicationContext<Context>()

            val inMemoryDb = Room
                .inMemoryDatabaseBuilder(
                    context,
                    ArnaDatabase::class.java
                )
                .allowMainThreadQueries()
                .build()

            try {
                val loanDao = inMemoryDb.loanDao()

                val loanA = LoanEntity(
                    loanId = "LN-101",
                    borrowerId = "CUST-A",
                    borrowerName = "Customer A",
                    borrowerVillage = "Branch 1",
                    loanType = "Micro Business",
                    principalAmount = 20000.0,
                    interestRate = 8.5,
                    tenorMonths = 12,
                    totalRepayable = 21700.0,
                    totalPaid = 5000.0,
                    emiAmount = 1808.33,
                    status = LoanStatus.APPROVED,
                    applicationDate = "01 Jan 2025",
                    disbursedDate = "05 Jan 2025",
                    purpose = "Shop Expansion"
                )

                val loanB = LoanEntity(
                    loanId = "LN-102",
                    borrowerId = "CUST-B",
                    borrowerName = "Customer B",
                    borrowerVillage = "Branch 2",
                    loanType = "Agriculture",
                    principalAmount = 50000.0,
                    interestRate = 7.5,
                    tenorMonths = 24,
                    totalRepayable = 57500.0,
                    totalPaid = 0.0,
                    emiAmount = 2395.83,
                    status = LoanStatus.ACTIVE,
                    applicationDate = "10 Jan 2025",
                    disbursedDate = "12 Jan 2025",
                    purpose = "Farming Seeds"
                )

                loanDao.insertLoan(loanA)
                loanDao.insertLoan(loanB)

                val loansForA =
                    loanDao.getLoansByCustomerId("CUST-A")

                assertEquals(1, loansForA.size)
                assertEquals(
                    "LN-101",
                    loansForA[0].loanId
                )
                assertEquals(
                    "CUST-A",
                    loansForA[0].borrowerId
                )

                val loansForB =
                    loanDao.getLoansByCustomerId("CUST-B")

                assertEquals(1, loansForB.size)
                assertEquals(
                    "LN-102",
                    loansForB[0].loanId
                )
                assertEquals(
                    "CUST-B",
                    loansForB[0].borrowerId
                )

                val fetchedLoan =
                    loanDao.getLoanById("LN-101")

                assertNotNull(fetchedLoan)
                assertEquals(
                    20000.0,
                    fetchedLoan!!.principalAmount,
                    0.01
                )
            } finally {
                inMemoryDb.close()
            }
        }

    @Test
    fun `verify EmiDao customer filtering and multi-customer isolation`() =
        runBlocking {
            val context =
                ApplicationProvider.getApplicationContext<Context>()

            val inMemoryDb = Room
                .inMemoryDatabaseBuilder(
                    context,
                    ArnaDatabase::class.java
                )
                .allowMainThreadQueries()
                .build()

            try {
                val emiDao = inMemoryDb.emiDao()
                val sessionDao = inMemoryDb.sessionDao()

                val emiA1 = EmiEntity(
                    emiId = "EMI-101-1",
                    loanId = "LN-101",
                    borrowerId = "CUST-A",
                    borrowerName = "Customer A",
                    installmentNo = 1,
                    totalInstallments = 12,
                    dueDate = "05 Feb 2025",
                    amount = 1808.33,
                    principalPortion = 1500.0,
                    interestPortion = 308.33,
                    paidAmount = 1808.33,
                    paidDate = "04 Feb 2025",
                    paymentMode = PaymentMode.UPI,
                    status = EmiStatus.PAID,
                    collectedBy = "Branch Agent 1",
                    receiptNo = "REC-101-1"
                )

                val emiA2 = EmiEntity(
                    emiId = "EMI-101-2",
                    loanId = "LN-101",
                    borrowerId = "CUST-A",
                    borrowerName = "Customer A",
                    installmentNo = 2,
                    totalInstallments = 12,
                    dueDate = "05 Mar 2025",
                    amount = 1808.33,
                    principalPortion = 1500.0,
                    interestPortion = 308.33,
                    paidAmount = 0.0,
                    paidDate = null,
                    paymentMode = null,
                    status = EmiStatus.PENDING,
                    collectedBy = null,
                    receiptNo = null
                )

                val emiB1 = EmiEntity(
                    emiId = "EMI-102-1",
                    loanId = "LN-102",
                    borrowerId = "CUST-B",
                    borrowerName = "Customer B",
                    installmentNo = 1,
                    totalInstallments = 24,
                    dueDate = "12 Feb 2025",
                    amount = 2395.83,
                    principalPortion = 2000.0,
                    interestPortion = 395.83,
                    paidAmount = 0.0,
                    paidDate = null,
                    paymentMode = null,
                    status = EmiStatus.PENDING,
                    collectedBy = null,
                    receiptNo = null
                )

                emiDao.insertAllEmis(
                    listOf(emiA1, emiA2, emiB1)
                )

                val emisForA =
                    emiDao.getEmisByCustomerId("CUST-A")

                assertEquals(2, emisForA.size)
                assertEquals(
                    "EMI-101-1",
                    emisForA[0].emiId
                )
                assertEquals(
                    "EMI-101-2",
                    emisForA[1].emiId
                )

                assertEquals(
                    "CUST-A",
                    emisForA[0].borrowerId
                )
                assertEquals(
                    "CUST-A",
                    emisForA[1].borrowerId
                )

                val emisForB =
                    emiDao.getEmisByCustomerId("CUST-B")

                assertEquals(1, emisForB.size)
                assertEquals(
                    "EMI-102-1",
                    emisForB[0].emiId
                )
                assertEquals(
                    "CUST-B",
                    emisForB[0].borrowerId
                )

                val emptyEmis =
                    emiDao.getEmisByCustomerId("CUST-UNKNOWN")

                assertEquals(0, emptyEmis.size)

                val emisForLoan101 =
                    emiDao.getEmisByLoanId("LN-101")

                assertEquals(2, emisForLoan101.size)

                val fetchedEmi =
                    emiDao.getEmiById("EMI-101-1")

                assertNotNull(fetchedEmi)
                assertEquals(
                    1808.33,
                    fetchedEmi!!.amount,
                    0.01
                )
                assertEquals(
                    EmiStatus.PAID,
                    fetchedEmi?.status
                )

                emiDao.insertEmi(emiA1)

                val emisAfterDuplicate =
                    emiDao.getEmisByCustomerId("CUST-A")

                assertEquals(2, emisAfterDuplicate.size)

                val session = UserSessionEntity(
                    sessionId = 1,
                    userId = "CUST-A",
                    role = UserRole.CUSTOMER,
                    fullName = "Customer A",
                    phone = "9830011111",
                    email = "a@example.com",
                    identifierCode = "CUST-A",
                    lastLoginTime = System.currentTimeMillis(),
                    authToken = ""
                )

                sessionDao.setSession(session)
                sessionDao.clearSession()

                val emisAfterLogout =
                    emiDao.getEmisByCustomerId("CUST-A")

                assertEquals(2, emisAfterLogout.size)
            } finally {
                inMemoryDb.close()
            }
        }

    @Test
    fun `verify Customer EMI repository sync and DTO mapping`() =
        runBlocking {
            val context =
                ApplicationProvider.getApplicationContext<Context>()

            val inMemoryDb = Room
                .inMemoryDatabaseBuilder(
                    context,
                    ArnaDatabase::class.java
                )
                .allowMainThreadQueries()
                .build()

            try {
                val repo = ArnaRepository(inMemoryDb)
                val emiDao = inMemoryDb.emiDao()

                val customer = BorrowerEntity(
                    borrowerId = "M00000042",
                    name = "Rahim Mondal",
                    phone = "9800000042",
                    email = "rahim@arnagram.org",
                    village = "Shantipur",
                    district = "Nadia",
                    aadhaarNo = "XXXX-XXXX-9999",
                    panNo = "ABCDE9999Z",
                    occupation = "Agriculture",
                    monthlyIncome = 20000.0,
                    walletBalance = 1000.0,
                    status = "Active",
                    joinedDate = "01 Jan 2025",
                    assignedEmployeeId = "E00001"
                )

                repo.saveCustomerProfile(customer)

                val emi1 = EmiEntity(
                    emiId = "EMI-42-1",
                    loanId = "LN-42",
                    borrowerId = "M00000042",
                    borrowerName = "Rahim Mondal",
                    installmentNo = 1,
                    totalInstallments = 12,
                    dueDate = "10 Mar 2025",
                    amount = 1750.0,
                    principalPortion = 1500.0,
                    interestPortion = 250.0,
                    paidAmount = 0.0,
                    paidDate = null,
                    paymentMode = null,
                    status = EmiStatus.PENDING,
                    collectedBy = null,
                    receiptNo = null
                )

                repo.saveEmi(emi1)

                val syncResult =
                    repo.syncCustomerEmis("M00000042")

                assertTrue(syncResult.isSuccess)

                val emis = syncResult.getOrNull()

                assertNotNull(emis)
                assertEquals(1, emis!!.size)
                assertEquals(
                    "EMI-42-1",
                    emis[0].emiId
                )
                assertEquals(
                    "M00000042",
                    emis[0].borrowerId
                )

                val syncResult2 =
                    repo.syncCustomerEmis("M00000042")

                assertTrue(syncResult2.isSuccess)

                val emisAfterRepeat =
                    emiDao.getEmisByCustomerId("M00000042")

                assertEquals(1, emisAfterRepeat.size)

                val emptySync =
                    repo.syncCustomerEmis("   ")

                assertTrue(emptySync.isSuccess)
                assertEquals(
                    0,
                    emptySync.getOrNull()?.size ?: 0
                )
            } finally {
                inMemoryDb.close()
            }
        }

    @Test
    fun `verify Customer Loan Dashboard data integration and session isolation`() =
        runBlocking {
            val context =
                ApplicationProvider.getApplicationContext<Context>()

            val inMemoryDb = Room
                .inMemoryDatabaseBuilder(
                    context,
                    ArnaDatabase::class.java
                )
                .allowMainThreadQueries()
                .build()

            try {
                val repo = ArnaRepository(inMemoryDb)
                val loanDao = inMemoryDb.loanDao()
                val sessionDao = inMemoryDb.sessionDao()

                val customerSession = UserSessionEntity(
                    sessionId = 1,
                    userId = "M00000001",
                    role = UserRole.CUSTOMER,
                    fullName = "Sujata Mondal",
                    phone = "9830012345",
                    email = "sujata@arnagram.org",
                    identifierCode = "M00000001",
                    lastLoginTime = System.currentTimeMillis(),
                    authToken = ""
                )

                sessionDao.setSession(customerSession)

                val loan1 = LoanEntity(
                    loanId = "AGU-LN-101",
                    borrowerId = "M00000001",
                    borrowerName = "Sujata Mondal",
                    borrowerVillage = "Ranaghat Branch",
                    loanType = "Micro Enterprise",
                    principalAmount = 35000.0,
                    interestRate = 8.5,
                    tenorMonths = 12,
                    totalRepayable = 37975.0,
                    totalPaid = 9493.75,
                    emiAmount = 3164.58,
                    status = LoanStatus.ACTIVE,
                    applicationDate = "01 Jan 2025",
                    disbursedDate = "05 Jan 2025",
                    purpose = "Handloom Weaving Loom"
                )

                val loan2 = LoanEntity(
                    loanId = "AGU-LN-102",
                    borrowerId = "M00000099",
                    borrowerName = "Other Customer",
                    borrowerVillage = "Other Branch",
                    loanType = "Agriculture",
                    principalAmount = 50000.0,
                    interestRate = 7.5,
                    tenorMonths = 24,
                    totalRepayable = 57500.0,
                    totalPaid = 0.0,
                    emiAmount = 2395.83,
                    status = LoanStatus.ACTIVE,
                    applicationDate = "10 Jan 2025",
                    disbursedDate = "15 Jan 2025",
                    purpose = "Irrigation Pump"
                )

                loanDao.insertLoan(loan1)
                loanDao.insertLoan(loan2)

                val customerLoans =
                    repo.getCustomerLoans("M00000001")

                assertEquals(1, customerLoans.size)
                assertEquals(
                    "AGU-LN-101",
                    customerLoans[0].loanId
                )
                assertEquals(
                    35000.0,
                    customerLoans[0].principalAmount,
                    0.01
                )
                assertEquals(
                    LoanStatus.ACTIVE,
                    customerLoans[0].status
                )
                assertEquals(
                    9493.75,
                    customerLoans[0].totalPaid,
                    0.01
                )

                val expectedOutstanding =
                    37975.0 - 9493.75

                assertEquals(
                    expectedOutstanding,
                    customerLoans[0].outstandingAmount,
                    0.01
                )

                val otherCustomerLoans =
                    repo.getCustomerLoans("M00000099")

                assertEquals(
                    1,
                    otherCustomerLoans.size
                )
                assertEquals(
                    "AGU-LN-102",
                    otherCustomerLoans[0].loanId
                )

                val nonExistentLoans =
                    repo.getCustomerLoans("M00000000")

                assertTrue(
                    nonExistentLoans.isEmpty()
                )

                val syncResult =
                    repo.syncCustomerLoans("M00000001")

                assertTrue(syncResult.isSuccess)

                val syncedLoans =
                    syncResult.getOrNull()

                assertNotNull(syncedLoans)
                assertTrue(
                    syncedLoans!!.isNotEmpty()
                )
                assertTrue(
                    syncedLoans[0].loanId.isNotBlank()
                )
            } finally {
                inMemoryDb.close()
            }
        }

    @Test
    fun `verify Customer EMI UI StateFlow calculations and Room source of truth`() =
        runBlocking {
            val context =
                ApplicationProvider.getApplicationContext<Context>()

            val inMemoryDb = Room
                .inMemoryDatabaseBuilder(
                    context,
                    ArnaDatabase::class.java
                )
                .allowMainThreadQueries()
                .build()

            try {
                val repo = ArnaRepository(inMemoryDb)
                val emiDao = inMemoryDb.emiDao()
                val sessionDao = inMemoryDb.sessionDao()

                val customerSession = UserSessionEntity(
                    sessionId = 1,
                    userId = "M00000001",
                    role = UserRole.CUSTOMER,
                    fullName = "Sujata Mondal",
                    phone = "9830012345",
                    email = "sujata@arnagram.org",
                    identifierCode = "M00000001",
                    lastLoginTime = System.currentTimeMillis(),
                    authToken = ""
                )

                sessionDao.setSession(customerSession)

                val emiPaid = EmiEntity(
                    emiId = "EMI-01",
                    loanId = "AGU-LN-101",
                    borrowerId = "M00000001",
                    borrowerName = "Sujata Mondal",
                    installmentNo = 1,
                    totalInstallments = 3,
                    dueDate = "05 Jan 2025",
                    amount = 3164.58,
                    principalPortion = 2900.0,
                    interestPortion = 264.58,
                    paidAmount = 3164.58,
                    paidDate = "04 Jan 2025",
                    paymentMode = PaymentMode.WALLET,
                    status = EmiStatus.PAID,
                    collectedBy = "Auto Debit",
                    receiptNo = "REC-01"
                )

                val emiUpcoming = EmiEntity(
                    emiId = "EMI-02",
                    loanId = "AGU-LN-101",
                    borrowerId = "M00000001",
                    borrowerName = "Sujata Mondal",
                    installmentNo = 2,
                    totalInstallments = 3,
                    dueDate = "05 Feb 2025",
                    amount = 3164.58,
                    principalPortion = 2900.0,
                    interestPortion = 264.58,
                    paidAmount = 0.0,
                    paidDate = null,
                    paymentMode = null,
                    status = EmiStatus.PENDING,
                    collectedBy = null,
                    receiptNo = null
                )

                val emiOverdue = EmiEntity(
                    emiId = "EMI-03",
                    loanId = "AGU-LN-101",
                    borrowerId = "M00000001",
                    borrowerName = "Sujata Mondal",
                    installmentNo = 3,
                    totalInstallments = 3,
                    dueDate = "05 Dec 2024",
                    amount = 3164.58,
                    principalPortion = 2900.0,
                    interestPortion = 264.58,
                    paidAmount = 0.0,
                    paidDate = null,
                    paymentMode = null,
                    status = EmiStatus.OVERDUE,
                    collectedBy = null,
                    receiptNo = null
                )

                val emiOther = EmiEntity(
                    emiId = "EMI-99",
                    loanId = "AGU-LN-999",
                    borrowerId = "M00000099",
                    borrowerName = "Other Person",
                    installmentNo = 1,
                    totalInstallments = 12,
                    dueDate = "10 Jan 2025",
                    amount = 5000.0,
                    principalPortion = 4500.0,
                    interestPortion = 500.0,
                    paidAmount = 0.0,
                    paidDate = null,
                    paymentMode = null,
                    status = EmiStatus.PENDING,
                    collectedBy = null,
                    receiptNo = null
                )

                emiDao.insertAllEmis(
                    listOf(
                        emiPaid,
                        emiUpcoming,
                        emiOverdue,
                        emiOther
                    )
                )

                val emisM1 =
                    repo.getCustomerEmis("M00000001")

                assertEquals(3, emisM1.size)

                val totalEmis = emisM1.size

                val paidEmis =
                    emisM1.count {
                        it.status == EmiStatus.PAID
                    }

                val dueEmis =
                    emisM1.count {
                        it.status == EmiStatus.OVERDUE
                    }

                val upcomingEmis =
                    emisM1.count {
                        it.status == EmiStatus.PENDING
                    }

                assertEquals(3, totalEmis)
                assertEquals(1, paidEmis)
                assertEquals(1, dueEmis)
                assertEquals(1, upcomingEmis)

                val emisM99 =
                    repo.getCustomerEmis("M00000099")

                assertEquals(1, emisM99.size)
                assertEquals(
                    "EMI-99",
                    emisM99[0].emiId
                )

                val emisEmpty =
                    repo.getCustomerEmis("M00000000")

                assertTrue(emisEmpty.isEmpty())
            } finally {
                inMemoryDb.close()
            }
        }

    @Test
    fun `verify MainActivity launches cleanly`() {
        val scenario =
            androidx.test.core.app.ActivityScenario.launch(
                MainActivity::class.java
            )

        assertNotNull(scenario)

        scenario.close()
    }
}