package com.corestack.khidmatai.ui.bookings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corestack.khidmatai.core.domain.model.Booking
import com.corestack.khidmatai.core.domain.model.TraceItem
import com.corestack.khidmatai.core.domain.repository.AuthRepository
import com.corestack.khidmatai.core.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@Immutable
data class BookingsState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // For single booking detail
    val activeBooking: Booking? = null,
    val activeBookingTraces: List<TraceItem> = emptyList(),
    val isDetailLoading: Boolean = false,
    val detailError: String? = null
)

sealed interface BookingsIntent {
    data object LoadBookings : BookingsIntent
    data class LoadBookingDetail(val bookingId: String) : BookingsIntent
    data class CancelBooking(val bookingId: String) : BookingsIntent
    data class CompleteBooking(val bookingId: String) : BookingsIntent
}

@KoinViewModel
class BookingsViewModel(
    private val serviceRepository: ServiceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingsState())
    val uiState: StateFlow<BookingsState> = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    fun onAction(intent: BookingsIntent) {
        when (intent) {
            BookingsIntent.LoadBookings -> loadBookings()
            is BookingsIntent.LoadBookingDetail -> loadBookingDetail(intent.bookingId)
            is BookingsIntent.CancelBooking -> cancelBooking(intent.bookingId)
            is BookingsIntent.CompleteBooking -> completeBooking(intent.bookingId)
        }
    }

    private fun getUserId(): String {
        val email = authRepository.getLastEmail()
        return email.ifBlank { "user_001" }
    }

    fun loadBookings() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            runCatching {
                serviceRepository.getBookingHistory(getUserId())
            }.onSuccess { list ->
                _uiState.update { it.copy(bookings = list, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message ?: "Failed to load bookings", isLoading = false) }
            }
        }
    }

    private fun loadBookingDetail(bookingId: String) {
        _uiState.update { it.copy(isDetailLoading = true, detailError = null, activeBooking = null, activeBookingTraces = emptyList()) }
        viewModelScope.launch {
            runCatching {
                serviceRepository.getBookingDetails(bookingId)
            }.onSuccess { booking ->
                _uiState.update { it.copy(activeBooking = booking, isDetailLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(detailError = error.message ?: "Failed to load booking details", isDetailLoading = false) }
            }
        }
    }

    private fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            runCatching {
                serviceRepository.cancelBooking(bookingId)
            }.onSuccess { updatedBooking ->
                _uiState.update { state ->
                    val updatedList = state.bookings.map {
                        if (it.id == bookingId) updatedBooking else it
                    }
                    val newActive = if (state.activeBooking?.id == bookingId) updatedBooking else state.activeBooking
                    state.copy(bookings = updatedList, activeBooking = newActive)
                }
            }
        }
    }

    private fun completeBooking(bookingId: String) {
        viewModelScope.launch {
            runCatching {
                serviceRepository.completeBooking(bookingId)
            }.onSuccess { traces ->
                _uiState.update { state ->
                    val newActive = if (state.activeBooking?.id == bookingId) {
                        state.activeBooking.copy(status = "completed")
                    } else {
                        state.activeBooking
                    }
                    val updatedList = state.bookings.map {
                        if (it.id == bookingId) it.copy(status = "completed") else it
                    }
                    state.copy(
                        bookings = updatedList,
                        activeBooking = newActive,
                        activeBookingTraces = traces
                    )
                }
            }
        }
    }
}
