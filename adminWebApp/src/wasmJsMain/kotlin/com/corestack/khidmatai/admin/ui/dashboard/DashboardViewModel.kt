package com.corestack.khidmatai.admin.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corestack.khidmatai.core.domain.model.AdminBooking
import com.corestack.khidmatai.core.domain.model.AdminState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

data class DashboardData(
    val totalBookings: Int,
    val activeProviders: Int,
    val totalRequests: Int,
    val recentBookings: List<AdminBooking>,
    val recentRequests: List<com.corestack.khidmatai.core.domain.model.AdminRequest>
)

@KoinViewModel
class DashboardViewModel(private val adminRepository: com.corestack.khidmatai.core.domain.repository.AdminRepository) : ViewModel() {

    private val _state = MutableStateFlow<AdminState<DashboardData>>(
        AdminState.Loading)
    val state: StateFlow<AdminState<DashboardData>> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = AdminState.Loading
        viewModelScope.launch {
            try {
                val bookingsDeferred = async { adminRepository.getAllBookings() }
                val providersDeferred = async { adminRepository.getAllProviders() }
                val requestsDeferred = async { adminRepository.getAllRequests() }

                val bookings = bookingsDeferred.await()
                val providers = providersDeferred.await()
                val requests = requestsDeferred.await()

                _state.value = AdminState.Success(
                    DashboardData(
                        totalBookings = bookings.size,
                        activeProviders = providers.count { it.availability },
                        totalRequests = requests.size,
                        recentBookings = bookings.takeLast(5).reversed(),
                        recentRequests = requests.takeLast(5).reversed()
                    )
                )
            } catch (e: Exception) {
                _state.value = AdminState.Error(e.message ?: "Failed to load dashboard")
            }
        }
    }
}
