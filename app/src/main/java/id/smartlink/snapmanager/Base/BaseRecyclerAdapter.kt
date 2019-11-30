package id.smartlink.snapmanager.Base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.subjects.PublishSubject

open abstract class BaseRecyclerAdapter<T, VH : RecyclerView.ViewHolder>(var data: MutableList<T>) :
    RecyclerView.Adapter<VH>() {

    var ps = PublishSubject.create<AdapterBundle>()

    inner class AdapterBundle(var v: View,var d: T,var pos: Int)

    override fun getItemCount(): Int {
        return data.size
    }
}