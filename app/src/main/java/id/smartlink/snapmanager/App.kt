package id.smartlink.snapmanager

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.tuya.smart.sdk.TuyaSdk
import id.smartlink.snapmanager.Domain.Authentication.Activity.ActivityLogin
import io.paperdb.Paper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
        TuyaSdk.init(this)
        TuyaSdk.setOnNeedLoginListener {
            val intent = Intent(it, ActivityLogin::class.java)
            if (it!is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }
}