package com.talentdafrique.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talentdafrique.app.data.remote.dto.ProfileDto
import com.talentdafrique.app.data.remote.dto.UserDto
import com.talentdafrique.app.data.repository.AuthRepository
import com.talentdafrique.app.data.repository.CvRepository
import com.talentdafrique.app.data.repository.ProfilesRepository
import com.talentdafrique.app.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: UserDto? = null,
    val profile: ProfileDto? = null,
    val errorMessage: String? = null,
    val isUploadingCv: Boolean = false,
    val cvUploadError: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profilesRepository: ProfilesRepository,
    private val cvRepository: CvRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val userResult = authRepository.me()) {
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = userResult.message)
                    return@launch
                }
                is Result.Success -> {
                    val user = userResult.data
                    when (val profileResult = profilesRepository.getMyProfile(user.typeProfil)) {
                        is Result.Success -> _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            user = user,
                            profile = profileResult.data,
                        )
                        is Result.Error -> _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            user = user,
                            errorMessage = profileResult.message,
                        )
                    }
                }
            }
        }
    }
        fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun uploadCv(file: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingCv = true, cvUploadError = null)
            when (val result = cvRepository.uploadCv(file)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isUploadingCv = false)
                    loadProfile()
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(isUploadingCv = false, cvUploadError = result.message)
            }
        }
    }
}