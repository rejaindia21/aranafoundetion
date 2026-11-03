package com.arnafoundation.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arnafoundation.app.data.local.ArnaDatabase
import com.arnafoundation.app.data.model.UserRole
import com.arnafoundation.app.data.repository.ArnaRepository
import com.arnafoundation.app.ui.components.StampedReceiptDialog
import com.arnafoundation.app.ui.screens.admin.AdminMainScreen
import com.arnafoundation.app.ui.screens.auth.LoginScreen
import com.arnafoundation.app.ui.screens.customer.CustomerMainScreen
import com.arnafoundation.app.ui.screens.employee.EmployeeMainScreen
import com.arnafoundation.app.ui.screens.splash.SplashScreen
import com.arnafoundation.app.ui.theme.ArnaTheme
import com.arnafoundation.app.ui.viewmodel.MainViewModel
import com.arnafoundation.app.ui.viewmodel.MainViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // ============================================================
        // ARNA DATABASE
        // ============================================================

        val database =
            ArnaDatabase.getDatabase(
                applicationContext
            )

        // ============================================================
        // ARNA REPOSITORY
        // ============================================================

        val repository =
            ArnaRepository(database)

        // ============================================================
        // MAIN VIEWMODEL FACTORY
        // ============================================================

        val factory =
            MainViewModelFactory(repository)

        // ============================================================
        // MAIN VIEWMODEL
        // ============================================================

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[MainViewModel::class.java]

        // ============================================================
        // COMPOSE CONTENT
        // ============================================================

        setContent {

            ArnaTheme {

                val snackbarHostState =
                    remember {
                        SnackbarHostState()
                    }

                val scope =
                    rememberCoroutineScope()

                var isSplashActive by remember {
                    mutableStateOf(true)
                }

                // ====================================================
                // AUTHENTICATED SESSION
                // ====================================================

                val session by
                    viewModel.sessionState.collectAsStateWithLifecycle()

                // ====================================================
                // RECEIPT / VOUCHER
                // ====================================================

                val receiptToShow by
                    viewModel.receiptToShow.collectAsStateWithLifecycle()

                // ====================================================
                // GLOBAL UI MESSAGE
                // ====================================================

                LaunchedEffect(Unit) {

                    viewModel.uiMessage.collectLatest { message ->

                        scope.launch {

                            snackbarHostState.showSnackbar(
                                message
                            )
                        }
                    }
                }

                // ====================================================
                // GLOBAL RECEIPT VOUCHER DIALOG
                // ====================================================

                if (receiptToShow != null) {

                    StampedReceiptDialog(
                        receipt = receiptToShow!!,
                        onDismiss = {
                            viewModel.dismissReceipt()
                        }
                    )
                }

                // ====================================================
                // ROOT SCAFFOLD
                // ====================================================

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState
                        )
                    }
                ) { innerPadding ->

                    // =================================================
                    // SPLASH / MAIN SCREEN TRANSITION
                    // =================================================

                    Crossfade(
                        targetState = isSplashActive,
                        label = "app_root_transition",
                        modifier = Modifier.padding(
                            innerPadding
                        )
                    ) { splashActive ->

                        if (splashActive) {

                            // =========================================
                            // SPLASH SCREEN
                            // =========================================

                            SplashScreen(
                                onTimeout = {
                                    isSplashActive = false
                                }
                            )

                        } else {

                            // =========================================
                            // ROLE BASED NAVIGATION
                            // =========================================

                            when (session?.role) {

                                // =====================================
                                // CUSTOMER
                                // =====================================

                                UserRole.CUSTOMER -> {

                                    CustomerMainScreen(
                                        viewModel = viewModel
                                    )
                                }

                                // =====================================
                                // EMPLOYEE
                                // =====================================

                                UserRole.EMPLOYEE -> {

                                    EmployeeMainScreen(
                                        viewModel = viewModel
                                    )
                                }

                                // =====================================
                                // ADMIN
                                // =====================================

                                UserRole.ADMIN -> {

                                    AdminMainScreen(
                                        viewModel = viewModel
                                    )
                                }

                                // =====================================
                                // NOT LOGGED IN
                                // =====================================

                                null -> {

                                    LoginScreen(
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}