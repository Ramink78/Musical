package rk.playbackservice

import android.app.Service
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {
    @ServiceScoped
    @Provides
    fun provideServiceScope(service: Service):CoroutineScope =
        (service as LifecycleOwner).lifecycleScope
}