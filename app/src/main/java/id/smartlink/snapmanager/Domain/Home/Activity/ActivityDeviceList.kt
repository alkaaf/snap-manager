package id.smartlink.snapmanager.Domain.Home.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.api.ITuyaHome
import com.tuya.smart.home.sdk.api.ITuyaHomeDeviceStatusListener
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.sdk.bean.DeviceBean
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.Domain.Home.Adapter.AdapterDevice
import id.smartlink.snapmanager.R
import kotlinx.android.synthetic.main.activity_device.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.onRefresh

class ActivityDeviceList : ActivityBase() {
    var homeId = 0L
    var list = ArrayList<DeviceBean>()
    var adapter = AdapterDevice(list)
    lateinit var homeInstance: ITuyaHome
    var listener = object:ITuyaHomeDeviceStatusListener{
        override fun onDeviceStatusChanged(devId: String?, online: Boolean) {
            adapter.notifyDataSetChanged()
        }

        override fun onDeviceInfoUpdate(devId: String?) {
            adapter.notifyDataSetChanged()
        }

        override fun onDeviceDpUpdate(devId: String?, dpStr: String?) {
            adapter.notifyDataSetChanged()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        homeId = intent.getLongExtra(HOME_ID, 0L)
        homeInstance = TuyaHomeSdk.newHomeInstance(homeId)
        homeInstance.registerHomeDeviceStatusListener(listener)

        initView()
        setupTitle()
    }

    override fun onDestroy() {
        super.onDestroy()
        homeInstance.unRegisterHomeDeviceStatusListener(listener)
    }

    fun setupTitle() {
        supportActionBar.apply {
            //            this?.title = "${homeInstance.homeBean.name}"
            this?.title = "Device List"
        }
    }

    fun initView() {

        rvDevice.adapter = adapter
        rvDevice.layoutManager = LinearLayoutManager(this)
        rvDevice.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        addDisposable(adapter.ps.subscribe {
            var dev = TuyaHomeSdk.getDataInstance().getDeviceBean(it.d.devId)
            if (dev.isOnline){
                ActivityDeviceCommand.start(act, dev.devId, homeId)
            } else {
                toast("Device is offline")
            }
        })
        swipe.onRefresh {
            initData()
        }
        fabAddDevice.onClick {
            ActivityDeviceAdd.start(this@ActivityDeviceList, homeId)
        }
    }

    override fun onStart() {
        super.onStart()
        initData()
    }

    fun initData() {
        swipe.isRefreshing = true
        homeInstance.getHomeDetail(object : ITuyaHomeResultCallback {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 0, 0, "Change home name")
        menu?.add(0, 1, 0, "Delete home")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var e = EditText(ctx)
        when (item?.itemId) {
            0 -> {
                alert {
                    title = "Enter new name"
                    customView {
                        verticalLayout {
                            padding = dip(16)
                            e = editText(homeInstance.homeBean.name)
                        }
                    }
                    okButton {
                        changeHome(e.text.toString())
                    }
                }.show()
            }
            1 -> {
                alert {
                    title = "Deleting home"
                    message = "Are you sure want to delete this home?"
                    yesButton {
                        deleteHome()
                    }
                    noButton {
                        null
                    }
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun deleteHome() {
        swipe.isRefreshing = true
        TuyaHomeSdk.newHomeInstance(homeId).dismissHome(object : IResultCallback {
            override fun onSuccess() {
                swipe.isRefreshing = false
                toast("Home deleted")
                finish()
            }

            override fun onError(code: String?, error: String?) {
                swipe.isRefreshing = false
                toast("$code $error")
                initData()
                setupTitle()
            }
        })
    }

    fun changeHome(name: String) {
        swipe.isRefreshing = true
        homeInstance.updateHome(name, 0.0, 0.0, null, object : IResultCallback {
            override fun onSuccess() {
                swipe.isRefreshing = false
                initData()
                setupTitle()
            }

            override fun onError(code: String?, error: String?) {
                swipe.isRefreshing = false
                toast("$code $error")
                initData()
                setupTitle()
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