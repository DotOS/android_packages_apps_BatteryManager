package com.dot.lifesaver.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.dot.lifesaver.R

class DotMaterialPreference(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context!!, attrs) {

    var titleView: TextView? = null
    var summaryView: TextView? = null
    var iconView: ImageView? = null
    var switchView: Switch? = null
    var seekBar: SeekBar? = null
    var countText: TextView? = null
    private var layoutView: LinearLayout? = null
    private var widgetFrame: LinearLayout? = null

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(
            R.layout.preference_base, this, true
        )
        if (attrs != null) {
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.DotMaterialPreference)
            val isSeekable = a.getBoolean(R.styleable.DotMaterialPreference_seekable, false)
            when {
                a.getBoolean(R.styleable.DotMaterialPreference_cardStyle, false) -> {
                    if (childCount != 0) removeAllViews()
                    LayoutInflater.from(context).inflate(R.layout.preference_card_base, this, true)
                }
                isSeekable -> {
                    if (childCount != 0) removeAllViews()
                    LayoutInflater.from(context).inflate(R.layout.preference_seekbar, this, true)
                }
                else -> {
                    if (childCount != 0) removeAllViews()
                    LayoutInflater.from(context).inflate(R.layout.preference_base, this, true)
                }
            }
            titleView = requireViewById(android.R.id.title)
            titleView!!.typeface = Typeface.create("google-sans-medium", Typeface.NORMAL)
            summaryView = requireViewById(android.R.id.summary)
            summaryView!!.typeface = Typeface.create("google-sans", Typeface.NORMAL)
            iconView = requireViewById(android.R.id.icon)
            layoutView = requireViewById(R.id.preference_layout)
            widgetFrame = requireViewById(android.R.id.widget_frame)
            if (a.getBoolean(R.styleable.DotMaterialPreference_android_checkable, false)) {
                widgetFrame!!.addView(
                    LayoutInflater.from(context).inflate(
                        R.layout.preference_widget_switch,
                        this,
                        false
                    )
                )
                switchView = findViewById(android.R.id.switch_widget)
                switchView!!.isChecked = a.getBoolean(
                    R.styleable.DotMaterialPreference_android_checked,
                    false
                )
            } else {
                if (widgetFrame!!.childCount != 0)
                    widgetFrame!!.removeAllViews()
            }
            isEnabled = true
            titleView!!.text = a.getString(R.styleable.DotMaterialPreference_android_title)
            summaryView!!.text = a.getString(R.styleable.DotMaterialPreference_android_summary)
            titleView!!.visibility = if (titleView!!.text.isEmpty()) GONE else VISIBLE
            summaryView!!.visibility = if (summaryView!!.text.isEmpty()) GONE else VISIBLE
            val icon = a.getResourceId(
                R.styleable.DotMaterialPreference_android_icon,
                android.R.color.transparent
            )
            iconView!!.setImageResource(icon)
            iconView!!.imageTintList = ColorStateList.valueOf(
                a.getColor(
                    R.styleable.DotMaterialPreference_android_tint,
                    getContext().getColor(R.color.colorAccent)
                )
            )
            if (icon == android.R.color.transparent) {
                iconView!!.visibility = GONE
            }
            if (isSeekable) {
                seekBar = requireViewById(R.id.seekbar)
                seekBar!!.min = a.getInt(R.styleable.DotMaterialPreference_android_min, 0)
                seekBar!!.max = a.getInt(R.styleable.DotMaterialPreference_android_max, 100)
                seekBar!!.setProgress(
                    a.getInt(
                        R.styleable.DotMaterialPreference_android_progress,
                        seekBar!!.min
                    ), false
                )
                widgetFrame!!.addView(
                    LayoutInflater.from(context).inflate(
                        R.layout.preference_widget_count,
                        this,
                        false
                    )
                )
                countText = requireViewById(R.id.countText)
                countText!!.text =
                    a.getInt(R.styleable.DotMaterialPreference_android_progress, 0).toString()
            }
            val showDivider = a.getBoolean(R.styleable.DotMaterialPreference_showDivider, false)
            if (showDivider) addView(
                LayoutInflater.from(context).inflate(R.layout.item_divider, this, false)
            )
            a.recycle()
        }
    }

    override fun isEnabled(): Boolean {
        val isEnabled = super.isEnabled()
        titleView!!.isEnabled = isEnabled
        summaryView!!.isEnabled = isEnabled
        switchView?.isEnabled = isEnabled
        seekBar?.isEnabled = isEnabled
        return isEnabled
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        seekBar?.isEnabled = enabled
        switchView?.isEnabled = enabled
    }

    fun setProgress(value: Int) {
        seekBar?.progress = value
        countText?.text = value.toString()
    }

    fun getProgress(): Int = seekBar?.progress!!

    fun setMax(value: Int) {
        seekBar?.max = value
    }

    fun setMin(value: Int) {
        seekBar?.min = value
    }

    fun setChecked(checked: Boolean) {
        switchView?.isChecked = checked
    }

    fun setOnCheckListener(onCheckedChangeListener: CompoundButton.OnCheckedChangeListener) {
        setOnClickPreference(null)
        setOnClickPreference {
            switchView?.isChecked = !switchView?.isChecked!!
        }
        switchView?.setOnCheckedChangeListener(null)
        switchView?.setOnCheckedChangeListener(onCheckedChangeListener)
    }

    fun setOnClickPreference(onClickListener: OnClickListener?) {
        layoutView!!.setOnClickListener(onClickListener)
    }

    fun setOnProgressChangedPreference(postUnit: (progress: Int) -> Unit) {
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                countText?.text = progress.toString()
                postUnit(seekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }

        })
    }
}