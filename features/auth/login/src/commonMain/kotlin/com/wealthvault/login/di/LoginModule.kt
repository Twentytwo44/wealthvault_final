
import com.example.login.ui.LoginScreenModel
import com.wealthvault.data_store.TokenStore
import com.wealthvault.login.data.AuthNetworkDataSource
import com.wealthvault.login.data.AuthRepositoryImpl
import com.wealthvault.login.usecase.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

object LoginModule {
    val allModules = module {

        factory { AuthNetworkDataSource(get()) }

        single<TokenStore> { TokenStore(get()) }


        single<AuthRepositoryImpl> {
            AuthRepositoryImpl(
                networkDataSource = get(),
                localDataSource = get()
            )
        }
        single { Dispatchers.IO }

        factory { LoginUseCase(get(),get(),get() )}

        factory { LoginScreenModel(get(),get()) }
    }
}
