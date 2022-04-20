package ua.cn.stu.navigation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import ua.cn.stu.navigation.MainActivity.Companion.connectedDevice
import ua.cn.stu.navigation.MainActivity.Companion.connectedDeviceAddress
import ua.cn.stu.navigation.MainActivity.Companion.tupOnList
import ua.cn.stu.navigation.R
import ua.cn.stu.navigation.contract.*
import ua.cn.stu.navigation.databinding.FragmentScanningBinding
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys
import ua.cn.stu.navigation.recyclers_adapters.ScanningAdapter
import ua.cn.stu.navigation.rx.RxUpdateMainEvent

class ScanningFragment : Fragment(), HasCustomTitle, HasReturnAction {

    private lateinit var binding: FragmentScanningBinding
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: ScanningAdapter? = null

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentScanningBinding.inflate(inflater, container, false)

        RxUpdateMainEvent.getInstance().scanListObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { addScanListItem() }

        initAdapter(binding.scanningRv)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addScanListItem() {
        adapter?.notifyItemChanged(adapter!!.itemCount-1)
    }

    private fun initAdapter(profile_rv: RecyclerView) {
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        profile_rv.layoutManager = linearLayoutManager
        adapter = ScanningAdapter(object : OnScanClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClicked(name: String, address: String) {
                println("tup scan name = $name    address = $address")
                connectedDevice = name
                connectedDeviceAddress = address
                navigator().saveString(PreferenceKeys.CONNECTES_DEVICE, connectedDevice)
                navigator().saveString(PreferenceKeys.CONNECTES_DEVICE_ADDRESS, connectedDeviceAddress)
                navigator().scanLeDevice(false)
                tupOnList = true
                onCancelPressed()
            }
        })
        profile_rv.adapter = adapter
    }



    override fun getTitleRes(): String = getString(R.string.scanning_ble_device)
    override fun getReturnAction(): ReturnAction {
        return ReturnAction(
            onReturnAction = {
                onCancelPressed()
            }
        )
    }

    private fun onCancelPressed() {
        navigator().scanLeDevice(false)
        navigator().goBack()
    }
}