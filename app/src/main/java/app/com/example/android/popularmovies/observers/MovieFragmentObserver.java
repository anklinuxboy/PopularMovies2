package app.com.example.android.popularmovies.observers;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import timber.log.Timber;

public class MovieFragmentObserver implements LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onFragmentCreate() {
        Timber.d("Fragment created");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onFragmentResume() {
        Timber.d("Fragment resumed");
    }
}
