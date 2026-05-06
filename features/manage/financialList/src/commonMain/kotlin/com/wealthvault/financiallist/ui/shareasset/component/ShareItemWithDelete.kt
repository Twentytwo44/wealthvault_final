package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_bin
import com.wealthvault.core.generated.resources.ic_form_email_outline
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.theme.RedErr
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import org.jetbrains.compose.resources.painterResource

@Composable
fun ShareItemWithDelete(data: ShareInfo, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().background(LightBg),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSoftWhite),
        border = BorderStroke(1.dp, LightBorder),
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image / Placeholder
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(LightBg),
                contentAlignment = Alignment.Center
            ) {
                if (!data.profileUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = data.profileUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val icon = when (data.typeData) {
                        "E" -> painterResource(Res.drawable.ic_form_email_outline)
                        "G" -> painterResource(Res.drawable.ic_nav_social)
                        else -> painterResource(Res.drawable.ic_nav_profile)
                    }
                    Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LightText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (data.typeData == "E") {
                    if (data.date != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(data.date!!, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    }
                } else {
                    if (data.subText.isNotBlank() || data.date != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (data.subText.isNotBlank()) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .background(LightBg, RoundedCornerShape(200.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = if(data.typeData == "G") painterResource(Res.drawable.ic_nav_social) else painterResource(Res.drawable.ic_nav_profile)
                                    Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = data.subText,
                                        color = LightPrimary,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            if (data.date != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = data.date!!,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_bin),
                    contentDescription = "Delete",
                    tint = RedErr,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}