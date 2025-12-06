package com.cerqa.ui.theme

import platform.UIKit.*

class IOSStatusBarController(private val window: UIWindow) : StatusBarController {

    override fun setStatusBarColor(colorHex: Long, darkIcons: Boolean) {
        val red = ((colorHex shr 16) and 0xFF) / 255.0
        val green = ((colorHex shr 8) and 0xFF) / 255.0
        val blue = (colorHex and 0xFF) / 255.0
        val uiColor = UIColor.colorWithRed(red, green, blue, alpha = 1.0)

        window.rootViewController?.view?.backgroundColor = uiColor
    }
}