
import com.wealthvault.fogetpassword.data.ForgetNetworkDataSource
import com.wealthvault.fogetpassword.data.ForgetRepositoryImpl
import com.wealthvault.fogetpassword.data.OTPNetworkDataSource
import com.wealthvault.fogetpassword.data.OTPRepositoryImpl
import com.wealthvault.fogetpassword.data.ResetNetworkDataSource
import com.wealthvault.fogetpassword.data.ResetRepositoryImpl
import com.wealthvault.fogetpassword.usecase.ForgetUsecase
import com.wealthvault.fogetpassword.usecase.OTPUseCase
import com.wealthvault.fogetpassword.usecase.ResetPasswordUseCase
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
