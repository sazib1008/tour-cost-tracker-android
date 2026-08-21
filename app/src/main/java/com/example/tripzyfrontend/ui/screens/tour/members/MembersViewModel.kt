package com.example.tripzyfrontend.ui.screens.tour.members

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.domain.model.TourRole
import com.example.tripzyfrontend.domain.usecase.auth.CheckSessionUseCase
import com.example.tripzyfrontend.domain.usecase.tour.AddMemberUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import com.example.tripzyfrontend.domain.usecase.tour.RemoveMemberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MembersUiState {
    object Loading : MembersUiState
    data class Success(
        val tour: TourDetail,
        val members: List<TourMember>,
        val currentUserId: String?,
        val isAdmin: Boolean,
        val isAddingMember: Boolean = false
    ) : MembersUiState
    data class Error(val message: String) : MembersUiState
}

@HiltViewModel
class MembersViewModel @Inject constructor(
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    private val addMemberUseCase: AddMemberUseCase,
    private val removeMemberUseCase: RemoveMemberUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tourId: String = checkNotNull(savedStateHandle["tourId"])

    private val _uiState = MutableStateFlow<MembersUiState>(MembersUiState.Loading)
    val uiState: StateFlow<MembersUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    init {
        loadMembers()
    }

    fun loadMembers() {
        viewModelScope.launch {
            _uiState.value = MembersUiState.Loading
            val currentUserId = checkSessionUseCase.getCurrentUser().first()?.id

            getTourDetailsUseCase(tourId)
                .onSuccess { tour ->
                    val userMembership = tour.members.find { it.userId == currentUserId }
                    val isAdmin = (userMembership?.role == TourRole.ADMIN) || (tour.createdBy == currentUserId)

                    _uiState.value = MembersUiState.Success(
                        tour = tour,
                        members = tour.members,
                        currentUserId = currentUserId,
                        isAdmin = isAdmin
                    )
                }
                .onFailure { error ->
                    _uiState.value = MembersUiState.Error(error.localizedMessage ?: "Failed to load members")
                }
        }
    }

    fun inviteMember(email: String, role: String = "MEMBER") {
        val currentState = _uiState.value as? MembersUiState.Success ?: return
        if (email.isBlank()) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isAddingMember = true)
            addMemberUseCase(tourId, email, role)
                .onSuccess { updatedTour ->
                    val userMembership = updatedTour.members.find { it.userId == currentState.currentUserId }
                    val isAdmin = (userMembership?.role == TourRole.ADMIN) || (updatedTour.createdBy == currentState.currentUserId)

                    _uiState.value = currentState.copy(
                        tour = updatedTour,
                        members = updatedTour.members,
                        isAdmin = isAdmin,
                        isAddingMember = false
                    )
                    _snackbarEvent.emit("Member '$email' added successfully!")
                }
                .onFailure { error ->
                    _uiState.value = currentState.copy(isAddingMember = false)
                    _snackbarEvent.emit(error.localizedMessage ?: "Failed to add member")
                }
        }
    }

    fun removeMember(targetUserId: String) {
        viewModelScope.launch {
            removeMemberUseCase(tourId, targetUserId)
                .onSuccess {
                    _snackbarEvent.emit("Member removed from tour")
                    loadMembers()
                }
                .onFailure { error ->
                    _snackbarEvent.emit(error.localizedMessage ?: "Failed to remove member")
                }
        }
    }
}
