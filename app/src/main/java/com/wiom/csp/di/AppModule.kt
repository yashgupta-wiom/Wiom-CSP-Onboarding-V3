package com.wiom.csp.di

import com.wiom.csp.data.repository.MockOnboardingRepository
import com.wiom.csp.data.repository.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(impl: MockOnboardingRepository): OnboardingRepository
}
