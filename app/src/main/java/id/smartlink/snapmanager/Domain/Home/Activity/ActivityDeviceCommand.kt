package id.smartlink.snapmanager.Domain.Home.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.android.device.bean.SchemaBean
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.bean.DeviceBean
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.Domain.Home.Adapter.AdapterCommand
import id.smartlink.snapmanager.R
import kotlinx.android.synthetic.main.activity_device_command.*
import org.jetbrains.anko.support.v4.onRefresh

class ActivityDeviceCommand : ActivityBase() {
    var data = ArrayList<SchemaBean>()
    lateinit var adapter : AdapterCommand
    lateinit var dev: DeviceBean
    lateinit var devId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_command)
        initView()
        initData()

    }

    fun initData() {
        devId = intent.getStringExtra(INTENT_DEV_ID)
        dev = TuyaHomeSdk.getDataInstance().getDeviceBean(devId)
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

    companion object {
        val INTENT_DEV_ID = "intent.dev.id"
        fun start(act: Activity, devId: String) {
            act.startActivity(Intent(act, ActivityDeviceCommand::class.java).apply {
                putExtra(INTENT_DEV_ID, devId)
            })
        }
    }
}