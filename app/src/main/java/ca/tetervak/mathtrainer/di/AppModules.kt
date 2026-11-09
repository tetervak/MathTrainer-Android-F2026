package ca.tetervak.mathtrainer.di

import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import ca.tetervak.mathtrainer.data.database.MathTrainerDatabase
import ca.tetervak.mathtrainer.data.database.dao.ProblemDao
import ca.tetervak.mathtrainer.data.database.dao.QuizDao
import ca.tetervak.mathtrainer.data.database.dao.UserDao
import ca.tetervak.mathtrainer.data.factory.AlgebraProblemFactory
import ca.tetervak.mathtrainer.data.remote.RandomQuizApi
import ca.tetervak.mathtrainer.data.repository.PreferencesRepositoryDataStore
import ca.tetervak.mathtrainer.data.repository.QuizGenerationRepository
import ca.tetervak.mathtrainer.data.repository.QuizGenerationRepositoryLocal
import ca.tetervak.mathtrainer.data.repository.QuizGenerationRepositoryRemote
import ca.tetervak.mathtrainer.data.repository.QuizRepositoryImpl
import ca.tetervak.mathtrainer.data.repository.QuizStorageRepository
import ca.tetervak.mathtrainer.data.repository.QuizStorageRepositoryRoom
import ca.tetervak.mathtrainer.domain.repository.PreferencesRepository
import ca.tetervak.mathtrainer.domain.repository.QuizRepository
import ca.tetervak.mathtrainer.ui.home.HomeViewModel
import ca.tetervak.mathtrainer.ui.problem.details.ProblemDetailsViewModel
import ca.tetervak.mathtrainer.ui.problem.list.ProblemListViewModel
import ca.tetervak.mathtrainer.ui.quiz.details.QuizDetailsViewModel
import ca.tetervak.mathtrainer.ui.quiz.list.QuizListViewModel
import ca.tetervak.mathtrainer.ui.settings.SettingsViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val databaseModule = module {
    single<MathTrainerDatabase> {
        Room.databaseBuilder(
            androidContext(), MathTrainerDatabase::class.java, "multiple_quizzes_db")
            //.addCallback(callback)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single<UserDao> { get<MathTrainerDatabase>().userDao }
    single<QuizDao> { get<MathTrainerDatabase>().quizDao }
    single<ProblemDao> { get<MathTrainerDatabase>().problemDao }
}

private const val USER_PREFERENCES = "user_preferences"
val dataStoreModule =  module {
    single<DataStore<Preferences>> {
        preferencesDataStore(name = USER_PREFERENCES).getValue(androidContext(), String::javaClass)
    }
}

val factoryModule = module {
    single<AlgebraProblemFactory>{ AlgebraProblemFactory() }
}

private const val BASE_URL = "http://10.0.2.2:8080/"
val retrofitModule = module {
    single<Retrofit>{
        Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BASE_URL)
            .build()
    }
    single<RandomQuizApi>{
        get<Retrofit>().create(RandomQuizApi::class.java)
    }
}

val repositoryModule = module {
    single<QuizStorageRepository> { QuizStorageRepositoryRoom(userDao = get(), quizDao = get(), problemDao = get()) }
    single<PreferencesRepository> { PreferencesRepositoryDataStore(dataStore = get()) }
    single<QuizGenerationRepositoryLocal> { QuizGenerationRepositoryLocal(problemFactory = get()) }
    single<QuizGenerationRepositoryRemote> { QuizGenerationRepositoryRemote(randomQuizApi = get()) }
    single<QuizGenerationRepository> {
        QuizGenerationRepository(
            quizGenerationRepositoryLocal = get(),
            quizGenerationRepositoryRemote = get(),
            preferencesRepository = get()
        )
    }
    single<QuizRepository> { QuizRepositoryImpl(quizStorageRepository = get(), randomQuizRepository = get())}
}

val viewModelModule = module {
    viewModel<HomeViewModel> { HomeViewModel(repository = get()) }
    viewModel<ProblemDetailsViewModel> { ProblemDetailsViewModel(repository = get()) }
    viewModel<ProblemListViewModel> { ProblemListViewModel(repository = get()) }
    viewModel<QuizDetailsViewModel> { QuizDetailsViewModel(repository = get()) }
    viewModel<QuizListViewModel> { QuizListViewModel(repository = get()) }
    viewModel<SettingsViewModel> { SettingsViewModel(repository = get()) }
}

val appModules = listOf(databaseModule, dataStoreModule, factoryModule, retrofitModule, repositoryModule, viewModelModule)
