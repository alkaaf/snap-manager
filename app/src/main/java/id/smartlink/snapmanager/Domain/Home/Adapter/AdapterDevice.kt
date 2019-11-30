package id.smartlink.snapmanager.Domain.Home.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tuya.smart.sdk.bean.DeviceBean
import id.smartlink.snapmanager.Base.BaseRecyclerAdapter
import id.smartlink.snapmanager.R
import kotlinx.android.synthetic.main.row_device.view.*

class AdapterDevice(data: MutableList<DeviceBean>) :
    BaseRecyclerAdapter<DeviceBean, RecyclerView.ViewHolder>(data) {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.row_device,
                parent,
                false
            )
        ) {
            init {
                itemView.setOnClickListener {
                    ps.onNext(AdapterBundle(itemView, data[adapterPosition], adapterPosition))
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var v = holder.itemView
        var d = data[position]
        v.tvDevName.text = d.name
        if (d.getIsOnline()) {
            v.tvStatus.text = "ONLINE"
            v.tvStatus.setTextColor(ContextCompat.getColor(context,R.color.green_600))
        } else {
            v.tvStatus.text = "OFFLINE"
            v.tvStatus.setTextColor(ContextCompat.getColor(context,R.color.red_600))
        }
    }
}