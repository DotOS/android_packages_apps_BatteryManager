package com.dot.lifesaver.repositories.constants

/**
 * Expose secure settings hidden in the Android APIs
 */
object BatterySaverSecureSettings {
    /**
     * Parameters for low power mode
     */
    const val BATTERY_SAVER_CONSTANTS = "battery_saver_constants"

    /**
     * Low power mode toggle
     */
    const val LOW_POWER = "low_power"

    /**
     * Re-enable low power mode on reboots / unplugs
     */
    const val LOW_POWER_STICKY = "low_power_sticky"

    /**
     * Battery percentage to auto-disable low power mode
     */
    const val LOW_POWER_STICKY_AUTO_DISABLE_ENABLED = "low_power_sticky_auto_disable_enabled"

    const val ADAPTIVE_BATTERY_MANAGEMENT_ENABLED = "adaptive_battery_management_enabled"

    const val LOW_POWER_STICKY_AUTO_DISABLE_LEVEL = "low_power_sticky_auto_disable_level"
}