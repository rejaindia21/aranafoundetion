package com.arnafoundation.app.data.remote

import com.arnafoundation.app.data.remote.dto.AdminLoginRequest
import com.arnafoundation.app.data.remote.dto.ApiResponse
import com.arnafoundation.app.data.remote.dto.ApplyLoanRequest
import com.arnafoundation.app.data.remote.dto.CreateInvestmentRequest
import com.arnafoundation.app.data.remote.dto.CustomerDashboardDto
import com.arnafoundation.app.data.remote.dto.CustomerLoginResponse
import com.arnafoundation.app.data.remote.dto.EmiDto
import com.arnafoundation.app.data.remote.dto.EmployeeLoginRequest
import com.arnafoundation.app.data.remote.dto.InvestmentDto
import com.arnafoundation.app.data.remote.dto.LoanDto
import com.arnafoundation.app.data.remote.dto.LoginResponse
import com.arnafoundation.app.data.remote.dto.PayEmiRequest
import com.arnafoundation.app.data.remote.dto.RealCustomerListResponse
import com.arnafoundation.app.data.remote.dto.RealEmiListResponse
import com.arnafoundation.app.data.remote.dto.RealLoanListResponse
import com.arnafoundation.app.data.remote.dto.TopUpWalletRequest
import com.arnafoundation.app.data.remote.dto.TransactionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // -------------------------------------------------------------
    // Security & Auth Endpoints (PHP Backend: customer-login.php)
    // -------------------------------------------------------------

    @POST("api-security.php")
    suspend fun verifySecurityToken(
        @Query("token") token: String
    ): Response<ApiResponse<Map<String, String>>>

    /**
     * Exact Customer Login Endpoint
     * Resolved relative to baseUrl (https://arnafoundation.com/api/app/)
     * URL: https://arnafoundation.com/api/api/api-customer-login.php
     * Content-Type: application/x-www-form-urlencoded
     */
    @FormUrlEncoded
    @POST("../api/api-customer-login.php")
    suspend fun customerLogin(
        @Field("id") id: String,
        @Field("password") password: String
    ): Response<CustomerLoginResponse>

    // -------------------------------------------------------------
    // Real Live ARNA Database Endpoints (https://arnafoundation.com/api/app/)
    // -------------------------------------------------------------

    /**
     * Real ARNA Customers Endpoint
     * POST https://arnafoundation.com/api/app/admin/admin-customers.php
     */
    @FormUrlEncoded
    @POST("admin/admin-customers.php")
    suspend fun getRealCustomers(
        @Field("action") action: String = "list",
        @Field("search") search: String = ""
    ): Response<RealCustomerListResponse>

    /**
     * Real ARNA Loans Endpoint
     * POST https://arnafoundation.com/api/app/admin/admin-loans.php
     */
    @FormUrlEncoded
    @POST("admin/admin-loans.php")
    suspend fun getRealLoans(
        @Field("action") action: String = "list",
        @Field("tid") tid: String = "E00000",
        @Field("search") search: String = ""
    ): Response<RealLoanListResponse>

    /**
     * Real ARNA EMI List Endpoint
     * POST https://arnafoundation.com/api/app/admin/admin-loans.php
     */
    @FormUrlEncoded
    @POST("admin/admin-loans.php")
    suspend fun getRealEmis(
        @Field("action") action: String = "emi_list",
        @Field("tid") tid: String = "E00000",
        @Field("loan_id") loanId: String
    ): Response<RealEmiListResponse>

    /**
     * Unified User Authentication Endpoint (ARNA MySQL user table)
     * Resolved relative to baseUrl (https://arnafoundation.com/api/app/)
     * URL: https://arnafoundation.com/api/api/api-login.php
     * Authenticates Admin, M.D, C.E.O, and all staff accounts.
     * Content-Type: application/x-www-form-urlencoded
     */
    @FormUrlEncoded
    @POST("../api/api-login.php")
    suspend fun userLogin(
        @Field("id") id: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    /**
     * Direct app path user login (if deployed directly under /api/app/ in future backend releases)
     * POST https://arnafoundation.com/api/app/api-login.php
     */
    @FormUrlEncoded
    @POST("api-login.php")
    suspend fun appUserLogin(
        @Field("id") id: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    /**
     * Employee Login Endpoint
     * POST https://arnafoundation.com/api/api/api-employee-login.php
     */
    @FormUrlEncoded
    @POST("../api/api-employee-login.php")
    suspend fun employeeLogin(
        @Field("id") id: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    /**
     * Administrator Login Endpoint
     * POST https://arnafoundation.com/api/api/api-admin-login.php
     */
    @FormUrlEncoded
    @POST("../api/api-admin-login.php")
    suspend fun adminLogin(
        @Field("id") id: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    // -------------------------------------------------------------
    // Customer Dashboard & Modules
    // -------------------------------------------------------------

    @GET("api-customer-dashboard.php")
    suspend fun getCustomerDashboard(
        @Query("customer_id") customerId: String
    ): Response<ApiResponse<CustomerDashboardDto>>

    @GET("api-loans.php")
    suspend fun getCustomerLoans(
        @Query("customer_id") customerId: String
    ): Response<ApiResponse<List<LoanDto>>>

    @POST("api-loan-apply.php")
    suspend fun applyLoan(
        @Body request: ApplyLoanRequest
    ): Response<ApiResponse<LoanDto>>

    @GET("api-emis.php")
    suspend fun getCustomerEmis(
        @Query("customer_id") customerId: String,
        @Query("loan_id") loanId: String? = null
    ): Response<ApiResponse<List<EmiDto>>>

    @POST("api-emi-pay.php")
    suspend fun payEmi(
        @Body request: PayEmiRequest
    ): Response<ApiResponse<EmiDto>>

    @GET("api-investments.php")
    suspend fun getCustomerInvestments(
        @Query("customer_id") customerId: String
    ): Response<ApiResponse<List<InvestmentDto>>>

    @POST("api-investment-create.php")
    suspend fun createInvestment(
        @Body request: CreateInvestmentRequest
    ): Response<ApiResponse<InvestmentDto>>

    @GET("api-transactions.php")
    suspend fun getCustomerTransactions(
        @Query("customer_id") customerId: String
    ): Response<ApiResponse<List<TransactionDto>>>

    @POST("api-wallet-topup.php")
    suspend fun topUpWallet(
        @Body request: TopUpWalletRequest
    ): Response<ApiResponse<TransactionDto>>
}
