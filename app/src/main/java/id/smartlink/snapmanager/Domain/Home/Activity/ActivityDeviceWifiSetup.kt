package id.smartlink.snapmanager.Domain.Home.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.PAPER_WIFI_SAVE
import id.smartlink.snapmanager.R
import io.paperdb.Book
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_device_wifi_setup.*

class ActivityDeviceWifiSetup : ActivityBase() {
    var homeId = 0L
    lateinit var paper: Book
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_wifi_setup)
        supportActionBar?.title = "Pengaturan WIFI"
        initData()
        initView()
    }

    fun initData() {
        paper = Paper.book(PAPER_WIFI_SAVE)
        homeId = intent.getLongExtra(HOME_ID_DATA, 0L)
    }

    fun initView() {
        var wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var info = wm.connectionInfo
        if (info != null) {
            var ssid = info.ssid
            iSsid.setText(ssid)
            iPass.setText(paper.read(ssid, ""))
        }
    }

    companion object {
        val HOME_ID_DATA = "home_id.data"
        fun start(act: Activity, homeId: Long) {
            act.startActivity(Intent(act, ActivityDeviceAdd::class.java).apply {
                putExtra(HOME_ID_DATA, homeId)
            })
        }
    }
}