package com.dot.lifesaver.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import androidx.fragment.app.Fragment
import com.dot.lifesaver.R
import com.dot.lifesaver.repositories.profiles.BatterySaverConstantsConfigProfiles
import com.dot.lifesaver.utils.BatterySaverManager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_battery_manager.*

class BatteryManagerFragment : Fragment(R.layout.fragment_battery_manager),
    TabLayout.OnTabSelectedListener {

    private lateinit var batterySaverManager: BatterySaverManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        batterySaverManager = BatterySaverManager(context.contentResolver)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardAppBar.canGoBack(requireActivity())

        adaptiveBattery.setChecked(batterySaverManager.getSmartBattery())
        adaptiveBattery.setOnCheckListener { _, isChecked ->
            batterySaverManager.setSmartBattery(isChecked)
        }
        profileSelector.selectTab(
            when (batterySaverManager.getConstantsString()) {
                BatterySaverConstantsConfigProfiles.MODERATE.toString() -> profileSelector.getTabAt(1)
                BatterySaverConstantsConfigProfiles.HIGH.toString() -> profileSelector.getTabAt(2)
                BatterySaverConstantsConfigProfiles.EXTREME.toString() -> profileSelector.getTabAt(3)
                else -> profileSelector.getTabAt(0)
            }
        )
        profileSelector.addOnTabSelectedListener(this)

        val powerManager = requireActivity().getSystemService(PowerManager::class.java)
        batterySaver.setChecked(powerManager.isPowerSaveMode)
        batterySaver.setOnCheckListener { _, isChecked ->
            if (powerManager.isPowerSaveMode != isChecked)
                powerManager.setPowerSaveModeEnabled(isChecked)
        }
        customize.setOnClickPreference {
            ProfileCustomizationFragment().show(parentFragmentManager, "customize")
        }
        stickyTurnoff.setChecked(batterySaverManager.getLowPowerStickyAutoDisableEnabled())
        stickyTurnoff.setOnCheckListener { _, isChecked ->
            batterySaverManager.setLowPowerStickyAutoDisableEnabled(isChecked)
        }
        stickyTurnoff.summaryView!!.text = getString(
            R.string.battery_saver_sticky_description_new,
            batterySaverManager.getLowPowerStickyAutoDisableLevel().toString()
        ) + "%"
        stickyLevel.setMin(1)
        stickyLevel.setMax(100)
        stickyLevel.setProgress(batterySaverManager.getLowPowerStickyAutoDisableLevel())
        stickyLevel.countText!!.text = "${stickyLevel.getProgress()}%"
        stickyLevel.setOnProgressChangedPreference { progress ->
            batterySaverManager.setLowPowerStickyAutoDisableLevel(progress)
            stickyTurnoff.summaryView!!.text = getString(
                R.string.battery_saver_sticky_description_new,
                batterySaverManager.getLowPowerStickyAutoDisableLevel().toString()
            ) + "%"
            stickyLevel.countText!!.text = "${stickyLevel.getProgress()}%"
        }
        scheduleSaver.setOnClickPreference {
            BatteryScheduleFragment().show(parentFragmentManager, "schedule")
        }
        smartChargingFragment.setOnClickPreference {
            SmartChargingFragment().show(parentFragmentManager, "smartCharging")
        }
        smartCutoffFragment.setOnClickPreference {
            SmartCutoffFragment().show(parentFragmentManager, "smartCutoff")
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        when (tab.position) {
            0 -> {
                batterySaverManager.resetToDefault()
            }
            1 -> {
                batterySaverManager.apply(BatterySaverConstantsConfigProfiles.MODERATE)
            }
            2 -> {
                batterySaverManager.apply(BatterySaverConstantsConfigProfiles.HIGH)
            }
            3 -> {
                batterySaverManager.apply(BatterySaverConstantsConfigProfiles.EXTREME)
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}

    override fun onTabReselected(tab: TabLayout.Tab) {}

}