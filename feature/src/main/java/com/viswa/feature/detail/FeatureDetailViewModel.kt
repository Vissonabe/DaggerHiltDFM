package com.viswa.feature.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.viswa.core.UserModel
import com.viswa.core.di.UserModelFeatureQualifier

/**
 * @author kienht
 * @since 15/09/2020
 */
class FeatureDetailViewModel @ViewModelInject constructor(
    @UserModelFeatureQualifier val userModel: UserModel,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
}