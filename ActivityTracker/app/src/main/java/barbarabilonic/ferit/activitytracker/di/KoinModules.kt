package barbarabilonic.ferit.activitytracker.di



import barbarabilonic.ferit.activitytracker.ui.viewmodels.MainViewModel
import barbarabilonic.ferit.activitytracker.UserRepository

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules= module {

    factory <UserRepository> {  UserRepository()}
}
val viewModelModules= module{
    viewModel <MainViewModel>{ MainViewModel(get()) }
}