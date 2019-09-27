package net.nicbell.news.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import net.nicbell.news.viewModel.Event

object FragmentExtensions {
    /**
     * Observe live data
     */
    fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
        liveData.observe(this, Observer(body))
    }

    /**
     * Observe live data event
     */
    fun <T : Any, L : LiveData<Event<T>>> LifecycleOwner.observeEvent(liveData: L, body: (T?) -> Unit) {
        liveData.observe(this, Observer<Event<T>> { event ->
            event?.getContentIfNotHandled()?.let {
                body.invoke(it)
            }
        })
    }
}