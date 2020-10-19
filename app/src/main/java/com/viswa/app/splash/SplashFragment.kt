package com.viswa.app.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.viswa.dfm.R
import com.viswa.dfm.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val splashViewModel by viewModels<SplashViewModel>()

    private lateinit var fragmentSplashBinding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        fragmentSplashBinding = FragmentSplashBinding.bind(view)
        return fragmentSplashBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentSplashBinding.buttonFeature.setOnClickListener {
            val clazz = Class.forName("com.viswa.feature.FeatureActivity")
            startActivity(Intent(this.context, clazz))
        }

        fragmentSplashBinding.chatFeature.setOnClickListener {

//            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToChatNavGraph())

            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToChatNavActivity())
        }

        println("xxx splash frag viewmodel ${splashViewModel.getTempString()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentSplashBinding.unbind()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SplashFragment()
    }
}
