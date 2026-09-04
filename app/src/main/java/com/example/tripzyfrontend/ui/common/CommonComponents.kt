package com.example.tripzyfrontend.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripzyfrontend.R
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.ui.theme.BalanceNegative
import com.example.tripzyfrontend.ui.theme.BalanceNegativeBg
import com.example.tripzyfrontend.ui.theme.BalancePositive
import com.example.tripzyfrontend.ui.theme.BalancePositiveBg
import com.example.tripzyfrontend.ui.theme.BalanceZero
import com.example.tripzyfrontend.ui.theme.BalanceZeroBg
import com.example.tripzyfrontend.ui.theme.StatusActiveBg
import com.example.tripzyfrontend.ui.theme.StatusActiveText
import com.example.tripzyfrontend.ui.theme.StatusArchivedBg
import com.example.tripzyfrontend.ui.theme.StatusArchivedText
import com.example.tripzyfrontend.ui.theme.StatusSettledBg
import com.example.tripzyfrontend.ui.theme.StatusSettledText
import com.example.tripzyfrontend.ui.theme.TagPersonalBg
import com.example.tripzyfrontend.ui.theme.TagPersonalText
import com.example.tripzyfrontend.ui.theme.TagSharedBg
import com.example.tripzyfrontend.ui.theme.TagSharedText

@Composable
fun TripzyLogo(
    modifier: Modifier = Modifier,
    height: Dp = 48.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_tripzy_logo),
        contentDescription = "Tripzy Logo",
        modifier = modifier.height(height)
    )
}

@Composable
fun TripzyIcon(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_tripzy_icon),
        contentDescription = "Tripzy Icon",
        modifier = modifier.size(size)
    )
}

@Composable
fun AmountText(
    money: Money,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = money.formatted(),
        style = style.copy(fontWeight = FontWeight.Bold),
        color = color,
        modifier = modifier
    )
}

@Composable
fun StatusBadge(
    status: TourStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        TourStatus.ACTIVE -> Triple(StatusActiveBg, StatusActiveText, "ACTIVE")
        TourStatus.SETTLED -> Triple(StatusSettledBg, StatusSettledText, "SETTLED")
        TourStatus.ARCHIVED -> Triple(StatusArchivedBg, StatusArchivedText, "ARCHIVED")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = textColor
        )
    }
}

@Composable
fun BalancePill(
    netBalance: Money,
    modifier: Modifier = Modifier
) {
    val paisa = netBalance.paisa
    val (bgColor, textColor, icon) = when {
        paisa > 0 -> Triple(BalancePositiveBg, BalancePositive, Icons.Default.ArrowUpward)
        paisa < 0 -> Triple(BalanceNegativeBg, BalanceNegative, Icons.Default.ArrowDownward)
        else -> Triple(BalanceZeroBg, BalanceZero, Icons.Default.Check)
    }

    val displayText = when {
        paisa > 0 -> "+${netBalance.formatted()} (Receives)"
        paisa < 0 -> "${netBalance.formatted()} (Owes)"
        else -> "Settled (৳0)"
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

@Composable
fun ExpenseTypeBadge(
    isPersonal: Boolean,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = if (isPersonal) {
        Triple(TagPersonalBg, TagPersonalText, "PERSONAL")
    } else {
        Triple(TagSharedBg, TagSharedText, "SHARED")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

/**
 * Stitch Glassmorphism Card:
 * Translucent container with soft border, elevated backdrop feel, and optional click behavior.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    backgroundColor: Color = com.example.tripzyfrontend.ui.theme.MidnightSurfaceContainer.copy(alpha = 0.75f),
    borderColor: Color = com.example.tripzyfrontend.ui.theme.GlassBorder,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .background(backgroundColor)
            .border(BorderStroke(borderWidth, borderColor), shape)
            .clickable { onClick() }
    } else {
        modifier
            .clip(shape)
            .background(backgroundColor)
            .border(BorderStroke(borderWidth, borderColor), shape)
    }

    Box(modifier = cardModifier) {
        content()
    }
}

/**
 * Stitch Glowing Progress Bar:
 * Sleek thin track with vibrant gradient progress indicator for budget and trip status.
 */
@Composable
fun GlowingProgressBar(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0x33FFFFFF),
    progressBrush: androidx.compose.ui.graphics.Brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(com.example.tripzyfrontend.ui.theme.BrandPrimary, com.example.tripzyfrontend.ui.theme.NeonTeal)
    ),
    height: Dp = 6.dp
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clampedProgress)
                .height(height)
                .clip(RoundedCornerShape(percent = 50))
                .background(progressBrush)
        )
    }
}

/**
 * Stitch StatCard:
 * 3-column compact metric item used in Profile and Dashboards.
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = com.example.tripzyfrontend.ui.theme.BalancePositive
                )
            }
        }
    }
}

