package yb.com.vocieRecoder

import androidx.multidex.MultiDexApplication

class RecoderApplication : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        instance = this
        AppDatabase.getInstance(this)
    }


    companion object {

        private lateinit var instance: RecoderApplication
        /**
         * singleton 애플리케이션 객체를 얻는다.
         *
         * @return singleton 애플리케이션 객체
         */
        @JvmStatic
        fun getGlobalContext(): RecoderApplication {
            return instance
        }
    }
}