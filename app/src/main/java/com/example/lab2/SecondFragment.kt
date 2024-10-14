package com.example.lab2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.graphics.PathParser
import androidx.fragment.app.activityViewModels
import com.example.lab2.databinding.FragmentSecondBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

        val drawingId = arguments?.getInt("drawing_id") ?: 0
        Log.d("Navigation", "Received drawing_id: $drawingId")

        if (drawingId != 0) {
            viewModel.loadDrawingFromDatabase(drawingId).observe(viewLifecycleOwner) { drawingEntity ->
                drawingEntity?.let {
                    Log.d("Database", "Loaded drawing with ID: ${it.id}, file path: ${it.filePath}")
                    val bitmap = BitmapFactory.decodeFile(it.filePath)
                    if (bitmap != null) {
                        binding.customView.setBitmap(bitmap)
                    }
                    binding.customView.setColor(it.color)
                    binding.customView.setBrushSize(it.brushSize)
                } ?: run {
                    Log.d("Database", "No drawing found for ID: $drawingId")
                }
            }
        } else {
            Toast.makeText(context, "Starting a new drawing.", Toast.LENGTH_SHORT).show()
        }

        setupBrushAndColorListeners()

        return binding.root
    }

    private fun setupBrushAndColorListeners() {
        val customView = binding.customView

        binding.roundBrushButton.setOnClickListener {
            customView.setBrushShape(BrushShape.ROUND)
        }

        binding.squareBrushButton.setOnClickListener {
            customView.setBrushShape(BrushShape.SQUARE)
        }

        binding.blueColor.setOnClickListener {
            binding.customView.setColor(Color.BLUE)
        }

        binding.redColor.setOnClickListener {
            binding.customView.setColor(Color.RED)
        }

        binding.whiteColor.setOnClickListener {
            binding.customView.setColor(Color.WHITE)
        }

        binding.blackColor.setOnClickListener {
            binding.customView.setColor(Color.BLACK)
        }

        binding.brushSizeSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.customView.setBrushSize(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.saveBrushSize(binding.customView.getBrushSize())
            }
        })
    }

    override fun onPause() {
        super.onPause()
        val currentBitmap = binding.customView.getBitmap()
        val currentColor = binding.customView.getCurrentColor()
        val currentBrushSize = binding.customView.getBrushSize()

        if (currentBitmap != null) {

            viewModel.saveDrawingToDatabase(requireContext(), currentBitmap, currentColor, currentBrushSize)
        }
    }




}
