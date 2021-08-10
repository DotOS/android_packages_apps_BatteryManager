package com.dot.lifesaver.fragments

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dot.lifesaver.R
import com.dot.lifesaver.adapters.BatteryScheduleAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_battery_saver_schedule.*
import kotlin.math.max

class BatteryScheduleFragment : BottomSheetDialogFragment() {

    val MIN_SEEKBAR_VALUE = 1

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_battery_saver_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : BatteryScheduleAdapter.Callback {
            override fun onApply(option: BatteryScheduleAdapter.Option) {
                schedulePercentage.visibility =
                    if (option.value == 1 && option.selected) View.VISIBLE else View.GONE
                if (schedulePercentage.visibility == View.VISIBLE) updateSeekbar()
            }
        }
        val adapter = BatteryScheduleAdapter(
            arrayListOf(
                BatteryScheduleAdapter.Option("No Schedule", 0),
                BatteryScheduleAdapter.Option("Based on your routine", 2),
                BatteryScheduleAdapter.Option("Based on percentage", 1)
            ), callback
        )
        scheduleRecycler.adapter = adapter
        val option = adapter.getSelected()
        if (option != null) schedulePercentage.visibility =
            if (option.value == 1 && option.selected) View.VISIBLE else View.GONE
        scheduleRecycler.layoutManager = LinearLayoutManager(requireContext())
        val threshold = Settings.Global.getInt(
            requireContext().contentResolver,
            Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL,
            0
        )
        when {
            threshold <= 0 -> schedulePercentage.visibility = View.GONE
            else -> schedulePercentage.visibility = View.VISIBLE
        }
        updateSeekbar()
    }

    private fun updateSeekbar() {
        val threshold = Settings.Global.getInt(
            requireContext().contentResolver,
            Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL,
            0
        )
        val currentSeekbarValue = max(threshold / 5, MIN_SEEKBAR_VALUE)
        schedulePercentage.setProgress(currentSeekbarValue)
        schedulePercentage.countText!!.text = "${currentSeekbarValue * 5}%"
        schedulePercentage.setOnProgressChangedPreference { progress ->
            val percentage = progress * 5
            Settings.Global.putInt(
                requireContext().contentResolver, Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL,
                percentage
            )
            schedulePercentage.countText!!.text = "$percentage%"
        }
    }
}