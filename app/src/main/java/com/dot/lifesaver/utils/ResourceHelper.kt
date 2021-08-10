package com.dot.lifesaver.utils

import android.content.Context
import android.content.res.Resources

object ResourceHelper {
    fun getInternalInteger(res: String, context: Context): Int {
        return try {
            context.resources.getInteger(Resources.getSystem().getIdentifier(res, "integer", "android"))
        } catch (e: Resources.NotFoundException) {
            -1
        }
    }
}