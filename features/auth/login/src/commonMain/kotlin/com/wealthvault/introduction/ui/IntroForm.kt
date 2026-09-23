package com.wealthvault.introduction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.generated.resources.ic_common_pen
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun IntroForm(
    userName: String,
    onUserNameChange: (String) -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    phoneNum: String,
    onPhoneNumChange: (String) -> Unit,
    birthday: String,
    picture: ByteArray?,
    isFormValid: Boolean,
    onBackClick: () -> Unit,
    onPickPicture: () -> Unit,
    onShowDatePicker: () -> Unit,
    onNextClick: () -> Unit,
) {
    WavyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, bottom = 24.dp)
                .statusBarsPadding()
                .imePadding(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 26.dp),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_back),
                        contentDescription = "Back",
                        tint = LightPrimary,
                        modifier = Modifier.size(24.dp).clickable(onClick = onBackClick),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "ข้อมูลส่วนตัว",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary,
                    )
                }

                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.clickable(onClick = onPickPicture),
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .border(width = 3.dp, color = LightPrimary, shape = CircleShape)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(LightSurface, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (picture != null) {
                            AsyncImage(
                                model = picture,
                                contentDescription = "Profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.ic_nav_profile),
                                contentDescription = "Default",
                                tint = LightPrimary.copy(alpha = 0.5f),
                                modifier = Modifier.size(50.dp),
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .clip(CircleShape)
                            .background(LightPrimary, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_common_pen),
                            contentDescription = "Edit Picture",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                ProfileTextField(
                    label = "ชื่อผู้ใช้งาน*",
                    value = userName,
                    onValueChange = onUserNameChange,
                    placeholder = "กรอกชื่อผู้ใช้งาน",
                )
                Spacer(modifier = Modifier.height(14.dp))
                ProfileTextField(
                    label = "ชื่อจริง*",
                    value = firstName,
                    onValueChange = onFirstNameChange,
                    placeholder = "กรอกชื่อจริง",
                )
                Spacer(modifier = Modifier.height(14.dp))
                ProfileTextField(
                    label = "นามสกุล*",
                    value = lastName,
                    onValueChange = onLastNameChange,
                    placeholder = "กรอกนามสกุล",
                )
                Spacer(modifier = Modifier.height(14.dp))
                ProfileTextField(
                    label = "วันเกิด*",
                    value = birthday,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = "เลือกวันเกิด",
                    trailingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_common_calendar),
                            contentDescription = "Calendar",
                            tint = LightPrimary,
                            modifier = Modifier.size(24.dp).clickable(onClick = onShowDatePicker),
                        )
                    },
                )
                Spacer(modifier = Modifier.height(14.dp))
                ProfileTextField(
                    label = "เบอร์โทร*",
                    value = phoneNum,
                    onValueChange = onPhoneNumChange,
                    placeholder = "กรอกเบอร์โทร",
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            ) {
                Button(
                    onClick = onNextClick,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightPrimary,
                        disabledContainerColor = LightBorder,
                    ),
                    enabled = isFormValid,
                ) {
                    Text(
                        text = "ต่อไป",
                        color = if (isFormValid) Color.White else Color.Gray,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}
