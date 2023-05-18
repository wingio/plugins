package xyz.wingio.plugins.utils

import androidx.lifecycle.ViewModel
import com.aliucord.Utils
import com.discord.app.AppComponent
import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject
import rx.subjects.Subject

abstract class RxViewModel<V>(v: V? = null) : ViewModel(), AppComponent {
    private val unsubscribeSignal: Subject<Void, Void> = PublishSubject.k0()
    private val viewStateSubject: BehaviorSubject<V> = BehaviorSubject.k0()

    init {
        v?.apply {
            viewStateSubject.onNext(this)
        }
    }

    private val viewState: V
        get() = viewStateSubject.n0()

    fun observeViewState(): Observable<V> = viewStateSubject

    fun updateViewState(vs: V) {
        Utils.mainThread.post {
            viewStateSubject.onNext(vs)
        }
    }

    override fun getUnsubscribeSignal(): Subject<Void, Void> = unsubscribeSignal

}
