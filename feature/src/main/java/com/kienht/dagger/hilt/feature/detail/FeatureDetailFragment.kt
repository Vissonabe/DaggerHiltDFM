package com.kienht.dagger.hilt.feature.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import com.kienht.dagger.hilt.core.DFMSavedStateViewModelFactory
import com.kienht.dagger.hilt.core.UserModel
import com.kienht.dagger.hilt.core.di.UserModelSingletonQualifier
import com.kienht.dagger.hilt.dfm.databinding.SplashActivityBinding
import com.kienht.dagger.hilt.feature.FeatureActivityViewModel
import com.kienht.dagger.hilt.feature.FeatureSharedNavViewModel
import com.kienht.dagger.hilt.feature.R
import com.kienht.dagger.hilt.feature.model.CounterState
import timber.log.Timber
import javax.inject.Inject

/**
 * @author kienht
 * @since 15/09/2020
 */
class FeatureDetailFragment : Fragment() {

    @Inject
    @UserModelSingletonQualifier
    lateinit var singletonUserModel: UserModel

    @Inject
    lateinit var savedStateViewModelFactory: DFMSavedStateViewModelFactory

    private val featureActivityViewModel by activityViewModels<FeatureActivityViewModel> { savedStateViewModelFactory }

    private val featureDetailViewModel by viewModels<FeatureDetailViewModel> { savedStateViewModelFactory }

    private val featureSharedNavViewModel by navGraphViewModels<FeatureSharedNavViewModel>(R.id.feature_nav_graph) { savedStateViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                featureSharedNavViewModel.counterValue.observeAsState(initial = CounterState(0)).value.let {
                    getContentView(it.counterValue, featureSharedNavViewModel::onPlusClicked, featureSharedNavViewModel::onMinusClicked)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.e("singleton userModel = $singletonUserModel")
        singletonUserModel.value += " => FeatureDetailFragment"

        Timber.e("userModel of Activity= ${featureActivityViewModel.userModel}")
        Timber.e("userModel of Fragment = ${featureDetailViewModel.userModel}")
    }
}