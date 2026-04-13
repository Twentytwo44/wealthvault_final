package com.wealthvault_final.`financial-asset`.ui.insurance.usecase

import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.insurance_api.model.InsuranceData
import com.wealthvault.insurance_api.model.InsuranceRequest
import com.wealthvault_final.`financial-asset`.data.insurance.InsuranceRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

// domain/usecase/AddInsuranceUseCase.kt

class AddInsuranceUseCase(
    private val repository: InsuranceRepositoryImpl, // แนะนำให้ใช้ Interface
    dispatcher: CoroutineDispatcher
): FlowUseCase<InsuranceRequest, InsuranceData>(dispatcher) {

    override fun execute(parameters: InsuranceRequest): Flow<FlowResult<InsuranceData>> = flow {
        println("🚀 [AddInsuranceUseCase] เริ่มต้นบันทึกหุ้น: ${parameters.name}")

        // 1. เรียก Repository (ซึ่งภายในควรจัดการเรื่อง Thread และ Result มาแล้ว)
        val result = repository.createInsurance(parameters)

        result.onSuccess { insurance ->
            println("✅ [AddInsuranceUseCase] บันทึกสำเร็จ: ${insurance.name}")

            // 2. ส่งข้อมูล Investment (Domain Model) กลับไปให้ UI ผ่าน FlowResult
            emit(FlowResult.Continue(insurance))
        }.onFailure { exception ->
            println("❌ [AddInsuranceUseCase] บันทึกไม่สำเร็จ: ${exception.message}")

            // 3. ส่ง Error กลับไป
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨 [AddInsuranceUseCase] เกิดข้อผิดพลาดไม่คาดคิด: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}
