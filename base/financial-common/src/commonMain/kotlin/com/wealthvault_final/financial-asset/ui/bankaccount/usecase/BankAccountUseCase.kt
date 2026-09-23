package com.wealthvault.`financial-asset`.ui.bankaccount.usecase


import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BankAccountRequest
import com.wealthvault.domain.portfolio.CreateBankAccountRepository
import kotlinx.coroutines.CoroutineDispatcher

// domain/usecase/AddBankAccountUseCase.kt

class AddBankAccountUseCase(
    private val repository: CreateBankAccountRepository,
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = platformLogger(),
): AppUseCase<BankAccountRequest, BankAccountData>(dispatcher) {

    override suspend fun execute(parameters: BankAccountRequest): AppResult<BankAccountData> {
        logger.debug("Create bank account started")
        return repository.createBankAccount(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Create bank account succeeded")
                is AppResult.Failure -> logger.warn("Create bank account failed")
            }
        }
    }
}
