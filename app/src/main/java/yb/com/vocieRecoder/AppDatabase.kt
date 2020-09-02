package yb.com.vocieRecoder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import yb.com.vocieRecoder.model.TrainingEntity
import yb.com.vocieRecoder.RecoderApplication.Companion.getGlobalContext
import yb.com.vocieRecoder.dao.TrainingDao
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


@Database(
        entities = [TrainingEntity::class],
        version = RecoderConfig.DB_VERSION
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase = instance
                ?: synchronized(this) {
                    instance ?: Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, RecoderConfig.DB_NAME
                    ).build()
                }

        fun deleteInstance() {
            instance = null
        }

        /**
         * 중요
         * 일부 데이터에 ,로 처리된 항목이 있어 ;를 통해 구분자 처리
         */
        fun ConvertCSV(): ArrayList<TrainingEntity> {
            val am = getGlobalContext().assets
            val list = ArrayList<TrainingEntity>()
            val `is`: InputStream = am.open(RecoderConfig.CSV_NAME)
            val reader = BufferedReader(
                    InputStreamReader(`is`, Charset.forName("UTF-8"))
            )

            try {
                reader.readLine()

                reader.readLines().forEach {
                    val item = it.split(";".toRegex()).toTypedArray()
                    val name = TrainingEntity(category = item[0], pinyin = item[1], word = item[2], mean = item[3], recordTime = item[4].toInt(), mediaFile = item[5])
                    list.add(name)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return list
        }

    }
}