package yb.com.vocieRecoder.model.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import yb.com.vocieRecoder.RecoderConfig

@Keep
@Entity(tableName = RecoderConfig.TRAINING_TABLE)
data class TrainingEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int = 0,
        @ColumnInfo(name = "category")
        var category: String,
        // 병음
        @ColumnInfo(name = "pinyin")
        var pinyin: String,
        @ColumnInfo(name = "word")
        // 중국어
        var word: String,
        // 한국어
        @ColumnInfo(name = "mean")
        var mean: String,
        @ColumnInfo(name = "recordTime")
        var recordTime: Int,
        @ColumnInfo(name = "mediaFile")
        var mediaFile: String,
        @ColumnInfo(name = "isRecord")
        var isRecord: Boolean = false
)