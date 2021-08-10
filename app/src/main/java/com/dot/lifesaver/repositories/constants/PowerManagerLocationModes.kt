package com.dot.lifesaver.repositories.constants

/**
 * A rip from android.os.PowerManager LOCATION_MODE constants, as
 * PowerManager requires Android P APIs
 */
object PowerManagerLocationModes {
    /**
     * Default behavior
     */
    const val NO_CHANGE = 0

    /**
     * Disable just GPS when the screen is off
     */
    const val GPS_DISABLED_SCREEN_OFF = 1

    /**
     * Disable all location providers when the screen is off
     */
    const val ALL_DISABLED_SCREEN_OFF = 2

    /**
     * Only foreground apps can query new locations
     */
    const val FOREGROUND_ONLY = 3

    /**
     * Throttle requests when the screen is off
     */
    const val THROTTLE_SCREEN_OFF = 4
}