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
class BookingsViewModel(private val adminRepository: com.corestack.khidmatai.core.domain.repository.AdminRepository) : ViewModel() {

    private val _listState = MutableStateFlow<com.corestack.khidmatai.core.domain.model.AdminState<List<com.corestack.khidmatai.core.domain.model.AdminBooking>>>(
        _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading)
    val listState: StateFlow<com.corestack.khidmatai.core.domain.model.AdminState<List<com.corestack.khidmatai.core.domain.model.AdminBooking>>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<com.corestack.khidmatai.core.domain.model.AdminState<com.corestack.khidmatai.core.domain.model.AdminBooking>>(
        _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading)
    val detailState: StateFlow<com.corestack.khidmatai.core.domain.model.AdminState<com.corestack.khidmatai.core.domain.model.AdminBooking>> = _detailState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading
        viewModelScope.launch {
            try {
                _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Success(adminRepository.getAllBookings())
            } catch (e: Exception) {
                _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to load bookings")
            }
        }
    }

    fun loadDetail(bookingId: String) {
        _detailState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading
        viewModelScope.launch {
            try {
                _detailState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Success(adminRepository.getBookingById(bookingId))
            } catch (e: Exception) {
                _detailState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to load booking")
            }
        }
    }

    fun complete(bookingId: String) {
        viewModelScope.launch {
            try {
                val updated = adminRepository.completeBooking(bookingId)
                _detailState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Success(updated)
                loadAll()
            } catch (e: Exception) {
                _detailState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to complete booking")
            }
        }
    }

    fun cancel(bookingId: String) {
        viewModelScope.launch {
            try {
                val updated = adminRepository.cancelBooking(bookingId)
                _detailState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Success(updated)
                loadAll()
            } catch (e: Exception) {
                _detailState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to cancel booking")
            }
        }
    }
}
