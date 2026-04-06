package com.wealthvault_final.`financial-obligations`.ui.expense.usecase


import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.liability_api.model.LiabilityData
import com.wealthvault.liability_api.model.LiabilityRequest
import com.wealthvault_final.`financial-obligations`.data.liability.LiabilityRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AddExpenseUseCase(
    private val repository: LiabilityRepositoryImpl, // แนะนำให้ใช้ Interface
    dispatcher: CoroutineDispatcher
): FlowUseCase<LiabilityRequest, LiabilityData>(dispatcher) {

    override fun execute(parameters: LiabilityRequest): Flow<FlowResult<LiabilityData>> = flow {
        println("🚀 [AddExpenseUseCase] เริ่มต้นบันทึกหุ้น: ${parameters.name}")

        // 1. เรียก Repository (ซึ่งภายในควรจัดการเรื่อง Thread และ Result มาแล้ว)
        val result = repository.createLiability(parameters)

        result.onSuccess { cash ->
            println("✅ [AddExpenseUseCase] บันทึกสำเร็จ: ${cash.name}")

            // 2. ส่งข้อมูล Investment (Domain Model) กลับไปให้ UI ผ่าน FlowResult
            emit(FlowResult.Continue(cash))
        }.onFailure { exception ->
            println("❌ [AddExpenseUseCase] บันทึกไม่สำเร็จ: ${exception.message}")

            // 3. ส่ง Error กลับไป
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨 [AddExpenseUseCase] เกิดข้อผิดพลาดไม่คาดคิด: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}
