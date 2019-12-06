package id.smartlink.snapmanager.Domain.Home.Activity

import android.os.Bundle
import android.widget.EditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import id.smartlink.snapmanager.Base.ActivityBase
import id.smartlink.snapmanager.Domain.Home.Adapter.AdapterHome
import id.smartlink.snapmanager.R
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.onRefresh

class ActivityHome : ActivityBase() {
    lateinit var adapter: AdapterHome
    var data: MutableList<HomeBean> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.title = "Home List"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        initView()
//        initData()
    }

    override fun onStart() {
        super.onStart()
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
            var e = EditText(ctx)
            alert {
                title = "Enter new name"
                customView {
                    verticalLayout {
                        padding = dip(16)
                        e = editText {
                            hint = "Sarah's Home"
                        }
                    }
                }
                okButton {
                    createHome(e.text.toString())
                }
            }.show()
        }
    }

    fun createHome(name: String) {
        showLoading()
        TuyaHomeSdk.getHomeManagerInstance().createHome(name, 0.0, 0.0, name, ArrayList<String>(), object : ITuyaHomeResultCallback {
            override fun onSuccess(bean: HomeBean?) {
                hideLoading()
                initData()
            }

            override fun onError(errorCode: String?, errorMsg: String?) {
                hideLoading()
                toast("$errorCode $errorMsg")
            }
        })
    }
}