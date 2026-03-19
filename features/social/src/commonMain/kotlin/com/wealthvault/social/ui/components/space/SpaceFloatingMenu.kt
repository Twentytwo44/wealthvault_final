package com.wealthvault.social.ui.components.space

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material.icons.filled.Share
//import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SpaceFloatingMenu(
    onShareClick: () -> Unit,
    onManageClick: () -> Unit,
    themeColor: Color = Color(0xFFC27A5A)
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        label = "fab_rotate"
    )

    Column(
        horizontalAlignment = Alignment.End,
        // 🌟 เพิ่มบรรทัดนี้: ดันทั้งแผงขึ้นด้านบน 24.dp และขยับหนีขอบขวา 8.dp 🌟
        modifier = Modifier.padding(bottom = 40.dp, end = 10.dp)
    ) {

        // --- เลเยอร์ปุ่มย่อยที่จะเด้งออกมา ---
        AnimatedVisibility(
            visible = expanded,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.End) {
                // ปุ่มที่ 1: แชร์
                FloatingActionButton(
                    onClick = {
                        expanded = false
                        onShareClick()
                    },
                    containerColor = themeColor.copy(alpha = 0.6f),
                    contentColor = Color.White,
                    modifier = Modifier.padding(end = 16.dp).size(48.dp),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
//                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.height(6.dp))

                // ปุ่มที่ 2: จัดการ/ฟิลเตอร์
                FloatingActionButton(
                    onClick = {
                        expanded = false
                        onManageClick()
                    },
                    containerColor = themeColor.copy(alpha = 0.6f),
                    contentColor = Color.White,
                    modifier = Modifier.padding(end = 74.dp).size(48.dp),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
//                    Icon(Icons.Default.Tune, contentDescription = "Manage", modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // --- ปุ่มหลัก (ฟันเฟือง) ---
        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = themeColor,
            contentColor = Color.White,
            modifier = Modifier.offset(y = (-30).dp).size(56.dp),
            shape = CircleShape
        ) {
//            Icon(
//                imageVector = Icons.Default.Settings,
//                contentDescription = "Menu",
//                modifier = Modifier.rotate(rotation)
//            )
        }
    }
}