
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
//        single<LoginApi> {
//            val retrofit: Retrofit = get() // ดึง Retrofit จาก Core Module
//            retrofit.create(LoginApi::class.java)
//        }

        // ประกาศ Network DataSource (Internal class ใน Feature)
        factory { AuthNetworkDataSource(get()) }

        // 2. Storage
        // ประกาศ TokenStore (ที่จัดการ DataStore)
        single<TokenStore> { TokenStore(get()) }

        // 3. Repository
        // เชื่อมต่อ Network + Local เข้าด้วยกัน
        single<AuthRepositoryImpl> {
            AuthRepositoryImpl(
                networkDataSource = get(),
                localDataSource = get()
            )
        }
        single { Dispatchers.IO }
        // 4. UseCases
        // สำหรับหน้า Login และการเช็คสถานะ Auth
        factory { LoginUseCase(get(),get(),get() )}
//        factory { ObserveAuthStateUseCase(get()) }
//
//        // 5. ScreenModels (สำหรับ Voyager)
        factory { LoginScreenModel(get()) }
//        factory { RootScreenModel(get()) }
    }
}
