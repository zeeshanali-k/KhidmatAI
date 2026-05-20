package com.corestack.khidmatai.admin.ui.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corestack.khidmatai.core.domain.model.AdminBooking
import com.corestack.khidmatai.core.domain.model.AdminState
import com.corestack.khidmatai.core.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class BookingsViewModel(private val adminRepository: AdminRepository) : ViewModel() {

    private val _listState = MutableStateFlow<AdminState<List<AdminBooking>>>(
        AdminState.Loading)
    val listState: StateFlow<AdminState<List<AdminBooking>>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<AdminState<AdminBooking>>(
        AdminState.Loading)
    val detailState: StateFlow<AdminState<AdminBooking>> = _detailState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        _listState.value = AdminState.Loading
        viewModelScope.launch {
            try {
                _listState.value = AdminState.Success(adminRepository.getAllBookings())
            } catch (e: Exception) {
                _listState.value = AdminState.Error(e.message ?: "Failed to load bookings")
            }
        }
    }

    fun loadDetail(bookingId: String) {
        _detailState.value = AdminState.Loading
        viewModelScope.launch {
            try {
                _detailState.value = AdminState.Success(adminRepository.getBookingById(bookingId))
            } catch (e: Exception) {
                _detailState.value = AdminState.Error(e.message ?: "Failed to load booking")
            }
        }
    }

    fun complete(bookingId: String) {
        viewModelScope.launch {
            try {
                val updated = adminRepository.completeBooking(bookingId)
                _detailState.value = AdminState.Success(updated)
                loadAll()
            } catch (e: Exception) {
                _detailState.value = AdminState.Error(e.message ?: "Failed to complete booking")
            }
        }
    }

    fun cancel(bookingId: String) {
        viewModelScope.launch {
            try {
                val updated = adminRepository.cancelBooking(bookingId)
                _detailState.value = AdminState.Success(updated)
                loadAll()
            } catch (e: Exception) {
                _detailState.value = AdminState.Error(e.message ?: "Failed to cancel booking")
            }
        }
    }
}
