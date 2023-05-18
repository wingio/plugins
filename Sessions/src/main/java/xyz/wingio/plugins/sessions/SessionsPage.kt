package xyz.wingio.plugins.sessions

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.*
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.*

import com.google.android.material.progressindicator.CircularProgressIndicator

import com.aliucord.Utils
import com.aliucord.utils.*
import com.aliucord.Http
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.DangerButton
import com.aliucord.utils.RxUtils.subscribe

import xyz.wingio.plugins.Sessions

import com.lytefast.flexinput.R

class SessionsPage: SettingsPage() {
    private val adapter = Adapter(mutableListOf())

    private lateinit var loadingContainer: LinearLayout
    private lateinit var currentSession: SessionCard
    private lateinit var content: LinearLayout
    private lateinit var otherSessionsTitle: TextView
    private lateinit var logOutAllBtn: DangerButton

    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        val ctx = linearLayout.context!!

        val viewModel = ViewModelProvider(this).get(SessionPageViewModel::class.java)
        viewModel.observeViewState().subscribe {
            configureUi(this)
        }

        loadingContainer = LinearLayout(ctx).apply {
            setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )

            gravity = Gravity.CENTER
        }

        CircularProgressIndicator(ctx).apply {
            indicatorSize = 24.dp
            setIndicatorColor(-0xa79a0e)
            isIndeterminate = true
            trackThickness = DimenUtils.dpToPx(3)
            loadingContainer.addView(this)
        }

        linearLayout.addView(loadingContainer)

        content = LinearLayout(ctx).apply {
            println("Setting up layout")
            setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            orientation = LinearLayout.VERTICAL

            TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).also { v ->
                v.text = "Current Session"
                addView(v)
            }
            currentSession = SessionCard(ctx).also { v ->
                v.isCurrent = true
                v.isMobile = true

                addView(v)
            }

            otherSessionsTitle = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).also { v ->
                v.text = "Other Sessions"
                addView(v)
            }
            RecyclerView(ctx).also { v ->
                v.adapter = adapter
                v.layoutManager = LinearLayoutManager(v.context)
                addView(v)
            }
            adapter.onLogoutClick = {
                fragmentManager?.let { fm ->
                    viewModel.showPasswordDialog(fm, listOf(it))
                }
            }

            logOutAllBtn = DangerButton(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(16.dp, 16.dp, 16.dp, 16.dp)
                }
                text = "Log out of all devices"
                setOnClickListener {
                    fragmentManager?.let { fm ->
                        viewModel.showPasswordDialog(fm, adapter.data.map { it.idHash })
                    }
                }

                addView(this)
            }

            linearLayout.addView(this)
        }
    }

    fun configureUi(viewState: SessionPageViewModel.ViewState) {
        configureTitleBar()
        setPadding(0)

        when(viewState) {
            is SessionPageViewModel.ViewState.Loading -> {
                loadingContainer.visibility = View.VISIBLE
                content.visibility = View.GONE
            }
            is SessionPageViewModel.ViewState.Loaded -> {
                loadingContainer.visibility = View.GONE
                configureLayout()
                configureCurrentSession(viewState.currentSession)
                configureSessionsList(viewState.sessions)
            }
        }
    }

    fun configureTitleBar() {
        setActionBarTitle("Sessions")
        setActionBarSubtitle(null)
    }

    fun configureLayout() {
        content.visibility = View.VISIBLE
    }

    private fun configureCurrentSession(session: Session) {
        currentSession.apply {
            title = "${session.clientInfo.os} · ${session.clientInfo.platform}"
            location = session.clientInfo.location
        }
    }

    private fun configureSessionsList(sessions: List<Session>) {
        adapter.updateData(sessions)
        otherSessionsTitle.visibility = if(sessions.isEmpty()) View.GONE else View.VISIBLE
        logOutAllBtn.visibility = if(sessions.isEmpty()) View.GONE else View.VISIBLE
    }

}