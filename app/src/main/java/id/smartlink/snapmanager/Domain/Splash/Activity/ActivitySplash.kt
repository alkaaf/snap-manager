package id.smartlink.snapmanager.Domain.Splash.Activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import com.tuya.smart.home.sdk.TuyaHomeSdk
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.Domain.Authentication.Activity.ActivityLogin
import id.smartlink.snapmanager.Domain.Home.Activity.ActivityHome
import id.smartlink.snapmanager.R

class ActivitySplash : ActivityBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkLogin()
    }

    fun checkLogin() {
        if (TuyaHomeSdk.getUserInstance().isLogin()) {
            startActivity(Intent(ctx, ActivityHome::class.java))
        } else {
            startActivity(Intent(ctx, ActivityLogin::class.java))
        }
        finish()
    }
}