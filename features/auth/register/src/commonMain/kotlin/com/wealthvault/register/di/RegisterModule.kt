
import com.wealthvault.register.data.RegisterDataSource
import com.wealthvault.register.data.RegisterRepositoryImpl
import com.wealthvault.register.ui.RegisterScreenModel
import com.wealthvault.register.usecase.RegisterUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

object RegisterModule {
    val allModules = module {
        factory { RegisterDataSource(get()) }

        single<RegisterRepositoryImpl> {
            RegisterRepositoryImpl(
                networkDataSource = get(),
            )
        }
        single { Dispatchers.IO }

        factory { RegisterUseCase(get(), get()) }

        factory { RegisterScreenModel(get()) }
    }
}
