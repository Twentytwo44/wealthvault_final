package com.wealthvault_final.`financial-asset`.ui.bankaccount.usecase


import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.account_api.model.BankAccountRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault_final.`financial-asset`.data.bankaccount.BankAccountRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

// domain/usecase/AddBankAccountUseCase.kt

class AddBankAccountUseCase(
    private val repository: BankAccountRepositoryImpl, // แนะนำให้ใช้ Interface
    dispatcher: CoroutineDispatcher
): FlowUseCase<BankAccountRequest, BankAccountData>(dispatcher) {

    override fun execute(parameters: BankAccountRequest): Flow<FlowResult<BankAccountData>> = flow {
        println("🚀 [AddBankAccountUseCase] เริ่มต้นบันทึกหุ้น: ${parameters.name}")

        // 1. เรียก Repository (ซึ่งภายในควรจัดการเรื่อง Thread และ Result มาแล้ว)
        val result = repository.createBankAccount(parameters)

        result.onSuccess { cash ->
            println("✅ [AddBankAccountUseCase] บันทึกสำเร็จ: ${cash.name}")

            // 2. ส่งข้อมูล Investment (Domain Model) กลับไปให้ UI ผ่าน FlowResult
            emit(FlowResult.Continue(cash))
        }.onFailure { exception ->
            println("❌ [AddBankAccountUseCase] บันทึกไม่สำเร็จ: ${exception.message}")

            // 3. ส่ง Error กลับไป
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨 [AddBankAccountUseCase] เกิดข้อผิดพลาดไม่คาดคิด: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}
