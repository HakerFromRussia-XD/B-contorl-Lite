package ua.cn.stu.navigation.contract

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import ua.cn.stu.navigation.Options

typealias ResultListener<T> = (T) -> Unit

fun Fragment.navigator(): Navigator {
    return requireActivity() as Navigator
}

interface Navigator {

    fun showSacnScreen()
    fun showTemporaryBasalScreen()
    fun showBasalProfileSettingsScreen()
    fun showProfileScreen()
    fun showSettingsScreen()
    fun showBolusScreen()
    fun showStepBolusScreen()
    fun showExtendedBolusScreen()
    fun showDualPatternBolusScreen()
    fun showSuperBolusScreen()
    fun showRefillingScreen()
    fun showRefilledScreen()
    fun showMenuScreen()
    fun <T> saveArrayList(key: String, list: ArrayList<T>)
    fun seveIntArrayList(key: String, list: ArrayList<IntArray>)
    fun seveArrayStringList(key: String, list: ArrayList<Array<String>>)
    fun saveInt(key: String, value: Int)
    fun saveString(key: String, text: String)
    fun initBLEStructure()
    fun scanLeDevice(enable: Boolean)
    fun disconnect ()
    fun reconnect ()
    fun bleCommand(byteArray: ByteArray?, command: String, typeCommand: String)
    fun runConnectToPump(pass : ByteArray?, showInfoDialogs: Boolean, countRestart: Int)
    fun showGoBolusDialog(title: String, massage: String, numberOfHundredthsInsulin: Int, numberOfHundredStrechedInsulin: Int, timeBolus: Int)
    fun runSuperBolus(numberOfHundredthsInsulin: Int, timeSuperBolus: Int)
    fun runCannuleTimeRegister(countRestart: Int, resetTimer: Boolean)
    fun runInitRefillingRegister(countRestart: Int)
    fun runTemporaryBasal(numberTemporaryBasalVelocity: Int, timeBasal: Int)
    fun runTemporaryBasalStatus()
    fun runReadNumBasalProfiles()
    fun runReadBasalProfiles()
    fun runWriteBasalProfiles(numWritingProfile: Int, name: String)
    fun runAddBasalProfiles(numAddProfile: Int, name: String)
    fun runDeleteBasalProfiles(numDeleteProfile: Int)
    fun runActivateBasalProfiles(numActivateProfile: Int)
    fun runLogCommand(requestNotify: Boolean, logCommand: String, countRestart: Int)
    fun runDateAndTime()

    fun firstOpenMenu()
    fun goBack()
    fun goToMenu()

    fun <T : Parcelable> publishResult(result: T)
    fun <T : Parcelable> listenResult(clazz: Class<T>, owner: LifecycleOwner, listener: ResultListener<T>)


    fun setNewTitle(newTitle: String)
}