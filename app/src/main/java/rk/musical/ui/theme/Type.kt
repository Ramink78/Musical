package rk.musical.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import rk.musical.R

val openSans =
    FontFamily(
        Font(R.font.opensans_light, FontWeight.Light),
        Font(R.font.opensans_regular, FontWeight.Normal),
        Font(R.font.opensans_medium, FontWeight.Medium),
        Font(R.font.opensans_semibold, FontWeight.SemiBold),
    )
val Typography =
    Typography(
        bodyLarge =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Light,
                fontSize = 18.sp,
            ),
    )
