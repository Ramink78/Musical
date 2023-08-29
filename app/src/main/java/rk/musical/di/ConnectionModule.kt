package rk.musical.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import rk.musical.player.MusicalRemoteControl
import rk.musical.player.ServiceConnection

@Module
@InstallIn(ActivityRetainedComponent::class)
class ConnectionModule {

    @Provides
    @ActivityRetainedScoped
    fun provideConnection() = ServiceConnection()

    @Provides
    @ActivityRetainedScoped
    fun provideMusicalRemoteControl(serviceConnection: ServiceConnection) =
        MusicalRemoteControl(serviceConnection)

}