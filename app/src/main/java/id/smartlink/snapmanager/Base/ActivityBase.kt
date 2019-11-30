package id.smartlink.snapmanager.Base

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class ActivityBase : AppCompatActivity() {
    lateinit var ctx: Context
    lateinit var act: Activity
    lateinit var pd: ProgressDialog
    var cd = CompositeDisposable()
    public fun showLoading() {
        pd.show()
    }

    public fun hideLoading() {
        pd.dismiss()
    }

    public fun loadingMsg(msg: String) {
        pd.setMessage(msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = this
        act = this
        pd = ProgressDialog(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
    fun addDisposable(d:Disposable){
        cd.add(d)
    }

    fun clearDisposable(){
        cd.clear()
    }

}