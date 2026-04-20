package com.wealthvault.notification.usecase

import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.notification.data.notification.NotificationRepositoryImpl
import com.wealthvault.notification_api.model.NotificationData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class NotificationUseCase(
    private val notificationRepository: NotificationRepositoryImpl,
    dispatcher: CoroutineDispatcher
): FlowUseCase<Unit, List<NotificationData>>(dispatcher) {

    // 💡 หมายเหตุ: ถ้า FlowUseCase ของคุณบังคับให้รับ parameter ด้วย อย่าลืมใส่เป็น execute(parameters: Unit) นะครับ
    override fun execute(parameters: Unit): Flow<FlowResult<List<NotificationData>>> = flow {
        // 1. เรียก Repository ดึงข้อมูลแจ้งเตือน
        val result = notificationRepository.getNoti()

        result.onSuccess { notifications -> // ✅ แก้ชื่อจาก cash เป็น notifications ให้สื่อความหมาย
            // ✅ เปลี่ยน Log ให้ตรงกับ UseCase และใช้ .size แทน .name เพราะเป็น List
            println("✅ [NotificationUseCase] ดึงข้อมูลสำเร็จ จำนวน: ${notifications.size} รายการ")

            // 2. ส่งข้อมูล List<NotificationData> กลับไปให้ UI ผ่าน FlowResult
            emit(FlowResult.Continue(notifications))

        }.onFailure { exception ->
            println("❌ [NotificationUseCase] ดึงข้อมูลไม่สำเร็จ: ${exception.message}")

            // 3. ส่ง Error กลับไป
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨 [NotificationUseCase] เกิดข้อผิดพลาดไม่คาดคิด: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}
