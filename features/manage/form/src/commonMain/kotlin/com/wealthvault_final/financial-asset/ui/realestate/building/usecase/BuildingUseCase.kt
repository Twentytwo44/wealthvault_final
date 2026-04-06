package com.wealthvault_final.`financial-asset`.ui.realestate.building.usecase

import com.wealthvault.building_api.model.BuildingData
import com.wealthvault.building_api.model.BuildingRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault_final.`financial-asset`.data.building.BuildingRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

// domain/usecase/AddBuildingUseCase.kt

class AddBuildingUseCase(
    private val repository: BuildingRepositoryImpl, // แนะนำให้ใช้ Interface
    dispatcher: CoroutineDispatcher
): FlowUseCase<BuildingRequest, BuildingData>(dispatcher) {

    override fun execute(parameters: BuildingRequest): Flow<FlowResult<BuildingData>> = flow {
        println("🚀 [AddBuildingUseCase] เริ่มต้นบันทึกหุ้น: ${parameters.name}")

        // 1. เรียก Repository (ซึ่งภายในควรจัดการเรื่อง Thread และ Result มาแล้ว)
        val result = repository.createBuilding(parameters)

        result.onSuccess { building ->
            println("✅ [AddBuildingUseCase] บันทึกสำเร็จ: ${building.name}")

            // 2. ส่งข้อมูล Investment (Domain Model) กลับไปให้ UI ผ่าน FlowResult
            emit(FlowResult.Continue(building))
        }.onFailure { exception ->
            println("❌ [AddBuildingUseCase] บันทึกไม่สำเร็จ: ${exception.message}")

            // 3. ส่ง Error กลับไป
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨 [AddBuildingUseCase] เกิดข้อผิดพลาดไม่คาดคิด: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}
