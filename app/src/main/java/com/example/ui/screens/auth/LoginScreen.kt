package com.arnafoundation.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arnafoundation.app.data.model.UserRole
import com.arnafoundation.app.ui.components.AppButton
import com.arnafoundation.app.ui.components.ArnaLogo
import com.arnafoundation.app.ui.theme.*
import com.arnafoundation.app.ui.viewmodel.MainViewModel

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedRoleIndex by remember { mutableIntStateOf(0) }
    val roles = listOf(UserRole.CUSTOMER, UserRole.EMPLOYEE, UserRole.ADMIN)
    val selectedRole = roles[selectedRoleIndex]

    var phoneOrId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top Green Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Emerald900, Emerald700)
                        )
                    )
                    .padding(top = 40.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            ArnaLogo(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "ARNA GRAM UNNAYA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                "FOUNDATION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Amber100
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        "Secure Digital Management Portal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Official Microfinance & Rural Inclusion Services",
                        fontSize = 12.sp,
                        color = Emerald100,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Main Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSoft)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        "SELECT WORKSPACE PORTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Role Selector Tabs
                    TabRow(
                        selectedTabIndex = selectedRoleIndex,
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = Emerald800,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedRoleIndex]),
                                color = Emerald800,
                                height = 3.dp
                            )
                        },
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedRoleIndex == 0,
                            onClick = {
                                selectedRoleIndex = 0
                            },
                            text = {
                                Text(
                                    "Customer",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedRoleIndex == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            icon = {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        )
                        Tab(
                            selected = selectedRoleIndex == 1,
                            onClick = {
                                selectedRoleIndex = 1
                            },
                            text = {
                                Text(
                                    "Employee",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedRoleIndex == 1) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            icon = {
                                Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        )
                        Tab(
                            selected = selectedRoleIndex == 2,
                            onClick = {
                                selectedRoleIndex = 2
                            },
                            text = {
                                Text(
                                    "Admin",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedRoleIndex == 2) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            icon = {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Input Fields based on Role
                    val idLabel = when (selectedRole) {
                        UserRole.CUSTOMER -> "USER ID / CUSTOMER ID"
                        UserRole.EMPLOYEE -> "EMPLOYEE ID / USERNAME"
                        UserRole.ADMIN -> "ADMIN ID / USERNAME"
                    }

                    val idPlaceholder = when (selectedRole) {
                        UserRole.CUSTOMER -> "e.g. M00000001"
                        UserRole.EMPLOYEE -> "Enter staff code or username"
                        UserRole.ADMIN -> "Enter admin code or username"
                    }

                    val idIcon = when (selectedRole) {
                        UserRole.CUSTOMER -> Icons.Default.Person
                        UserRole.EMPLOYEE -> Icons.Default.Badge
                        UserRole.ADMIN -> Icons.Default.Shield
                    }

                    val isCustomerPinInvalid = selectedRole == UserRole.CUSTOMER && pin.isNotEmpty() && (pin.length != 6 || !pin.all { it.isDigit() })

                    OutlinedTextField(
                        value = phoneOrId,
                        onValueChange = { input ->
                            phoneOrId = input
                            val trimmed = input.trim()
                            if (trimmed.startsWith("M", ignoreCase = true) || trimmed.startsWith("CUST", ignoreCase = true)) {
                                selectedRoleIndex = 0
                            }
                        },
                        label = { Text(idLabel) },
                        placeholder = { Text(idPlaceholder, fontSize = 12.sp, color = TextMuted) },
                        leadingIcon = { Icon(idIcon, contentDescription = null, tint = Emerald800) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_id_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald700,
                            unfocusedBorderColor = OutlineSoft
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = pin,
                        onValueChange = { input ->
                            if (selectedRole == UserRole.CUSTOMER) {
                                if (input.length <= 6 && input.all { it.isDigit() }) {
                                    pin = input
                                }
                            } else {
                                pin = input
                            }
                        },
                        label = { Text("PASSWORD") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Emerald800) },
                        trailingIcon = {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible }
                            ) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "HIDE PASSWORD" else "SHOW PASSWORD",
                                    tint = TextMuted
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = if (selectedRole == UserRole.CUSTOMER) KeyboardOptions(keyboardType = KeyboardType.NumberPassword) else KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = isCustomerPinInvalid,
                        supportingText = {
                            if (selectedRole == UserRole.CUSTOMER) {
                                val isComplete = pin.length == 6 && pin.all { it.isDigit() }
                                Text(
                                    text = if (isComplete) "✓ 6 digits entered" else "Enter 6-digit password (${pin.length}/6)",
                                    fontSize = 11.sp,
                                    color = if (isComplete) Emerald700 else if (pin.isNotEmpty() && pin.length < 6) Color(0xFFB45309) else TextMuted
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_pin_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald700,
                            unfocusedBorderColor = OutlineSoft,
                            errorBorderColor = Color(0xFFBA1A1A)
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    AppButton(
                        text = "LOGIN TO DASHBOARD",
                        onClick = {
                            viewModel.login(selectedRole, phoneOrId, pin)
                        },
                        isLoading = isLoading,
                        enabled = !isLoading,
                        testTag = "login_submit_button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "ARNA GRAM UNNAYA FOUNDATION • Microfinance & Rural Inclusion",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun DemoPillRow(
    roleName: String,
    roleSubtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(roleName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald900)
            Text(roleSubtitle, fontSize = 11.sp, color = TextMuted)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Emerald100)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Launch", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald800)
        }
    }
}
