package com.example.lab2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.lab2.databinding.FragmentFirstBinding
import androidx.fragment.app.setFragmentResultListener

class FirstFragment : Fragment() {
    private val viewModel: CustomViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFirstBinding.inflate(layoutInflater, container, false)

        binding.navigateButton.setOnClickListener {
            viewModel.getLastSavedDrawingId().observe(viewLifecycleOwner) { drawingId ->
                if (drawingId != null) {
                    val bundle = Bundle().apply {
                        putInt("drawing_id", drawingId)
                    }
                    val secondFragment = SecondFragment()
                    secondFragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, secondFragment)
                        .addToBackStack(null)
                        .commit()
                    Toast.makeText(requireContext(), "Navigating with drawing_id: $drawingId", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No saved drawing found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }
}
