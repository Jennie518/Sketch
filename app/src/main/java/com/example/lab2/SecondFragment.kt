package com.example.lab2

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.graphics.PathParser
import androidx.fragment.app.activityViewModels
import com.example.lab2.databinding.FragmentSecondBinding
import androidx.fragment.app.setFragmentResult

class SecondFragment : Fragment() {

    private val viewModel: CustomViewModel by activityViewModels()
    private lateinit var binding: FragmentSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_second, container, false
        )

        // handle the call back
//        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

            val currentPath = binding.customView.getCurrentPath()
            val currentColor = binding.customView.getCurrentColor()
            val currentBrushSize = binding.customView.getBrushSize()
//            viewModel.saveDrawing(currentPath)
            viewModel.saveDrawingToDatabase(currentPath.toString(), currentColor, currentBrushSize)
//            viewModel.saveColor(currentColor)
//            viewModel.saveBrushSize(currentBrushSize)
//            Log.d("here1", viewModel.getSavedDrawing().toString())
//            requireActivity().supportFragmentManager.popBackStack()
            val drawingId = arguments?.getInt("drawing_id") ?: 0
            if (drawingId != 0) {
                viewModel.loadDrawingFromDatabase(drawingId).observe(viewLifecycleOwner) { drawingEntity ->
                    drawingEntity?.let {
                        binding.customView.setPath(PathParser.createPathFromPathData(it.pathData))
                        binding.customView.setColor(it.color)
                        binding.customView.setBrushSize(it.brushSize)
                    }
                }
            }
//        }

        viewModel.brushSize.observe(viewLifecycleOwner) { size ->
            size?.let {
                binding.customView.setBrushSize(it)
                binding.brushSizeSeekbar.progress = it.toInt()
            }
        }
        val customView = binding.customView

        // Click listeners for brush shape selection
        binding.roundBrushButton.setOnClickListener {
            customView.setBrushShape(BrushShape.ROUND)
        }

        binding.squareBrushButton.setOnClickListener {
            customView.setBrushShape(BrushShape.SQUARE)
        }

        binding.lifecycleOwner = viewLifecycleOwner

        binding.brushSizeSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                binding.customView.setBrushSize(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.saveBrushSize(binding.customView.getBrushSize())
            }
        })
        // observe live data from view model
        viewModel.drawingPath.observe(viewLifecycleOwner) { savedPath ->
            // update path if changes
            savedPath?.let { binding.customView.setPath(it) }
        }

        viewModel.colorP.observe(viewLifecycleOwner) { savedPath ->

            savedPath?.let { binding.customView.setColor(it) }
        }

        viewModel.brushSize.observe(viewLifecycleOwner) { size ->
            size?.let { binding.customView.setBrushSize(it) }
        }


        // below are the logics for path's colors
        binding.blueColor.setOnClickListener() {

//            Toast.makeText(requireActivity(),"Clicked blueColor",Toast.LENGTH_SHORT).show()
            binding.customView.setColor(Color.BLUE)

        }

        binding.redColor.setOnClickListener() {
//            Toast.makeText(requireActivity(),"Clicked redColor",Toast.LENGTH_SHORT).show()
            binding.customView.setColor(Color.RED)
        }

        binding.whiteColor.setOnClickListener() {
//            Toast.makeText(requireActivity(),"Clicked whiteColor",Toast.LENGTH_SHORT).show()
            binding.customView.setColor(Color.WHITE)
        }

        binding.blackColor.setOnClickListener() {
//            Toast.makeText(requireActivity(),"Clicked blackColor",Toast.LENGTH_SHORT).show()
            binding.customView.setColor(Color.BLACK)
        }


        return binding.root
    }

//    private fun sendBrushShapeResult(brushShape: BrushShape) {
//        // Send result back to FirstFragment
//        val bundle = Bundle().apply {
//            putSerializable("brushShape", brushShape)
//        }
//        setFragmentResult("brushShapeRequest", bundle)
//        parentFragmentManager.popBackStack() // Go back to FirstFragment
//    }

    override fun onPause() {
        super.onPause()
        val currentPath = binding.customView.getCurrentPath()
        val currentColor = binding.customView.getCurrentColor()
        val currentBrushSize = binding.customView.getBrushSize()
        viewModel.saveDrawingToDatabase(currentPath.toString(), currentColor, currentBrushSize)
    }

}