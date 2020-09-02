package yb.com.vocieRecoder.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import yb.com.vocieRecoder.model.TrainingEntity

import io.reactivex.Flowable
import io.reactivex.Single
import yb.com.vocieRecoder.RecoderConfig

@Dao
interface TrainingDao {

    @Query("select * from ${RecoderConfig.TRAINING_TABLE}")
    fun getDatas(): Flowable<List<TrainingEntity>>

//

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedia(entity: List<TrainingEntity>): Single<List<Long>>

}