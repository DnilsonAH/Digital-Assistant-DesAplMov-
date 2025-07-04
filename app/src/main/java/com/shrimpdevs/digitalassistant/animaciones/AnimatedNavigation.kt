package com.shrimpdevs.digitalassistant.animaciones

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object AnimatedNavigation {
    private const val ANIMATION_DURATION = 100


    fun getFadeInEnterTransition() =
        fadeIn(
            animationSpec = tween(
                durationMillis = ANIMATION_DURATION
            )
        )

    fun getFadeOutExitTransition() =
        fadeOut(
            animationSpec = tween(
                durationMillis = ANIMATION_DURATION
            )
        )
    @OptIn(ExperimentalAnimationApi::class)
    fun NavGraphBuilder.composableWithFadeAnimation(
        route: String,
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route = route,
            enterTransition = { getFadeInEnterTransition() },
            exitTransition = { getFadeOutExitTransition() },
            popEnterTransition = { getFadeInEnterTransition() },
            popExitTransition = { getFadeOutExitTransition() },
            content = content
        )
    }
}