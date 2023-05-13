package com.geronso.pearler.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geronso.pearler.logging.Logger
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.xml.transform.Transformer

abstract class BaseEventManager<VME : BaseViewModelEvent, VS : BaseViewState> :
    ViewModel(), Observer<VS> {

    private val disposables = CompositeDisposable()
    private val events: PublishSubject<VME> = PublishSubject.create()
    var viewEventObservable: MutableLiveData<VS> = MutableLiveData()

    init {
        reinit()
    }

    fun reinit() {
        events
            .flatMap {
                onEvent(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this)
    }

    override fun onSubscribe(d: Disposable?) {
        disposables.add(d)
    }

    override fun onCleared() {
        if(!disposables.isDisposed) {
            disposables.dispose()
        }
        disposables.clear()
    }

    fun event(event: VME) {
        events.onNext(event)
    }

    protected abstract fun onEvent(event: VME): ObservableSource<out VS>
}