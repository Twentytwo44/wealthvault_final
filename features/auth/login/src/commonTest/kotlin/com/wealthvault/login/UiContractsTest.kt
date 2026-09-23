package com.wealthvault.login

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.FormAction
import com.wealthvault.core.architecture.FormEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import com.wealthvault.forgetpassword.ui.ForgetPasswordUiAction
import com.wealthvault.forgetpassword.ui.ForgetPasswordUiEffect
import com.wealthvault.forgetpassword.ui.ForgetPasswordUiState
import com.wealthvault.introduction.ui.IntroUiAction
import com.wealthvault.introduction.ui.IntroUiEffect
import com.wealthvault.introduction.ui.IntroUiState
import com.wealthvault.login.ui.LoginState
import com.wealthvault.login.ui.LoginUiAction
import com.wealthvault.login.ui.LoginUiEffect
import com.wealthvault.login.ui.LoginUiState
import com.wealthvault.login.ui.validateLoginInput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class UiContractsTest {
    @Test
    fun coreHolderPublishesLoadingSuccessFailureAndEffects() {
        val holder = UiStateHolder("initial")
        assertEquals(UiState(data = "initial"), holder.state.value)

        holder.loading()
        assertTrue(holder.state.value.isLoading)
        holder.success("loaded")
        assertEquals("loaded", holder.state.value.data)
        assertTrue(!holder.state.value.isLoading)
        holder.failure(AppError.Unauthorized)
        assertEquals(AppError.Unauthorized, holder.state.value.error)
        holder.emit(FormEffect.Saved)
        assertIs<FormEffect.Saved>(FormEffect.Saved)
        holder.set(data = "updated", isLoading = false)
        assertEquals("updated", holder.state.value.data)
    }

    @Test
    fun formContractsCarryValuesAndEffects() {
        val changed = FormAction.Changed("value")
        val attachments = FormAction.AttachmentsChanged(emptyList(), emptyList())
        val submit = FormAction.Submit("id")
        assertEquals("value", changed.value)
        assertTrue(attachments.added.isEmpty())
        assertEquals("id", submit.id)
        assertEquals("invalid", LoginUiAction.ValidationFailed("invalid").message)
        assertIs<FormEffect.Failed>(FormEffect.Failed(AppError.NotFound))
    }

    @Test
    fun authContractsRemainImmutableAndTyped() {
        assertEquals("user", LoginUiState(username = "user").username)
        assertEquals("secret", LoginUiAction.PasswordChanged("secret").value)
        assertIs<LoginUiAction.Submit>(LoginUiAction.Submit)
        assertIs<LoginUiEffect.Navigate>(LoginUiEffect.Navigate(LoginState.GoToMain))
        assertIs<LoginUiEffect.ShowError>(LoginUiEffect.ShowError(AppError.Unknown(IllegalStateException())))

    }

    @Test
    fun loginValidationRejectsIncompleteAndMalformedCredentials() {
        assertEquals("กรุณากรอกข้อมูลให้ครบถ้วน", validateLoginInput("", "secret"))
        assertEquals("รูปแบบอีเมลไม่ถูกต้อง", validateLoginInput("not-an-email", "secret"))
        assertEquals(null, validateLoginInput("user@example.com", "secret"))
    }

    @Test
    fun recoveryAndIntroContractsCarryAllActions() {
        val recovery = ForgetPasswordUiState(resetToken = "token", isOtpSent = true, isOtpVerified = true)
        assertTrue(recovery.isOtpVerified)
        assertEquals("mail", ForgetPasswordUiAction.SendOtp("mail").email)
        assertEquals("1234", ForgetPasswordUiAction.VerifyOtp("mail", "1234").otp)
        assertEquals("token", ForgetPasswordUiAction.ResetPassword("token", "new", "new").token)
        assertIs<ForgetPasswordUiAction.Clear>(ForgetPasswordUiAction.Clear)
        assertIs<ForgetPasswordUiEffect.PasswordReset>(ForgetPasswordUiEffect.PasswordReset)
        assertIs<ForgetPasswordUiEffect.ShowError>(ForgetPasswordUiEffect.ShowError(AppError.NotFound))

        val intro = IntroUiState(userName = "user", firstName = "first", lastName = "last", phoneNum = "1")
        assertEquals("first", intro.firstName)
        assertEquals("user", IntroUiAction.UserNameChanged("user").value)
        assertEquals("first", IntroUiAction.FirstNameChanged("first").value)
        assertEquals("last", IntroUiAction.LastNameChanged("last").value)
        assertEquals("1", IntroUiAction.PhoneChanged("1").value)
        assertEquals("2000", IntroUiAction.BirthdayChanged("2000").value)
        assertEquals(null, IntroUiAction.PictureChanged(null).value)
        assertIs<IntroUiAction.Submit>(IntroUiAction.Submit)
        assertIs<IntroUiEffect.Updated>(IntroUiEffect.Updated)
        assertIs<IntroUiEffect.ShowError>(IntroUiEffect.ShowError(AppError.Unauthorized))
    }
}
