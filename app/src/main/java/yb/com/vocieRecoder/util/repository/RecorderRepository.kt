package yb.com.vocieRecoder.util.repository

class RecorderRepository {


    companion object {
        // For Singleton instantiation.
        @Volatile
        private var instance: RecorderRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: RecorderRepository()
                        .also { instance = it }
            }
    }
}