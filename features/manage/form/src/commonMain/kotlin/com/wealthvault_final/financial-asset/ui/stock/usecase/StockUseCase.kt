package com.wealthvault_final.`financial-asset`.ui.stock.usecase

import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.investment_api.model.InvestmentData
import com.wealthvault.investment_api.model.InvestmentRequest
import com.wealthvault_final.`financial-asset`.data.stock.AssetRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

// domain/usecase/AddStockUseCase.kt

class AddStockUseCase(
    private val repository: AssetRepositoryImpl, // แนะนำให้ใช้ Interface
    dispatcher: CoroutineDispatcher
): FlowUseCase<InvestmentRequest, InvestmentData>(dispatcher) {

    override fun execute(parameters: InvestmentRequest): Flow<FlowResult<InvestmentData>> = flow {
        println("🚀 [AddStockUseCase] เริ่มต้นบันทึกหุ้น: ${parameters.name}")

        // 1. เรียก Repository (ซึ่งภายในควรจัดการเรื่อง Thread และ Result มาแล้ว)
        val result = repository.create(parameters)

        result.onSuccess { investment ->
            println("✅ [AddStockUseCase] บันทึกสำเร็จ: ${investment.name}")

            // 2. ส่งข้อมูล Investment (Domain Model) กลับไปให้ UI ผ่าน FlowResult
            emit(FlowResult.Continue(investment))
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
