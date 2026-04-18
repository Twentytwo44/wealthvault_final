
import com.wealthvault.forgetpassword.data.forget.ForgetNetworkDataSource
import com.wealthvault.forgetpassword.data.forget.ForgetRepositoryImpl
import com.wealthvault.forgetpassword.data.otp.OTPNetworkDataSource
import com.wealthvault.forgetpassword.data.otp.OTPRepositoryImpl
import com.wealthvault.forgetpassword.data.reset.ResetNetworkDataSource
import com.wealthvault.forgetpassword.data.reset.ResetRepositoryImpl
import com.wealthvault.forgetpassword.ui.ForgetPasswordScreenModel
import com.wealthvault.forgetpassword.usecase.ForgetUsecase
import com.wealthvault.forgetpassword.usecase.OTPUseCase
import com.wealthvault.forgetpassword.usecase.ResetPasswordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

object ForgetModule {
    val allModules = module {
        single { Dispatchers.IO }

        factory { ForgetNetworkDataSource(get()) }
        single<ForgetRepositoryImpl> {
            ForgetRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { ForgetUsecase(get(), get()) }
//        factory { RegisterScreenModel(get()) }

        factory { OTPNetworkDataSource(get()) }
        single<OTPRepositoryImpl> {
            OTPRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { OTPUseCase(get(),get()) }

        factory { ResetNetworkDataSource(get()) }
        single<ResetRepositoryImpl> {
            ResetRepositoryImpl(
                networkDataSource = get(),
            )
        }
        factory { ResetPasswordUseCase(get(),get()) }


        factory { ForgetPasswordScreenModel(get(), get(), get()) }

    }
}
