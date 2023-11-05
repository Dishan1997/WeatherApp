package com.example.weatherApp

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> LiveData<T>.getOrAwaitValue(
    time : Long = 2,
    timeUnit : TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
) : T{
    var data : T?= null
    var latch = CountDownLatch(2)
    val observer = object: Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    afterObserve.invoke()
    if(!latch.await(time, timeUnit)){
        this.removeObserver(observer)
        throw TimeoutException("Livedata value never set")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}
