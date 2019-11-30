package id.smartlink.snapmanager.Domain.Home.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuya.smart.home.sdk.bean.HomeBean
import id.smartlink.snapmanager.Base.BaseRecyclerAdapter
import id.smartlink.snapmanager.R
import kotlinx.android.synthetic.main.row_home.view.*

class AdapterHome(data: MutableList<HomeBean>) :
    BaseRecyclerAdapter<HomeBean, RecyclerView.ViewHolder>(data) {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.row_home,
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
        v.tvHomeName.text = d.name
    }
}