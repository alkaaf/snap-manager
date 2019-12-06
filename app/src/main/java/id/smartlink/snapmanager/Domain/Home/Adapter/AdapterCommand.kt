package id.smartlink.snapmanager.Domain.Home.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tuya.smart.android.device.bean.BoolSchemaBean
import com.tuya.smart.android.device.bean.SchemaBean
import com.tuya.smart.android.device.bean.ValueSchemaBean
import com.tuya.smart.android.device.enums.ModeEnum
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.api.IDevListener
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.sdk.api.ITuyaDevice
import com.tuya.smart.sdk.bean.DeviceBean
import id.smartlink.snapmanager.Base.BaseRecyclerAdapter
import id.smartlink.snapmanager.R
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.toast

class AdapterCommand(data: MutableList<SchemaBean>, var dev: DeviceBean) :
    BaseRecyclerAdapter<SchemaBean, RecyclerView.ViewHolder>(data) {
    lateinit var context: Context
    var iDev: ITuyaDevice = TuyaHomeSdk.newDeviceInstance(dev.devId)
    var listenerPs = PublishSubject.create<String>()

    companion object {
        val TYPE_BOOLEAN = 1
        val TYPE_VALUE = 2
    }

    init {

    }

    fun registerListener() {
        iDev.registerDevListener(object : IDevListener {
            override fun onDpUpdate(devId: String?, dpStr: String?) {
                notifyDataSetChanged()
                listenerPs.onNext(
                    "Got update\n" +
                            "$devId : $dpStr\n"
                )
            }

            override fun onStatusChanged(devId: String?, online: Boolean) {
                notifyDataSetChanged()
                listenerPs.onNext(
                    "Got status change\n" +
                            "$devId : $online\n"
                )
            }

            override fun onRemoved(devId: String?) {
                notifyDataSetChanged()
                listenerPs.onNext(
                    "Got device remoted\n" +
                            "$devId : removed\n"
                )
            }

            override fun onDevInfoUpdate(devId: String?) {
                notifyDataSetChanged()
            }

            override fun onNetworkStatusChanged(devId: String?, status: Boolean) {
                notifyDataSetChanged()
            }
        })
    }

    fun unregisterListener() {
        iDev.unRegisterDevListener()
    }

    override fun getItemViewType(position: Int): Int {
        when (data[position].getSchemaType()) {
            ValueSchemaBean.type -> {
                return TYPE_VALUE
            }
            BoolSchemaBean.type -> {
                return TYPE_BOOLEAN
            }
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        when (viewType) {
            TYPE_VALUE -> {
                return ViewHolderValue(
                    LayoutInflater.from(context).inflate(
                        R.layout.row_command_value,
                        parent,
                        false
                    )
                )
            }
            TYPE_BOOLEAN -> {
                return ViewHolderBoolean(
                    LayoutInflater.from(context).inflate(
                        R.layout.row_command_bool,
                        parent,
                        false
                    )
                )
            }
        }
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.row_device_command,
                parent,
                false
            )
        ) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var d = data[position]
        var readOnly = d.getMode().equals(ModeEnum.RO.type)
        when (holder) {
            is ViewHolderValue -> {
                var h = holder
                h.tvCode.text = d.code
                h.tvInfo.text = "${d.getMode()} | ${d.schemaType}"
                h.iValue.setText((dev.dps.get(d.getId()) as Int).toString())
                h.tvValue.text = (dev.dps.get(d.getId()) as Int).toString()
                if (!readOnly) {
                    h.tvValue.visibility = View.GONE
                    h.iValue.visibility = View.VISIBLE
                    h.itemView.setOnClickListener {

                    }
                } else {
                    h.tvValue.visibility = View.VISIBLE
                    h.iValue.visibility = View.GONE
                    h.itemView.setOnClickListener {
                        iDev.getDp(d.getId(), object : IResultCallback {
                            override fun onSuccess() {
//                                context.toast("Inquiry success")
                            }

                            override fun onError(code: String?, error: String?) {
                                context.toast("Inquiry failed")
                            }
                        })
                    }
                }
            }
            is ViewHolderBoolean -> {
                var h = holder
                h.tvCode.text = d.code
                h.tvInfo.text = "${d.getMode()} | ${d.schemaType}"
                h.sbBool.isChecked = dev.dps.get(d.getId()) as Boolean
            }
        }
    }

    inner class ViewHolderBoolean(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCode: TextView
        var tvInfo: TextView
        var sbBool: Switch

        init {
            tvCode = itemView.findViewById(R.id.tvCode)
            tvInfo = itemView.findViewById(R.id.tvInfo)
            sbBool = itemView.findViewById(R.id.sbBool)
            sbBool.setOnClickListener {
                iDev.publishDps(Gson().toJson(mapOf("1" to sbBool.isChecked)), object : IResultCallback {
                    override fun onSuccess() {
                    }

                    override fun onError(code: String?, error: String?) {
                        notifyDataSetChanged()
                        context.toast("$code $error")
                    }
                })
            }
        }
    }

    inner class ViewHolderValue(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCode: TextView
        var tvInfo: TextView
        var tvValue: TextView
        var iValue: EditText

        init {
            tvCode = itemView.findViewById(R.id.tvCode)
            tvInfo = itemView.findViewById(R.id.tvInfo)
            tvValue = itemView.findViewById(R.id.tvValue)
            iValue = itemView.findViewById(R.id.iValue)
        }
    }

}