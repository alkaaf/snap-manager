package id.smartlink.snapmanager.Domain.Home.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.ArrayAdapter
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.api.ITuyaActivator
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener
import com.tuya.smart.sdk.bean.DeviceBean
import com.tuya.smart.sdk.enums.ActivatorEZStepCode
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.Domain.Home.Model.DevActivator
import id.smartlink.snapmanager.R
import kotlinx.android.synthetic.main.activity_device_setup.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ActivityDeviceSetup : ActivityBase() {
    lateinit var setupData: SetupData
    lateinit var adapter: ArrayAdapter<String>
    var logs = ArrayList<String>()
    var activator: ITuyaActivator? = null

    enum class Log {
        ERROR,
        INFO,
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_setup)
        supportActionBar?.title = "Setup Perangkat"
        setupData = intent.getParcelableExtra(INTENT_DATA)
        initView()
        initSetup()
    }

    fun addLog(type: Log, msg: String) {
        logs.add(0, "[$type] $msg")
        adapter.notifyDataSetChanged()
    }

    fun initSetup() {
        // get token
        bStart.isEnabled = false
        addLog(Log.INFO, "Getting token")
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(setupData.homeId, object : ITuyaActivatorGetToken {
            override fun onSuccess(token: String?) {
                addLog(Log.INFO, "Got token")
                if (token != null) {
                    activateDevice(token)
                } else {
                    addLog(Log.ERROR, "Failed to get token")
                }
            }

            override fun onFailure(errorCode: String?, errorMsg: String?) {
                addLog(Log.ERROR, "$errorCode: $errorMsg")
                bStart.isEnabled = true
            }
        })
    }

    fun activateDevice(token: String) {
        var dev = DevActivator(token, setupData.ssid, setupData.pass)
        addLog(Log.INFO, "Mencari perangkat")
        activator = dev.connectEZ(ctx, object : ITuyaSmartActivatorListener {
            override fun onStep(step: String?, data: Any?) {
                when (step) {
                    ActivatorEZStepCode.DEVICE_BIND_SUCCESS -> {
                        addLog(Log.INFO, "Koneksi perangkat sukses")
                    }
                    ActivatorEZStepCode.DEVICE_FIND -> {
                        addLog(Log.INFO, "Perangkat ditemukan")
                    }
                }
            }

            override fun onActiveSuccess(devResp: DeviceBean?) {
                addLog(Log.INFO, "Perangkat berhasil ditambahkan")
            }

            override fun onError(errorCode: String?, errorMsg: String?) {
                addLog(Log.ERROR, "$errorCode: $errorMsg")
                bStart.isEnabled = true
            }
        })
        activator?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        activator?.stop()
    }

    fun initView() {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, logs)
        lvLog.adapter = adapter
        bStart.onClick {
            initSetup()
        }
        bFinish.onClick {
            activator?.stop()
            bStart.isEnabled = true
            finish()
        }
    }

    companion object {
        data class SetupData(var homeId: Long, var ssid: String, var pass: String) : Parcelable {
            constructor(source: Parcel) : this(
                source.readLong(),
                source.readString(),
                source.readString()
            )

            override fun describeContents() = 0

            override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
                writeLong(homeId)
                writeString(ssid)
                writeString(pass)
            }

            companion object {
                @JvmField
                val CREATOR: Parcelable.Creator<SetupData> = object : Parcelable.Creator<SetupData> {
                    override fun createFromParcel(source: Parcel): SetupData = SetupData(source)
                    override fun newArray(size: Int): Array<SetupData?> = arrayOfNulls(size)
                }
            }
        }

        val INTENT_DATA = "setup.intent.data"
        fun start(act: Activity, setupData: SetupData) {
            act.startActivity(Intent(act, ActivityDeviceSetup::class.java).apply {
                putExtra(INTENT_DATA, setupData)
            })
        }
    }
}