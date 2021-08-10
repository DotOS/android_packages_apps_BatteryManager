package com.dot.lifesaver.fragments

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dot.lifesaver.R
import com.dot.lifesaver.utils.ResourceHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_smart_charging.*

class SmartChargingFragment : BottomSheetDialogFragment() {

    private var mSmartChargingLevelDefaultConfig = 0
    private var mSmartChargingResumeLevelDefaultConfig = 0

    private val SMART_CHARGING = "smart_charging"
    private val SMART_CHARGING_LEVEL = "smart_charging_level"
    private val SMART_CHARGING_RESUME_LEVEL = "smart_charging_resume_level"
    private val SMART_CHARGING_RESET_STATS = "smart_charging_reset_stats"

    private val UNIT = "%"

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_smart_charging, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cr = requireContext().contentResolver
        mSmartChargingLevelDefaultConfig = ResourceHelper.getInternalInteger("config_smartChargingBatteryLevel", requireContext())
        mSmartChargingResumeLevelDefaultConfig = ResourceHelper.getInternalInteger("config_smartChargingBatteryResumeLevel", requireContext())

        smartChargingSwitch.setChecked(Settings.System.getInt(cr, SMART_CHARGING, 0) == 1)
        smartChargingSwitch.setOnCheckListener { _, isChecked ->
            Settings.System.putInt(cr, SMART_CHARGING, if (isChecked) 1 else 0)
        }

        val currentLevel = Settings.System.getInt(cr, SMART_CHARGING_LEVEL, mSmartChargingLevelDefaultConfig)
        smartChargingStopTrigger.setProgress(currentLevel)
        smartChargingStopTrigger.countText!!.text = "${smartChargingStopTrigger.getProgress()}$UNIT"
        smartChargingStopTrigger.setOnProgressChangedPreference { progress ->
            val mChargingResumeLevel = Settings.System.getInt(cr, SMART_CHARGING_RESUME_LEVEL, mSmartChargingResumeLevelDefaultConfig)
            Settings.System.putInt(cr, SMART_CHARGING_LEVEL, progress)
            smartChargingStartTrigger.setMax(progress - 1)
            if (progress <= mChargingResumeLevel) {
                smartChargingStartTrigger.setProgress(progress - 1)
                Settings.System.putInt(cr, SMART_CHARGING_RESUME_LEVEL, progress - 1)
            }
            smartChargingStopTrigger.countText!!.text = "${smartChargingStopTrigger.getProgress()}$UNIT"
            smartChargingStartTrigger.countText!!.text = "${smartChargingStartTrigger.getProgress()}$UNIT"
        }

        var currentResumeLevel = Settings.System.getInt(cr, SMART_CHARGING_RESUME_LEVEL, mSmartChargingResumeLevelDefaultConfig)
        if (currentResumeLevel >= currentLevel) currentResumeLevel = currentLevel - 1
        smartChargingStartTrigger.setMax(currentLevel - 1)
        smartChargingStartTrigger.setProgress(currentResumeLevel)
        smartChargingStartTrigger.countText!!.text = "${smartChargingStartTrigger.getProgress()}$UNIT"
        smartChargingStartTrigger.setOnProgressChangedPreference { progress ->
            val mChargingLevel = Settings.System.getInt(cr, SMART_CHARGING_LEVEL, mSmartChargingLevelDefaultConfig)
            smartChargingStartTrigger.setMax(mChargingLevel - 1)
            Settings.System.putInt(cr, SMART_CHARGING_RESUME_LEVEL, progress)
            smartChargingStartTrigger.countText!!.text = "${smartChargingStartTrigger.getProgress()}$UNIT"
        }

        smartChargingResetStats.setOnCheckListener{ _, isChecked ->
            Settings.System.putInt(cr, SMART_CHARGING_RESET_STATS, if (isChecked) 1 else 0)
        }
    }


}