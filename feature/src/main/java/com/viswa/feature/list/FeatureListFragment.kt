package com.viswa.feature.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.viswa.core.DFMSavedStateViewModelFactory
import com.viswa.core.UserModel
import com.viswa.core.di.UserModelSingletonQualifier
import com.viswa.feature.FeatureActivityViewModel
import com.viswa.feature.FeatureSharedNavViewModel
import com.viswa.feature.R
import com.viswa.feature.model.CounterState
import com.viswa.feature.model.MovieItem
import timber.log.Timber
import javax.inject.Inject

/**
 * @author kienht
 * @since 15/09/2020
 */
class FeatureListFragment : Fragment() {

    @Inject
    @UserModelSingletonQualifier
    lateinit var singletonUserModel: UserModel

    @Inject
    lateinit var savedStateViewModelFactory: DFMSavedStateViewModelFactory

    private val featureActivityViewModel by activityViewModels<FeatureActivityViewModel> { savedStateViewModelFactory }

    private val featureListViewModel by viewModels<FeatureListViewModel> { savedStateViewModelFactory }

    private val featureSharedNavViewModel by navGraphViewModels<FeatureSharedNavViewModel>(R.id.feature_nav_graph) { savedStateViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.feature_list_fragment, container, false
        ).apply {
            findViewById<ComposeView>(R.id.compose_list).setContent {
                featureListViewModel.getMovieCollectionData().observeAsState().value.let {
                    if(it != null) {
                        moviesList(items = it.items, ::onClickAction)
                    } else {
                        movieListNoContent()
                    }
                }
            }
        }
    }

    private fun onClickAction(item : MovieItem) {
        findNavController().navigate(FeatureListFragmentDirections.goToFeatureDetailFragment(item))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.e("singleton userModel = $singletonUserModel")
        singletonUserModel.value += " => FeatureListFragment"

        Timber.e("userModel of Activity= ${featureActivityViewModel.userModel}")
        Timber.e("userModel of Fragment = ${featureListViewModel.userModel}")
    }
}