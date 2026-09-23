
import com.wealthvault.forgetpassword.ui.ForgetPasswordScreenModel
import com.wealthvault.forgetpassword.usecase.ForgetUsecase
import com.wealthvault.forgetpassword.usecase.OTPUseCase
import com.wealthvault.forgetpassword.usecase.ResetPasswordUseCase
import org.koin.dsl.module

object ForgetModule {
    val allModules = module {
        factory { ForgetUsecase(get(), get(), get()) }
//        factory { RegisterScreenModel(get()) }

        factory { OTPUseCase(get(), get(), get()) }

        factory { ResetPasswordUseCase(get(), get(), get()) }


        factory { ForgetPasswordScreenModel(get(), get(), get()) }

    }
}
