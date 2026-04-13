package com.wealthvault_final.`financial-asset`.ui.cash.usecase

import com.wealthvault.cash_api.model.CashData
import com.wealthvault.cash_api.model.CashRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault_final.`financial-asset`.data.cash.CashRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

// domain/usecase/AddStockUseCase.kt

class AddCashUseCase(
    private val repository: CashRepositoryImpl, // แนะนำให้ใช้ Interface
    dispatcher: CoroutineDispatcher
): FlowUseCase<CashRequest, CashData>(dispatcher) {

    override fun execute(parameters: CashRequest): Flow<FlowResult<CashData>> = flow {
        println("🚀 [AddStockUseCase] เริ่มต้นบันทึกหุ้น: ${parameters.name}")

        // 1. เรียก Repository (ซึ่งภายในควรจัดการเรื่อง Thread และ Result มาแล้ว)
        val result = repository.create(parameters)

        result.onSuccess { cash ->
            println("✅ [AddStockUseCase] บันทึกสำเร็จ: ${cash.name}")

            // 2. ส่งข้อมูล Investment (Domain Model) กลับไปให้ UI ผ่าน FlowResult
            emit(FlowResult.Continue(cash))
        }.onFailure { exception ->
            println("❌ [AddStockUseCase] บันทึกไม่สำเร็จ: ${exception.message}")

            // 3. ส่ง Error กลับไป
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨 [AddStockUseCase] เกิดข้อผิดพลาดไม่คาดคิด: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}
