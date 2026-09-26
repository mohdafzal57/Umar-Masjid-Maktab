package com.example.umarmasjid.di

import com.example.umarmasjid.navigation.deeplink.DeepLinkHandler
import com.example.umarmasjid.navigation.deeplink.DefaultDeepLinkHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeepLinkModule {

    @Binds
    @Singleton
    abstract fun bindDeepLinkHandler(
        impl: DefaultDeepLinkHandler
    ): DeepLinkHandler
}
