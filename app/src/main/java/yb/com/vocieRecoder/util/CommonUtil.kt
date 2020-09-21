package yb.com.vocieRecoder.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

import java.io.*
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


object CommonUtil {
    fun getPrivateMusicStorageDir(context: Context, path: String): File? {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), path)
        return file
    }

    fun copyStreamToFile(file: File, input: InputStream) {
        try {
            val output = FileOutputStream(file)
            try {
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read = 0

                do {
                    output.write(buffer, 0, read)

                    read = input.read(buffer)
                } while (read != -1)

                output.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                output.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getTimeStamp(duration: String): String {
        val time: String
        val dur = duration.toLong()
        val seconds = ((dur % 60)).toString()

        val minutes = (dur / 60).toString()
        time = if (seconds.length == 1) {
            "0$minutes:0$seconds"
        } else {
            "0$minutes:$seconds"
        }
        return time
    }
}
