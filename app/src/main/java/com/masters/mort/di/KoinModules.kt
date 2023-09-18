package com.masters.mort.di

import com.masters.mort.repository.implementations.MortalityRepository
import com.masters.mort.repository.implementations.DeceasedPersonRepository
import com.masters.mort.repository.implementations.UserRepository
import com.masters.mort.viewmodel.MortalityViewModel
import com.masters.mort.viewmodel.DeceasedPersonViewModel
import com.masters.mort.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single{
        UserRepository()
    }

    single{
        DeceasedPersonRepository()
    }

    single {
        MortalityRepository()
    }
}

val viewModelModule = module{
    viewModel {
        UserViewModel(get())
    }

    viewModel {
        DeceasedPersonViewModel(get())
    }

    viewModel{
        MortalityViewModel(get())
    }
}