package rk.musical.data

sealed interface DataSourceState {
    object Created : DataSourceState
    object Loading : DataSourceState
    object Success : DataSourceState
}