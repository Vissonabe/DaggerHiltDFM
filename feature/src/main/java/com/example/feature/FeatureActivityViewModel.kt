package com.example.feature

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.core.UserModel
import com.example.core.di.UserModelFeatureQualifier

/**
 * @author kienht
 * @since 15/09/2020
 */
class FeatureActivityViewModel @ViewModelInject constructor(
    @UserModelFeatureQualifier val userModel: UserModel,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
}