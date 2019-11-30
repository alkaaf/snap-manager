package id.smartlink.snapmanager.Domain.Home.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.sdk.bean.DeviceBean
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.Domain.Home.Adapter.AdapterDevice
import id.smartlink.snapmanager.R
import kotlinx.android.synthetic.main.activity_device.*
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.toast

class ActivityDeviceList : ActivityBase() {
    var homeId = 0L
    var list = ArrayList<DeviceBean>()
    var adapter = AdapterDevice(list)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        supportActionBar.apply {
            title = "Daftar Alat"
        }
        initView()
        initData()
    }

    fun initView() {
        rvDevice.adapter = adapter
        rvDevice.layoutManager = LinearLayoutManager(this)
        rvDevice.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        addDisposable(adapter.ps.subscribe {
            var dev = TuyaHomeSdk.getDataInstance().getDeviceBean(it.d.devId)
            ActivityDeviceCommand.start(act, dev.devId)
        })
        swipe.onRefresh {
            initData()
        }
    }

    fun initData() {
        swipe.isRefreshing = true
        homeId = intent.getLongExtra(HOME_ID, 0L)
        TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(object : ITuyaHomeResultCallback {
            override fun onSuccess(bean: HomeBean?) {
                swipe.isRefreshing = false
                list.clear()
                bean?.deviceList?.forEach {
                    list.add(it)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onError(errorCode: String?, errorMsg: String?) {
                swipe.isRefreshing = false
                if (errorMsg != null) {
                    toast("$errorMsg")
                }
            }
        })
    }

    companion object {
        val HOME_ID = "home_id"
        fun start(act: Activity, homeId: Long) {
            act.startActivity(Intent(act, ActivityDeviceList::class.java).apply {
                putExtra(HOME_ID, homeId)
            })
        }
    }
}