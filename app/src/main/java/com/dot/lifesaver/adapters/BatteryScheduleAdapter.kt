/*
 * Copyright (C) 2021 The dotOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dot.lifesaver.adapters

import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dot.lifesaver.R

class BatteryScheduleAdapter(private val items: ArrayList<Option>, private val callback: Callback)
    : RecyclerView.Adapter<BatteryScheduleAdapter.ViewHolder>() {

    val TRIGGER_LEVEL_MIN = 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_object, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option: Option = items[position]
        val resolver = holder.itemView.context.contentResolver
        val mode = Settings.Global.getInt(resolver, Settings.Global.AUTOMATIC_POWER_SAVE_MODE,
            PowerManager.POWER_SAVE_MODE_TRIGGER_PERCENTAGE)
        val threshold = Settings.Global.getInt(resolver, Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL, 0)
        if (mode == PowerManager.POWER_SAVE_MODE_TRIGGER_PERCENTAGE) {
            if (option.value == 0 && threshold <= 0)
                option.selected = true
            else if (option.value == 1 && threshold > 0)
                option.selected = true
        } else {
            if (option.value == 2)
                option.selected = true
        }
        holder.itemEntry.text = option.entry
        holder.itemRadio.isChecked = option.selected
        holder.itemLayout.setOnClickListener {
            if (!option.selected) {
                select(position)
                holder.itemRadio.isChecked = option.selected
                var mode = PowerManager.POWER_SAVE_MODE_TRIGGER_PERCENTAGE
                var triggerLevel = 0
                when (option.value) {
                    0 -> {
                        triggerLevel = 0
                        mode = PowerManager.POWER_SAVE_MODE_TRIGGER_PERCENTAGE
                    }
                    1 -> {
                        triggerLevel = TRIGGER_LEVEL_MIN
                    }
                    2 -> {
                        mode = PowerManager.POWER_SAVE_MODE_TRIGGER_DYNAMIC
                    }
                }
                Settings.Global.putInt(resolver, Settings.Global.AUTOMATIC_POWER_SAVE_MODE, mode)
                if (mode != PowerManager.POWER_SAVE_MODE_TRIGGER_DYNAMIC) {
                    Settings.Global.putInt(resolver, Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL, triggerLevel)
                }
                // Suppress battery saver suggestion notification if enabling scheduling battery saver.
                if (mode == PowerManager.POWER_SAVE_MODE_TRIGGER_DYNAMIC || triggerLevel != 0) {
                    Settings.Secure.putInt(resolver, Settings.Secure.SUPPRESS_AUTO_BATTERY_SAVER_SUGGESTION, 1)
                }
                callback.onApply(option)
            }
        }
    }

    private fun select(pos: Int) {
        for (i in items.indices) {
            items[i].selected = pos == i
            notifyItemChanged(i)
        }
    }

    override fun getItemCount(): Int = items.size

    fun getSelected(): Option? {
        for (option in items) {
            if (option.selected) return option
        }
        return null
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemRadio: RadioButton = view.findViewById(R.id.list_item_radio)
        val itemEntry: TextView = view.findViewById(R.id.list_item_entry)
        val itemLayout: LinearLayout = view.findViewById(R.id.list_item_layout)
    }

    class Option(val entry: String, val value: Int) {
        var selected = false
    }

    interface Callback {
        fun onApply(option: Option)
    }
}