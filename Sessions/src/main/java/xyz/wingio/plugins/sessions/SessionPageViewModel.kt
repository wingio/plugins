package xyz.wingio.plugins.sessions

import android.text.method.PasswordTransformationMethod
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentManager
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.utils.GsonUtils
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import xyz.wingio.plugins.Sessions
import xyz.wingio.plugins.utils.SettingsViewModel
import com.lytefast.flexinput.R

class SessionPageViewModel: SettingsViewModel<SessionPageViewModel.ViewState>() {

    sealed class ViewState {
        object Loading : ViewState()
        data class Loaded(val currentSession: Session, val sessions: List<Session>) : ViewState()
        object Error : ViewState()
    }

    init {
        getSessions()
    }

    private fun getSessions() {
        Utils.threadPool.execute {
            updateViewState(ViewState.Loading)
            try {
                Http.Request.newDiscordRNRequest("/auth/sessions")
                    .execute()
                    .json(GsonUtils.gsonRestApi, SessionResponse::class.java)
                    .also {
                        println(it)
                        updateViewState(
                            ViewState.Loaded(
                                currentSession = it.userSessions.first { sesh -> Sessions.sessionIdHash == sesh.idHash },
                                sessions = it.userSessions.filterNot { sesh -> Sessions.sessionIdHash == sesh.idHash }
                            )
                        )
                    }
            } catch (e: Throwable) {
                updateViewState(ViewState.Error)
            }
        }
    }

    fun showPasswordDialog(fm: FragmentManager, sessionIds: List<String>) {
        val hasMfa = StoreStream.getUsers().me.mfaEnabled
        InputDialog().apply {
            setTitle("Enter password")
            setDescription("Password is required to log out device(s).")
            setPlaceholderText("Password")
            setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
            setOnDialogShownListener {
                inputLayout.editText?.transformationMethod = PasswordTransformationMethod.getInstance()
                okButton.text = if(hasMfa) "Next" else "Log out"
                fm.fragments.firstOrNull()?.context?.let {
                    okButton.setBackgroundColor(ColorCompat.getThemedColor(it, R.b.colorButtonDangerBackground))
                }
            }
            setOnOkListener {
                val password = input
                if(hasMfa) {
                    showMfaDialog(fm, sessionIds, password)
                } else {
                    logSessionsOut(sessionIds, password)
                }
                dismiss()
            }

            show(fm, "xyz.wingio.plugins.Sessions.PASSWORD_DIALOG")
        }
    }

    private fun showMfaDialog(fm: FragmentManager, sessionIds: List<String>, password: String) {
        InputDialog().apply {
            setTitle("Enter auth code")
            setDescription("2FA is required to log out device(s).")
            setPlaceholderText("6-digit authentication code")
            setInputType(EditorInfo.TYPE_CLASS_NUMBER)
            setOnDialogShownListener {
                okButton.text = "Log out"
                fm.fragments.firstOrNull()?.context?.let {
                    okButton.setBackgroundColor(ColorCompat.getThemedColor(it, R.b.colorButtonDangerBackground))
                }
            }
            setOnOkListener {
                val mfaCode = input
                logSessionsOut(sessionIds, password, mfaCode)
                dismiss()
            }

            show(fm, "xyz.wingio.plugins.Sessions.MFA_DIALOG")
        }
    }

    private fun logSessionsOut(sessionIds: List<String>, password: String, mfaCode: String? = null) {
        Utils.threadPool.execute {
            val body = SessionsLogoutBody(sessionIds, password, mfaCode)

            Http.Request.newDiscordRNRequest("/auth/sessions/logout", "POST")
                .executeWithJson(body)
                .also {
                    if(it.ok()) getSessions()
                }
        }
    }

}