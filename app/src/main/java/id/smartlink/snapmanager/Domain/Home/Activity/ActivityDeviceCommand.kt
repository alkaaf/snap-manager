package id.smartlink.snapmanager.Domain.Home.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.android.device.bean.SchemaBean
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.bean.DeviceBean
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.Domain.Home.Adapter.AdapterCommand
import id.smartlink.snapmanager.R
import kotlinx.android.synthetic.main.activity_device_command.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.onRefresh
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActivityDeviceCommand : ActivityBase() {
    var data = ArrayList<SchemaBean>()
    lateinit var adapter: AdapterCommand
    lateinit var dev: DeviceBean
    lateinit var devId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_command)
        devId = intent.getStringExtra(INTENT_DEV_ID)
        dev = TuyaHomeSdk.getDataInstance().getDeviceBean(devId)
        initView()
        initData()
        setuptitle()
    }

    override fun onStart() {
        super.onStart()
        adapter.registerListener()
        addDisposable(adapter.listenerPs.subscribe({
            tvLog.append(DateFormat.getDateTimeInstance().format(Date())+" > "+it+"\n")
            nsv.fullScroll(View.FOCUS_DOWN)
        }, {

        }))
    }

    override fun onStop() {
        super.onStop()
        adapter.unregisterListener()
    }

    fun setuptitle() {
        supportActionBar.apply {
            this?.title = dev.name
            this?.subtitle = dev.getDevId()
        }
    }

    fun initData() {

        supportActionBar?.title = dev.name
        var dps = TuyaHomeSdk.getDataInstance().getDps(devId)
        adapter = AdapterCommand(data, dev)

        data.clear()
        dev.getSchemaMap().iterator().forEach {
            data.add(it.value)
        }
        data.sortBy {
            it.id
        }
        rvDevCommand.adapter = adapter
        rvDevCommand.layoutManager = LinearLayoutManager(ctx)
        adapter.notifyDataSetChanged()
        swipe.isRefreshing = false
    }

    fun initView() {
        swipe.onRefresh {
            initData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 0, 0, "Delete device").apply {

        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            0 -> {
                alert {
                    title = "Not implemented yet"
                    message = "Reset the device if you want to delete the device from this account"
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun deleteDevice() {
        showLoading()
    }

    companion object {
        val INTENT_DEV_ID = "intent.dev.id"
        val INTENT_HOME_ID = "intent.home.id"
        fun start(act: Activity, devId: String, homeId: Long) {
            act.startActivity(Intent(act, ActivityDeviceCommand::class.java).apply {
                putExtra(INTENT_DEV_ID, devId)
                putExtra(INTENT_HOME_ID, homeId)
            })
        }
    }
}