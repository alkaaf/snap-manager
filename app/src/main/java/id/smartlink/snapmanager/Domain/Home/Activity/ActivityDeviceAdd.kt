package id.smartlink.snapmanager.Domain.Home.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.R
import kotlinx.android.synthetic.main.activity_device_add.*

class ActivityDeviceAdd : ActivityBase() {
    var homeId: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_add)
        supportActionBar?.title = "Tambah perangkat"
        homeId = intent.getLongExtra(HOME_ID_DATA, 0L)
        initView()
    }

    fun initView() {
        bSetupWifi.setOnClickListener {
            ActivityDeviceWifiSetup.start(act, homeId)
            finish()
        }
    }

    companion object {
        val HOME_ID_DATA = "home_id_data"
        fun start(act: Activity, homeId: Long) {
            act.startActivity(Intent(act, ActivityDeviceAdd::class.java).apply {
                putExtra(HOME_ID_DATA, homeId)
            })
        }
    }
}