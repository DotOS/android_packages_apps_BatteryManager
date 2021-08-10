package com.dot.lifesaver.fragments

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dot.lifesaver.R
import com.dot.lifesaver.utils.ResourceHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_smart_cutoff.*

class SmartCutoffFragment : BottomSheetDialogFragment() {

    private var mSmartCutoffTemperatureDefaultConfig = 0
    private var mSmartCutoffResumeTemperatureConfig = 0

    private val SMART_CUTOFF = "smart_cutoff"
    private val SMART_CUTOFF_TEMPERATURE = "smart_cutoff_temperature"
    private val SMART_CUTOFF_RESUME_TEMPERATURE = "smart_cutoff_resume_temperature"

    private val UNIT = "°C"

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_smart_cutoff, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cr = requireContext().contentResolver
        mSmartCutoffTemperatureDefaultConfig = ResourceHelper.getInternalInteger("config_smartCutoffTemperature", requireContext())
        mSmartCutoffResumeTemperatureConfig = ResourceHelper.getInternalInteger("config_smartCutoffResumeTemperature", requireContext())

        smartCutoffSwitch.setChecked(Settings.System.getInt(cr, SMART_CUTOFF, 0) == 1)
        smartCutoffSwitch.setOnCheckListener { _, isChecked ->
            Settings.System.putInt(cr, SMART_CUTOFF, if (isChecked) 1 else 0)
        }

        val currentTemperature = Settings.System.getInt(cr, SMART_CUTOFF_TEMPERATURE, mSmartCutoffTemperatureDefaultConfig)
        smartCutoffStopTrigger.setProgress(currentTemperature)
        smartCutoffStopTrigger.countText!!.text = "${smartCutoffStopTrigger.getProgress()}$UNIT"
        smartCutoffStopTrigger.setOnProgressChangedPreference { progress ->
            val mCutoffResumeLevel = Settings.System.getInt(cr, SMART_CUTOFF_RESUME_TEMPERATURE, mSmartCutoffResumeTemperatureConfig)
            Settings.System.putInt(cr, SMART_CUTOFF_TEMPERATURE, progress)
            smartCutoffStartTrigger.setMax(progress - 1)
            if (progress <= mCutoffResumeLevel) {
                smartCutoffStartTrigger.setProgress(progress - 1)
                Settings.System.putInt(cr, SMART_CUTOFF_RESUME_TEMPERATURE, progress - 1)
            }
            smartCutoffStopTrigger.countText!!.text = "${smartCutoffStopTrigger.getProgress()}$UNIT"
            smartCutoffStartTrigger.countText!!.text = "${smartCutoffStartTrigger.getProgress()}$UNIT"
        }

        var currentResumeLevel = Settings.System.getInt(cr, SMART_CUTOFF_RESUME_TEMPERATURE, mSmartCutoffResumeTemperatureConfig)
        if (currentResumeLevel >= currentTemperature ) currentResumeLevel = currentTemperature - 1
        smartCutoffStartTrigger.setMax(currentTemperature - 1)
        smartCutoffStartTrigger.setProgress(currentResumeLevel)
        smartCutoffStartTrigger.countText!!.text = "${smartCutoffStartTrigger.getProgress()}$UNIT"
        smartCutoffStartTrigger.setOnProgressChangedPreference { progress ->
            val mCutoffLevel = Settings.System.getInt(cr, SMART_CUTOFF_TEMPERATURE, mSmartCutoffTemperatureDefaultConfig)
            smartCutoffStartTrigger.setMax(mCutoffLevel - 1)
            Settings.System.putInt(cr, SMART_CUTOFF_RESUME_TEMPERATURE, progress)
            smartCutoffStartTrigger.countText!!.text = "${smartCutoffStartTrigger.getProgress()}$UNIT"
        }
    }


}