package yb.com.vocieRecoder

import android.util.Log
import java.util.*

object RecoderConfig {

    const val DB_VERSION = 1
    const val CSV_NAME = "recordInfo.csv"
    const val DB_NAME = "china_db"

    const val TRAINING_TABLE = "training_table"


    const val REQUEST_AUDIO_PERMISSION = 1000



    const val DELAY_TIME_SEC: Long = 1000
    const val DELAY_TIME_HSK: Int = (8 * 60 * DELAY_TIME_SEC).toInt()
    const val DELAY_TIME_LISTEN: Int = (5 * 60 * DELAY_TIME_SEC).toInt()




}