package com.cerqa.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

actual object CustomIcons {
    @Composable
    actual fun carsNav(): Painter {
        // TODO: Replace with your actual drawable resource ID
        // return painterResource(R.drawable.cars_nav)
        throw NotImplementedError("Add cars_nav.xml to androidMain/res/drawable first")
    }

    @Composable
    actual fun personsPlus(): Painter {
        // TODO: Replace with your actual drawable resource ID
        // return painterResource(R.drawable.persons_plus)
        throw NotImplementedError("Add persons_plus.xml to androidMain/res/drawable first")
    }
}
