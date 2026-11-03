package com.arnafoundation.app.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.arnafoundation.app.data.remote.ApiConfigManager
import com.arnafoundation.app.ui.theme.*
import com.arnafoundation.app.ui.viewmodel.ReceiptData
import java.util.Locale


// ============================================================
// ARNA GRAM UNNAYAN FOUNDATION
// REUSABLE COMPOSE UI COMPONENTS
// ============================================================


// ============================================================
// APP CARD
// ============================================================

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = Color(0xFFE1E3E1),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}


// ============================================================
// APP BUTTON
// ============================================================

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = Color(0xFF006B5B),
    contentColor: Color = Color.White,
    testTag: String = "app_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.7f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Processing...",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )
            }

            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


// ============================================================
// STATUS BADGE
// ============================================================

@Composable
fun StatusBadge(
    statusText: String,
    modifier: Modifier = Modifier
) {
    val normalizedStatus = statusText
        .trim()
        .uppercase(Locale.getDefault())

    val colors = when (normalizedStatus) {

        "ACTIVE",
        "PAID",
        "APPROVED",
        "SUCCESS" -> {
            Pair(
                Color(0xFFCCE8E2),
                Color(0xFF006B5B)
            )
        }

        "PENDING",
        "PENDING_APPROVAL" -> {
            Pair(
                Color(0xFFFEF3C7),
                Color(0xFFB45309)
            )
        }

        "OVERDUE",
        "REJECTED",
        "DUE" -> {
            Pair(
                Color(0xFFFFDAD4),
                Color(0xFF93000A)
            )
        }

        "CLOSED" -> {
            Pair(
                Color(0xFFF1F5F9),
                Color(0xFF707973)
            )
        }

        else -> {
            Pair(
                Color(0xFFCCE8E2),
                Color(0xFF006B5B)
            )
        }
    }

    val bgColor = colors.first
    val textColor = colors.second

    Box(
        modifier = modifier
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(bgColor)
            .padding(
                horizontal = 10.dp,
                vertical = 4.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = normalizedStatus
                .replace("_", " "),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}


// ============================================================
// STAMPED RECEIPT DIALOG
// ============================================================

@Composable
fun StampedReceiptDialog(
    receipt: ReceiptData,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ------------------------------------------------
                // HEADER
                // ------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Emerald100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Emerald800,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Column {

                            Text(
                                text = "ARNA GRAM UNNAYAN",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = Emerald900
                            )

                            Text(
                                text = "Foundation E-Voucher",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // ------------------------------------------------
                // VERIFICATION STAMP
                // ------------------------------------------------

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .background(
                            Color(0xFFF0FDF4)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFF86EFAC),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth()
                        .padding(
                            vertical = 12.dp,
                            horizontal = 16.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Emerald600,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = "OFFICIALLY RECORDED & VERIFIED",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Emerald800
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Receipt No: ${receipt.receiptNo}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // ------------------------------------------------
                // AMOUNT
                // ------------------------------------------------

                Text(
                    text = "AMOUNT TRANSACTED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                Text(
                    text = "₹${
                        String.format(
                            Locale.getDefault(),
                            "%,.2f",
                            receipt.amount
                        )
                    }",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Emerald900
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                HorizontalDivider(
                    color = OutlineSoft
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // ------------------------------------------------
                // RECEIPT DETAILS
                // ------------------------------------------------

                ReceiptRow(
                    label = "Payer / Beneficiary",
                    value = receipt.payerName
                )

                ReceiptRow(
                    label = "Member ID",
                    value = receipt.borrowerId
                )

                ReceiptRow(
                    label = "Transaction Purpose",
                    value = receipt.category
                )

                ReceiptRow(
                    label = "Reference / Note",
                    value = receipt.referenceInfo
                )

                ReceiptRow(
                    label = "Date & Time",
                    value = receipt.date
                )

                ReceiptRow(
                    label = "Mode of Payment",
                    value = receipt.paymentMode
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                // ------------------------------------------------
                // ACTIONS
                // ------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Close",
                            color = TextSecondary
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Emerald800
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = "Share Slip"
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
// RECEIPT ROW
// ============================================================

@Composable
private fun ReceiptRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            fontSize = 12.sp,
            color = TextMuted
        )

        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}


// ============================================================
// LOAN CALCULATOR DIALOG
// ============================================================

@Composable
fun LoanCalculatorDialog(
    onDismiss: () -> Unit,
    onApplyWithValues: (
        principal: Double,
        tenor: Int
    ) -> Unit
) {

    var principal by remember {
        mutableDoubleStateOf(50000.0)
    }

    var tenorMonths by remember {
        mutableIntStateOf(12)
    }

    val interestRate = when {
        principal <= 30000 -> 7.5
        principal <= 75000 -> 8.5
        else -> 9.5
    }

    val totalInterest =
        principal *
                (interestRate / 100.0) *
                (tenorMonths / 12.0)

    val totalRepayable =
        principal + totalInterest

    val emi =
        totalRepayable / tenorMonths


    Dialog(
        onDismissRequest = onDismiss
    ) {

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                // ------------------------------------------------
                // HEADER
                // ------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Emerald800
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Arna Loan EMI Calculator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Emerald900
                        )
                    }

                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // ------------------------------------------------
                // LOAN AMOUNT
                // ------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Loan Amount",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = "₹${
                            String.format(
                                Locale.getDefault(),
                                "%,.0f",
                                principal
                            )
                        }",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald800
                    )
                }

                Slider(
                    value = principal.toFloat(),
                    onValueChange = {
                        principal =
                            (it / 5000)
                                .toInt()
                                .coerceIn(2, 40) * 5000.0
                    },
                    valueRange = 10000f..200000f,
                    steps = 37
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // ------------------------------------------------
                // TENURE
                // ------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Tenure (Months)",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = "$tenorMonths Months",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald800
                    )
                }

                Slider(
                    value = tenorMonths.toFloat(),
                    onValueChange = {
                        tenorMonths =
                            it.toInt().coerceIn(6, 36)
                    },
                    valueRange = 6f..36f,
                    steps = 29
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // ------------------------------------------------
                // RESULT BOX
                // ------------------------------------------------

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Emerald900,
                                    Emerald700
                                )
                            )
                        )
                        .padding(16.dp)
                ) {

                    Column {

                        Text(
                            text = "ESTIMATED MONTHLY EMI",
                            fontSize = 11.sp,
                            color = Emerald100,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "₹${
                                String.format(
                                    Locale.getDefault(),
                                    "%,.2f",
                                    emi
                                )
                            } / mo",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        HorizontalDivider(
                            color = Emerald600
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column {

                                Text(
                                    text = "Interest Rate",
                                    fontSize = 11.sp,
                                    color = Emerald100
                                )

                                Text(
                                    text = "$interestRate% p.a.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column {

                                Text(
                                    text = "Total Interest",
                                    fontSize = 11.sp,
                                    color = Emerald100
                                )

                                Text(
                                    text = "₹${
                                        String.format(
                                            Locale.getDefault(),
                                            "%,.0f",
                                            totalInterest
                                        )
                                    }",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Amber400
                                )
                            }

                            Column {

                                Text(
                                    text = "Total Repayable",
                                    fontSize = 11.sp,
                                    color = Emerald100
                                )

                                Text(
                                    text = "₹${
                                        String.format(
                                            Locale.getDefault(),
                                            "%,.0f",
                                            totalRepayable
                                        )
                                    }",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                // ------------------------------------------------
                // APPLY
                // ------------------------------------------------

                AppButton(
                    text = "Apply With This Plan",
                    onClick = {
                        onApplyWithValues(
                            principal,
                            tenorMonths
                        )

                        onDismiss()
                    }
                )
            }
        }
    }
}


// ============================================================
// API CONFIG DIALOG
// ============================================================

@Composable
fun ApiConfigDialog(
    onDismiss: () -> Unit,
    onSaveConfig: (
        baseUrl: String,
        apiKey: String,
        isMock: Boolean
    ) -> Unit
) {

    val current =
        ApiConfigManager.currentConfig

    var baseUrl by remember {
        mutableStateOf(current.baseUrl)
    }

    var apiKey by remember {
        mutableStateOf(current.apiKey)
    }

    var isMockMode by remember {
        mutableStateOf(current.isMockMode)
    }

    var testPingMessage by remember {
        mutableStateOf<String?>(null)
    }


    Dialog(
        onDismissRequest = onDismiss
    ) {

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                // ------------------------------------------------
                // HEADER
                // ------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Arna REST API & Database Config",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Emerald900
                    )

                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // ------------------------------------------------
                // API MODE
                // ------------------------------------------------

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .background(
                            Color(0xFFF1F5F9)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = if (isMockMode) {
                                "Local SQLite/Room Mode"
                            } else {
                                "Remote PHP REST API Mode"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Text(
                            text = if (isMockMode) {
                                "Using local Room database"
                            } else {
                                "Connecting to live ARNA PHP REST API"
                            },
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Switch(
                        checked = !isMockMode,
                        onCheckedChange = {
                            isMockMode = !it
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // ------------------------------------------------
                // BASE URL
                // ------------------------------------------------

                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = {
                        baseUrl = it
                    },
                    label = {
                        Text("PHP REST API Base URL")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                // ------------------------------------------------
                // API KEY
                // ------------------------------------------------

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = {
                        apiKey = it
                    },
                    label = {
                        Text("Security Token / API Key")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )


                // ------------------------------------------------
                // PING MESSAGE
                // ------------------------------------------------

                if (testPingMessage != null) {

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = testPingMessage!!,
                        fontSize = 12.sp,
                        color = if (
                            testPingMessage!!.contains(
                                "Connected",
                                ignoreCase = true
                            )
                        ) {
                            SuccessGreen
                        } else {
                            InfoBlue
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // ------------------------------------------------
                // ACTIONS
                // ------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = {

                            testPingMessage =
                                if (isMockMode) {

                                    "Connected: Local SQLite Room Database is Active"

                                } else {

                                    "API Endpoint configured: $baseUrl"
                                }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = "Test Ping"
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Button(
                        onClick = {

                            onSaveConfig(
                                baseUrl,
                                apiKey,
                                isMockMode
                            )

                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Emerald800
                        )
                    ) {

                        Text(
                            text = "Save & Apply"
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
// ARNA LOGO
// ============================================================

@Composable
fun ArnaLogo(
    modifier: Modifier = Modifier,
    contentDescription: String? = "ARNA Foundation Logo"
) {

    val context =
        LocalContext.current

    val bitmap =
        remember(context) {

            try {

                context.assets
                    .open(
                        "images/logo/arnalogu.jpg"
                    )
                    .use { stream ->

                        BitmapFactory
                            .decodeStream(stream)
                            ?.asImageBitmap()
                    }

            } catch (
                e: Exception
            ) {

                null
            }
        }


    if (bitmap != null) {

        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )

    } else {

        Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = Emerald800
        )
    }
}


// ============================================================
// ARNA TOP BAR
// ============================================================

@Composable
fun ArnaTopBar(
    title: String = "ARNA FOUNDATION",
    subtitle: String,
    userName: String,
    userId: String,
    userRole: String,
    onAvatarClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    logoutTag: String = "logout_button"
) {

    Surface(
        color = Color.White,
        border = BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        ),
        shadowElevation = 0.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 14.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ArnaGreenPrimary,
                    letterSpacing = 1.3.sp
                )

                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ArnaGreenDark,
                    letterSpacing = 0.8.sp
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Welcome, $userName",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "$userRole ID: $userId",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                val initials =
                    remember(userName) {

                        val parts =
                            userName
                                .trim()
                                .split(" ")
                                .filter {
                                    it.isNotBlank()
                                }

                        if (
                            parts.size >= 2 &&
                            parts[0].isNotEmpty() &&
                            parts[1].isNotEmpty()
                        ) {

                            "${parts[0].take(1)}${
                                parts[1].take(1)
                            }".uppercase()

                        } else {

                            userName
                                .trim()
                                .take(2)
                                .uppercase()
                                .ifEmpty {
                                    "AR"
                                }
                        }
                    }


                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            ArnaGreenPrimary
                        )
                        .clickable {
                            onAvatarClick()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }


                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Color(0xFFFEE2E2)
                        )
                        .testTag(logoutTag)
                ) {

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Logout",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}


// ============================================================
// SECTION HEADER
// ============================================================

@Composable
fun ArnaSectionHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF475569),
            letterSpacing = 1.1.sp
        )

        if (actionLabel != null) {

            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 2.dp
                )
            ) {

                Text(
                    text = actionLabel,
                    fontSize = 12.sp,
                    color = ArnaGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// ============================================================
// FINANCIAL OVERVIEW CARD
// ============================================================

@Composable
fun ArnaFinancialCard(
    title: String = "Financial Overview",
    totalLoan: String,
    outstanding: String,
    nextEmi: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArnaGreenDark,
                    letterSpacing = 0.8.sp
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            ArnaGreenLight
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = ArnaGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // ------------------------------------------------
                // TOTAL LOAN
                // ------------------------------------------------

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(
                            Color(0xFFF8FAFC)
                        )
                        .padding(12.dp)
                ) {

                    Text(
                        text = "Total Loan",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = totalLoan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }


                // ------------------------------------------------
                // OUTSTANDING
                // ------------------------------------------------

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(
                            Color(0xFFFEF2F2)
                        )
                        .padding(12.dp)
                ) {

                    Text(
                        text = "Outstanding",
                        fontSize = 11.sp,
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = outstanding,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB91C1C)
                    )
                }


                // ------------------------------------------------
                // NEXT EMI
                // ------------------------------------------------

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(
                            ArnaGreenLight
                        )
                        .padding(12.dp)
                ) {

                    Text(
                        text = "Next EMI",
                        fontSize = 11.sp,
                        color = ArnaGreenDark,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = nextEmi,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArnaGreenDark
                    )
                }
            }
        }
    }
}


// ============================================================
// STAT CARD
// ============================================================

@Composable
fun ArnaStatCard(
    title: String,
    value: String,
    subtext: String? = null,
    icon: ImageVector? = null,
    containerColor: Color = Color.White,
    accentColor: Color = ArnaGreenPrimary,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {

    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable {
                        onClick()
                    }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B)
                )

                if (icon != null) {

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                accentColor.copy(
                                    alpha = 0.12f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )

            if (subtext != null) {

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = subtext,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


// ============================================================
// QUICK ACTION
// ============================================================

@Composable
fun ArnaQuickAction(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    iconBgColor: Color = ArnaGreenLight,
    iconTint: Color = ArnaGreenPrimary,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(
                        RoundedCornerShape(14.dp)
                    )
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}


// ============================================================
// SERVICE CARD
// ============================================================

@Composable
fun ArnaServiceCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            ArnaGreenLight
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ArnaGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {

                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    if (subtitle != null) {

                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


// ============================================================
// TRANSACTION ITEM
// ============================================================

@Composable
fun ArnaTransactionItem(
    title: String,
    date: String,
    amount: String,
    isCredit: Boolean,
    status: String = "SUCCESS",
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCredit) {
                                Color(0xFFDCFCE7)
                            } else {
                                Color(0xFFFEE2E2)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            if (isCredit) {
                                Icons.Default.ArrowDownward
                            } else {
                                Icons.Default.ArrowUpward
                            },
                        contentDescription = null,
                        tint =
                            if (isCredit) {
                                Color(0xFF16A34A)
                            } else {
                                Color(0xFFDC2626)
                            },
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {

                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Text(
                        text = date,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }


            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text =
                        "${if (isCredit) "+" else "-"}$amount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        if (isCredit) {
                            Color(0xFF16A34A)
                        } else {
                            Color(0xFFDC2626)
                        }
                )

                Text(
                    text = status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}