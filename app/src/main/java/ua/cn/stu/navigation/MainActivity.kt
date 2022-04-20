package ua.cn.stu.navigation

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.os.Parcelable
import android.provider.Settings
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_delete_active_boluses.view.*
import kotlinx.android.synthetic.main.dialog_go_bolus.*
import kotlinx.android.synthetic.main.dialog_info.*
import kotlinx.android.synthetic.main.dialog_info_basal.*
import kotlinx.android.synthetic.main.dialog_instruction_refilling.*
import ua.cn.stu.navigation.ble.BluetoothLeService
import ua.cn.stu.navigation.ble.SampleGattAttributes.*
import ua.cn.stu.navigation.connection.ScanItem
import ua.cn.stu.navigation.contract.*
import ua.cn.stu.navigation.contract.ConstantManager.Companion.ACTIVATE_BASAL_PROFILE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.ACTIVATE_BASAL_PROFILE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.AKB_PERCENT
import ua.cn.stu.navigation.contract.ConstantManager.Companion.AKB_PERCENT_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BALANCE_DRUG
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BALANCE_DRUG_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_LOCK_CONTROL
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_LOCK_CONTROL_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_SPEED
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_SPEED_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_TEMPORARY_PERFORMANCE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_TEMPORARY_PERFORMANCE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_TEMPORARY_TIME
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_TEMPORARY_TIME_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_TEMPORARY_TYPE_ADJUSTMENT
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_TEMPORARY_TYPE_ADJUSTMENT_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_TEMPORARY_VALUE_ADJUSTMENT
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BASAL_TEMPORARY_VALUE_ADJUSTMENT_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BATTERY_PERCENT
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BATTERY_PERCENT_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_ACTIVATE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_ACTIVATE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_AMOUNT
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_AMOUNT_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_DELETE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_DELETE_CONFIRM
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_DELETE_CONFIRM_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_DELETE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_STRECHED_AMOUNT_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_TYPE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.BOLUS_TYPE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.DATE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.DATE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.DELETE_BASAL_PROFILE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.DELETE_BASAL_PROFILE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.EXTENDED_AND_DUAL_PATTERN_BOLUS_TIME_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG
import ua.cn.stu.navigation.contract.ConstantManager.Companion.EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.FAKE_DATA_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.INIT_REFUELLING
import ua.cn.stu.navigation.contract.ConstantManager.Companion.INIT_REFUELLING_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.IOB
import ua.cn.stu.navigation.contract.ConstantManager.Companion.IOB_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.LOG_UPDATE_DEPTH
import ua.cn.stu.navigation.contract.ConstantManager.Companion.MAX_COUNT_PROFILES
import ua.cn.stu.navigation.contract.ConstantManager.Companion.MY_PERMISSIONS_REQUEST_LOCATION
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NAME_BASAL_PROFILE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NAME_BASAL_PROFILE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NUM_ACTIVE_BASAL_PROFILE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NUM_ACTIVE_BASAL_PROFILES_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NUM_BASAL_PROFILES
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NUM_BASAL_PROFILES_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NUM_MODIFIED_BASAL_PROFILES
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NUM_MODIFIED_BASAL_PROFILES_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NUM_PERIODS_MODIFIED_BASAL_PROFILE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.PERIOD_BASAL_PROFILE_DATA
import ua.cn.stu.navigation.contract.ConstantManager.Companion.PERIOD_BASAL_PROFILE_DATA_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.READ_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.RECONNECT_BLE_PERIOD
import ua.cn.stu.navigation.contract.ConstantManager.Companion.REQUEST_ENABLE_BT
import ua.cn.stu.navigation.contract.ConstantManager.Companion.SUPER_BOLUS_BASL_VOLIUM
import ua.cn.stu.navigation.contract.ConstantManager.Companion.SUPER_BOLUS_BASL_VOLIUM_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.SUPER_BOLUS_RESTRICTION_FLAG
import ua.cn.stu.navigation.contract.ConstantManager.Companion.SUPER_BOLUS_RESTRICTION_FLAG_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.SUPER_BOLUS_TIME
import ua.cn.stu.navigation.contract.ConstantManager.Companion.SUPER_BOLUS_TIME_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.SUPPLIES_RSOURCE
import ua.cn.stu.navigation.contract.ConstantManager.Companion.SUPPLIES_RSOURCE_REGISTER
import ua.cn.stu.navigation.contract.ConstantManager.Companion.TIME_WORK_PUMP
import ua.cn.stu.navigation.contract.ConstantManager.Companion.TIME_WORK_PUMP_REGISTER
import ua.cn.stu.navigation.databinding.ActivityMainBinding
import ua.cn.stu.navigation.fragments.*
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.ACTIVATE_DUAL_PATTERN_BOLUS
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.ACTIVATE_EXTENDED_BOLUS
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.ACTIVATE_PIN_CODE_APP
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.ACTIVATE_PIN_CODE_SETTINGS
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.ACTIVATE_STEP_BOLUS
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.ACTIVATE_SUPER_BOLUS
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.ATTEMPTS_TO_UN_LOCK
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.CONNECTES_DEVICE
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.CONNECTES_DEVICE_ADDRESS
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.CONNECTION_PASSWORD
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.DATA_ALL_CHARTS
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.INPUT_SPEED_ALL_PERIODS_MAIN
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.MASSAGES_LIST_MAIN
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.PERIOD_NAMES_MAIN
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.PIN_CODE_APP
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.PIN_CODE_SETTINGS
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.PROFILE_NAMES
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.SELECTED_PROFILE
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.START_TIME_ALL_PERIODS_MAIN
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.TEST_SAVE
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.TIMESTAMPS_LIST_MAIN
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.TIMESTAMP_LAST_LOG_MASSAGE
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.TIMESTAMP_LAST_READ_LOG
import ua.cn.stu.navigation.persistence.preference.PreferenceKeys.TYPE_CELLS_LIST_MAIN
import ua.cn.stu.navigation.rx.RxUpdateMainEvent
import ua.cn.stu.navigation.services.BasalPeriod
import kotlin.properties.Delegates


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding
    private var showActivBolusBtn: Boolean = false
    private var mSettings: SharedPreferences? = null
    private var countActivatedTitleFragment: Int = 0

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothLeService: BluetoothLeService? = null
    private var mGattCharacteristics = ArrayList<ArrayList<BluetoothGattCharacteristic>>()
    private var mGattServicesList: ExpandableListView? = null
    private var mCharacteristic: BluetoothGattCharacteristic? = null
    private var mNotifyCharacteristic: BluetoothGattCharacteristic? = null
    private var globalSemaphore = true // флаг, который преостанавливает отправку новой
    private val queue = ua.cn.stu.navigation.services.BlockingQueue()
    private var dataSortSemaphore = "" // строчка, показывающая с каким регистром мы сейчас работаем, чтобы однозначно понять кому пердназначаются принятые данные
    private var createDataChart = ArrayList<Int>()
    private var namePeriods = ArrayList<String>()
    private var startTimeAllPeriods = ArrayList<Int>()
    private var inputSpeedAllPeriods = ArrayList<Int>()

    private var readRegisterPointer: ByteArray? = null
    var reconnectThreadFlag: Boolean = false
    private var mConnected = false
    private var endFlag = false
    var percentSynchronize = 0
    private var mScanning = false
    private val listName = "NAME"
    private val listUUID = "UUID"
    private var actionState = WRITE
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            if (!mBluetoothLeService?.initialize()!!) {
                finish()

            }
            if (!flagScanWithoutConnect) {
                mBluetoothLeService?.connect(connectedDeviceAddress)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }
    private var flagScanWithoutConnect = false
    private var passRequest = 0
    private var superBoluseGoFlag = false
    private var countReadProfile = 1
    private var countReadPeriodInProfile = 1
    private var cancelReadBasalProfilesFlag = false
    private var basalProfilePeriodDataProcessedFlag = false
    private var basalProfilePeriodDataRereaadFlag = false
    private var basalProfileNumDataProcessedFlag = false
    private var basalProfileNumCorrectFlag = false
    private var readBasalProfilesNotStart = true
    private var logString = ""
    private var timer: CountDownTimer? = null
    private var stateLogRead = 0
    private var listYearsLog = ArrayList<Int>()
    private var maxYearInLog = 0
    private var listMonthsLog = ArrayList<Int>()
    private var maxMonthInLog = 0
    private var listDaysLog = ArrayList<Int>()
    private var maxDayInLog = 0
    private var pumpTimestamp = 0
    private var pumpTimeLive = 0
    private var countReadLogDays = LOG_UPDATE_DEPTH
    private var timestampLastLogMassage = 0
    private var timestampLastReadLog = 0

    private val currentFragment: Fragment
        get() = supportFragmentManager.findFragmentById(R.id.fragmentContainer)!!

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            updateUi()
        }
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        mSettings = this.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE)
        mGattServicesList = findViewById(R.id.gatt_services_list)
        setSupportActionBar(binding.toolbar)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.menu_bottom_layout_bg)
        initAllVariables()


        if (savedInstanceState == null) {
            if (activatePinCodeApp) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragmentContainer, PinFragment())
                    .commit()
            } else {
//                supportFragmentManager
//                    .beginTransaction()
//                    .add(R.id.fragmentContainer, RefilledFragment())
//                    .commit()
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragmentContainer, MenuFragment())
                    .commit()
            }
        }

        createProfilesList()
        createChatList()
        scanList = reinitScanList()
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, false)
        binding.activeBolusesBtn.setOnClickListener { showActiveBolusesDialog() }


        // это дожно обновляться динамически по приёму новых данных
        if (showActivBolusBtn) {
            binding.activeBolusesBtn.visibility = View.VISIBLE
            binding.activeBolusesIv.visibility = View.VISIBLE
        } else {
            binding.activeBolusesBtn.visibility = View.GONE
            binding.activeBolusesIv.visibility = View.GONE
        }
        binding.percentChargeTitleTv.text = "$battryPercent%"
        binding.bslToolbatSecondTv.text = getString(R.string.bsl_3_7_u_h, basalSpeed)
        binding.blsToolbatSecondTv.text = getString(R.string.bls____)
        binding.titleToolbatSecondTv.text = getString(R.string.iob_4_7_u, iob)

        // инициализация блютуз
        checkLocationPermission()
        initBLEStructure()
        scanLeDevice(true)

        RxUpdateMainEvent.getInstance().refiliingObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { variable ->
                if (!onRefillingScreen) {
                    if (variable) {
                        showRefillingScreen()
                    }
                    if (!variable) {
                    }
                }
                if (onRefillingScreen) {
                    if (variable) {
                    }
                    if (!variable) {
                        showRefilledScreen()
                    }
                }
            }

        //запуск очереди блютуз команд
        val worker = Thread {
            while (true) {
                val task: Runnable = queue.get()
                task.run()
            }
        }
        worker.start()

    }

    override fun onResume() {
        super.onResume()
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        if (mBluetoothLeService != null) {
            connectedDevice =  getString(CONNECTES_DEVICE)
            connectedDeviceAddress =  getString(CONNECTES_DEVICE_ADDRESS)
        }
        if (!mConnected) {
            reconnectThreadFlag = true
            reconnectThread()
        }
    }
    override fun onPause() {
        super.onPause()
        endFlag = true
        logString = ""
        println( "onPause logCommand обнуление logString=$logString")
    }
    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        pumpStatusNotifyDataThreadFlag = false
        if (mBluetoothLeService != null) {
            unbindService(mServiceConnection)
            mBluetoothLeService = null
        }
        endFlag = true
        if (mScanning) { mBluetoothAdapter!!.stopLeScan(mLeScanCallback) }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        updateUi()
        return true
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val fragment = currentFragment
        if (fragment is HasCustomTitle) {
            if ( fragment.getTitleRes() == getString(R.string.settings)) {
                scanLeDevice(false)
                flagScanWithoutConnect = false
            }
        }
        RxUpdateMainEvent.getInstance().updateBackPresedIvent()
    }

    override fun showSacnScreen() { launchFragment(ScanningFragment()) }
    override fun showTemporaryBasalScreen() { launchFragment(TemoraryBasalFragment()) }
    override fun showBasalProfileSettingsScreen() { launchFragment(ProfileSettingsFragment()) }
    override fun showProfileScreen() { launchFragment(ProfilesFragment()) }
    override fun showSettingsScreen() { launchFragment(SettingsFragment()) }
    override fun showBolusScreen() { launchFragment(BolusFragment()) }
    override fun showStepBolusScreen() { launchFragment(StepBolusFragment()) }
    override fun showExtendedBolusScreen() { launchFragment(ExtendedBolusFragment()) }
    override fun showDualPatternBolusScreen() { launchFragment(DualPatternBolusFragment()) }
    override fun showSuperBolusScreen() { launchFragment(SuperBolusFragment()) }
    override fun showRefillingScreen() { launchFragmentWihtoutStack(RefillingFragment()) }
    override fun showRefilledScreen() { launchFragmentWihtoutStack(RefilledFragment()) }
    override fun showMenuScreen() { launchFragmentWihtoutStack(MenuFragment()) }

    override fun firstOpenMenu() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, MenuFragment())
            .commit()
    }
    override fun goBack() {
        println("goBack")
        flagScanWithoutConnect = false
        onBackPressed()
    }
    override fun goToMenu() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun <T : Parcelable> publishResult(result: T) {
        supportFragmentManager.setFragmentResult(result.javaClass.name, bundleOf(KEY_RESULT to result))
    }
    override fun <T : Parcelable> listenResult(clazz: Class<T>, owner: LifecycleOwner, listener: ResultListener<T>) {
        supportFragmentManager.setFragmentResultListener(clazz.name, owner) { _, bundle ->
            listener.invoke(bundle.getParcelable(KEY_RESULT)!!)
        }
    }

    override fun setNewTitle(newTitle: String) {
        binding.titleFragmentTv.text = newTitle
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
    private fun launchFragmentWihtoutStack(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        transaction.replace(R.id.fragmentContainer, fragment)
        if (!supportFragmentManager.isDestroyed) transaction.commit()
    }

    private fun updateUi() {
        val fragment = currentFragment

        if (fragment is HasCustomTitle) {
            countActivatedTitleFragment += 1
            binding.titleFragmentCl.visibility = View.VISIBLE
            binding.titleFragmentTv.text = fragment.getTitleRes()
            binding.titleFragmentTv.layoutParams.height = 0
            binding.renameProfileIv.layoutParams.height = 0
            binding.renameProfileBtn.layoutParams.height = 0
            binding.titleFragmentCl.layoutParams.height = 0

            if (fragment is HasRenameProfileAction) {
                binding.renameProfileIv.visibility = View.VISIBLE
                binding.renameProfileBtn.visibility = View.VISIBLE
                renameProfile(fragment.getRenameProfileAction())
            } else {
                binding.renameProfileIv.visibility = View.GONE
                binding.renameProfileBtn.visibility = View.GONE
            }
            if (countActivatedTitleFragment == 1) {
                animatedShowTitleFragment(56f, binding.titleFragmentCl)
            } else {
                binding.titleFragmentCl.layoutParams.height = convertToDp(56f).toInt()
            }
        } else {
            if (countActivatedTitleFragment != 0) {
                animatedHideTitleFragment(56f, binding.titleFragmentCl)
            } else {
                binding.toolbar.title = ""
                binding.titleToolbatTv.text = getString(R.string.app_name)
                binding.titleFragmentCl.visibility = View.GONE
            }
            countActivatedTitleFragment = 0
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            return_rl.visibility = View.VISIBLE
            battery_rl.visibility = View.GONE
        } else {
            battery_rl.visibility = View.VISIBLE
            return_rl.visibility = View.GONE
        }

        if (fragment is HasReturnAction) { returned(fragment.getReturnAction()) }
        if (fragment is HasBatteryAction) { batteryClicked(fragment.getBatteryAction()) }
        if (fragment is HasCustomAction) { createCustomToolbarAction(fragment.getCustomAction())
        } else { binding.toolbar.menu.clear() }
    }

    private fun createCustomToolbarAction(action: CustomAction) {
        binding.toolbar.menu.clear() // clearing old action if it exists before assigning a new one

        val iconDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(this, action.iconRes)!!)
        iconDrawable.setTint(Color.WHITE)

        val menuItem = binding.toolbar.menu.add(action.textRes)
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuItem.icon = iconDrawable
        menuItem.setOnMenuItemClickListener {
            action.onCustomAction.run()
            return@setOnMenuItemClickListener true
        }
    }

    private fun returned(action: ReturnAction) {
        return_btn.setOnClickListener {
            action.onReturnAction.run()
            return@setOnClickListener
        }
    }
    private fun renameProfile(action: RenameProfileAction) {
        rename_profile_btn.setOnClickListener {
            action.onRenameProfileAction.run()
            return@setOnClickListener
        }
    }
    private fun batteryClicked(action: BatteryAction) {
        battery_btn.setOnClickListener {
            action.onBatteryAction.run()
            return@setOnClickListener
        }
    }
    private fun animatedShowTitleFragment(heightDP: Float, view: View) {
        val anim = ValueAnimator.ofFloat(0f, convertToDp(heightDP))
        anim.addUpdateListener { valueAnimator ->
            val parm = valueAnimator.animatedValue as Float
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = parm.toInt()
            view.layoutParams = layoutParams
        }
        anim.duration = 300
        anim.start()
    }
    private fun animatedHideTitleFragment(heightDP: Float, view: View) {
        val anim = ValueAnimator.ofFloat(convertToDp(heightDP), 0f)
        anim.addUpdateListener { valueAnimator ->
            val parm = valueAnimator.animatedValue as Float
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = parm.toInt()
            view.layoutParams = layoutParams
        }
        anim.duration = 300
        anim.start()
    }
    private fun convertToDp(unit: Float): Float {
        val r: Resources = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            unit,
            r.displayMetrics
        )
    }

    //TODO тут инициализация переменных
    private fun initAllVariables() {
        //init
        //the main
        battryPercent = 0
        liIonPercent = 0
        reservoirVolume = 0
        cannuleTime = 0
        iob = 0.00f
        //basal
        inProfileSettingsFragmentFlag = false
        percentSinhronizeBasalProfiles = 0
        refreshBasalProfile = false
        numBasalProfiles = 0
        numBasalProfilePeriods = 0
        nameReadBasalProfile = ""

        basalSpeed = 0f
        changeProfile = 0
        if (getInt(SELECTED_PROFILE) == 65000) {
            selectedProfile = 2
            saveInt(SELECTED_PROFILE, selectedProfile)
        } else { selectedProfile =  getInt(SELECTED_PROFILE)}

        //temporary basal
        stayOnTemporaryBasalScreen = false
        temporaryBasalActivated = false
        temporaryBasalVoliume = 0
        temporaryBasalTime = 0


        //bolus
        superBolusIsResolved = false
        extendedAndDualPatternBolusIsResolved = false

        balanceAllBoluses = 4.99f
        bolusType = 0
        superBolusBasalVoliume = 0
        superBolusVoliume = 0
        superBolusTime = 0

        numberInsertUnitsStepBolus = 0
        numberSumUnitsStepBolus = 0
        numberInsertUnitsSuperBolus = 0
        numberSumUnitsSuperBolus = 0
        timeBasalPauseSuperBolus = 0
        numberInsertUnitsExtendedBolus = 0
        numberSumUnitsExtendedBolus = 0
        remainingTimeExtendedBolus = 0
        insertionOfStretchedDualPatternBolus = 0
        numberFastUnitsDualPatternBolus = 0
        numberSlowUnitsDualPatternBolus = 0
        numberInsertUnitsDualPatternBolus = 0
        numberSumUnitsDualPatternBolus = 0
        remainingTimeDualPatternBolus = 0

        pumpStatus = 0
        refilling = 0
        onRefillingScreen = false
        countBolusInConveyor = 0
        typeFirstBolusInConveyor = 0
        typeSecondBolusInConveyor = 0
        typeThirdBolusInConveyor = 0
        typeFourthBolusInConveyor = 0

        //settings
        pumpStatusNotifyDataThreadFlag = true
        showInfoDialogsFlag = false
        inScanFragmentFlag = false
        tupOnList = false
        if (getInt(ATTEMPTS_TO_UN_LOCK) == 65000) {
            attemptsToUnlock = 3
            saveInt(ATTEMPTS_TO_UN_LOCK, attemptsToUnlock)
        } else { attemptsToUnlock =  getInt(ATTEMPTS_TO_UN_LOCK)}
        if (getString(CONNECTION_PASSWORD) == "NOT SET!") {
            connectionPassword = "123456"
            saveString(CONNECTION_PASSWORD, connectionPassword)
        } else {connectionPassword = getString(CONNECTION_PASSWORD)}
        if (getString(CONNECTES_DEVICE) == "NOT SET!") {
            connectedDevice = "BT-Pump 12"
            saveString(CONNECTES_DEVICE, connectedDevice)
        } else { connectedDevice =  getString(CONNECTES_DEVICE)}
        if (getString(CONNECTES_DEVICE_ADDRESS) == "NOT SET!") {
            connectedDeviceAddress = "D7:77:A9:47:F9:EC"//"12:34:56:78:90:12"
            saveString(CONNECTES_DEVICE_ADDRESS, connectedDeviceAddress)
        } else { connectedDeviceAddress =  getString(CONNECTES_DEVICE_ADDRESS)}
        if (getString(ACTIVATE_PIN_CODE_APP) == "NOT SET!") {
            activatePinCodeApp = false
            saveString(ACTIVATE_PIN_CODE_APP, activatePinCodeApp.toString())
        } else { activatePinCodeApp =  getString(ACTIVATE_PIN_CODE_APP).toBoolean()}
        if (getString(PIN_CODE_APP) == "NOT SET!") {
            pinCodeApp = "1235"
            saveString(PIN_CODE_APP, pinCodeApp)
        } else { pinCodeApp =  getString(PIN_CODE_APP)}
        if (getString(ACTIVATE_PIN_CODE_SETTINGS) == "NOT SET!") {
            activatePinCodeSettings = false
            saveString(ACTIVATE_PIN_CODE_SETTINGS, activatePinCodeSettings.toString())
        } else { activatePinCodeSettings =  getString(ACTIVATE_PIN_CODE_SETTINGS).toBoolean()}
        if (getString(PIN_CODE_SETTINGS) == "NOT SET!") {
            pinCodeSettings = "1234"
            saveString(PIN_CODE_SETTINGS, pinCodeSettings)
        } else { pinCodeSettings =  getString(PIN_CODE_SETTINGS) }
        if (getString(ACTIVATE_STEP_BOLUS) == "NOT SET!") {
            activateStepBolus = false
            saveString(ACTIVATE_STEP_BOLUS, activateStepBolus.toString())
        } else { activateStepBolus =  getString(ACTIVATE_STEP_BOLUS).toBoolean()}
        if (getString(ACTIVATE_EXTENDED_BOLUS) == "NOT SET!") {
            activateExtendedBolus = false
            saveString(ACTIVATE_EXTENDED_BOLUS, activateExtendedBolus.toString())
        } else { activateExtendedBolus =  getString(ACTIVATE_EXTENDED_BOLUS).toBoolean()}
        if (getString(ACTIVATE_DUAL_PATTERN_BOLUS) == "NOT SET!") {
            activateDualPatternBolus = false
            saveString(ACTIVATE_DUAL_PATTERN_BOLUS, activateDualPatternBolus.toString())
        } else { activateDualPatternBolus =  getString(ACTIVATE_DUAL_PATTERN_BOLUS).toBoolean()}
        if (getString(ACTIVATE_SUPER_BOLUS) == "NOT SET!") {
            activateSuperBolus = false
            saveString(ACTIVATE_SUPER_BOLUS, activateSuperBolus.toString())
        } else { activateSuperBolus =  getString(ACTIVATE_SUPER_BOLUS).toBoolean()}
        if (getInt(TIMESTAMP_LAST_READ_LOG) == 65000) {
            timestampLastReadLog = 0
            saveInt(TIMESTAMP_LAST_READ_LOG, timestampLastReadLog)
        } else { timestampLastReadLog =  getInt(TIMESTAMP_LAST_READ_LOG)}
        if (getInt(TIMESTAMP_LAST_LOG_MASSAGE) == 65000) {
            timestampLastLogMassage = 0
            saveInt(TIMESTAMP_LAST_LOG_MASSAGE, timestampLastLogMassage)
        } else { timestampLastLogMassage =  getInt(TIMESTAMP_LAST_LOG_MASSAGE)}

    }
    private fun createProfilesList(){
        if (loadArrayList<String>(PROFILE_NAMES).size == 0) {
            println("сохранение СОЗДАЁМ САМИ И ПИШЕМ В ПАМЯТЬ")
            val listN: ArrayList<String> = ArrayList()
            listN.add("First profile")
            listN.add("2 profile")
            listN.add("3 profile")
            listN.add("add")
            profileNames = listN
            saveArrayList(PROFILE_NAMES, profileNames)


            val listD: ArrayList<ArrayList<Int>> = ArrayList()
            listD.add(createFakeDataChart())
            listD.add(createFakeDataChart())
            listD.add(createFakeDataChart())
            dataAllCharts = listD
            saveArrayList(DATA_ALL_CHARTS, dataAllCharts)

            val listTempStr = ArrayList<Array<String>>()
            val arrayPN = arrayOf("period profile", "add")
            listTempStr.add(arrayPN)
            listTempStr.add(arrayPN)
            listTempStr.add(arrayPN)
            periodNamesMain = listTempStr
            seveArrayStringList(PERIOD_NAMES_MAIN, periodNamesMain)

            val listTempInt = ArrayList<IntArray>()
            val listST = intArrayOf(0)
            listTempInt.add(listST)
            listTempInt.add(listST)
            listTempInt.add(listST)
            startTimeAllPeriodsMain = listTempInt
            seveIntArrayList(START_TIME_ALL_PERIODS_MAIN, startTimeAllPeriodsMain)

            val listTempInt2 = ArrayList<IntArray>()
            val listIS = intArrayOf(100)
            listTempInt2.add(listIS)
            listTempInt2.add(listIS)
            listTempInt2.add(listIS)
            inputSpeedAllPeriodsMain = listTempInt2
            seveIntArrayList(INPUT_SPEED_ALL_PERIODS_MAIN, inputSpeedAllPeriodsMain)
        } else {
            println("сохранение ЧИТАЕМ ИЗ ПАМЯТИ")
            profileNames = loadArrayList(PROFILE_NAMES)
            dataAllCharts = loadArrayList(DATA_ALL_CHARTS)
            periodNamesMain = loadArrayStringList(PERIOD_NAMES_MAIN)
            startTimeAllPeriodsMain = loadIntArrayList(START_TIME_ALL_PERIODS_MAIN)
            inputSpeedAllPeriodsMain = loadIntArrayList(INPUT_SPEED_ALL_PERIODS_MAIN)
        }
    }
    private fun createChatList(){
        if (loadArrayList<String>(TYPE_CELLS_LIST_MAIN).size == 0) {
            val listT = ArrayList<String>()
            listT.add("type_2")
            listT.add("invalidate")
            typeCellsListMain = listT

            val listM = ArrayList<String>()
            listM.add("Привет, сюда будет выводиться лог событий помпы. Нажми на кнопку \"etc\", чтобы его обновить")
            massagesListMain = listM

            val listTS = ArrayList<String>()
            listTS.add("${(System.currentTimeMillis()/1000L)}")
            timestampsListMain = listTS
        } else {
            // читаем из памяти
            typeCellsListMain = loadArrayList(TYPE_CELLS_LIST_MAIN)
            massagesListMain = loadArrayList(MASSAGES_LIST_MAIN)
            timestampsListMain = loadArrayList(TIMESTAMPS_LIST_MAIN)
        }
    }
    private fun createFakeDataChart() :ArrayList<Int> {
        val dataChart = ArrayList<Int>()
        dataChart.add(0)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        dataChart.add(100)
        return dataChart
    }
    private fun reinitScanList():ArrayList<ScanItem> {
        val result = ArrayList<ScanItem>()
        result.add(ScanItem("NOT SET!", "null"))
        return result
    }

    @SuppressLint("InflateParams", "CheckResult")
    private fun showActiveBolusesDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_active_boluses, null)
        val myDialog = Dialog(this)
        myDialog.setContentView(dialogBinding)
        myDialog.setCancelable(false)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()

        //работа с ячейками
        val firstInQueueBolusCell = dialogBinding.findViewById<View>(R.id.first_in_queue_active_bolus_cl) as ConstraintLayout
        val secondInQueueBolusCell = dialogBinding.findViewById<View>(R.id.second_in_queue_active_bolus_cl) as ConstraintLayout
        val thirdInQueueBolusCell = dialogBinding.findViewById<View>(R.id.third_in_queue_active_bolus_cl) as ConstraintLayout
        val fourthInQueueBolusCell = dialogBinding.findViewById<View>(R.id.fourth_in_queue_active_bolus_cl) as ConstraintLayout

        // текстовое описание ячеек
        val firstInQueueBolusText = dialogBinding.findViewById<View>(R.id.dialog_active_boluses_first_in_queue_tv) as TextView
        val secondInQueueBolusText = dialogBinding.findViewById<View>(R.id.dialog_active_boluses_second_in_queue_tv) as TextView
        val thirdInQueueBolusText = dialogBinding.findViewById<View>(R.id.dialog_active_boluses_third_in_queue_tv) as TextView
        val fourthInQueueBolusText = dialogBinding.findViewById<View>(R.id.dialog_active_boluses_fourth_in_queue_tv) as TextView

        var updatingDialogThreadFlag = true
        val updatingThread = Thread {
            while (updatingDialogThreadFlag) {
                runOnUiThread {
                    if (countBolusInConveyor == 0) {
                        firstInQueueBolusCell.visibility = View.GONE
                        secondInQueueBolusCell.visibility = View.GONE
                        thirdInQueueBolusCell.visibility = View.GONE
                        fourthInQueueBolusCell.visibility = View.GONE
                    }
                    if (countBolusInConveyor > 0) {
                        firstInQueueBolusCell.visibility = View.VISIBLE
                        secondInQueueBolusCell.visibility = View.GONE
                        thirdInQueueBolusCell.visibility = View.GONE
                        fourthInQueueBolusCell.visibility = View.GONE
                        if (typeFirstBolusInConveyor == 0) {firstInQueueBolusText.text = getString(R.string.bolus_u_n_u_injected, numberSumUnitsStepBolus.toFloat()/100, numberInsertUnitsStepBolus.toFloat()/100)}
                        if (typeFirstBolusInConveyor == 1) {firstInQueueBolusText.text = getString(R.string.super_u_n_u_injected, numberSumUnitsSuperBolus.toFloat()/100, numberInsertUnitsSuperBolus.toFloat()/100)}
                        if (typeFirstBolusInConveyor == 2) {firstInQueueBolusText.text = getString(R.string.extended_u_min_n_u_injected, numberSumUnitsExtendedBolus.toFloat()/100, remainingTimeExtendedBolus, numberInsertUnitsExtendedBolus.toFloat()/100)}
                        if (typeFirstBolusInConveyor == 3) {firstInQueueBolusText.text = getString(R.string.dual_pattern_u_min_n_u_injected, numberSumUnitsDualPatternBolus.toFloat()/100, remainingTimeDualPatternBolus, numberInsertUnitsDualPatternBolus.toFloat()/100)}
                        if (countBolusInConveyor > 1) {
                            secondInQueueBolusCell.visibility = View.VISIBLE
                            thirdInQueueBolusCell.visibility = View.GONE
                            fourthInQueueBolusCell.visibility = View.GONE
                            if (typeSecondBolusInConveyor == 0) {secondInQueueBolusText.text = getString(R.string.bolus_u_n_u_injected, numberSumUnitsStepBolus.toFloat()/100, numberInsertUnitsStepBolus.toFloat()/100)}
                            if (typeSecondBolusInConveyor == 1) {secondInQueueBolusText.text = getString(R.string.super_u_n_u_injected, numberSumUnitsSuperBolus.toFloat()/100, numberInsertUnitsSuperBolus.toFloat()/100)}
                            if (typeSecondBolusInConveyor == 2) {secondInQueueBolusText.text = getString(R.string.extended_u_min_n_u_injected, numberSumUnitsExtendedBolus.toFloat()/100, remainingTimeExtendedBolus, numberInsertUnitsExtendedBolus.toFloat()/100)}
                            if (typeSecondBolusInConveyor == 3) {secondInQueueBolusText.text = getString(R.string.dual_pattern_u_min_n_u_injected, numberSumUnitsDualPatternBolus.toFloat()/100, remainingTimeDualPatternBolus, numberInsertUnitsDualPatternBolus.toFloat()/100)}
                            if (countBolusInConveyor > 2) {
                                thirdInQueueBolusCell.visibility = View.VISIBLE
                                fourthInQueueBolusCell.visibility = View.GONE
                                if (typeThirdBolusInConveyor == 0) {thirdInQueueBolusText.text = getString(R.string.bolus_u_n_u_injected, numberSumUnitsStepBolus.toFloat()/100, numberInsertUnitsStepBolus.toFloat()/100)}
                                if (typeThirdBolusInConveyor == 1) {thirdInQueueBolusText.text = getString(R.string.super_u_n_u_injected, numberSumUnitsSuperBolus.toFloat()/100, numberInsertUnitsSuperBolus.toFloat()/100)}
                                if (typeThirdBolusInConveyor == 2) {thirdInQueueBolusText.text = getString(R.string.extended_u_min_n_u_injected, numberSumUnitsExtendedBolus.toFloat()/100, remainingTimeExtendedBolus, numberInsertUnitsExtendedBolus.toFloat()/100)}
                                if (typeThirdBolusInConveyor == 3) {thirdInQueueBolusText.text = getString(R.string.dual_pattern_u_min_n_u_injected, numberSumUnitsDualPatternBolus.toFloat()/100, remainingTimeDualPatternBolus, numberInsertUnitsDualPatternBolus.toFloat()/100)}
                                if (countBolusInConveyor > 3) {
                                    fourthInQueueBolusCell.visibility = View.VISIBLE
                                    if (typeFourthBolusInConveyor == 0) {fourthInQueueBolusText.text = getString(R.string.bolus_u_n_u_injected, numberSumUnitsStepBolus.toFloat()/100, numberInsertUnitsStepBolus.toFloat()/100)}
                                    if (typeFourthBolusInConveyor == 1) {fourthInQueueBolusText.text = getString(R.string.super_u_n_u_injected, numberSumUnitsSuperBolus.toFloat()/100, numberInsertUnitsSuperBolus.toFloat()/100)}
                                    if (typeFourthBolusInConveyor == 2) {fourthInQueueBolusText.text = getString(R.string.extended_u_min_n_u_injected, numberSumUnitsExtendedBolus.toFloat()/100, remainingTimeExtendedBolus, numberInsertUnitsExtendedBolus.toFloat()/100)}
                                    if (typeFourthBolusInConveyor == 3) {fourthInQueueBolusText.text = getString(R.string.dual_pattern_u_min_n_u_injected, numberSumUnitsDualPatternBolus.toFloat()/100, remainingTimeDualPatternBolus, numberInsertUnitsDualPatternBolus.toFloat()/100)}
                                }
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(100)
                } catch (ignored: Exception) { }
            }
        }
        updatingThread.start()

        // работа с кнопками
        val deleteFirstInQueueBolus = dialogBinding.findViewById<View>(R.id.dialog_active_boluses_first_in_queue_delete_btn)
        deleteFirstInQueueBolus.setOnClickListener {
            deleteActiveBolusesDialog(firstInQueueBolusCell, "first")
        }

        val deleteSecondInQueueBolus = dialogBinding.findViewById<View>(R.id.dialog_active_boluses_second_in_queue_delete_btn)
        deleteSecondInQueueBolus.setOnClickListener {
            deleteActiveBolusesDialog(secondInQueueBolusCell, "second")
        }

        val deleteThirdInQueueBolus = dialogBinding.findViewById<View>(R.id.dialog_active_boluses_third_in_queue_delete_btn)
        deleteThirdInQueueBolus.setOnClickListener {
            deleteActiveBolusesDialog(thirdInQueueBolusCell, "third")
        }

        val deleteFourthInQueueBolus = dialogBinding.findViewById<View>(R.id.dialog_active_boluses_fourth_in_queue_delete_btn)
        deleteFourthInQueueBolus.setOnClickListener {
            deleteActiveBolusesDialog(fourthInQueueBolusCell, "fourth")
        }

        val yesBtn = dialogBinding.findViewById<View>(R.id.dialog_active_boluses_confirm)
        yesBtn.setOnClickListener {
            updatingDialogThreadFlag = false
            myDialog.dismiss()
        }
    }
    @SuppressLint("InflateParams")
    private fun deleteActiveBolusesDialog(hidedView: View, type: String) {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_delete_active_boluses, null)
        val myDialog = Dialog(this)
        myDialog.setContentView(dialogBinding)
        myDialog.setCancelable(false)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()

        dialogBinding.dialog_delete_active_bolus_cancel.setOnClickListener { myDialog.dismiss() }
        dialogBinding.dialog_delete_active_bolus_confirm.setOnClickListener {
            hidedView.visibility = View.GONE
            if (type == "first") {
                runDeleteBolus(1)
                println("type == first") }
            if (type == "second") {
                runDeleteBolus(2)
                println("type == second") }
            if (type == "third") {
                runDeleteBolus(3)
                println("type == third") }
            if (type == "fourth") {
                runDeleteBolus(4)
                println("type == fourth") }
            myDialog.dismiss()
        }
    }
    @SuppressLint("InflateParams")
    private fun showInfoDialog(title: String, massage: String, positive: Boolean) {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_info, null)
        val myDialog = Dialog(this)
        myDialog.setContentView(dialogBinding)
        myDialog.setCancelable(false)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()

        myDialog.dialog_info_title_tv.text = title
        myDialog.info_dialog_massage_tv.text = massage

        if (positive) { myDialog.info_animation_view.setAnimation(R.raw.success) }
        else { myDialog.info_animation_view.setAnimation(R.raw.error) }


        val yesBtn = dialogBinding.findViewById<View>(R.id.dialog_info_confirm)
        yesBtn.setOnClickListener {
            myDialog.dismiss()
        }
    }
    @SuppressLint("InflateParams")
    override fun showGoBolusDialog(title: String, massage: String, numberOfHundredthsInsulin: Int, numberOfHundredStrechedInsulin: Int, timeBolus: Int) {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_go_bolus, null)
        val myDialog = Dialog(this)
        myDialog.setContentView(dialogBinding)
        myDialog.setCancelable(false)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()

        myDialog.dialog_enter_bolus_title_tv.text = title
        myDialog.dialog_enter_bolus_massage_tv.text = massage

        val yesBtn = dialogBinding.findViewById<View>(R.id.dialog_enter_bolus_confirm)
        yesBtn.setOnClickListener {
            if (bolusType == 0)  runBolus(numberOfHundredthsInsulin)
            if (bolusType == 1)  superBoluseGoFlag = true
            if (bolusType == 2)  runExtendedBolus(numberOfHundredthsInsulin, timeBolus)
            if (bolusType == 3)  runDualPatternBolus(numberOfHundredthsInsulin, numberOfHundredStrechedInsulin, timeBolus)
            goToMenu()
            myDialog.dismiss()
        }
        val noBtn = dialogBinding.findViewById<View>(R.id.dialog_enter_bolus_cancel)
        noBtn.setOnClickListener {
            myDialog.dismiss()
        }
    }
    @SuppressLint("InflateParams")
    private fun showTemporaryBasalStatusDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_info_basal, null)
        val myDialog = Dialog(this)
        myDialog.setContentView(dialogBinding)
        myDialog.setCancelable(false)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()

        myDialog.dialog_enter_temporary_basal_massage_tv.text = getString(R.string.temporary_basal_is_introduced_at_speed_2fu_h_do_you_want_to_cancel_it, temporaryBasalVoliume.toFloat()/100, temporaryBasalTime)

        val yesBtn = dialogBinding.findViewById<View>(R.id.dialog_enter_temporary_basal_confirm)
        yesBtn.setOnClickListener {
            runTemporaryBasalStop()
            myDialog.dismiss()
        }
        val noBtn = dialogBinding.findViewById<View>(R.id.dialog_enter_temporary_basal_cancel)
        noBtn.setOnClickListener {
            myDialog.dismiss()
        }
    }

    @SuppressLint("InflateParams")
    private fun showLocationPermissionDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_instruction_refilling, null)
        val myDialog = Dialog(this)
        myDialog.setContentView(dialogBinding)
        myDialog.setCancelable(false)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()

        myDialog.dialog_instraction_refilling_title_tv.text = getString(R.string.location_permission)
        myDialog.instraction_refilling_dialog_massage_tv.text = getString(R.string.gps_network_not_enabled)
        myDialog.dialog_instraction_refilling_cancel_tv.text = getString(R.string.open_location_settings)


        val yesBtn = dialogBinding.findViewById<View>(R.id.dialog_instraction_refilling_confirm)
        yesBtn.setOnClickListener {
            startActivity(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            )
            myDialog.dismiss()
        }
    }


    // сохранение и загрузка данных
    override fun seveIntArrayList(key: String, list: ArrayList<IntArray>) {
        saveInt(key+"_list_size", list.size)
        for (item in 0 until list.size) { saveIntArray(key+item, list[item]) }
    }
    private fun saveIntArray(key: String, array: IntArray) {
        saveInt(key + "_size", array.size)
        for (i in array.indices) saveInt(key + "_" + i, array[i])
    }
    private fun loadIntArray(key: String): IntArray {
        val size = getInt(key + "_size")
        val array = IntArray(size)//Array(size) { "" }
        for (i in 0 until size) array[i] = getInt(key + "_" + i)
        return array
    }
    private fun loadIntArrayList(key: String) :ArrayList<IntArray>{
        val size = getInt(key + "_list_size")
        val result = ArrayList<IntArray>()
        for (i in 0 until size) result.add(loadIntArray(key+i))
        return result
    }
    override fun seveArrayStringList(key: String, list: ArrayList<Array<String>>) {
        saveInt(key+"_list_size", list.size)
        for (item in 0 until list.size) { saveStringArray(key+item, list[item]) }
    }
    private fun loadArrayStringList(key: String) :ArrayList<Array<String>>{
        val size = getInt(key + "_list_size")
        val result = ArrayList<Array<String>>()
        for (i in 0 until size) result.add(loadStringArray(key+i))
        return result
    }
    private fun saveStringArray(key: String, array: Array<String>) {
        saveInt(key + "_size", array.size)
        for (i in array.indices) saveString(key + "_" + i, array[i])
    }
    private fun loadStringArray(key: String): Array<String> {
        val size = getInt(key + "_size")
        val array = Array(size) { "" }
        for (i in 0 until size) array[i] = getString(key + "_" + i)
        return array
    }
    override fun <T> saveArrayList(key: String, list: ArrayList<T>){
        val arrayString = Gson().toJson(list)
        saveString(key, arrayString)
    }
    private inline fun <reified T> loadArrayList(key: String) :ArrayList<T>{
        val listJson = getString(key)
        val result = ArrayList<T>()
        if (listJson != "NOT SET!") {
            val type = object : TypeToken<List<T>>() {}.type
            return Gson().fromJson(listJson, type)
        }
        return result
    }
    override fun saveInt(key: String, value: Int) {
        val editor: SharedPreferences.Editor = mSettings!!.edit()
        editor.putInt(key, value)
        editor.apply()
    }
    private fun getInt(key: String) :Int {
        return mSettings!!.getInt(key, 65000)
    }
    override fun saveString(key: String, text: String) {
        val editor: SharedPreferences.Editor = mSettings!!.edit()
        editor.putString(key, text)
        editor.apply()
    }
    private fun getString(key: String) :String {
        return mSettings!!.getString(key, "NOT SET!").toString()
    }

    // работа с блютузом
    override fun initBLEStructure() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ошибка 1", Toast.LENGTH_SHORT).show()
            finish()
        }
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "ошибка 2", Toast.LENGTH_SHORT).show()
            finish()
        } else {
//            Toast.makeText(this, "mBluetoothAdapter != null", Toast.LENGTH_SHORT).show()
        }
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
    }
    private val mGattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("ResourceAsColor")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when {
                BluetoothLeService.ACTION_GATT_CONNECTED == action -> {
                    Toast.makeText(context, "подключение установлено к $connectedDeviceAddress", Toast.LENGTH_SHORT).show()
                    reconnectThreadFlag = false
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED == action -> {
                    mConnected = false
                    endFlag = true
                    status_connection_tv.text = getString(R.string.offline)
                    status_connection_tv.setTextColor(Color.rgb(255, 49,49))
                    invalidateOptionsMenu()
                    mGattServicesList!!.setAdapter(null as SimpleExpandableListAdapter?)
                    percentSynchronize = 0

                    if(!reconnectThreadFlag && !mScanning && !inScanFragmentFlag){
                        reconnectThreadFlag = true
                        reconnectThread()
                    }
                }
                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED == action -> {
                    mConnected = true
                    status_connection_tv.text = getString(R.string.connected)
                    status_connection_tv.setTextColor(Color.rgb(10, 132,255))
                    if (showInfoDialogsFlag) {
                        runConnectToPump(connectionPassword.toByteArray(), true, 3)
                        showInfoDialogsFlag = false
                    } else {
                        runConnectToPump(connectionPassword.toByteArray(), false, 3)
                    }
//                    println("Notify подписка mGattUpdateReceiver")
//                    startSubscribeStatusPumpDataThread()
                    if (mBluetoothLeService != null) {
                        displayGattServices(mBluetoothLeService!!.supportedGattServices)
                        println("Notify подписка mGattUpdateReceiver")
                        bleCommand(null, NOTIFICATION_PUMP_STATUS, NOTIFY)
                    }
                }
                BluetoothLeService.ACTION_DATA_AVAILABLE == action -> {
                    if(intent.getByteArrayExtra(BluetoothLeService.PASS_DATA) != null) {
                        intent.getStringExtra(BluetoothLeService.ACTION_STATE)?.let { setActionState(it) }
                        displayDataPass(intent.getByteArrayExtra(BluetoothLeService.PASS_DATA))
                    }
                    if(intent.getByteArrayExtra(BluetoothLeService.REGISTER_POINTER) != null) displayDataRegisterPointer(intent.getByteArrayExtra(BluetoothLeService.REGISTER_POINTER))
                    if(intent.getByteArrayExtra(BluetoothLeService.REGISTER_DATA) != null) displayDataRegister(intent.getByteArrayExtra(BluetoothLeService.REGISTER_DATA))
                    if(intent.getByteArrayExtra(BluetoothLeService.LOG_POINTER) != null) displayLogPointer()

                    if(intent.getByteArrayExtra(BluetoothLeService.NOTIFICATION_PUMP_STATUS) != null) displayPumpStatusNotify (intent.getByteArrayExtra(BluetoothLeService.NOTIFICATION_PUMP_STATUS))
                    if(intent.getByteArrayExtra(BluetoothLeService.NOTIFICATION_PUMP_LOG) != null) displayPumpLogNotify (intent.getByteArrayExtra(BluetoothLeService.NOTIFICATION_PUMP_LOG))

                    setSensorsDataThreadFlag(intent.getBooleanExtra(BluetoothLeService.SENSORS_DATA_THREAD_FLAG, true))
                }

            }
        }
    }
    fun setActionState(value: String) {
        actionState = value
    }
    fun setSensorsDataThreadFlag(value: Boolean){ pumpStatusNotifyDataThreadFlag = value }
    private fun displayDataPass(data: ByteArray?) {
        if (data != null) {
            if (actionState == READ) { passRequest = data[0].toInt() }
        }
        globalSemaphore = true
    }
    private fun displayDataRegisterPointer(data: ByteArray?) {
        if (data != null) { readRegisterPointer = data }
        globalSemaphore = true
    }
    private fun displayPumpStatusNotify(data: ByteArray?) {
        if (data != null) {

            var i = 0
            val pumpStatusMask = 0b11100000
            val refillingMask = 0b00010000
            val countBolusConveyorMask = 0b00001110
            val typeFirstBolusInConveyorMask = 0b11000000
            val typeSecondBolusInConveyorMask = 0b00110000
            val typeThirdBolusInConveyorMask = 0b00001100
            val typeFourthBolusInConveyorMask = 0b00000011

            pumpStatus = data[i].toInt().and(pumpStatusMask) shr 5
            temporaryBasalActivated = pumpStatus == 1
            if (pumpStatus == 3) {
                //TODO чтение всех базальных профилей, потому что они изменились
//                if (readBasalProfilesNotStart) {
                    cancelReadBasalProfilesFlag = true
                    runReadNumBasalProfiles()
                    runReadBasalProfiles()
                    println("readBasalProfiles readBasalProfilesNotStart=$readBasalProfilesNotStart")
                    readBasalProfilesNotStart = false
//                }

                //механизм отсечки слишком часто приходящего флага обновления профилей
            }
            if (pumpStatus == 5) {
                runReadActiveBasalProfile()
                println("refreshBasalProfile=$refreshBasalProfile  pumpStatus=$pumpStatus")
            }

            refilling = data[i].toInt().and(refillingMask) shr 4
            RxUpdateMainEvent.getInstance().updateRefilling(refilling == 1)
            countBolusInConveyor = data[i].toInt().and(countBolusConveyorMask) shr 1
            typeFirstBolusInConveyor = data[i+1].toInt().and(typeFirstBolusInConveyorMask) shr 6
            typeSecondBolusInConveyor = data[i+1].toInt().and(typeSecondBolusInConveyorMask) shr 4
            typeThirdBolusInConveyor = data[i+1].toInt().and(typeThirdBolusInConveyorMask) shr 2
            typeFourthBolusInConveyor = data[i+1].toInt().and(typeFourthBolusInConveyorMask)
            i += 2

            var sumAmountToInsert = 0

            if (countBolusInConveyor > 0) {
                if (typeFirstBolusInConveyor == 0) {
                    numberInsertUnitsStepBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    numberSumUnitsStepBolus = castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    sumAmountToInsert += numberSumUnitsStepBolus - numberInsertUnitsStepBolus
                    println("numberInsertUnitsStepBolus=$numberInsertUnitsStepBolus    numberSumUnitsStepBolus=$numberSumUnitsStepBolus")
                    i += 4
                }
                if (typeFirstBolusInConveyor == 1) {
                    numberInsertUnitsSuperBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    numberSumUnitsSuperBolus = castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    sumAmountToInsert += numberSumUnitsSuperBolus - numberInsertUnitsSuperBolus
                    i += 4
                }
                if (typeFirstBolusInConveyor == 2) {
                    numberInsertUnitsExtendedBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    numberSumUnitsExtendedBolus = castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    remainingTimeExtendedBolus = castUnsignedCharToInt(data[i + 4]) * 256 + castUnsignedCharToInt(data[i + 5])
                    sumAmountToInsert += numberSumUnitsExtendedBolus - numberInsertUnitsExtendedBolus
                    i += 6
                }
                if (typeFirstBolusInConveyor == 3) {
                    insertionOfStretchedDualPatternBolus = castUnsignedCharToInt(data[i])
                    numberInsertUnitsDualPatternBolus =
                        castUnsignedCharToInt(data[i + 1]) * 256 + castUnsignedCharToInt(data[i + 2])
                    numberSumUnitsDualPatternBolus = castUnsignedCharToInt(data[i + 3]) * 256 + castUnsignedCharToInt(data[i + 4])
                    remainingTimeDualPatternBolus = castUnsignedCharToInt(data[i + 5]) * 256 + castUnsignedCharToInt(data[i + 6])
                    sumAmountToInsert += numberSumUnitsDualPatternBolus - numberInsertUnitsDualPatternBolus
                    i += 7
                }
            }
            if (countBolusInConveyor > 1) {
                if (typeSecondBolusInConveyor == 0) {
                    numberSumUnitsStepBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    sumAmountToInsert += numberSumUnitsStepBolus
                    i += 2
                }
                if (typeSecondBolusInConveyor == 1) {
                    numberSumUnitsSuperBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    timeBasalPauseSuperBolus = castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    sumAmountToInsert += numberSumUnitsSuperBolus
                    i += 4
                }
                if (typeSecondBolusInConveyor == 2) {
                    numberSumUnitsExtendedBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    remainingTimeExtendedBolus = castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    sumAmountToInsert += numberSumUnitsExtendedBolus
                    i += 4
                }
                if (typeSecondBolusInConveyor == 3) {
                    numberFastUnitsDualPatternBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    numberSlowUnitsDualPatternBolus =
                        castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    remainingTimeDualPatternBolus = castUnsignedCharToInt(data[i + 4]) * 256 + castUnsignedCharToInt(data[i + 5])
                    numberSumUnitsDualPatternBolus = numberFastUnitsDualPatternBolus + numberSlowUnitsDualPatternBolus
                    sumAmountToInsert += numberFastUnitsDualPatternBolus + numberSlowUnitsDualPatternBolus
                    i += 6
                }
            }
            if (countBolusInConveyor > 2) {
                if (typeThirdBolusInConveyor == 0) {
                    numberSumUnitsStepBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    sumAmountToInsert += numberSumUnitsStepBolus
                    i += 2
                }
                if (typeThirdBolusInConveyor == 1) {
                    numberSumUnitsSuperBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    timeBasalPauseSuperBolus = castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    sumAmountToInsert += numberSumUnitsSuperBolus
                    i += 4
                }
                if (typeThirdBolusInConveyor == 2) {
                    numberSumUnitsExtendedBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    remainingTimeExtendedBolus = castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    sumAmountToInsert += numberSumUnitsExtendedBolus
                    i += 4
                }
                if (typeThirdBolusInConveyor == 3) {
                    numberFastUnitsDualPatternBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    numberSlowUnitsDualPatternBolus =
                        castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    remainingTimeDualPatternBolus = castUnsignedCharToInt(data[i + 4]) * 256 + castUnsignedCharToInt(data[i + 5])
                    numberSumUnitsDualPatternBolus = numberFastUnitsDualPatternBolus + numberSlowUnitsDualPatternBolus
                    sumAmountToInsert += numberFastUnitsDualPatternBolus + numberSlowUnitsDualPatternBolus
                    i += 6
                }
            }
            if (countBolusInConveyor > 3) {
                if (typeFourthBolusInConveyor == 0) {
                    numberSumUnitsStepBolus = (castUnsignedCharToInt(data[i])*256 + castUnsignedCharToInt(data[i + 1]))
                    sumAmountToInsert += numberSumUnitsStepBolus
                }
                if (typeFourthBolusInConveyor == 1) {
                    numberSumUnitsSuperBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    timeBasalPauseSuperBolus = castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    sumAmountToInsert += numberSumUnitsSuperBolus
                }
                if (typeFourthBolusInConveyor == 2) {
                    numberSumUnitsExtendedBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    remainingTimeExtendedBolus = castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    sumAmountToInsert += numberSumUnitsExtendedBolus
                }
                if (typeFourthBolusInConveyor == 3) {
                    numberFastUnitsDualPatternBolus = castUnsignedCharToInt(data[i]) * 256 + castUnsignedCharToInt(data[i + 1])
                    numberSlowUnitsDualPatternBolus =
                            castUnsignedCharToInt(data[i + 2]) * 256 + castUnsignedCharToInt(data[i + 3])
                    remainingTimeDualPatternBolus = castUnsignedCharToInt(data[i + 4]) * 256 + castUnsignedCharToInt(data[i + 5])
                    numberSumUnitsDualPatternBolus = numberFastUnitsDualPatternBolus + numberSlowUnitsDualPatternBolus
                    sumAmountToInsert += numberFastUnitsDualPatternBolus + numberSlowUnitsDualPatternBolus
                }
            }

            if (sumAmountToInsert != 0) {
                bls_toolbat_second_tv.text = getString(R.string.bls_4_99u, (sumAmountToInsert.toFloat()/100))
            } else {
                bls_toolbat_second_tv.text = getString(R.string.bls____)
            }

            if (countBolusInConveyor > 0 ) {
                active_boluses_iv.visibility = View.VISIBLE
                active_boluses_btn.visibility = View.VISIBLE
            } else {
                active_boluses_iv.visibility = View.GONE
                active_boluses_btn.visibility = View.GONE
            }
        }
    }
    private fun displayPumpLogNotify(data: ByteArray?) {
        if (data != null) {
            logString += String(data, charset("UTF-8"))
            logString = logString.replace("\u0000", "")

            val lstValues: List<String> = logString.split("\n").map { it.trim() }
            if (stateLogRead == 0 ) {
                if (data[0] != 0.toByte() && (logString.split("\t").size == 1) && (logString.split(";").size == 1) && (logString.split(":").size == 1)) {
                    val intYear: Int = logString.replace("\u0000", "").toInt()
                    listYearsLog.add(intYear)
                }
                println("logString = $logString")
                logString = ""
            }
            if (stateLogRead == 1 ) {
                if (data[0] != 0.toByte()) {
                    val intMonth: Int = logString.replace("\u0000", "").toInt()
                    listMonthsLog.add(intMonth)
                }
                logString = ""
            }
            if (stateLogRead == 2 ) {
                if (data[0] != 0.toByte()) {
                    val intDay = logString.split(".")[0].toInt()
                    listDaysLog.add(intDay)
                }
                logString = ""
            }

//            println( "displayPumpLogNotify logCommand  count=${logString.length} logString=$logString")

            timer?.cancel()
            timer = object : CountDownTimer(500, 1) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    when (stateLogRead) {
                        0 -> {
                            // анализируем принятые года и читаем последний
                            listYearsLog.forEach {
                                println("displayPumpLogNotify logCommand года=$it")
                            }
                            if (listYearsLog.maxOrNull() != null) {
                                runLogCommand(false, "/${listYearsLog.maxOrNull()}",4)
                                maxYearInLog = listYearsLog.maxOrNull()!!
                                showToast("Года считаны. Кол-во:${listYearsLog.size}")
                            } else {
                                showToast("Лог пуст. (нет ни одного года)")
                            }
                            listYearsLog.clear()
                            stateLogRead = 1
                        }
                        1 -> {
                            // анализируем принятые месяца и читаем последний
                            listMonthsLog.forEach {
                                println("displayPumpLogNotify logCommand месяца=$it")
                            }
                            if (listMonthsLog.maxOrNull() != null) {
                                runLogCommand(false, "/$maxYearInLog/${listMonthsLog.maxOrNull()}", 4)
                                maxMonthInLog = listMonthsLog.maxOrNull()!!
                                showToast("Месяца считаны. Кол-во:${listMonthsLog.size}")
                            } else {
                                showToast("Лог пуст. (нет ни одного месяца)")
                            }
                            listMonthsLog.clear()
                            stateLogRead = 2
                        }
                        2 -> {
                            // анализируем принятые дни и читаем последние семь

                            listDaysLog.forEach {
                                println("displayPumpLogNotify logCommand дни=$it")
                            }

                            timestampLastReadLog
                            val timestampNow = (System.currentTimeMillis()/1000L).toInt()
                            if ((timestampNow - timestampLastReadLog) > 86400) {
                                countReadLogDays = 1 + ((timestampNow - timestampLastReadLog) / 86400)
                                if (countReadLogDays > LOG_UPDATE_DEPTH) countReadLogDays = LOG_UPDATE_DEPTH
                                println("if logCommand читаем $countReadLogDays последних дня")

                            } else {
                                countReadLogDays = 1
                                println("if logCommand читаем 1 последний день")
                            }
                            // если реальное время - timestampLastReadLog имеют разницу больше дня, то countReadLogDays = 2
                            // в противоположном случае countReadLogDays = 1 (перечитываем последний день)
                            if (listDaysLog.maxOrNull() != null) {
                                showToast("Дни считаны. Кол-во:${listDaysLog.size}")
                                maxDayInLog = listDaysLog.maxOrNull()!!


                                showToast("Читаем день: ${(maxDayInLog - countReadLogDays + 1)}")
                                println("displayPumpLogNotify logCommand читаем файл /$maxYearInLog/$maxMonthInLog/${(maxDayInLog - countReadLogDays + 1)}.txt")
                                println("Читаем день: ${(maxDayInLog - countReadLogDays + 1)}")

                                runLogCommand(false, "/$maxYearInLog/$maxMonthInLog/${(maxDayInLog - countReadLogDays + 1)}.txt -r", 2)
                                countReadLogDays --
                            } else {
                                showToast("Лог пуст. (нет ни одного дня)")
                            }

                            stateLogRead = 3
                        }
                        3 -> {
                            // анализируем принятые данные дня и решаем, читать ли следующий день

                            lstValues.forEach {
                                println("displayPumpLogNotify logCommand value=$it")

                                if ((it != "") && (it != "NO FILE") && (it.split("\t").size == 3)) {
                                    // разбираем коды посылок и формируем читаемые сообщения чата
                                    println("(it.split(\"\\t\").size = ${it.split("\t").size}")
                                    val timestampReseivingMassage = pumpTimestamp - (pumpTimeLive - (it.split("\t")[0]).toInt())
//                                    val timestampMy = System.currentTimeMillis()/1000L
                                    var massage = ""
                                    var inMobileAppFlag = false

                                    // проверка для добавления в списки данных только последних записей лога
                                    // время которых больше последнего считанного с помпы сообщения
                                    if (timestampReseivingMassage > timestampLastLogMassage + 1 ) {
                                        timestampLastLogMassage = timestampReseivingMassage
                                        if ((it.split("\t")[1]).toInt() == 2) {
                                            val reseiveMassage = it.split("\t")[2]
                                            val reseiveMassageMass = reseiveMassage.split(";")
                                            for (i in reseiveMassageMass.indices) {
                                                if (reseiveMassageMass[i].split(":").size == 2) {
                                                    if ((reseiveMassageMass[i].split(":")[0] != "") && (reseiveMassageMass[i].split(":")[1] != "")) {
                                                        println("составляем сообщение из данных=${reseiveMassageMass[i]}     logCommand ")
                                                        val codeData =
                                                            (reseiveMassageMass[i].split(":")[0]).toInt()
                                                        val dataInCode =
                                                            (reseiveMassageMass[i].split(":")[1]).toInt()
                                                        when (codeData) {
                                                            1 -> {
                                                                when (dataInCode) {
                                                                    0 -> {
                                                                        massage += "Инициация ввода болюса. "
                                                                    }
                                                                    1 -> {
                                                                        massage += "Болюс вводится. "
                                                                    }
                                                                    2 -> {
                                                                        massage += "Отмена болюса. "
                                                                    }
                                                                    3 -> {
                                                                        massage += "Успешное завершение болюса. "
                                                                    }
                                                                }
                                                            }
                                                            2 -> {
                                                                massage += getString(
                                                                    R.string.temporary_basal_is_introduced_at_speed_2fu_h_do_you_want_to_cancel_itу,
                                                                    (dataInCode.toFloat()) / 100
                                                                ) + " "
                                                            }
                                                            3 -> {
                                                                massage += getString(
                                                                    R.string.temporary_basal_is_introduced_at_speed_2fu_h_do_you_want_to_cancel_itуx,
                                                                    (dataInCode.toFloat()) / 100
                                                                ) + " "
                                                            }
                                                            5 -> {
                                                                when (dataInCode) {
                                                                    0 -> {
                                                                        massage += "Стандартный болюс. "
                                                                    }
                                                                    1 -> {
                                                                        massage += "Суперболюс. "
                                                                    }
                                                                    2 -> {
                                                                        massage += "Растянутый болюс. "
                                                                    }
                                                                    3 -> {
                                                                        massage += "Квадратный болюс. "
                                                                    }
                                                                    4 -> {
                                                                        massage += "Многоволновой болюс. "
                                                                    }
                                                                    5 -> {
                                                                        massage += "Быстрый болюс (с экрана блокировки). "
                                                                    }
                                                                }
                                                            }
                                                            255 -> {
                                                                when (dataInCode) {
                                                                    1 -> {
                                                                        inMobileAppFlag = true
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (massage != "") {
                                                timestampsListMain.add(timestampReseivingMassage.toString())
                                                if (inMobileAppFlag) { typeCellsListMain.add("type_1") }
                                                else { typeCellsListMain.add("type_3") }
                                                massagesListMain.add(massage)
                                            }
                                        }
                                        if ((it.split("\t")[1]).toInt() == 3) {
                                            val reseiveMassage = it.split("\t")[2]
                                            val reseiveMassageMass = reseiveMassage.split(";")
                                            for (i in reseiveMassageMass.indices) {
                                                if (reseiveMassageMass[i].split(":").size == 2) {
                                                    println("составляем базальное сообщение из данных=${reseiveMassageMass[i]}     logCommand ")
                                                    if ((reseiveMassageMass[i].split(":")[0] != "") && (reseiveMassageMass[i].split(":")[1] != "")) {
                                                        val codeData =
                                                            (reseiveMassageMass[i].split(":")[0]).toInt()
                                                        var dataInCode = 0
                                                        if (codeData != 12) {
                                                            dataInCode =
                                                                (reseiveMassageMass[i].split(":")[1]).toInt()
                                                        }

                                                        when (codeData) {
                                                            1 -> {
                                                                massage += getString(
                                                                    R.string.bhjrbvuhslvbnidsjvkamovk,
                                                                    dataInCode
                                                                ) + " "
                                                            }
                                                            2 -> {
                                                                massage += getString(
                                                                    R.string.bhjrbvuhslvbnidsjvskamovk,
                                                                    ((dataInCode.toFloat()) / 100)
                                                                ) + " "
                                                            }
//                                                    3 -> {
//                                                        massage += getString(R.string.bhjrbvuhslvbnidsjvsksamovk, ((dataInCode.toFloat()) / 100)) + " "
//                                                    }
//                                                    4 -> {
//                                                        massage += getString(R.string.bhjrbvuhslvbnifdsjvsksamovk, (dataInCode / 60)) + " "
//                                                    }
//                                                    5 -> {
//                                                        massage += getString(R.string.bhjrbvuhslvsbnifdsjvsksamovk, (dataInCode / 60)) + " "
//                                                    }
//                                                    6 -> {
//                                                        when (dataInCode) {
//                                                            1 -> { massage += "Активация временного базала. " }
//                                                        }
//                                                    }
//                                                    7 -> {
//                                                        when (dataInCode) {
//                                                            1 -> { massage += "Завершение работы временного базала. " }
//                                                        }
//                                                    }
//                                                    8 -> {
//                                                        when (dataInCode) {
//                                                            0 -> { massage += "Абсолютный временый базал. " }
//                                                            1 -> { massage += "Относительный временный базал. " }
//                                                        }
//                                                    }
//                                                    9 -> {
//                                                        when (dataInCode) {
//                                                            1 -> { massage += "Изменение базального профиля. " }
//                                                        }
//                                                    }
//                                                    10 -> {
//                                                        massage += "Удаление базального профиля №$dataInCode. "
//                                                    }
//                                                    11 -> {
//                                                        when (dataInCode) {
//                                                            1 -> { massage += "Добавление базального профиля. " }
//                                                        }
//                                                    }
//                                                    12 -> {
//                                                        massage += "На помпе $dataInCode базальных профилей. "
//                                                    }
//                                                    13 -> {
//                                                        massage += "Изменение базального профиля №$dataInCode. "
//                                                    }
//                                                    15 -> {
//                                                        when (dataInCode) {
//                                                            0 -> { massage += "Базал вводится. " }
//                                                            1 -> { massage += "Базал остановлен. " }
//                                                        }
//                                                    }
//                                                    16 -> {
//                                                        massage += getString(R.string.bhjrbvuhsdlvsbnifdsjvsksamovk, ((dataInCode.toFloat()) / 100)) + " "
//                                                    }

                                                            255 -> {
                                                                when (dataInCode) {
                                                                    1 -> {
                                                                        inMobileAppFlag = true
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (massage != "") {
                                                timestampsListMain.add(timestampReseivingMassage.toString())
                                                if (inMobileAppFlag) { typeCellsListMain.add("type_1") }
                                                else { typeCellsListMain.add("type_3") }
                                                massagesListMain.add(massage)
                                            }
                                        }
                                        if ((it.split("\t")[1]).toInt() == 12) {
                                            val reseiveMassage = it.split("\t")[2]
                                            val reseiveMassageMass = reseiveMassage.split(";")
                                            for (i in reseiveMassageMass.indices) {
                                                if (reseiveMassageMass[i].split(":").size == 2) {
                                                    if ((reseiveMassageMass[i].split(":")[0] != "") && (reseiveMassageMass[i].split(":")[1] != "")) {
                                                        println("составляем сообщение перезаправки из данных=${reseiveMassageMass[i]}     logCommand ")
                                                        val codeData =
                                                            (reseiveMassageMass[i].split(":")[0]).toInt()
                                                        val dataInCode =
                                                            (reseiveMassageMass[i].split(":")[1]).toInt()
                                                        when (codeData) {
                                                            1 -> {
                                                                when (dataInCode) {
                                                                    6 -> {
                                                                        massage += "Замена резервуара, "
                                                                    }
                                                                }
                                                            }
                                                            2 -> {
                                                                massage += " остаток ${dataInCode / 100} Ед"
                                                            }
                                                            255 -> {
                                                                when (dataInCode) {
                                                                    1 -> {
                                                                        inMobileAppFlag = true
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (massage != "") {
                                                timestampsListMain.add(timestampReseivingMassage.toString())
                                                if (inMobileAppFlag) { typeCellsListMain.add("type_1") }
                                                else { typeCellsListMain.add("type_3") }
                                                massagesListMain.add(massage)
                                            }
                                        }
                                        if ((it.split("\t")[1]).toInt() == 11) {
                                            val reseiveMassage = it.split("\t")[2]
                                            val reseiveMassageMass = reseiveMassage.split(";")
                                            for (i in reseiveMassageMass.indices) {
                                                if (reseiveMassageMass[i].split(":").size == 2) {
                                                    if ((reseiveMassageMass[i].split(":")[0] != "") && (reseiveMassageMass[i].split(":")[1] != "")) {
                                                        println("составляем сообщение остановки из данных=${reseiveMassageMass[i]}     logCommand ")
                                                        val codeData =
                                                            (reseiveMassageMass[i].split(":")[0]).toInt()
                                                        val dataInCode =
                                                            (reseiveMassageMass[i].split(":")[1]).toInt()
                                                        when (codeData) {
                                                            1 -> {
                                                                when (dataInCode) {
                                                                    1 -> {
                                                                        massage += "Активация временной остановки. "
                                                                    }
                                                                }
                                                            }
                                                            2 -> {
                                                                when (dataInCode) {
                                                                    1 -> {
                                                                        massage += "Активация постоянной остановки. "
                                                                    }
                                                                }
                                                            }
                                                            3 -> {
                                                                massage += "Остановка будет длиться $dataInCode минут. "
                                                            }
                                                            4 -> {
                                                                massage += "Временная остановка длиться $dataInCode минут. "
                                                            }
                                                            5 -> {
                                                                when (dataInCode) {
                                                                    1 -> {
                                                                        massage += "Окончание постоянной остановки. "
                                                                    }
                                                                }
                                                            }
                                                            6 -> {
                                                                when (dataInCode) {
                                                                    1 -> {
                                                                        massage += "Окончание временной остановки. "
                                                                    }
                                                                }
                                                            }
                                                            7 -> {
                                                                when (dataInCode) {
                                                                    1 -> {
                                                                        massage += "Отмена временной остановки. "
                                                                    }
                                                                }
                                                            }
                                                            8 -> {
                                                                when (dataInCode) {
                                                                    1 -> {
                                                                        massage += "Отмена постоянной остановки. "
                                                                    }
                                                                }
                                                            }
                                                            255 -> {
                                                                when (dataInCode) {
                                                                    1 -> {
                                                                        inMobileAppFlag = true
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (massage != "") {
                                                timestampsListMain.add(timestampReseivingMassage.toString())
                                                if (inMobileAppFlag) { typeCellsListMain.add("type_1") }
                                                else { typeCellsListMain.add("type_3") }
                                                massagesListMain.add(massage)
                                            }
                                        }
                                    }
                                }
                            }

                            if (countReadLogDays > 0) {
                                maxDayInLog = listDaysLog.maxOrNull()!!

                                showToast("Читаем день: ${(maxDayInLog - countReadLogDays + 1)}")
                                println("displayPumpLogNotify logCommand читаем файл /$maxYearInLog/$maxMonthInLog/${(maxDayInLog - countReadLogDays + 1)}.txt")
                                println("Читаем день: ${(maxDayInLog - countReadLogDays + 1)}")


                                runLogCommand(false, "/$maxYearInLog/$maxMonthInLog/${(maxDayInLog - countReadLogDays + 1)}.txt -r", 2) //${listDaysLog.maxOrNull()}.txt -r")
                                countReadLogDays --
                            } else {
                                timestampLastReadLog = (System.currentTimeMillis()/1000L).toInt()
                                showToast("Чтение лога закончено $timestampLastReadLog")
                                saveInt(TIMESTAMP_LAST_READ_LOG, timestampLastReadLog)
                                saveInt(TIMESTAMP_LAST_LOG_MASSAGE, timestampLastLogMassage)

                                stateLogRead = 0
                                listDaysLog.clear()
                                typeCellsListMain.add("invalidate")
                                saveAllChatLists()
                            }

                            RxUpdateMainEvent.getInstance().updateChat()

                            logString = ""
                        }
                    }

                }
            }.start()
        }
    }
    private fun displayDataBolusAmount(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 2) {
                var temp: Float =
                    (castUnsignedCharToInt(data[0]) * 256).toFloat() // Домножение на 256 эквивалентно умножению на 100 только шестнадцатиричного числа
                temp += castUnsignedCharToInt(data[1])
            }
        }
    }
    private fun displayDataSuperBolusTime(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 2) {
                var temp: Float = (castUnsignedCharToInt(data[0]) * 256).toFloat()
                temp += castUnsignedCharToInt(data[1])
            }
        }
    }
    @SuppressLint("StringFormatMatches")
    private fun displayDataSuperBolusBasalVolume(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 2) {
                var temp: Int = castUnsignedCharToInt(data[0]) * 256
                temp += castUnsignedCharToInt(data[1])
                superBolusBasalVoliume = temp

                showGoBolusDialog(
                    title = getString(R.string.enter_bolus),
                    getString(
                        R.string.do_you_want_enter_super_bolus_whith_2fu_basal_volume,
                        superBolusVoliume.toFloat() / 100,
                        superBolusBasalVoliume.toFloat() / 100,
                        superBolusTime
                    ),
                    superBolusVoliume,
                    0,
                    superBolusTime
                )
            }
        }
    }
    private fun displayDataBolusType() {  }
    private fun displayDataSuperBolusRestrictionLimit(data: ByteArray?) {
        if (data != null) {
            superBolusIsResolved = castUnsignedCharToInt(data[0]) != 0
        }
    }
    private fun displayDataExtendedBolusRestrictionLimit(data: ByteArray?) {
        if (data != null) {
            extendedAndDualPatternBolusIsResolved = castUnsignedCharToInt(data[0]) != 0
        }
    }
    private fun displayDataIOB(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 2) {
                val signMask = 0b10000000

                val sign: Boolean = (((castUnsignedCharToInt(data[0])).and(signMask) shr 7) == 1)
                var temp: Float

                if (sign) {
                    temp = ((data[0]).toInt().inv() shl 8).toFloat()
                    temp += (castUnsignedCharToInt((data[1]).toInt().inv().toByte())).toFloat()
                    temp *= -1
                } else {
                    temp = (castUnsignedCharToInt(data[0]) shl 8).toFloat()
                    temp += castUnsignedCharToInt(data[1]).toFloat()
                }

                iob = (temp / 100)
                binding.titleToolbatSecondTv.text = getString(R.string.iob_4_7_u, iob)
            }
        }
    }
    private fun displayDataCannuleTime(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 2) {
                if (actionState == READ) {
                    var temp =
                        castUnsignedCharToInt(data[0]) * 256 // Домножение на 256 эквивалентно умножению на 100 только шестнадцатиричного числа
                    temp += castUnsignedCharToInt(data[1])
                    cannuleTime = temp
                }
            }
            if (actionState == WRITE) {
                cannuleTime = castUnsignedCharToInt(data[0])
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun displayDataBatteryPercent(data: ByteArray?) {
        if (data != null) {
            battryPercent = castUnsignedCharToInt(data[0])
            binding.percentChargeTitleTv.text = "$battryPercent%"
            if (battryPercent >= 0) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_10))
            if (battryPercent > 10) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_30))
            if (battryPercent > 30) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_50))
            if (battryPercent > 50) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_70))
            if (battryPercent > 70) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_90))
            if (battryPercent > 90) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_100))
        }

        main_battery_iv
    }
    @SuppressLint("SetTextI18n")
    private fun displayDataAkbPercent(data: ByteArray?) {
        if (data != null) {
            liIonPercent = castUnsignedCharToInt(data[0])
            if (liIonPercent < battryPercent) {
                binding.percentChargeTitleTv.text = "$liIonPercent%"
                if (liIonPercent >= 0) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_10))
                if (liIonPercent > 10) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_30))
                if (liIonPercent > 30) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_50))
                if (liIonPercent > 50) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_70))
                if (liIonPercent > 70) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_90))
                if (liIonPercent > 90) binding.mainBatteryIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.battery_100))
            }
        }
    }
    private fun displayDataBalanceDrag(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 2) {
                var temp =
                    castUnsignedCharToInt(data[0]) * 256 // Домножение на 256 эквивалентно умножению на 100 только шестнадцатиричного числа
                temp += castUnsignedCharToInt(data[1])
                reservoirVolume = temp
            }
        }
    }
    private fun displayDataBasalSpeed(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 2) {
                var temp =
                    castUnsignedCharToInt(data[0]) * 256 // Домножение на 256 эквивалентно умножению на 100 только шестнадцатиричного числа
                temp += castUnsignedCharToInt(data[1])
                basalSpeed = temp.toFloat()/100
                binding.bslToolbatSecondTv.text = getString(R.string.bsl_3_7_u_h, basalSpeed)
            }
        }
    }
    private fun displayDataBasalTemporaryTime(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 2) {
                var temp = castUnsignedCharToInt(data[0]) * 256
                temp += castUnsignedCharToInt(data[1])
                temporaryBasalTime = temp
                if (!stayOnTemporaryBasalScreen) {
                    showTemporaryBasalStatusDialog()
                }
            }
        }
    }
    private fun displayDataBasalTemporaryValueAdjustment(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 2) {
                var temp = castUnsignedCharToInt(data[0]) * 256
                temp += castUnsignedCharToInt(data[1])
                temporaryBasalVoliume = temp
            }
        }
    }
    private fun displayDataNumBasalProfiles(data: ByteArray?) {
        if (data != null) {
            numBasalProfiles = castUnsignedCharToInt(data[0])
            println( "readBasalProfiles numBasalProfiles = $numBasalProfiles")
        }
    }
    private fun displayDataNameBasalProfile(data: ByteArray?) {
        if (data != null) {
            nameReadBasalProfile = ""
            nameReadBasalProfile = String(data, charset("Windows-1251"))
            println( "readBasalProfiles nameReadBasalProfile = $nameReadBasalProfile")
        }
    }
    private fun displayDataNumPeriodsModifiedBasalProfile(data: ByteArray?) {
        if (data != null) {
            numBasalProfilePeriods = castUnsignedCharToInt(data[0])
            println( "readBasalProfiles numBasalProfilePeriods = $numBasalProfilePeriods")
        }
    }
    private fun displayDataPeriodBasalProfile(data: ByteArray?) {
        if (actionState == READ) {
            if (data != null) {
                if (data.size >= 6) {
                    val startTime =
                        castUnsignedCharToInt(data[0]) * 256 + castUnsignedCharToInt(data[1])
                    val endTime =
                        castUnsignedCharToInt(data[2]) * 256 + castUnsignedCharToInt(data[3])
                    val basalSpeed =
                        castUnsignedCharToInt(data[4]) * 256 + castUnsignedCharToInt(data[5])
                    var recurringPeriodFlag = false
                    if (countReadPeriodInProfile == 1) {
                        println("if count = 1 readBasalProfiles")
                        createDataChart.add(0)
                        if ((namePeriods.size != 0) || (startTimeAllPeriods.size != 0) || (inputSpeedAllPeriods.size != 0)) {
                            println("if namePeriods.size не пустой в момент начала записи данных нового профиля readBasalProfiles")
                            namePeriods.clear()
                            startTimeAllPeriods.clear()
                            inputSpeedAllPeriods.clear()
                        }
                    }

                    // смотрим, есть ли повторяющиеся периоды при приёме
                    if (startTimeAllPeriods.size > 0) {
                        for (i in 0 until startTimeAllPeriods.size) {
                            if (startTimeAllPeriods[i] == startTime) {
                                println("if принято повторение периода профиля readBasalProfiles countReadPeriodInProfile=$countReadPeriodInProfile  startTime=$startTime")
                                recurringPeriodFlag = true
                                basalProfilePeriodDataRereaadFlag = true
                            }
                        }
                    }

                    // если есть повторы то просто их на пишем
                    if (!recurringPeriodFlag) {
                        println("if добавлен уникальный период профиля readBasalProfiles countReadPeriodInProfile=$countReadPeriodInProfile  startTime=$startTime    endTime=$endTime")
                        namePeriods.add("period profile")
                        startTimeAllPeriods.add(startTime)
                        inputSpeedAllPeriods.add(basalSpeed)
                        for (i in startTime until endTime) {
                            createDataChart.add(basalSpeed)
                        }
                        if (startTime == endTime) {
                            println("if заполняем фейковыми данными потому что пришла хуйня readBasalProfiles")
                            for (i in 0..23) {
                                createDataChart.add(0)
                            }
                        }
                        basalProfilePeriodDataProcessedFlag = true
                    }
                }
            }
        }
    }
    private fun displayDataNumModifiedBasalProfiles(data: ByteArray?) {
        if (data != null) {
            if (actionState == READ) {

                if ((countReadProfile) == castUnsignedCharToInt(data[0])) {
                    basalProfileNumCorrectFlag = true
                    println("if READ читаем правильный профиль readBasalProfiles ")
                } else {
                    basalProfileNumCorrectFlag = false
                    // ситуация, когда записанный мной номер читаемого профиля не совпадает с тем, что там считает помпа (я записал, помпа проигнорила)
                    println("if READ НЕПРАВИЛЬНЫЙ ПРОФИЛЬ! readBasalProfiles отправляемый профиль=${(countReadProfile)}   читаемый номер профиля=${castUnsignedCharToInt(data[0])}")
                }
                // флаг разрешающий двигаться по стейтмашине
                basalProfileNumDataProcessedFlag = true
            }
            if (actionState == WRITE) {
                println("WRITE readBasalProfiles NUM_MODIFIED_BASAL_PROFILES ${castUnsignedCharToInt(data[0])}")
            }

        }
    }
    private fun displayDataNumActiveBasalProfile(data: ByteArray?) {
        if (data != null) {
            if ((castUnsignedCharToInt(data[0]) - 1) < profileNames.size) {
                selectedProfile = castUnsignedCharToInt(data[0]) - 1
                RxUpdateMainEvent.getInstance().updateSelectBasalProfile()
            }
        }
    }
    private fun displayDatePump(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 4) {
                val date = castUnsignedCharToInt(data[0])*256*256*256 + castUnsignedCharToInt(data[1])*256*256 + castUnsignedCharToInt(data[2])*256 + castUnsignedCharToInt(data[3])
                println("displayDatePump=$date logCommand")
                pumpTimestamp = date-10800
            }
        }
    }
    private fun displayTimeWorkPump(data: ByteArray?) {
        if (data != null) {
            if (data.size >= 4) {
                val date = castUnsignedCharToInt(data[0])*256*256*256 + castUnsignedCharToInt(data[1])*256*256 + castUnsignedCharToInt(data[2])*256 + castUnsignedCharToInt(data[3])
                println("displayTimeWorkPump=$date logCommand")
                pumpTimeLive = date
            }
        }
    }

    private fun addNewProfile() {
        if (namePeriods.size < 24) namePeriods.add("add")
        profileNames.removeLast()
        profileNames.add(nameReadBasalProfile)
        if (profileNames.size < MAX_COUNT_PROFILES) profileNames.add("add")
        val cleanDataChart = ArrayList<Int>()
        saveArrayList(TEST_SAVE+countReadProfile , cleanDataChart)
        saveArrayList(TEST_SAVE+countReadProfile , createDataChart)
        dataAllCharts.add(loadArrayList(TEST_SAVE+countReadProfile))
        periodNamesMain.add(convertListStringToArray(namePeriods))
        startTimeAllPeriodsMain.add(convertListIntToArray(startTimeAllPeriods))
        inputSpeedAllPeriodsMain.add(convertListIntToArray(inputSpeedAllPeriods))

        println( "readBasalProfiles addNewProfile countReadPrifile=$countReadProfile")
        println( "readBasalProfiles addNewProfile dataAllCharts=$dataAllCharts")
        println( "readBasalProfiles addNewProfile profileNames=$profileNames")
        println("readBasalProfiles addNewProfile periodNamesMain=${namePeriods}")
        println("readBasalProfiles addNewProfile startTimeAllPeriodsMain=${startTimeAllPeriods}")
        println("readBasalProfiles addNewProfile inputSpeedAllPeriodsMain=${inputSpeedAllPeriods}")

        saveArrayList(PROFILE_NAMES, profileNames)
        saveArrayList(DATA_ALL_CHARTS, dataAllCharts)

        seveArrayStringList(PERIOD_NAMES_MAIN, periodNamesMain)
        seveIntArrayList(START_TIME_ALL_PERIODS_MAIN, startTimeAllPeriodsMain)
        seveIntArrayList(INPUT_SPEED_ALL_PERIODS_MAIN, inputSpeedAllPeriodsMain)
    }
    private fun resetAllLocalLists() {
        createDataChart.clear()
        namePeriods.clear()
        startTimeAllPeriods.clear()
        inputSpeedAllPeriods.clear()
        countReadPeriodInProfile = 1
    }
    private fun resetAllLists() {
        profileNames.clear()
        profileNames.add("add")
        for (i in 0 until dataAllCharts.size){
            dataAllCharts[i].clear()
        }
        dataAllCharts.clear()
        for (i in 0 until periodNamesMain.size){
            val arrayPN = arrayOf<String>()
            periodNamesMain[i] = arrayPN
        }
        periodNamesMain.clear()
        for (i in 0 until startTimeAllPeriodsMain.size){
            val arrayPN = IntArray(0)
            startTimeAllPeriodsMain[i] = arrayPN
        }
        startTimeAllPeriodsMain.clear()
        for (i in 0 until inputSpeedAllPeriodsMain.size){
            val arrayPN = IntArray(0)
            inputSpeedAllPeriodsMain[i] = arrayPN
        }
        inputSpeedAllPeriodsMain.clear()
    }

    private fun displayDataRegister(data: ByteArray?) {
        if (data != null) {
            if (dataSortSemaphore == IOB) displayDataIOB(data)
            if (dataSortSemaphore == SUPPLIES_RSOURCE) displayDataCannuleTime(data)
            if (dataSortSemaphore == BATTERY_PERCENT) displayDataBatteryPercent(data)
            if (dataSortSemaphore == AKB_PERCENT) displayDataAkbPercent(data)
            if (dataSortSemaphore == BALANCE_DRUG) displayDataBalanceDrag(data)
            if (dataSortSemaphore == BASAL_SPEED) displayDataBasalSpeed(data)
            if (dataSortSemaphore == INIT_REFUELLING) {}
            if (dataSortSemaphore == BASAL_TEMPORARY_VALUE_ADJUSTMENT) displayDataBasalTemporaryValueAdjustment(data)
            if (dataSortSemaphore == BASAL_TEMPORARY_TIME) displayDataBasalTemporaryTime(data)
            if (dataSortSemaphore == BASAL_TEMPORARY_PERFORMANCE) {}
            if (dataSortSemaphore == BASAL_TEMPORARY_TYPE_ADJUSTMENT) {}
            if (dataSortSemaphore == NUM_BASAL_PROFILES) displayDataNumBasalProfiles(data)
            if (dataSortSemaphore == NUM_MODIFIED_BASAL_PROFILES) displayDataNumModifiedBasalProfiles(data)
            if (dataSortSemaphore == NAME_BASAL_PROFILE) displayDataNameBasalProfile(data)
            if (dataSortSemaphore == NUM_PERIODS_MODIFIED_BASAL_PROFILE) displayDataNumPeriodsModifiedBasalProfile(data)
            if (dataSortSemaphore == NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE) {}
            if (dataSortSemaphore == PERIOD_BASAL_PROFILE_DATA) displayDataPeriodBasalProfile(data)
            if (dataSortSemaphore == BASAL_LOCK_CONTROL) {}
            if (dataSortSemaphore == ACTIVATE_BASAL_PROFILE) {}
            if (dataSortSemaphore == DELETE_BASAL_PROFILE) {}
            if (dataSortSemaphore == NUM_ACTIVE_BASAL_PROFILE) displayDataNumActiveBasalProfile(data)
            if (dataSortSemaphore == DATE) displayDatePump(data)
            if (dataSortSemaphore == TIME_WORK_PUMP) displayTimeWorkPump(data)

            if (dataSortSemaphore == BOLUS_DELETE_CONFIRM) {}
            if (dataSortSemaphore == BOLUS_DELETE) {}
            if (dataSortSemaphore == EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG) { displayDataExtendedBolusRestrictionLimit(data) }
            if (dataSortSemaphore == SUPER_BOLUS_RESTRICTION_FLAG) displayDataSuperBolusRestrictionLimit(data)
            if (dataSortSemaphore == BOLUS_TYPE) displayDataBolusType()
            if (dataSortSemaphore == BOLUS_AMOUNT) displayDataBolusAmount(data)
            if (dataSortSemaphore == SUPER_BOLUS_TIME) displayDataSuperBolusTime(data)
            if (dataSortSemaphore == SUPER_BOLUS_BASL_VOLIUM) displayDataSuperBolusBasalVolume(data)
        }
        globalSemaphore = true
    }
    private fun displayLogPointer() {
        globalSemaphore = true
        println( "displayLogPointer logCommand globalSemaphore=$globalSemaphore")
    }
    private fun moveDataSortSemaphore() {
        if (contentEquals(readRegisterPointer!!, IOB_REGISTER)) {
            dataSortSemaphore = IOB
        }
        if (contentEquals(readRegisterPointer!!, SUPPLIES_RSOURCE_REGISTER)) {
            dataSortSemaphore = SUPPLIES_RSOURCE
        }
        if (contentEquals(readRegisterPointer!!, BATTERY_PERCENT_REGISTER)) {
            dataSortSemaphore = BATTERY_PERCENT
        }
        if (contentEquals(readRegisterPointer!!, AKB_PERCENT_REGISTER)) {
            dataSortSemaphore = AKB_PERCENT
        }
        if (contentEquals(readRegisterPointer!!, BALANCE_DRUG_REGISTER)) {
            dataSortSemaphore = BALANCE_DRUG
        }
        if (contentEquals(readRegisterPointer!!, BASAL_SPEED_REGISTER)) {
            dataSortSemaphore = BASAL_SPEED
        }
        if (contentEquals(readRegisterPointer!!, INIT_REFUELLING_REGISTER)) {
            dataSortSemaphore = INIT_REFUELLING
        }
        if (contentEquals(readRegisterPointer!!, BASAL_TEMPORARY_VALUE_ADJUSTMENT_REGISTER)) {
            dataSortSemaphore = BASAL_TEMPORARY_VALUE_ADJUSTMENT
        }
        if (contentEquals(readRegisterPointer!!, BASAL_TEMPORARY_TIME_REGISTER)) {
            dataSortSemaphore = BASAL_TEMPORARY_TIME
        }
        if (contentEquals(readRegisterPointer!!, BASAL_TEMPORARY_PERFORMANCE_REGISTER)) {
            dataSortSemaphore = BASAL_TEMPORARY_PERFORMANCE
        }
        if (contentEquals(readRegisterPointer!!, BASAL_TEMPORARY_TYPE_ADJUSTMENT_REGISTER)) {
            dataSortSemaphore = BASAL_TEMPORARY_TYPE_ADJUSTMENT
        }
        if (contentEquals(readRegisterPointer!!, NUM_BASAL_PROFILES_REGISTER)) {
            dataSortSemaphore = NUM_BASAL_PROFILES
        }
        if (contentEquals(readRegisterPointer!!, NUM_MODIFIED_BASAL_PROFILES_REGISTER)) {
            dataSortSemaphore = NUM_MODIFIED_BASAL_PROFILES
        }
        if (contentEquals(readRegisterPointer!!, NAME_BASAL_PROFILE_REGISTER)) {
            dataSortSemaphore = NAME_BASAL_PROFILE
        }
        if (contentEquals(readRegisterPointer!!, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER)) {
            dataSortSemaphore = NUM_PERIODS_MODIFIED_BASAL_PROFILE
        }
        if (contentEquals(readRegisterPointer!!, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER)) {
            dataSortSemaphore = NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE
        }
        if (contentEquals(readRegisterPointer!!, PERIOD_BASAL_PROFILE_DATA_REGISTER)) {
            dataSortSemaphore = PERIOD_BASAL_PROFILE_DATA
        }
        if (contentEquals(readRegisterPointer!!, BASAL_LOCK_CONTROL_REGISTER)) {
            dataSortSemaphore = BASAL_LOCK_CONTROL
        }
        if (contentEquals(readRegisterPointer!!, ACTIVATE_BASAL_PROFILE_REGISTER)) {
            dataSortSemaphore = ACTIVATE_BASAL_PROFILE
        }
        if (contentEquals(readRegisterPointer!!, DELETE_BASAL_PROFILE_REGISTER)) {
            dataSortSemaphore = DELETE_BASAL_PROFILE
        }
        if (contentEquals(readRegisterPointer!!, NUM_ACTIVE_BASAL_PROFILES_REGISTER)) {
            dataSortSemaphore = NUM_ACTIVE_BASAL_PROFILE
        }
        if (contentEquals(readRegisterPointer!!, DATE_REGISTER)) {
            dataSortSemaphore = DATE
        }
        if (contentEquals(readRegisterPointer!!, TIME_WORK_PUMP_REGISTER)) {
            dataSortSemaphore = TIME_WORK_PUMP
        }



        if (contentEquals(readRegisterPointer!!, BOLUS_DELETE_CONFIRM_REGISTER)) {
            dataSortSemaphore = BOLUS_DELETE_CONFIRM
        }
        if (contentEquals(readRegisterPointer!!, BOLUS_DELETE_REGISTER)) {
            dataSortSemaphore = BOLUS_DELETE
        }
        if (contentEquals(readRegisterPointer!!, EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG_REGISTER)) {
            dataSortSemaphore = EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG
        }
        if (contentEquals(readRegisterPointer!!, SUPER_BOLUS_RESTRICTION_FLAG_REGISTER)) {
            dataSortSemaphore = SUPER_BOLUS_RESTRICTION_FLAG
        }
        if (contentEquals(readRegisterPointer!!, BOLUS_TYPE_REGISTER)) {
            dataSortSemaphore = BOLUS_TYPE
        }
        if (contentEquals(readRegisterPointer!!, BOLUS_AMOUNT_REGISTER)) {
            dataSortSemaphore = BOLUS_AMOUNT
        }
        if (contentEquals(readRegisterPointer!!, BOLUS_ACTIVATE_REGISTER)) {
            dataSortSemaphore = BOLUS_ACTIVATE
        }
        if (contentEquals(readRegisterPointer!!, SUPER_BOLUS_TIME_REGISTER)) {
            dataSortSemaphore = SUPER_BOLUS_TIME
        }
        if (contentEquals(readRegisterPointer!!, SUPER_BOLUS_BASL_VOLIUM_REGISTER)) {
            dataSortSemaphore = SUPER_BOLUS_BASL_VOLIUM
        }
    }

    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {
//        System.err.println("DeviceControlActivity------->   момент начала выстраивания списка параметров")
        if (gattServices == null) return
        var uuid: String?
        val unknownServiceString = ("unknown_service")
        val unknownCharaString =("unknown_characteristic")
        val gattServiceData = ArrayList<HashMap<String, String?>>()
        val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String?>>>()
        mGattCharacteristics = java.util.ArrayList()


        // Loops through available GATT Services.
        for (gattService in gattServices) {
            val currentServiceData = HashMap<String, String?>()
            uuid = gattService.uuid.toString()
            currentServiceData[listName] = lookup(uuid, unknownServiceString)
            currentServiceData[listUUID] = uuid
            gattServiceData.add(currentServiceData)
            val gattCharacteristicGroupData = ArrayList<HashMap<String, String?>>()
            val gattCharacteristics = gattService.characteristics
            val charas = ArrayList<BluetoothGattCharacteristic>()

            // Loops through available Characteristics.
            for (gattCharacteristic in gattCharacteristics) {
                charas.add(gattCharacteristic)
                val currentCharaData = HashMap<String, String?>()
                uuid = gattCharacteristic.uuid.toString()
                currentCharaData[listName] = lookup(uuid, unknownCharaString)
                currentCharaData[listUUID] = uuid
                gattCharacteristicGroupData.add(currentCharaData)
//                System.err.println("------->   ХАРАКТЕРИСТИКА: $uuid")
            }
            mGattCharacteristics.add(charas)
            gattCharacteristicData.add(gattCharacteristicGroupData)
        }
        val gattServiceAdapter = SimpleExpandableListAdapter(
            this,
            gattServiceData,
            android.R.layout.simple_expandable_list_item_2, arrayOf(listName, listUUID), intArrayOf(android.R.id.text1, android.R.id.text2),
            gattCharacteristicData,
            android.R.layout.simple_expandable_list_item_2, arrayOf(listName, listUUID), intArrayOf(android.R.id.text1, android.R.id.text2))
        mGattServicesList!!.setAdapter(gattServiceAdapter)
        if (mScanning) { scanLeDevice(false) }
    }
    private fun reconnectThread() {
//        System.err.println("--> reconnectThread started")
        var j = 1
        val reconnectThread = Thread {
            while (reconnectThreadFlag) {
                runOnUiThread {
                    if(j % 5 == 0) {
                        reconnectThreadFlag = false
//                        scanLeDevice(true)
//                        System.err.println("DeviceControlActivity------->   Переподключение со сканированием №$j")
                    } else {
                        reconnect()
//                        System.err.println("DeviceControlActivity------->   Переподключение без сканирования №$j")
                    }
                    j++
                }
                try {
                    Thread.sleep(RECONNECT_BLE_PERIOD.toLong())
                } catch (ignored: Exception) { }
            }
        }
        reconnectThread.start()
    }
    override fun reconnect () {
        //полное завершение сеанса связи и создание нового в onResume
        if (mBluetoothLeService != null) {
            unbindService(mServiceConnection)
            mBluetoothLeService = null
        }

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)

        //BLE
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
        if (mBluetoothLeService != null) {
            mBluetoothLeService!!.connect(getString(CONNECTES_DEVICE_ADDRESS))
        } else {
//            println("--> вызываем функцию коннекта к устройству $connectedDevice = null")
        }
    }
    override fun disconnect () {
        pumpStatusNotifyDataThreadFlag = false
        if (mBluetoothLeService != null) {
            println("--> дисконнектим всё к хуям и анбайндим")
            mBluetoothLeService!!.disconnect()
            unbindService(mServiceConnection)
            mBluetoothLeService = null
        }
        mConnected = false
        endFlag = true
        runOnUiThread {
            status_connection_tv.text = getString(R.string.offline)
            status_connection_tv.setTextColor(Color.rgb(255, 49,49))
            mGattServicesList!!.setAdapter(null as SimpleExpandableListAdapter?)
        }
        invalidateOptionsMenu()
        percentSynchronize = 0

        if(!reconnectThreadFlag && !mScanning && !inScanFragmentFlag){
            reconnectThreadFlag = true
            reconnectThread()
        }
        flagScanWithoutConnect = true
    }
    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
        return intentFilter
    }
    override fun scanLeDevice(enable: Boolean) {
        if (enable) {
            mScanning = true
            scanList = reinitScanList()
            mBluetoothAdapter!!.startLeScan(mLeScanCallback)
            System.err.println("DeviceControlActivity------->   startLeScan flagScanWithoutConnect=$flagScanWithoutConnect")
        } else {
            mScanning = false
            mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
            System.err.println("DeviceControlActivity------->   stopLeScan flagScanWithoutConnect=$flagScanWithoutConnect")
        }
    }
    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, _, _ ->
        runOnUiThread {
            if (device.name != null) {
//                System.err.println("------->   ===============найден девайс: " + device.address +"-"+ device.name +"==============")
                if (device.address == connectedDeviceAddress) {
//                    System.err.println("------->   ==========это нужный нам девайс $device  $flagScanWithoutConnect ==============")
                    System.err.println("DeviceControlActivity------->   mLeScanCallback flagScanWithoutConnect=$flagScanWithoutConnect")
                    if (!flagScanWithoutConnect) {
                        scanLeDevice(false)
                        reconnectThreadFlag = true
                        reconnectThread()
                    }
                }
                addLEDeviceToScanList(device.name, device)
            }
        }
    }
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.title_location_permission)
                    .setMessage(R.string.text_location_permission)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION)
                        scanLeDevice(true)
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION)
            }
        }

        //проверка включена ли геолокация и если выключена, то показ предложения её включить
        val lm = this.getSystemService(LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ignored: java.lang.Exception) {
        }
        if (!gpsEnabled) {
            // notify user
            showLocationPermissionDialog()
        }
    }
    private fun addLEDeviceToScanList(item: String, device: BluetoothDevice?) {
        var canAdd = true
        for (i in scanList.indices) {
            if (device != null) {
                if (scanList[i].getAddr() == device.address) {
                    canAdd = false
                }
            }
        }
        if (canAdd) {
            if (device != null) {
                scanList.add(ScanItem(item, device.address.toString()))
                RxUpdateMainEvent.getInstance().updateScanList()
            }
        }
    }
    override fun bleCommand(byteArray: ByteArray?, command: String, typeCommand: String){
//        println("пароль command=$command")
        for (i in mGattCharacteristics.indices) {
            for (j in mGattCharacteristics[i].indices) {
//                println("пароль mGattCharacteristics[i][j]=${mGattCharacteristics[i][j].uuid}")
                if (mGattCharacteristics[i][j].uuid.toString() == command) {
                    mCharacteristic = mGattCharacteristics[i][j]
//                    println("пароль command=$command")
                    if (typeCommand == WRITE){
//                        println("пароль WRITE")
                        if (mCharacteristic?.properties!! and BluetoothGattCharacteristic.PROPERTY_WRITE > 0) {
                            mCharacteristic?.value = byteArray
//                            println("пароль WRITE!!!!")
                            mBluetoothLeService?.writeCharacteristic(mCharacteristic)
                        }
                    }

                    if (typeCommand == READ){
                        if (mCharacteristic?.properties!! and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                            mBluetoothLeService?.readCharacteristic(mCharacteristic)
                        }
                    }

                    if (typeCommand == NOTIFY){
                        if (mCharacteristic?.properties!! and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                            mNotifyCharacteristic = mCharacteristic
                            mBluetoothLeService!!.setCharacteristicNotification(
                                mCharacteristic, true)
                        }
                    }

                }
            }
        }
    }

    /**
     * Отправка команды кода подключения
     */
    override fun runConnectToPump(pass : ByteArray?, showInfoDialogs: Boolean, countRestart: Int) {
        getConnectToPump(pass, showInfoDialogs, countRestart).let { queue.put(it) }
    }
    private fun getConnectToPump(pass : ByteArray?, showInfoDialogs: Boolean, countRestart: Int): Runnable { return Runnable { connectToPump(pass, showInfoDialogs, countRestart) } }
    private fun connectToPump(pass : ByteArray?, showInfoDialogs: Boolean, countRestart: Int) {
        val info = "startCheckPumpPass"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва
        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        bleCommand(pass, PASS_DATA, WRITE)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        bleCommand(pass, PASS_DATA, READ)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        if (passRequest == 0) {
                            if (showInfoDialogs) {
                                runOnUiThread {
                                    showInfoDialog("Код правильный", "Подключение к помпе успешно", true)
                                }
                            }
                            saveString(CONNECTION_PASSWORD, connectionPassword)
                            bleCommand(null, NOTIFICATION_PUMP_STATUS, READ)//второй запрос нотификации(контрольный)
                            updatingImportantParametersThread()

                        } else {
                            if (passRequest == 1) {
                                if (showInfoDialogs) {
                                    runOnUiThread {
                                        showInfoDialog("Код не правильный", "Подключение с помпой не установлено", false)
                                    }
                                }
                                inScanFragmentFlag = true // просто удобный флаг чтобы отключить повторные попытки подключения
                                disconnect()
                            }
                        }
                        state += 1
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                        showToast("верификация пройдена $passRequest")
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    if (countRestart != 0) {connectToPump(pass, showInfoDialogs, (countRestart-1))}
                    else {showToast("связи с помпой нет")}
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) {
            }
        }
    }
    private fun updatingImportantParametersThread() {
        val reconnectThreadupdatingImportantParametersThread = Thread {
            while (mConnected) {
                runIobRegister(2)
                runCannuleTimeRegister(2, false)
                runBatteryRegister(2)
                runAkbRegister(2)
                runBalanceDragRegister(2)
                runReadBasalSpeed(2)
                try {
                    Thread.sleep(30000)
                } catch (ignored: Exception) { }
            }
        }
        reconnectThreadupdatingImportantParametersThread.start()
    }

    /**
     * Чтение/запись регистра IOB
     */
    private fun runIobRegister(countRestart: Int) { getIobRegister(countRestart).let { queue.put(it) } }
    private fun getIobRegister(countRestart: Int): Runnable { return Runnable { iobRegister(countRestart) } }
    private fun iobRegister(countRestart: Int) {
        val info = "iobRegister"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, IOB_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, IOB_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, IOB_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, IOB_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        globalSemaphore = false
                        state = 0
//                        showToast("Iob считан")
                    }
                }
                count = 0
            }
            else {
                count++
                System.err.println("Количество запросов без ответа $info = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    if (countRestart > 0) {runIobRegister(countRestart-1)}
                    else {
                        showToast("iob не считан")
                    }
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Чтение/запись регистра расходных материалов
     */
    override fun runCannuleTimeRegister(countRestart: Int, resetTimer: Boolean) { getCannuleTimeRegister(countRestart, resetTimer).let { queue.put(it) } }
    private fun getCannuleTimeRegister(countRestart: Int, reset: Boolean): Runnable { return Runnable { cannuleTimeRegister(countRestart, reset) } }
    private fun cannuleTimeRegister(countRestart: Int, reset: Boolean) {
        val info = "cannuleTimeState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, SUPPLIES_RSOURCE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, SUPPLIES_RSOURCE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, SUPPLIES_RSOURCE_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        if (reset) {writeRegister(3, state, SUPPLIES_RSOURCE_REGISTER, byteArrayOf(0x00))  }
                        else { readRegister(3, state, SUPPLIES_RSOURCE_REGISTER) }
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        globalSemaphore = false
                        state = 0
//                        if (reset) { showToast("ресурс расходных материалов записан") }
//                        else { showToast("ресурс расходных материалов считан") }
                    }
                }
                count = 0
            }
            else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    if (countRestart > 0) {runCannuleTimeRegister(countRestart-1, reset)}
                    else {
                        showToast("расходные материалы не записаны/считаны")
                    }
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Чтение регистра процента заряда батареи
     */
    private fun runBatteryRegister(countRestart: Int) { getBatteryRegister(countRestart).let { queue.put(it) } }
    private fun getBatteryRegister(countRestart: Int): Runnable { return Runnable { batteryRegister(countRestart) } }
    private fun batteryRegister(countRestart: Int) {
        val info = "batteryState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, BATTERY_PERCENT_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, BATTERY_PERCENT_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, BATTERY_PERCENT_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, BATTERY_PERCENT_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        globalSemaphore = false
                        state = 0
//                        showToast("заряд батареи считан")
                    }
                }
                count = 0
            }
            else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    if (countRestart > 0) {runBatteryRegister(countRestart-1)}
                    else {
                        showToast("уровень заряда батареи не считан")
                    }
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Чтение регистра процента заряда аккумулятора
     */
    private fun runAkbRegister(countRestart: Int) { getAkbRegister(countRestart).let { queue.put(it) } }
    private fun getAkbRegister(countRestart: Int): Runnable { return Runnable { akbRegister(countRestart) } }
    private fun akbRegister(countRestart: Int) {
        val info = "akbState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, AKB_PERCENT_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, AKB_PERCENT_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, AKB_PERCENT_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, AKB_PERCENT_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        globalSemaphore = false
                        state = 0
//                        showToast("заряд акб считан")
                    }
                }
                count = 0
            }
            else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    if (countRestart > 0) {runAkbRegister(countRestart-1)}
                    else {
                        showToast("уровень заряда акб не считан")
                    }
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Чтение регистра остатка препарата
     */
    private fun runBalanceDragRegister(countRestart: Int) { getBalanceDragRegister(countRestart).let { queue.put(it) } }
    private fun getBalanceDragRegister(countRestart: Int): Runnable { return Runnable { balanceDragRegister(countRestart) } }
    private fun balanceDragRegister(countRestart: Int) {
        val info = "balanceDragState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, BALANCE_DRUG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, BALANCE_DRUG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, BALANCE_DRUG_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, BALANCE_DRUG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        globalSemaphore = false
                        state = 0
//                        showToast("оставшийся инсулин считан")
                    }
                }
                count = 0
            }
            else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    if (countRestart > 0) {runBalanceDragRegister(countRestart-1)}
                    else {
                        showToast("кол-во остатка препарата не считано")
                    }
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Чтение текущей базальной скорости
     */
    private fun runReadBasalSpeed(countRestart: Int) { getReadBasalSpeed(countRestart).let { queue.put(it) } }
    private fun getReadBasalSpeed(countRestart: Int): Runnable { return Runnable { readBasalSpeedRegister(countRestart) } }
    private fun readBasalSpeedRegister(countRestart: Int) {
        val info = "readBasalSpeedState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, BASAL_SPEED_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, BASAL_SPEED_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, BASAL_SPEED_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, BASAL_SPEED_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        globalSemaphore = false
                        state = 0
                    }
                }
                count = 0
            }
            else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    if (countRestart > 0) {runReadBasalSpeed(countRestart-1)}
                    else {
                        showToast("текущая базальная скорость не считана")
                    }
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Алгоритм чтения активного базального профиля
     */
    private fun runReadActiveBasalProfile() { getReadActiveBasalProfile().let { queue.put(it) } }
    private fun getReadActiveBasalProfile(): Runnable { return Runnable { readActiveBasalProfile() } }
    private fun readActiveBasalProfile () {
        val info = "readActiveBasalProfileState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, NUM_ACTIVE_BASAL_PROFILES_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, NUM_ACTIVE_BASAL_PROFILES_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, NUM_ACTIVE_BASAL_PROFILES_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, NUM_ACTIVE_BASAL_PROFILES_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    showToast("номер активного базального профиля не считан")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Инициация перезаправки
     */
    override fun runInitRefillingRegister(countRestart: Int) { getInitRefillingRegister(countRestart).let { queue.put(it) } }
    private fun getInitRefillingRegister(countRestart: Int): Runnable { return Runnable { initRefillingRegister(countRestart) } }
    private fun initRefillingRegister(countRestart: Int) {
        val info = "initRefillingState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, INIT_REFUELLING_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, INIT_REFUELLING_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, INIT_REFUELLING_REGISTER, byteArrayOf(0x01))
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, INIT_REFUELLING_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        globalSemaphore = false
                        state = 0
                        showToast("перезаправка начата")
                    }
                }
                count = 0
            }
            else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    if (countRestart != 0) {runInitRefillingRegister(countRestart-1)}
                    else {showToast("перезаправка не инициализирована")}
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Алгоритм записи болюса
     */
    private fun runBolus(numberOfHundredthsInsulin: Int) { getBolus(numberOfHundredthsInsulin).let { queue.put(it) } }
    private fun getBolus(numberOfHundredthsInsulin: Int): Runnable { return Runnable { bolus(numberOfHundredthsInsulin) } }
    private fun bolus (numberOfHundredthsInsulin: Int) {
        val info = "bolusState"
        val transmitInsulinArray: ByteArray = castIntToByteArray(numberOfHundredthsInsulin)
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }

                    4 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    5 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                    }
                    7 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    9 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    10 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                    }
                    11 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    12 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                        showToast("болюс активирован")
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    showToast("болус не активирован")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм записи супер болюса
     */
    override fun runSuperBolus(numberOfHundredthsInsulin: Int, timeSuperBolus: Int ) {
        getSuperBolus(numberOfHundredthsInsulin,timeSuperBolus).let { queue.put(it) }
    }
    private fun getSuperBolus(numberOfHundredthsInsulin: Int, timeSuperbolus: Int): Runnable { return Runnable { superBolus(numberOfHundredthsInsulin,timeSuperbolus) } }
    private fun superBolus (numberOfHundredthsInsulin: Int, timeSuperbolus: Int) {
        val info = "superBolusState"
        val transmitInsulinArray: ByteArray = castIntToByteArray(numberOfHundredthsInsulin)
        val transmitTimeArray: ByteArray = castIntToByteArray(timeSuperbolus)
        var count = 0
        var countSB = 0
        superBoluseGoFlag = false
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, SUPER_BOLUS_RESTRICTION_FLAG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, SUPER_BOLUS_RESTRICTION_FLAG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, SUPER_BOLUS_RESTRICTION_FLAG_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, SUPER_BOLUS_RESTRICTION_FLAG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        if (superBolusIsResolved) {
                            state += 1
                        } else {
                            endFlag = true
                            state -= 4
                            globalSemaphore = false
                            showToast("супер болюс запрещён на помпе")
                        }
                    }
                    5 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    7 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    9 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    10 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    11 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                    }
                    12 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    13 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, SUPER_BOLUS_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    14 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, SUPER_BOLUS_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    15 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, SUPER_BOLUS_TIME_REGISTER, transmitTimeArray)
                    }
                    16 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, SUPER_BOLUS_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    17 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, SUPER_BOLUS_BASL_VOLIUM_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    18 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, SUPER_BOLUS_BASL_VOLIUM_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    19 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, SUPER_BOLUS_BASL_VOLIUM_REGISTER)
                    }
                    20 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, SUPER_BOLUS_BASL_VOLIUM_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    21 -> {
                        System.err.println("$info = $state")
                        if (superBoluseGoFlag) { state += 1 }
                        else  {
                            countSB += 1
                            if (countSB == 10000) {
                                endFlag = true
                                state = 0
                                countSB = 0
                            }
                        }
                    }

                    22 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    23 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    24 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                    }
                    25 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    26 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                        showToast("болюс активирован")
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    countSB = 0
                    showToast("суперболюс не активирован")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм записи растянутого болюса
     */
    private fun runExtendedBolus(numberOfHundredthsInsulin: Int, timeBolus: Int ) {
        getExtendedBolus(numberOfHundredthsInsulin, timeBolus).let { queue.put(it) }
    }
    private fun getExtendedBolus(numberOfHundredthsInsulin: Int, timeBolus: Int): Runnable { return Runnable { extendedBolus(numberOfHundredthsInsulin,timeBolus) } }
    private fun extendedBolus (numberOfHundredthsInsulin: Int, timeBolus: Int) {
        val info = "extendedBolusState"
        val transmitInsulinArray: ByteArray = castIntToByteArray(numberOfHundredthsInsulin)
        val transmitTimeArray: ByteArray = castIntToByteArray(timeBolus)
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        if (extendedAndDualPatternBolusIsResolved) {
                            state += 1
                        } else {
                            endFlag = true
                            state -= 4
                            globalSemaphore = false
                            showToast("растянутый болюс запрещён на помпе")
                        }
                    }
                    5 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    7 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    9 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    10 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    11 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                    }
                    12 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    13 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, EXTENDED_AND_DUAL_PATTERN_BOLUS_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    14 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, EXTENDED_AND_DUAL_PATTERN_BOLUS_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    15 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, EXTENDED_AND_DUAL_PATTERN_BOLUS_TIME_REGISTER, transmitTimeArray)
                    }
                    16 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, EXTENDED_AND_DUAL_PATTERN_BOLUS_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    17 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    18 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    19 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                    }
                    20 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    21 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                        showToast("болюс активирован")
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    showToast("растянутый болюс не активирован")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм записи многоволнового болюса
     */
    private fun runDualPatternBolus(numberOfHundredthsInsulin: Int, numberOfHundredStrechedInsulin: Int, timeBolus: Int ) {
        getDualPatternBolus(numberOfHundredthsInsulin, numberOfHundredStrechedInsulin, timeBolus).let { queue.put(it) }
    }
    private fun getDualPatternBolus(numberOfHundredthsInsulin: Int, numberOfHundredStrechedInsulin: Int, timeBolus: Int): Runnable { return Runnable { dualPatternBolus(numberOfHundredthsInsulin,numberOfHundredStrechedInsulin,timeBolus) } }
    private fun dualPatternBolus (numberOfHundredthsInsulin: Int, numberOfHundredStrechedInsulin: Int, timeBolus: Int) {
        val info = "dualPatternBolusState"
        val transmitInsulinArray: ByteArray = castIntToByteArray(numberOfHundredthsInsulin)
        val transmitStrechedInsulinArray: ByteArray = castIntToByteArray(numberOfHundredStrechedInsulin)
        val transmitTimeArray: ByteArray = castIntToByteArray(timeBolus)
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, EXTENEDED_AND_DUAL_PATTERN_BOLUS_RESTRICTION_FLAG_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        if (extendedAndDualPatternBolusIsResolved) {
                            state += 1
                        } else {
                            endFlag = true
                            state -= 4
                            globalSemaphore = false
                            showToast("многоволновой болюс запрещён на помпе")
                        }
                    }
                    5 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    7 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_TYPE_REGISTER, byteArrayOf(bolusType.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    9 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    10 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    11 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                    }
                    12 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_AMOUNT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    13 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_STRECHED_AMOUNT_REGISTER, transmitStrechedInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    14 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_STRECHED_AMOUNT_REGISTER, transmitStrechedInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    15 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_STRECHED_AMOUNT_REGISTER, transmitStrechedInsulinArray)
                    }
                    16 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_STRECHED_AMOUNT_REGISTER, transmitStrechedInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    17 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, EXTENDED_AND_DUAL_PATTERN_BOLUS_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    18 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, EXTENDED_AND_DUAL_PATTERN_BOLUS_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    19 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, EXTENDED_AND_DUAL_PATTERN_BOLUS_TIME_REGISTER, transmitTimeArray)
                    }
                    20 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, EXTENDED_AND_DUAL_PATTERN_BOLUS_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    21 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    22 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    23 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                    }
                    24 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_ACTIVATE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    25 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                        showToast("болюс активирован")
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    showToast("мнроговолновой болюс не активирован")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Отправка команды удаления болюса их конвейера
     */
    private fun runDeleteBolus(countConveyor: Int) { getDeleteBolus(countConveyor).let { queue.put(it) } }
    private fun getDeleteBolus(countConveyor: Int): Runnable { return Runnable { deleteBolus(countConveyor) } }
    private fun deleteBolus(countConveyor: Int) {
        val info = "deleteBolus"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва
        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BOLUS_DELETE_REGISTER, byteArrayOf(countConveyor.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BOLUS_DELETE_REGISTER, byteArrayOf(countConveyor.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BOLUS_DELETE_REGISTER, byteArrayOf(countConveyor.toByte()))
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BOLUS_DELETE_REGISTER, byteArrayOf(countConveyor.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                        showToast("болюс №$countConveyor в конвеере удалён")
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    showToast("болюс не удалён из конвейера")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Алгоритм записи временного базала
     */
    override fun runTemporaryBasal(numberTemporaryBasalVelocity: Int, timeBasal: Int) {
        getTemporaryBasal(numberTemporaryBasalVelocity, timeBasal).let { queue.put(it) }
    }
    private fun getTemporaryBasal(numberTemporaryBasalVelocity: Int, timeBasal: Int): Runnable { return Runnable { temporaryBasal(numberTemporaryBasalVelocity, timeBasal) } }
    private fun temporaryBasal (numberTemporaryBasalVelocity: Int, timeBasal: Int) {
        val info = "temporaryBasalState"
        val transmitInsulinArray: ByteArray = castIntToByteArray(numberTemporaryBasalVelocity)
        val transmitTimeArray: ByteArray = castIntToByteArray(timeBasal)
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_TEMPORARY_VALUE_ADJUSTMENT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_TEMPORARY_VALUE_ADJUSTMENT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_TEMPORARY_VALUE_ADJUSTMENT_REGISTER, transmitInsulinArray)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_TEMPORARY_VALUE_ADJUSTMENT_REGISTER, transmitInsulinArray)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_TEMPORARY_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    5 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_TEMPORARY_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_TEMPORARY_TIME_REGISTER, transmitTimeArray)
                    }
                    7 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_TEMPORARY_TIME_REGISTER, transmitTimeArray)
                        globalSemaphore = false
                        state += 1
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_TEMPORARY_TYPE_ADJUSTMENT_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    9 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_TEMPORARY_TYPE_ADJUSTMENT_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    10 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_TEMPORARY_TYPE_ADJUSTMENT_REGISTER, byteArrayOf(0x01))
                    }
                    11 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_TEMPORARY_TYPE_ADJUSTMENT_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    12 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_TEMPORARY_PERFORMANCE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    13 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_TEMPORARY_PERFORMANCE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    14 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_TEMPORARY_PERFORMANCE_REGISTER, byteArrayOf(0x01))
                    }
                    15 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_TEMPORARY_PERFORMANCE_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    16 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                        showToast("временный базал активирован")
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    showToast("временный базал не активирован")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм чтения временного базала
     */
    override fun runTemporaryBasalStatus() { getTemporaryBasalStatus().let { queue.put(it) } }
    private fun getTemporaryBasalStatus(): Runnable { return Runnable { temporaryBasalStatus() } }
    private fun temporaryBasalStatus () {
        val info = "temporaryBasalStatusState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, BASAL_TEMPORARY_VALUE_ADJUSTMENT_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, BASAL_TEMPORARY_VALUE_ADJUSTMENT_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, BASAL_TEMPORARY_VALUE_ADJUSTMENT_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, BASAL_TEMPORARY_VALUE_ADJUSTMENT_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, BASAL_TEMPORARY_TIME_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    5 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, BASAL_TEMPORARY_TIME_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, BASAL_TEMPORARY_TIME_REGISTER)
                    }
                    7 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, BASAL_TEMPORARY_TIME_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
//                        showToast("временный базал активирован")
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    showToast("временный базал не прочитан")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм отмены временного базала
     */
    private fun runTemporaryBasalStop() { getTemporaryBasalStop().let { queue.put(it) } }
    private fun getTemporaryBasalStop(): Runnable { return Runnable { temporaryBasalStop() } }
    private fun temporaryBasalStop () {
        val info = "temporaryBasalStatusState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_TEMPORARY_PERFORMANCE_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_TEMPORARY_PERFORMANCE_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_TEMPORARY_PERFORMANCE_REGISTER, byteArrayOf(0x00))
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_TEMPORARY_PERFORMANCE_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    showToast("временный базал не отменён")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм чтения количества базальных профилей
     */
    override fun runReadNumBasalProfiles() { getReadNumBasalProfiles().let { queue.put(it) } }
    private fun getReadNumBasalProfiles(): Runnable { return Runnable { readNumBasalProfiles() } }
    private fun readNumBasalProfiles () {
        goToMenu()
        val info = "readNumBasalProfilesState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, NUM_BASAL_PROFILES_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, NUM_BASAL_PROFILES_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, NUM_BASAL_PROFILES_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, NUM_BASAL_PROFILES_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    showToast("кол-во базальных профилей не считано")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм чтения базальных профилей
     */
    override fun runReadBasalProfiles() { getReadBasalProfiles().let { queue.put(it) } }
    private fun getReadBasalProfiles(): Runnable { return Runnable { readBasalProfiles() } }
    private fun readBasalProfiles () {
        goToMenu()

        resetAllLists()
        resetAllLocalLists()
        refreshBasalProfile = true
        cancelReadBasalProfilesFlag = false

        println("readBasalProfiles start countReadPrifile=$countReadProfile")
        println("readBasalProfiles start dataAllCharts=$dataAllCharts")
        println("readBasalProfiles start profileNames=$profileNames")
        println("readBasalProfiles start periodNamesMain=${namePeriods}")
        println("readBasalProfiles start startTimeAllPeriods=${startTimeAllPeriods}")
        println("readBasalProfiles start inputSpeedAllPeriods=${inputSpeedAllPeriods}")
        println("readBasalProfiles start startTimeAllPeriodsMain=${startTimeAllPeriodsMain}")
        println("readBasalProfiles start inputSpeedAllPeriodsMain=${inputSpeedAllPeriodsMain}")

        val info = "readBasalProfilesState"
        var count = 0
        val percentPart: Float = 100f/numBasalProfiles
        countReadProfile = 1
        countReadPeriodInProfile = 1
        numBasalProfiles         //кол-во базальных профилей
        numBasalProfilePeriods   //колво периодов в конкретном базальном профиле под номером countRepeat

        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore && !cancelReadBasalProfilesFlag) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(countReadProfile.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(countReadProfile.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(countReadProfile.toByte()))
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(countReadProfile.toByte()))
                        globalSemaphore = false
                        state += 1
                    }

                    // проверить тот ли регистр мы читаем
                    4 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    5 -> {
                        System.err.println("$info = $state  basalProfileNumDataProcessedFlag = $basalProfileNumDataProcessedFlag")
                        if (basalProfileNumDataProcessedFlag) {
                            if (basalProfileNumCorrectFlag) {
                                state += 1
                            } else {
                                state -= 2
                            }
                            basalProfileNumDataProcessedFlag = false
                        }
                    }
                    //данные одного профиля
                    6 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, NAME_BASAL_PROFILE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    7 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, NAME_BASAL_PROFILE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, NAME_BASAL_PROFILE_REGISTER)
                    }
                    9 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, NAME_BASAL_PROFILE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    10 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    11 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    12 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER)
                    }
                    13 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }

                    //данные одного периода профиля
                    14 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(countReadPeriodInProfile.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    15 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(countReadPeriodInProfile.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    16 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(countReadPeriodInProfile.toByte()))
                    }
                    17 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(countReadPeriodInProfile.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    18 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, PERIOD_BASAL_PROFILE_DATA_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    19 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, PERIOD_BASAL_PROFILE_DATA_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    20 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, PERIOD_BASAL_PROFILE_DATA_REGISTER)
                    }
                    21 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, PERIOD_BASAL_PROFILE_DATA_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    22 -> {
                        System.err.println("$info = $state basalProfilePeriodDataProcessedFlag=$basalProfilePeriodDataProcessedFlag")
                        if (basalProfilePeriodDataRereaadFlag) {
                            state -= 8
                            basalProfilePeriodDataRereaadFlag = false
                        }
                        if (basalProfilePeriodDataProcessedFlag) {
                            if (countReadPeriodInProfile < numBasalProfilePeriods) {
                                countReadPeriodInProfile += 1
                                state -= 8
                                percentSinhronizeBasalProfiles =
                                    ((percentPart * countReadProfile - percentPart) + percentPart / numBasalProfilePeriods * countReadPeriodInProfile).toInt()
                                println("(percentSinhronizeBasalProfiles 1 $percentPart * $countReadProfile - $percentPart) + $percentPart/$numBasalProfilePeriods*$countReadPeriodInProfile = $percentSinhronizeBasalProfiles")
                                RxUpdateMainEvent.getInstance().updatePercentSinhronizationProfile()
                            } else {
                                addNewProfile()
                                resetAllLocalLists()
                                state += 1
                            }
                            basalProfilePeriodDataProcessedFlag = false
                        }
                    }
                    23 -> {
                        System.err.println("$info = $state")
                        if (countReadProfile < numBasalProfiles) {
                            countReadProfile += 1
                            state -= 21
//                            percentSinhronizeBasalProfiles = ((percentPart*countReadPrifile - percentPart) + percentPart/numBasalProfilePeriods*countReadPeriodInProfile).toInt()
//                            println("(percentSinhronizeBasalProfiles 3 $percentPart * $countReadPrifile - $percentPart) + $percentPart/$numBasalProfilePeriods*$countReadPeriodInProfile = $percentSinhronizeBasalProfiles")
//                            RxUpdateMainEvent.getInstance().updatePercentSinhronizationProfile()
                        }
                        else {
                            state += 1
                            percentSinhronizeBasalProfiles = (((percentPart*countReadProfile - percentPart) + percentPart/numBasalProfilePeriods*countReadPeriodInProfile)-2).toInt()
                            println("(percentSinhronizeBasalProfiles 4 $percentPart * $countReadProfile - $percentPart) + $percentPart/$numBasalProfilePeriods*$countReadPeriodInProfile = $percentSinhronizeBasalProfiles")
                            RxUpdateMainEvent.getInstance().updatePercentSinhronizationProfile()
                        }

                    }
                    24 -> {
                        System.err.println("$info = $state")
                        percentSinhronizeBasalProfiles = 100
                        println("(percentSinhronizeBasalProfiles 4 $percentPart * $countReadProfile - $percentPart) + $percentPart/$numBasalProfilePeriods*$countReadPeriodInProfile = $percentSinhronizeBasalProfiles")
                        RxUpdateMainEvent.getInstance().updatePercentSinhronizationProfile()
                        endFlag = true
                        state = 0
                        countReadProfile = 1
                        globalSemaphore = false
                        refreshBasalProfile = false
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    countReadPeriodInProfile = 1
                    countReadProfile = 1
                    showToast("базальные профили не считаны")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм редактирования базального профиля
     */
    override fun runWriteBasalProfiles(numWritingProfile: Int, name: String) { getWriteBasalProfiles(numWritingProfile, name).let { queue.put(it) } }
    private fun getWriteBasalProfiles(numWritingProfile: Int, name: String): Runnable { return Runnable { writeBasalProfiles(numWritingProfile, name) } }
    private fun writeBasalProfiles (numWritingProfile: Int, name: String) {
        val info = "writeBasalProfilesState"
        var count = 0
        var basalNumberWritePeriod = 1

        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(numWritingProfile.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    5 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(numWritingProfile.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(numWritingProfile.toByte()))
                    }
                    7 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(numWritingProfile.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(separateBasalPeriod(numWritingProfile).size.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    9 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(separateBasalPeriod(numWritingProfile).size.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    10 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(separateBasalPeriod(numWritingProfile).size.toByte()))
                    }
                    11 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(separateBasalPeriod(numWritingProfile).size.toByte()))
                        globalSemaphore = false
                        state += 1
                    }

                    12 -> {
                        System.err.println("$info = $state  basalNumberWritePeriod=${basalNumberWritePeriod}")
                        writeRegister(0, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf((basalNumberWritePeriod).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    13 -> {
                        System.err.println("$info = $state  basalNumberWritePeriod=${basalNumberWritePeriod}")
                        writeRegister(1, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf((basalNumberWritePeriod).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    14 -> {
                        System.err.println("$info = $state  basalNumberWritePeriod=${basalNumberWritePeriod}")
                        state = writeRegister(2, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf((basalNumberWritePeriod).toByte()))
                    }
                    15 -> {
                        System.err.println("$info = $state  basalNumberWritePeriod=${basalNumberWritePeriod}")
                        writeRegister(3, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf((basalNumberWritePeriod).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    16 -> {
                        System.err.println("$info = $state  separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]=${separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]}")
                        writeRegister(0, state, PERIOD_BASAL_PROFILE_DATA_REGISTER, separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1])
                        globalSemaphore = false
                        state += 1
                    }
                    17 -> {
                        System.err.println("$info = $state  separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]=${separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]}")
                        writeRegister(1, state, PERIOD_BASAL_PROFILE_DATA_REGISTER, separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1])
                        globalSemaphore = false
                        state += 1
                    }
                    18 -> {
                        System.err.println("$info = $state  separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]=${separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]}")
                        state = writeRegister(2, state, PERIOD_BASAL_PROFILE_DATA_REGISTER, separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1])
                    }
                    19 -> {
                        System.err.println("$info = $state  separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]=${separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]}")
                        writeRegister(3, state, PERIOD_BASAL_PROFILE_DATA_REGISTER, separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1])
                        globalSemaphore = false
                        state += 1
                    }
                    20 -> {
                        System.err.println("$info = $state")
                        basalNumberWritePeriod++
                        if ((basalNumberWritePeriod-1) < separateBasalPeriod(numWritingProfile).size) {
                            state -= 8
                        } else {
                            state += 1
                        }
                    }
                    21 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, NAME_BASAL_PROFILE_REGISTER, name.toByteArray(charset("Windows-1251")))
                        globalSemaphore = false
                        state += 1
                    }
                    22 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, NAME_BASAL_PROFILE_REGISTER, name.toByteArray(charset("Windows-1251")))
                        globalSemaphore = false
                        state += 1
                    }
                    23 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, NAME_BASAL_PROFILE_REGISTER, name.toByteArray(charset("Windows-1251")))
                    }
                    24 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, NAME_BASAL_PROFILE_REGISTER, name.toByteArray(charset("Windows-1251")))
                        globalSemaphore = false
                        state += 1
                    }
                    25 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    26 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    27 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                    }
                    28 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    29 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        countReadProfile = 1
                        globalSemaphore = false
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    countReadPeriodInProfile = 1
                    countReadProfile = 1
                    showToast("изменения базального профиля не переданы помпу")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм добавления базального профиля
     */
    override fun runAddBasalProfiles(numAddProfile: Int, name: String) { getAddBasalProfiles(numAddProfile, name).let { queue.put(it) } }
    private fun getAddBasalProfiles(numAddProfile: Int, name: String): Runnable { return Runnable { addBasalProfiles(numAddProfile, name) } }
    private fun addBasalProfiles (numAddProfile: Int, name: String) {
        val info = "addBasalProfilesState"
        var count = 0
        var basalNumberWritePeriod = 1
        refreshBasalProfile = true

        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    5 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(0x00))
                    }
                    7 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, NUM_MODIFIED_BASAL_PROFILES_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(separateBasalPeriod(numAddProfile-1).size.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    9 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(separateBasalPeriod(numAddProfile-1).size.toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    10 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(separateBasalPeriod(numAddProfile-1).size.toByte()))
                    }
                    11 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, NUM_PERIODS_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf(separateBasalPeriod(numAddProfile-1).size.toByte()))
                        globalSemaphore = false
                        state += 1
                    }

                    12 -> {
                        System.err.println("$info = $state  basalNumberWritePeriod=${basalNumberWritePeriod}")
                        writeRegister(0, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf((basalNumberWritePeriod).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    13 -> {
                        System.err.println("$info = $state  basalNumberWritePeriod=${basalNumberWritePeriod}")
                        writeRegister(1, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf((basalNumberWritePeriod).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    14 -> {
                        System.err.println("$info = $state  basalNumberWritePeriod=${basalNumberWritePeriod}")
                        state = writeRegister(2, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf((basalNumberWritePeriod).toByte()))
                    }
                    15 -> {
                        System.err.println("$info = $state  basalNumberWritePeriod=${basalNumberWritePeriod}")
                        writeRegister(3, state, NUM_MODIFIED_PERIOD_MODIFIED_BASAL_PROFILE_REGISTER, byteArrayOf((basalNumberWritePeriod).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    16 -> {
                        System.err.println("$info = $state  separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]=${separateBasalPeriod(numAddProfile-1)[basalNumberWritePeriod-1]}")
                        writeRegister(0, state, PERIOD_BASAL_PROFILE_DATA_REGISTER, separateBasalPeriod(numAddProfile-1)[basalNumberWritePeriod-1])
                        globalSemaphore = false
                        state += 1
                    }
                    17 -> {
                        System.err.println("$info = $state  separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]=${separateBasalPeriod(numAddProfile-1)[basalNumberWritePeriod-1]}")
                        writeRegister(1, state, PERIOD_BASAL_PROFILE_DATA_REGISTER, separateBasalPeriod(numAddProfile-1)[basalNumberWritePeriod-1])
                        globalSemaphore = false
                        state += 1
                    }
                    18 -> {
                        System.err.println("$info = $state  separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]=${separateBasalPeriod(numAddProfile-1)[basalNumberWritePeriod-1]}")
                        state = writeRegister(2, state, PERIOD_BASAL_PROFILE_DATA_REGISTER, separateBasalPeriod(numAddProfile-1)[basalNumberWritePeriod-1])
                    }
                    19 -> {
                        System.err.println("$info = $state  separateBasalPeriod(numWritingProfile)[basalNumberWritePeriod-1]=${separateBasalPeriod(numAddProfile-1)[basalNumberWritePeriod-1]}")
                        writeRegister(3, state, PERIOD_BASAL_PROFILE_DATA_REGISTER, separateBasalPeriod(numAddProfile-1)[basalNumberWritePeriod-1])
                        globalSemaphore = false
                        state += 1
                    }
                    20 -> {
                        System.err.println("$info = $state")
                        basalNumberWritePeriod++
                        if ((basalNumberWritePeriod-1) < separateBasalPeriod(numAddProfile-1).size) {
                            state -= 8
                        } else {
                            state += 1
                        }
                    }
                    21 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, NAME_BASAL_PROFILE_REGISTER, name.toByteArray(charset("Windows-1251")))
                        globalSemaphore = false
                        state += 1
                    }
                    22 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, NAME_BASAL_PROFILE_REGISTER, name.toByteArray(charset("Windows-1251")))
                        globalSemaphore = false
                        state += 1
                    }
                    23 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, NAME_BASAL_PROFILE_REGISTER, name.toByteArray(charset("Windows-1251")))
                    }
                    24 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, NAME_BASAL_PROFILE_REGISTER, name.toByteArray(charset("Windows-1251")))
                        globalSemaphore = false
                        state += 1
                    }
                    25 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    26 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    27 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                    }
                    28 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    29 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        countReadProfile = 1
                        globalSemaphore = false
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    countReadPeriodInProfile = 1
                    countReadProfile = 1
                    showToast("базальный профиль не добавлен на помпу")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм выбор для использования базального профиля
     */
    override fun runActivateBasalProfiles(numActivateProfile: Int) { getActivateBasalProfiles(numActivateProfile).let { queue.put(it) } }
    private fun getActivateBasalProfiles(numActivateProfile: Int): Runnable { return Runnable { activateBasalProfiles(numActivateProfile) } }
    private fun activateBasalProfiles (numActivateProfile: Int) {
        val info = "activateBasalProfilesState"
        var count = 0

        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state    numActivateProfile=${numActivateProfile+1}")
                        writeRegister(0, state, ACTIVATE_BASAL_PROFILE_REGISTER, byteArrayOf((numActivateProfile+1).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    5 -> {
                        System.err.println("$info = $state    numActivateProfile=${numActivateProfile+1}")
                        writeRegister(1, state, ACTIVATE_BASAL_PROFILE_REGISTER, byteArrayOf((numActivateProfile+1).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state    numActivateProfile=${numActivateProfile+1}")
                        state = writeRegister(2, state, ACTIVATE_BASAL_PROFILE_REGISTER, byteArrayOf((numActivateProfile+1).toByte()))
                    }
                    7 -> {
                        System.err.println("$info = $state  numActivateProfile=${numActivateProfile+1}")
                        writeRegister(3, state, ACTIVATE_BASAL_PROFILE_REGISTER, byteArrayOf((numActivateProfile+1).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    9 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    10 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                    }
                    11 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    12 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        countReadProfile = 1
                        globalSemaphore = false
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    countReadPeriodInProfile = 1
                    countReadProfile = 1
                    showToast("базальный профиль не выбран на помпе")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм удаления базального профиля
     */
    override fun runDeleteBasalProfiles(numDeleteProfile: Int) { getDeleteBasalProfiles(numDeleteProfile).let { queue.put(it) } }
    private fun getDeleteBasalProfiles(numDeleteProfile: Int): Runnable { return Runnable { deleteBasalProfiles(numDeleteProfile) } }
    private fun deleteBasalProfiles (numDeleteProfile: Int) {
        val info = "deleteBasalProfilesState"
        var count = 0

        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x01))
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state  ${(numDeleteProfile+1)}")
                        writeRegister(0, state, DELETE_BASAL_PROFILE_REGISTER, byteArrayOf((numDeleteProfile+1).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    5 -> {
                        System.err.println("$info = $state ${(numDeleteProfile+1)}")
                        writeRegister(1, state, DELETE_BASAL_PROFILE_REGISTER, byteArrayOf((numDeleteProfile+1).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state ${(numDeleteProfile+1)}")
                        state = writeRegister(2, state, DELETE_BASAL_PROFILE_REGISTER, byteArrayOf((numDeleteProfile+1).toByte()))
                    }
                    7 -> {
                        System.err.println("$info = $state ${(numDeleteProfile+1)}")
                        writeRegister(3, state, DELETE_BASAL_PROFILE_REGISTER, byteArrayOf((numDeleteProfile+1).toByte()))
                        globalSemaphore = false
                        state += 1
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        writeRegister(0, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    9 -> {
                        System.err.println("$info = $state")
                        writeRegister(1, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    10 -> {
                        System.err.println("$info = $state")
                        state = writeRegister(2, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                    }
                    11 -> {
                        System.err.println("$info = $state")
                        writeRegister(3, state, BASAL_LOCK_CONTROL_REGISTER, byteArrayOf(0x00))
                        globalSemaphore = false
                        state += 1
                    }
                    12 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        countReadProfile = 1
                        globalSemaphore = false
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    countReadPeriodInProfile = 1
                    countReadProfile = 1
                    showToast("базальный профиль не удалён на помпе")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм запроса файлов дневника
     */
    override fun runLogCommand(requestNotify: Boolean, logCommand: String, countRestart: Int) { getLogCommand(requestNotify, logCommand, countRestart).let { queue.put(it) } }
    private fun getLogCommand(requestNotify: Boolean, logCommand: String, countRestart: Int): Runnable { return Runnable { logCommand(requestNotify, logCommand, countRestart) } }
    private fun logCommand (requestNotify: Boolean, logCommand: String, countRestart: Int) {
        val info = "logCommandState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        if (requestNotify) {
                            System.err.println("$info = $state")
                            writeLogCommand(0, byteArrayOf(0x00))
                            globalSemaphore = false
                        }
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        writeLogCommand(1, logCommand.toByteArray(charset("UTF-8")))
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        globalSemaphore = false
                    }
                }
                count = 0
            } else {
                count++
//                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    countReadPeriodInProfile = 1
                    countReadProfile = 1
                    if (countRestart != 0) {
                        runLogCommand(requestNotify, logCommand, countRestart-1)
                        println("requestNotify = $requestNotify")
                        println("logCommand = $logCommand")
                    }
//                    else {showToast("чтение лога не удалось")}
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    /**
     * Алгоритм чтения даты и времени с помпы
     */
    override fun runDateAndTime() { getDateAndTime().let { queue.put(it) } }
    private fun getDateAndTime(): Runnable { return Runnable { dateAndTime() } }
    private fun dateAndTime () {
        typeCellsListMain.removeLast()
        RxUpdateMainEvent.getInstance().updateChat()
        val info = "dateAndTimeState"
        var count = 0
        var state = 0 // переключается здесь в потоке
        var endFlag = false // меняется на последней стадии машины состояний, служит для немедленного прекращния операции
        globalSemaphore = true // меняется по приходу ответа от подключаемого уст-ва

        while (!endFlag) {
            if (globalSemaphore) {
                when (state) {
                    0 -> {
                        showToast("обновление лога")
                        System.err.println("$info = $state")
                        readRegister(0, state, DATE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    1 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, DATE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    2 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, DATE_REGISTER)
                    }
                    3 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, DATE_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    4 -> {
                        System.err.println("$info = $state")
                        readRegister(0, state, TIME_WORK_PUMP_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    5 -> {
                        System.err.println("$info = $state")
                        readRegister(1, state, TIME_WORK_PUMP_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    6 -> {
                        System.err.println("$info = $state")
                        state = readRegister(2, state, TIME_WORK_PUMP_REGISTER)
                    }
                    7 -> {
                        System.err.println("$info = $state")
                        readRegister(3, state, TIME_WORK_PUMP_REGISTER)
                        globalSemaphore = false
                        state += 1
                    }
                    8 -> {
                        System.err.println("$info = $state")
                        endFlag = true
                        state = 0
                        countReadProfile = 1
                        globalSemaphore = false
                        runLogCommand(true, "/", 3)
                        runLogCommand(false, "/", 3)
                    }
                }
                count = 0
            } else {
                count++
                System.err.println("Количество запросов без ответа = $count")
                if (count == 100) {
                    endFlag = true
                    state = 0
                    count = 0
                    countReadPeriodInProfile = 1
                    countReadProfile = 1
                    showToast("дата и время помпы не считаны")
                }
            }
            try {
                Thread.sleep(10)
            } catch (ignored: Exception) { }
        }
    }

    private fun readRegister(state: Int, stateMachineState: Int, registerPointer: ByteArray):Int {
        var mStateMachineState = stateMachineState
        when (state) {
            0 -> { bleCommand(registerPointer, REGISTER_POINTER, WRITE) }
            1 -> { bleCommand(READ_REGISTER, REGISTER_POINTER, READ) }
            2 -> {
                moveDataSortSemaphore()
                return if (contentEquals(readRegisterPointer!!, registerPointer)) {
                    mStateMachineState++
                    mStateMachineState
                } else {
                    mStateMachineState -= 2
                    mStateMachineState
                }
            }
            3 -> { bleCommand(FAKE_DATA_REGISTER, REGISTER_DATA, READ) }
        }
        return 555
    }
    private fun writeRegister(state: Int, stateMachineState: Int, registerPointer: ByteArray, dataForRecord: ByteArray):Int {
        var mStateMachineState = stateMachineState
        when (state) {
            0 -> { bleCommand(registerPointer, REGISTER_POINTER, WRITE) }
            1 -> { bleCommand(READ_REGISTER, REGISTER_POINTER, READ) }
            2 -> {
                moveDataSortSemaphore()

                return if (contentEquals(readRegisterPointer!!, registerPointer)) {
                    mStateMachineState++
                    mStateMachineState
                } else {
                    mStateMachineState -= 2
                    mStateMachineState
                }
            }
            3 -> { bleCommand(dataForRecord, REGISTER_DATA, WRITE) }
        }
        return 555
    }
    private fun writeLogCommand(state: Int, dataForRecord: ByteArray):Int {
        when (state) {
            0 -> { bleCommand(null, NOTIFICATION_PUMP_LOG, NOTIFY) }
            1 -> { bleCommand(dataForRecord, LOG_POINTER, WRITE) }
        }
        return 555
    }

    /**
     * Реализация автоматического подсчёта количества периодов базала и их границ
     * по двадцати четырём числам базальной скорости на каждый час
     */
    private fun separateBasalPeriod(numberEditingProfile: Int): List<ByteArray> {
        val basalHour = IntArray(24)
        val temp = dataAllCharts[numberEditingProfile-1]
        val basals = temp.toMutableList().apply {
            removeAt(0)
        }

        println("basals123 = $basals")
        for (i in basalHour.indices) {
            if (i <= basals.size) {
                basalHour[i] = basals[i]
            }
        }

        val periods = mutableListOf<BasalPeriod>()
        var j = 0
        var aaaa = 0
        var bbbb = 0
        var cccc = 0
        var firstHourNewPeriod = true

        while (j < basalHour.size) {
            if ( firstHourNewPeriod ) {
                aaaa = j
                bbbb = j + 1
                cccc = basalHour[j]
                firstHourNewPeriod = false
            } else {
                bbbb += 1
            }

            if ( j <  basalHour.size - 1) {
                if ( basalHour[j] != basalHour[j + 1]) {
                    periods.add(BasalPeriod(aaaa, bbbb, cccc))
                    firstHourNewPeriod = true
                }
            }
            if (j == basalHour.size - 1) {
                periods.add(BasalPeriod(aaaa, bbbb, cccc))
                firstHourNewPeriod = true
            }
            j++
        }

        System.err.println("basals123 MakeResult MainActivity $periods" + "  количество периодов = " + periods.size)
        val separatePeriods = mutableListOf<ByteArray>()
        j = 0
        while (j < periods.size) {
            val sendMassageBuffer = ByteArray(6)
            sendMassageBuffer[0] = (periods[j].startTime ushr 8).toByte() // startTime >> 8
            sendMassageBuffer[1] = periods[j].startTime.toByte()
            sendMassageBuffer[2] = (periods[j].endTime ushr 8).toByte() // endTime >> 8
            sendMassageBuffer[3] = periods[j].endTime.toByte()
            sendMassageBuffer[4] = (periods[j].basalSpeed ushr 8).toByte() // basalSpeed >> 8
            sendMassageBuffer[5] = periods[j].basalSpeed.toByte()
            separatePeriods.add(sendMassageBuffer)
            System.err.println("basals123 Speed int = " + periods[j].basalSpeed + " ||| basalSpeed 1 byte = " + castUnsignedCharToInt(sendMassageBuffer[4]) + " basalSpeed 2 byte = " + castUnsignedCharToInt(sendMassageBuffer[5]))
            j++
        }
        return separatePeriods
    }
    /**
     * Создаёт массив двух байт из входного числа. Переполнение игнорирует.
     * @param convertibleInt - конвертируемое число из диапазона 0-65535
     */
    private fun castIntToByteArray (convertibleInt: Int): ByteArray {
        var seniorByte: Byte = 0x00
        val result = byteArrayOf(0x00, 0x00)

        if (convertibleInt % 255 > 0) {
            seniorByte = (convertibleInt / 256).toByte()
        }
        val youngerByte: Byte = convertibleInt.toByte()
        result[0] = seniorByte
        result[1] = youngerByte
        return result
    }
    /**
     * Реализация перевода байта в обычное беззнаковое число диапазона 0-255
     * @param Ubyte - диапазон -127 : 128
     */
    private fun castUnsignedCharToInt(Ubyte: Byte): Int {
        var cast = Ubyte.toInt()
        if (cast < 0) {
            cast += 256
        }
        return cast
    }
    //  Реализация сравнения двух массивов байт побайтно
    private fun contentEquals(array1: ByteArray, array2: ByteArray): Boolean {
        var equalsByteArray = true
        var j = 0
        if ((array1.size - array2.size) == 0) { //защита от сравнения массивов разной длинны
            while (j < array1.size){
                if (array1[j] != array2[j]) equalsByteArray = false
                j++
            }
        } else equalsByteArray = false
        return equalsByteArray
    }
    //  Реализация toast доступного из BlockingQueue
    private fun showToast(massage: String) {
        runOnUiThread {
            Toast.makeText(baseContext, massage, Toast.LENGTH_LONG).show()
        }
    }
    private fun convertListStringToArray(convertedList: ArrayList<String>): Array<String> {
        val result = Array(convertedList.size) { "" }
        for (i in 0 until convertedList.size) {
            result[i] = convertedList[i]
        }
        return result
    }
    private fun convertListIntToArray(convertedList: ArrayList<Int>) :IntArray {
        val result = IntArray(convertedList.size)
        for (i in 0 until convertedList.size) {
            result[i] = convertedList[i]
        }
        return result
    }
    private fun saveAllChatLists() {
        saveArrayList(TYPE_CELLS_LIST_MAIN, typeCellsListMain)
        saveArrayList(MASSAGES_LIST_MAIN, massagesListMain)
        saveArrayList(TIMESTAMPS_LIST_MAIN, timestampsListMain)
    }

    companion object {
        @JvmStatic private val KEY_RESULT = "RESULT"

        //переменные апбара
        var battryPercent by Delegates.notNull<Int>()
        var liIonPercent by Delegates.notNull<Int>()
        var reservoirVolume by Delegates.notNull<Int>()
        var cannuleTime by Delegates.notNull<Int>()
        var iob by Delegates.notNull<Float>()

        //переменные чата
        var typeCellsListMain by Delegates.notNull<ArrayList<String>>()
        var massagesListMain by Delegates.notNull<ArrayList<String>>()
        var timestampsListMain by Delegates.notNull<ArrayList<String>>()

        //пременные базальных профилей
        var inProfileSettingsFragmentFlag by Delegates.notNull<Boolean>()
        var percentSinhronizeBasalProfiles by Delegates.notNull<Int>()
        var numBasalProfiles by Delegates.notNull<Int>()
        var numBasalProfilePeriods by Delegates.notNull<Int>()
        var nameReadBasalProfile by Delegates.notNull<String>()
        var basalSpeed by Delegates.notNull<Float>()
        var refreshBasalProfile by Delegates.notNull<Boolean>()

        var selectedProfile by Delegates.notNull<Int>()
        var changeProfile by Delegates.notNull<Int>()
        var profileNames by Delegates.notNull<ArrayList<String>>()
        var dataAllCharts by Delegates.notNull<ArrayList<ArrayList<Int>>>()

        var periodNamesMain by Delegates.notNull<ArrayList<Array<String>>>()
        var startTimeAllPeriodsMain by Delegates.notNull<ArrayList<IntArray>>()
        var inputSpeedAllPeriodsMain by Delegates.notNull<ArrayList<IntArray>>()

        //переменные временного базала
        var stayOnTemporaryBasalScreen by Delegates.notNull<Boolean>()
        var temporaryBasalActivated by Delegates.notNull<Boolean>()
        var temporaryBasalVoliume by Delegates.notNull<Int>()
        var temporaryBasalTime by Delegates.notNull<Int>()

        //переменные болюсов
        var superBolusIsResolved by Delegates.notNull<Boolean>()
        var extendedAndDualPatternBolusIsResolved by Delegates.notNull<Boolean>()

        var balanceAllBoluses by Delegates.notNull<Float>()

        var bolusType by Delegates.notNull<Int>() // тип выполняемого болюса
        var superBolusBasalVoliume by Delegates.notNull<Int>()
        var superBolusVoliume by Delegates.notNull<Int>()
        var superBolusTime by Delegates.notNull<Int>()

//        var stepBolusValInsertFromApp by Delegates.notNull<Int>() //объём болюса
//        var superBolusValInsertFromApp by Delegates.notNull<Int>()
//        var extendedBolusValInsertFromApp by Delegates.notNull<Int>()
//        var dualPatternBolusValInsertFromApp by Delegates.notNull<Int>()


        var numberInsertUnitsStepBolus by Delegates.notNull<Int>()
        var numberSumUnitsStepBolus by Delegates.notNull<Int>()

        var numberInsertUnitsSuperBolus by Delegates.notNull<Int>()
        var numberSumUnitsSuperBolus by Delegates.notNull<Int>()
        var timeBasalPauseSuperBolus by Delegates.notNull<Int>()

        var numberInsertUnitsExtendedBolus by Delegates.notNull<Int>()
        var numberSumUnitsExtendedBolus by Delegates.notNull<Int>()
        var remainingTimeExtendedBolus by Delegates.notNull<Int>()

        var insertionOfStretchedDualPatternBolus by Delegates.notNull<Int>()
        var numberFastUnitsDualPatternBolus by Delegates.notNull<Int>()
        var numberSlowUnitsDualPatternBolus by Delegates.notNull<Int>()
        var numberInsertUnitsDualPatternBolus by Delegates.notNull<Int>()
        var numberSumUnitsDualPatternBolus by Delegates.notNull<Int>()
        var remainingTimeDualPatternBolus by Delegates.notNull<Int>()

        var pumpStatus by Delegates.notNull<Int>()
        var refilling by Delegates.notNull<Int>()
        var onRefillingScreen by Delegates.notNull<Boolean>()
        var countBolusInConveyor by Delegates.notNull<Int>()
        var typeFirstBolusInConveyor by Delegates.notNull<Int>()
        var typeSecondBolusInConveyor by Delegates.notNull<Int>()
        var typeThirdBolusInConveyor by Delegates.notNull<Int>()
        var typeFourthBolusInConveyor by Delegates.notNull<Int>()
        //настройки
        var pumpStatusNotifyDataThreadFlag by Delegates.notNull<Boolean>()
        var attemptsToUnlock by Delegates.notNull<Int>()

        var showInfoDialogsFlag by Delegates.notNull<Boolean>()
        var tupOnList by Delegates.notNull<Boolean>()
        var inScanFragmentFlag by Delegates.notNull<Boolean>()
        var scanList by Delegates.notNull<ArrayList<ScanItem>>()
        var connectionPassword by Delegates.notNull<String>()
        var connectedDevice by Delegates.notNull<String>()
        var connectedDeviceAddress by Delegates.notNull<String>()
        var pinCodeApp by Delegates.notNull<String>()
        var activatePinCodeApp by Delegates.notNull<Boolean>()
        var pinCodeSettings by Delegates.notNull<String>()
        var activatePinCodeSettings by Delegates.notNull<Boolean>()

        var activateStepBolus by Delegates.notNull<Boolean>()
        var activateExtendedBolus by Delegates.notNull<Boolean>()
        var activateDualPatternBolus by Delegates.notNull<Boolean>()
        var activateSuperBolus by Delegates.notNull<Boolean>()
    }
}
