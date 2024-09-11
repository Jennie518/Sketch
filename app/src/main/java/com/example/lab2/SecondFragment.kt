package com.example.lab2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import com.example.lab2.databinding.FragmentFirstBinding
import com.example.lab2.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {


    private lateinit var binding: FragmentSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewModel: CustomViewModel by activityViewModels()

        binding = FragmentSecondBinding.inflate(layoutInflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        // handle back button clicked
        val callBack = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            val currentPath = binding.customView.getCurrentPath()
            viewModel.saveDrawing(currentPath)
//            Log.d("here1", viewModel.getSavedDrawing().toString())
            requireActivity().supportFragmentManager.popBackStack()
        }




        // listen to drawingPath
        viewModel.drawingPath.observe(viewLifecycleOwner) { savedPath ->
            // update the view when drawingPath is changed
            savedPath?.let { binding.customView.setPath(it) }
        }

        return binding.root
    }


//    override fun onPause() {
//        super.onPause()
//
//        val currentPath = binding.customView.getCurrentPath()
//        viewModel.saveDrawing(currentPath)
//    }
}