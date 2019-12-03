package id.smartlink.snapmanager.Domain.Home.Model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.builder.ActivatorBuilder
import com.tuya.smart.sdk.api.ITuyaActivator
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener
import com.tuya.smart.sdk.bean.DeviceBean
import com.tuya.smart.sdk.enums.ActivatorModelEnum

class DevActivator(var activatorToken: String, var ssid: String, var pass: String) : Parcelable {
    fun connectAP(context: Context, callback: ITuyaSmartActivatorListener): ITuyaActivator? {
        return TuyaHomeSdk.getActivatorInstance().newMultiActivator(
            ActivatorBuilder()
                .setContext(context)
                .setSsid(ssid)
                .setPassword(pass)
                .setActivatorModel(ActivatorModelEnum.TY_AP)
                .setTimeOut(TIMEOUT)
                .setToken(activatorToken)
                .setListener(callback)
        )
    }

    fun connectEZ(context: Context, callback: ITuyaSmartActivatorListener): ITuyaActivator? {
        return TuyaHomeSdk.getActivatorInstance().newMultiActivator(
            ActivatorBuilder()
                .setContext(context)
                .setSsid(ssid)
                .setPassword(pass)
                .setActivatorModel(ActivatorModelEnum.TY_EZ)
                .setTimeOut(TIMEOUT)
                .setToken(activatorToken)
                .setListener(callback)
        )
    }

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(activatorToken)
        writeString(ssid)
        writeString(pass)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DevActivator> = object : Parcelable.Creator<DevActivator> {
            override fun createFromParcel(source: Parcel): DevActivator = DevActivator(source)
            override fun newArray(size: Int): Array<DevActivator?> = arrayOfNulls(size)
        }
        val TIMEOUT = 100L
    }
}