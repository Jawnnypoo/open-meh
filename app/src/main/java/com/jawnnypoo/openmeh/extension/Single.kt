package com.jawnnypoo.openmeh.extension

import com.jawnnypoo.openmeh.activity.BaseActivity
import com.uber.autodispose.SingleSubscribeProxy
import com.uber.autodispose.autoDisposable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.bind(baseActivity: BaseActivity): SingleSubscribeProxy<T> {
    return this.threaded().autoDisposable(baseActivity.scopeProvider)
}

fun <T> Single<T>.threaded(): Single<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}
