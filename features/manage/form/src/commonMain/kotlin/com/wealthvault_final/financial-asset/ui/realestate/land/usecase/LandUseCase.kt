package com.wealthvault_final.`financial-asset`.ui.realestate.land.usecase

import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.land_api.model.LandData
import com.wealthvault.land_api.model.LandRequest
import com.wealthvault_final.`financial-asset`.data.land.LandRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

// domain/usecase/AddLandUseCase.kt

class AddLandUseCase(
    private val repository: LandRepositoryImpl, // แนะนำให้ใช้ Interface
    dispatcher: CoroutineDispatcher
): FlowUseCase<LandRequest, LandData>(dispatcher) {

    override fun execute(parameters: LandRequest): Flow<FlowResult<LandData>> = flow {
        println("🚀 [AddLandUseCase] เริ่มต้นบันทึกหุ้น: ${parameters.name}")

        // 1. เรียก Repository (ซึ่งภายในควรจัดการเรื่อง Thread และ Result มาแล้ว)
        val result = repository.create(parameters)

        result.onSuccess { building ->
            println("✅ [AddLandUseCase] บันทึกสำเร็จ: ${building.name}")

            // 2. ส่งข้อมูล Investment (Domain Model) กลับไปให้ UI ผ่าน FlowResult
            emit(FlowResult.Continue(building))
        }.onFailure { exception ->
            println("❌ [AddLandUseCase] บันทึกไม่สำเร็จ: ${exception.message}")

            // 3. ส่ง Error กลับไป
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨 [AddLandUseCase] เกิดข้อผิดพลาดไม่คาดคิด: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}
