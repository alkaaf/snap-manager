package id.smartlink.snapmanager.Domain.Home.Activity

import GetTransaksiQuery
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.Domain.Home.Adapter.AdapterHome
import id.smartlink.snapmanager.R
import id.smartlink.snapmanager.Utils.Apollo
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.toast

class ActivityHome : ActivityBase() {
    lateinit var adapter: AdapterHome
    var data: MutableList<HomeBean> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.title = "Daftar Ruangan"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        initView()
        initData()
    }

    fun initData() {
        swipe.isRefreshing = true
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback {
            override fun onSuccess(homeBeans: MutableList<HomeBean>?) {
                swipe.isRefreshing = false
                data.clear()
                homeBeans?.forEach {
                    data.add(it)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onError(errorCode: String?, error: String?) {
                swipe.isRefreshing = false
                toast("$error")
            }
        })
    }

    fun initView() {
        adapter = AdapterHome(data)
        addDisposable(adapter.ps.subscribe {
            ActivityDeviceList.start(act, it.d.homeId)
        })
        rvHome.adapter = adapter
        rvHome.layoutManager = LinearLayoutManager(ctx)
        rvHome.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        swipe.onRefresh {
            initData()
        }
        fabAddHome.onClick {
            var ap = Apollo.getApollo()
            ap.query(GetTransaksiQuery.builder().build()).enqueue(object : ApolloCall.Callback<GetTransaksiQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    e.printStackTrace()
                }

                override fun onResponse(response: Response<GetTransaksiQuery.Data>) {
                    var data = response.data()?.transaksi()
                    var d = data
                }
            })
        }
    }
}