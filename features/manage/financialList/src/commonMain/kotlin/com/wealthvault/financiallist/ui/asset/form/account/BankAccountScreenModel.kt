package com.wealthvault.financiallist.ui.asset.form.account

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.portfolio.BankAccountFileUploadData
import com.wealthvault.domain.portfolio.BankAccountRequest
import com.wealthvault.domain.portfolio.UpdateBankAccountRepository
import com.wealthvault.domain.portfolio.CreateBankAccountRepository
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.BankAccountModel
import com.wealthvault.core.architecture.FormAction
import com.wealthvault.core.architecture.FormEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import com.wealthvault.core.architecture.toAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class BankAccountScreenModel(
    private val bankAccountRepository: UpdateBankAccountRepository,
    private val createBankAccountRepository: CreateBankAccountRepository,
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _accountData = MutableStateFlow(
        BankAccountModel(
            type = "",
            bankName = "",
            bankId = "",
            name = "",
            amount = Money(0),
            description = "",
            attachments = emptyList()

        )
    )
    val state = _accountData.asStateFlow()
    private val udf = UiStateHolder(_accountData.value)
    val uiState: StateFlow<UiState<BankAccountModel>> = udf.state
    val effects = udf.effects
    private var submitJob: Job? = null

    fun onAction(action: FormAction<BankAccountModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateAttachment(action.added, action.deleted)
            is FormAction.Submit -> submitAccount(action.id)
        }
    }

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())


    fun updateForm(data: BankAccountModel) {
        _accountData.update {
            it.copy(
               type = data.type,
                bankName = data.bankName,
                bankId = data.bankId,
                name = data.name,
                amount = data.amount,
                description = data.description,
                attachments = data.attachments,

            ) }
        udf.set(_accountData.value, isLoading = false, error = null)
    }

    fun updateAttachment(addedList: List<Attachment>,deletedList: List<Attachment>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
    }

    private fun asRequest(): BankAccountRequest {
        val current = _accountData.value



        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = _addedAttachments.value.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.name}.$extension"

            BankAccountFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        }

        return BankAccountRequest(
            name = current.name,
            type = current.type,
            bankName = current.bankName,
            bankAccount = current.bankId,
            amount = current.amount,
            description = current.description,
            files = allFiles,
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }

        )
    }

    fun submitAccount(id:String, onSuccess: () -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
//                isLoading = true
//                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง BankAccount ก่อน ---

                val requestBody = asRequest()

                val bankAccountResult = bankAccountRepository.updateAccount(id,requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val bankAccountResponse = bankAccountResult.getOrNull()

                if (bankAccountResult.isSuccess && bankAccountResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง BankAccount
                    // สมมติว่า field id อยู่ใน bankAccountResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = bankAccountResponse.id
                    udf.success()
                    udf.emit(FormEffect.Saved)
                    onSuccess()

                }
                else {
                    udf.failure(bankAccountResult.exceptionOrNull()?.toAppError()
                        ?: IllegalStateException("Bank account update failed").toAppError())
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                udf.failure(e.toAppError())
            } finally {
//                isLoading = false
            }
        }
        submitJob = job
        job.invokeOnCompletion { if (submitJob === job) submitJob = null }
    }

    /** Creates a new bank account and returns its domain id to the share step. */
    fun submitCreate(onSuccess: (String) -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                when (val result = createBankAccountRepository.createBankAccount(asRequest().copy(deleteListId = emptyList()))) {
                    is com.wealthvault.core.architecture.AppResult.Success -> {
                        val id = result.value.id.takeIf { it.isNotBlank() }
                            ?: error("Bank account create response did not include an id")
                        udf.success()
                        udf.emit(FormEffect.Saved)
                        onSuccess(id)
                    }
                    is com.wealthvault.core.architecture.AppResult.Failure -> udf.failure(result.error)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                udf.failure(e.toAppError())
            }
        }
        submitJob = job
        job.invokeOnCompletion { if (submitJob === job) submitJob = null }
    }

}
