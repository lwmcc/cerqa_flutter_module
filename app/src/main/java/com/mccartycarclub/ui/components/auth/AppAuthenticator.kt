package com.mccartycarclub.ui.components.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.amplifyframework.ui.authenticator.AuthenticatorState
import com.amplifyframework.ui.authenticator.AuthenticatorStepState
import com.amplifyframework.ui.authenticator.ErrorState
import com.amplifyframework.ui.authenticator.LoadingState
import com.amplifyframework.ui.authenticator.PasswordResetConfirmState
import com.amplifyframework.ui.authenticator.PasswordResetState
import com.amplifyframework.ui.authenticator.SignInConfirmCustomState
import com.amplifyframework.ui.authenticator.SignInConfirmMfaState
import com.amplifyframework.ui.authenticator.SignInConfirmNewPasswordState
import com.amplifyframework.ui.authenticator.SignInConfirmTotpCodeState
import com.amplifyframework.ui.authenticator.SignInContinueWithEmailSetupState
import com.amplifyframework.ui.authenticator.SignInContinueWithMfaSelectionState
import com.amplifyframework.ui.authenticator.SignInContinueWithMfaSetupSelectionState
import com.amplifyframework.ui.authenticator.SignInContinueWithTotpSetupState
import com.amplifyframework.ui.authenticator.SignInState
import com.amplifyframework.ui.authenticator.SignUpConfirmState
import com.amplifyframework.ui.authenticator.SignUpState
import com.amplifyframework.ui.authenticator.SignedInState
import com.amplifyframework.ui.authenticator.VerifyUserConfirmState
import com.amplifyframework.ui.authenticator.VerifyUserState
import com.amplifyframework.ui.authenticator.rememberAuthenticatorState
import com.amplifyframework.ui.authenticator.ui.AuthenticatorError
import com.amplifyframework.ui.authenticator.ui.AuthenticatorLoading
import com.amplifyframework.ui.authenticator.ui.PasswordReset
import com.amplifyframework.ui.authenticator.ui.PasswordResetConfirm
import com.amplifyframework.ui.authenticator.ui.SignIn
import com.amplifyframework.ui.authenticator.ui.SignInConfirmCustom
import com.amplifyframework.ui.authenticator.ui.SignInConfirmMfa
import com.amplifyframework.ui.authenticator.ui.SignInConfirmNewPassword
import com.amplifyframework.ui.authenticator.ui.SignInConfirmTotpCode
import com.amplifyframework.ui.authenticator.ui.SignInContinueWithEmailSetup
import com.amplifyframework.ui.authenticator.ui.SignInContinueWithMfaSelection
import com.amplifyframework.ui.authenticator.ui.SignInContinueWithMfaSetupSelection
import com.amplifyframework.ui.authenticator.ui.SignInContinueWithTotpSetup
import com.amplifyframework.ui.authenticator.ui.SignUp
import com.amplifyframework.ui.authenticator.ui.SignUpConfirm
import com.amplifyframework.ui.authenticator.ui.VerifyUser
import com.amplifyframework.ui.authenticator.ui.VerifyUserConfirm
import com.amplifyframework.ui.authenticator.util.AuthenticatorMessage
import javax.inject.Inject

/**
 * This is a copy of Amplify Authenticator so that it can be replaced in tests
 * in order to get passed the login screen
 */
@Suppress("CyclomaticComplexMethod", "LongParameterList")
@Composable
fun AppAuthenticator(
    modifier: Modifier = Modifier,
    state: AuthenticatorState,
    loadingContent: @Composable () -> Unit = { AuthenticatorLoading() },
    signInContent: @Composable (state: SignInState) -> Unit = { SignIn(it) },
    signInConfirmMfaContent: @Composable (state: SignInConfirmMfaState) -> Unit = { SignInConfirmMfa(it) },
    signInConfirmCustomContent: @Composable (state: SignInConfirmCustomState) -> Unit = { SignInConfirmCustom(it) },
    signInConfirmNewPasswordContent: @Composable (state: SignInConfirmNewPasswordState) -> Unit = {
        SignInConfirmNewPassword(it)
    },
    signInConfirmTotpCodeContent: @Composable (state: SignInConfirmTotpCodeState) -> Unit = {
        SignInConfirmTotpCode(it)
    },
    signInContinueWithTotpSetupContent: @Composable (state: SignInContinueWithTotpSetupState) -> Unit = {
        SignInContinueWithTotpSetup(it)
    },
    signInContinueWithEmailSetupContent: @Composable (state: SignInContinueWithEmailSetupState) -> Unit = {
        SignInContinueWithEmailSetup(it)
    },
    signInContinueWithMfaSetupSelectionContent: @Composable (state: SignInContinueWithMfaSetupSelectionState) ->
    Unit = {
        SignInContinueWithMfaSetupSelection(it)
    },
    signInContinueWithMfaSelectionContent: @Composable (state: SignInContinueWithMfaSelectionState) -> Unit = {
        SignInContinueWithMfaSelection(it)
    },
    signUpContent: @Composable (state: SignUpState) -> Unit = { SignUp(it) },
    signUpConfirmContent: @Composable (state: SignUpConfirmState) -> Unit = { SignUpConfirm(it) },
    passwordResetContent: @Composable (state: PasswordResetState) -> Unit = { PasswordReset(it) },
    passwordResetConfirmContent: @Composable (state: PasswordResetConfirmState) -> Unit = { PasswordResetConfirm(it) },
    verifyUserContent: @Composable (state: VerifyUserState) -> Unit = { VerifyUser(it) },
    verifyUserConfirmContent: @Composable (state: VerifyUserConfirmState) -> Unit = { VerifyUserConfirm(it) },
    errorContent: @Composable (state: ErrorState) -> Unit = { AuthenticatorError(it) },
    headerContent: @Composable () -> Unit = {},
    footerContent: @Composable () -> Unit = {},
    onDisplayMessage: ((AuthenticatorMessage) -> Unit)? = null,
    content: @Composable (state: SignedInState) -> Unit
) {
    val snackbarState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val stepState = state.stepState

    if (stepState is SignedInState) {
        println("MainActivity ***** STATE ${stepState.user}")
        content(stepState)
    } else {
        Box(modifier = modifier) {
            AnimatedContent(
                targetState = stepState,
                transitionSpec = { defaultTransition() },
                label = "AuthenticatorContentTransition"
            ) { targetState ->
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    headerContent()
                    when (targetState) {
                        is LoadingState -> loadingContent()
                        is SignInState -> signInContent(targetState)
                        is SignInConfirmMfaState -> signInConfirmMfaContent(targetState)
                        is SignInConfirmCustomState -> signInConfirmCustomContent(targetState)
                        is SignInConfirmNewPasswordState -> signInConfirmNewPasswordContent(
                            targetState
                        )

                        is SignInConfirmTotpCodeState -> signInConfirmTotpCodeContent(targetState)
                        is SignInContinueWithTotpSetupState -> signInContinueWithTotpSetupContent(targetState)

                        is SignInContinueWithEmailSetupState -> signInContinueWithEmailSetupContent(targetState)

                        is SignInContinueWithMfaSetupSelectionState ->
                            signInContinueWithMfaSetupSelectionContent(targetState)

                        is SignInContinueWithMfaSelectionState -> signInContinueWithMfaSelectionContent(targetState)

                        is SignUpState -> signUpContent(targetState)
                        is PasswordResetState -> passwordResetContent(targetState)
                        is PasswordResetConfirmState -> passwordResetConfirmContent(targetState)
                        is ErrorState -> errorContent(targetState)
                        is SignUpConfirmState -> signUpConfirmContent(targetState)
                        is VerifyUserState -> verifyUserContent(targetState)
                        is VerifyUserConfirmState -> verifyUserConfirmContent(targetState)
                        else -> Unit
                    }
                    footerContent()
                }
            }
            SnackbarHost(hostState = snackbarState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }

    LaunchedEffect(Unit) {
        state.messages.collect { event ->
            if (onDisplayMessage != null) {
                onDisplayMessage(event)
            } else {
                snackbarState.showSnackbar(event.message(context))
            }
        }
    }
}

internal fun AnimatedContentTransitionScope<AuthenticatorStepState>.defaultTransition(): ContentTransform {
    // Show reverse transition when going back to signIn
    if (targetState is SignInState && initialState != LoadingState) {
        return fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
                scaleOut(targetScale = 0.92f, animationSpec = tween(90)) + fadeOut(animationSpec = tween(90))
    }
    // Show forward transition for all others
    return fadeIn(animationSpec = tween(220, delayMillis = 90)) +
            scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)) togetherWith
            fadeOut(animationSpec = tween(90))
}

open class AuthenticatorStateProvider @Inject constructor() {
    @Composable
    open fun provide(): AuthenticatorState = rememberAuthenticatorState()
}

