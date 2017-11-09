package app.com.example.android.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

import app.com.example.android.popularmovies.di.AppComponent;
import app.com.example.android.popularmovies.di.DaggerAppComponent;
import app.com.example.android.popularmovies.di.DbModule;
import app.com.example.android.popularmovies.di.AppModule;
import app.com.example.android.popularmovies.di.NetModule;
import timber.log.Timber;

/**
 * Created by ankit on 9/9/16.
 */
public class MoviesApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);

        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        );

        // Enable command line interface
        initializerBuilder.enableDumpapp(
                Stetho.defaultDumperPluginsProvider(getApplicationContext())
        );

        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
        Timber.plant(new Timber.DebugTree());

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule())
                .dbModule(new DbModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
