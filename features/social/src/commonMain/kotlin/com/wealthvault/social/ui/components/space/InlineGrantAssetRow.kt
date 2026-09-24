package com.wealthvault.social.ui.components.space

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_asset_type_account
import com.wealthvault.core.generated.resources.ic_asset_type_building
import com.wealthvault.core.generated.resources.ic_asset_type_cash
import com.wealthvault.core.generated.resources.ic_asset_type_expense
import com.wealthvault.core.generated.resources.ic_asset_type_insurance
import com.wealthvault.core.generated.resources.ic_asset_type_investment
import com.wealthvault.core.generated.resources.ic_asset_type_land
import com.wealthvault.core.generated.resources.ic_asset_type_loan
import com.wealthvault.core.generated.resources.ic_form_check
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.domain.social.ShareGroup
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun InlineGrantAssetRow(
    asset: ShareGroup,
    isChecked: Boolean,
    themeColor: Color,
    onToggle: () -> Unit,
) {
    val assetName = asset.assetDetail?.name ?: "ไม่ระบุชื่อ"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp, end = 8.dp)
            .clickable(onClick = onToggle),
    ) {
        AssetPreview(asset = asset, assetName = assetName)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = assetName,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isChecked) themeColor else Color.Transparent)
                .border(
                    width = 2.dp,
                    color = if (isChecked) themeColor else Color.LightGray,
                    shape = RoundedCornerShape(8.dp),
                )
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            if (isChecked) {
                Icon(
                    painter = painterResource(Res.drawable.ic_form_check),
                    contentDescription = null,
                    tint = LightSoftWhite,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun AssetPreview(asset: ShareGroup, assetName: String) {
    val imageUrl = asset.assetDetail?.image
    val isImageUrl = imageUrl?.let {
        it.endsWith(".png", ignoreCase = true) ||
            it.endsWith(".jpg", ignoreCase = true) ||
            it.endsWith(".jpeg", ignoreCase = true) ||
            it.endsWith(".webp", ignoreCase = true)
    } == true

    if (isImageUrl) {
        AsyncImage(
            model = imageUrl,
            contentDescription = assetName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)),
        )
    } else {
        val assetType = asset.type ?: ""
        val iconRes = when {
            assetType.contains("account", ignoreCase = true) -> Res.drawable.ic_asset_type_account
            assetType.contains("building", ignoreCase = true) -> Res.drawable.ic_asset_type_building
            assetType.contains("cash", ignoreCase = true) -> Res.drawable.ic_asset_type_cash
            assetType.contains("expense", ignoreCase = true) -> Res.drawable.ic_asset_type_expense
            assetType.contains("insurance", ignoreCase = true) -> Res.drawable.ic_asset_type_insurance
            assetType.contains("investment", ignoreCase = true) -> Res.drawable.ic_asset_type_investment
            assetType.contains("land", ignoreCase = true) -> Res.drawable.ic_asset_type_land
            assetType.contains("loan", ignoreCase = true) || assetType.contains("liability", ignoreCase = true) -> Res.drawable.ic_asset_type_loan
            else -> null
        }
        Box(
            modifier = Modifier.size(40.dp).background(LightBg, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = assetType,
                    tint = LightPrimary,
                    modifier = Modifier.size(24.dp),
                )
            } else {
                Text(
                    text = assetType.take(4).uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    maxLines = 1,
                )
            }
        }
    }
}
