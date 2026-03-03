
import com.wealthvault.forgetpassword.data.ForgetNetworkDataSource
import com.wealthvault.forgetpassword.data.ForgetRepositoryImpl
import com.wealthvault.forgetpassword.data.OTPNetworkDataSource
import com.wealthvault.forgetpassword.data.OTPRepositoryImpl
import com.wealthvault.forgetpassword.data.ResetNetworkDataSource
import com.wealthvault.forgetpassword.data.ResetRepositoryImpl
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



    }
}
