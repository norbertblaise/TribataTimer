package com.blaise.tribatatimer.session

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blaise.tribatatimer.R

class CurrentSessionFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentSessionFragment()
    }

    private lateinit var viewModel: CurrentSessionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_session_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentSessionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}