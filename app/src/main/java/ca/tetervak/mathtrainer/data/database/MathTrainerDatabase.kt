package ca.tetervak.mathtrainer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.tetervak.mathtrainer.data.database.converter.DateConverter
import ca.tetervak.mathtrainer.data.database.dao.ProblemDao
import ca.tetervak.mathtrainer.data.database.dao.QuizDao
import ca.tetervak.mathtrainer.data.database.dao.UserDao
import ca.tetervak.mathtrainer.data.database.entity.ProblemEntity
import ca.tetervak.mathtrainer.data.database.entity.QuizEntity
import ca.tetervak.mathtrainer.data.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, QuizEntity::class, ProblemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class MathTrainerDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val quizDao: QuizDao
    abstract val problemDao: ProblemDao
}