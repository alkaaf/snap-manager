package id.smartlink.snapmanager.Domain.Authentication.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tuya.smart.android.user.api.ILoginCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.Domain.Home.Activity.ActivityHome
import id.smartlink.snapmanager.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

class ActivityLogin : ActivityBase() {
    val TAG = "snap_login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initData()
        initView()
    }

    fun initData() {

    }

    fun initView() {
        bLogin.onClick {
            if (iUserId.text.toString().isEmpty() || iPassword.text.toString().isEmpty()) {
                toast("Username dan password kosong")
            } else {
                login(
                    iCountryCode.text.toString(),
                    iUserId.text.toString(),
                    iPassword.text.toString()
                )
            }
        }
    }

    fun login(countryCode: String, user: String, pass: String) {
        showLoading()
        TuyaHomeSdk.getUserInstance()
            .loginWithPhonePassword(countryCode, user, pass, object : ILoginCallback {
                override fun onSuccess(user: User?) {
                    Log.i(TAG, "Login success")
                    startActivity(Intent(ctx, ActivityHome::class.java))
                    hideLoading()
                    finish()
                }

                override fun onError(code: String?, error: String?) {
                    toast("$error")
                    hideLoading()
                    Log.i(TAG, "$code - $error")
                }
            })
    }
}