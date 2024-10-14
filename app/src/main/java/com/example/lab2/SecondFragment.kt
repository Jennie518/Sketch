package com.example.lab2


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory


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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.PathParser
import androidx.fragment.app.activityViewModels
import com.example.lab2.databinding.FragmentSecondBinding
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

//class DrawingFragment : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return ComposeView(requireContext()).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                DrawingScreen()
//            }
//        }
//    }
//}
//
//class SecondFragment : Fragment() {
//
//    private val viewModel: CustomViewModel by activityViewModels()
//    private lateinit var binding: FragmentSecondBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = DataBindingUtil.inflate(
//            inflater, R.layout.fragment_second, container, false
//        )
//
//        // handle the call back
////        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//
//            val currentPath = binding.customView.getCurrentPath()
//            val currentColor = binding.customView.getCurrentColor()
//            val currentBrushSize = binding.customView.getBrushSize()
////            viewModel.saveDrawing(currentPath)
//            viewModel.saveDrawingToDatabase(currentPath.toString(), currentColor, currentBrushSize)
////            viewModel.saveColor(currentColor)
////            viewModel.saveBrushSize(currentBrushSize)
////            Log.d("here1", viewModel.getSavedDrawing().toString())
////            requireActivity().supportFragmentManager.popBackStack()
//            val drawingId = arguments?.getInt("drawing_id") ?: 0
//            if (drawingId != 0) {
//                viewModel.loadDrawingFromDatabase(drawingId).observe(viewLifecycleOwner) { drawingEntity ->
//                    drawingEntity?.let {
//                        binding.customView.setPath(PathParser.createPathFromPathData(it.pathData))
//                        binding.customView.setColor(it.color)
//                        binding.customView.setBrushSize(it.brushSize)
//                    }
//                }
//            }
////        }
//
//        viewModel.brushSize.observe(viewLifecycleOwner) { size ->
//            size?.let {
//                binding.customView.setBrushSize(it)
//                binding.brushSizeSeekbar.progress = it.toInt()
//            }
//        }
//        val customView = binding.customView
//
//        // Click listeners for brush shape selection
//        binding.roundBrushButton.setOnClickListener {
//            customView.setBrushShape(BrushShape.ROUND)
//        }
//
//        binding.squareBrushButton.setOnClickListener {
//            customView.setBrushShape(BrushShape.SQUARE)
//        }
//
//        binding.lifecycleOwner = viewLifecycleOwner
//
//        binding.brushSizeSeekbar.setOnSeekBarChangeListener(object :
//            SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//
//                binding.customView.setBrushSize(progress.toFloat())
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                // Do nothing
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                viewModel.saveBrushSize(binding.customView.getBrushSize())
//            }
//        })
//        // observe live data from view model
//        viewModel.drawingPath.observe(viewLifecycleOwner) { savedPath ->
//            // update path if changes
//            savedPath?.let { binding.customView.setPath(it) }
//        }
//
//        viewModel.colorP.observe(viewLifecycleOwner) { savedPath ->
//
//            savedPath?.let { binding.customView.setColor(it) }
//        }
//
//        viewModel.brushSize.observe(viewLifecycleOwner) { size ->
//            size?.let { binding.customView.setBrushSize(it) }
//        }
//
//
//        // below are the logics for path's colors
//        binding.blueColor.setOnClickListener() {
//
////            Toast.makeText(requireActivity(),"Clicked blueColor",Toast.LENGTH_SHORT).show()
//            binding.customView.setColor(Color.BLUE)
//
//        }
//
//        binding.redColor.setOnClickListener() {
////            Toast.makeText(requireActivity(),"Clicked redColor",Toast.LENGTH_SHORT).show()
//            binding.customView.setColor(Color.RED)
//        }
//
//        binding.whiteColor.setOnClickListener() {
////            Toast.makeText(requireActivity(),"Clicked whiteColor",Toast.LENGTH_SHORT).show()
//            binding.customView.setColor(Color.WHITE)
//        }
//
//        binding.blackColor.setOnClickListener() {
////            Toast.makeText(requireActivity(),"Clicked blackColor",Toast.LENGTH_SHORT).show()
//            binding.customView.setColor(Color.BLACK)
//        }
//
//
//        return binding.root
//    }
//
////    private fun sendBrushShapeResult(brushShape: BrushShape) {
////        // Send result back to FirstFragment
////        val bundle = Bundle().apply {
////            putSerializable("brushShape", brushShape)
////        }
////        setFragmentResult("brushShapeRequest", bundle)
////        parentFragmentManager.popBackStack() // Go back to FirstFragment
////    }
//
//    override fun onPause() {
//        super.onPause()
//        val currentPath = binding.customView.getCurrentPath()
//        val currentColor = binding.customView.getCurrentColor()
//        val currentBrushSize = binding.customView.getBrushSize()
//        viewModel.saveDrawingToDatabase(currentPath.toString(), currentColor, currentBrushSize)
//    }
//
//}


@Composable
fun CanvasScreen(
    navController: NavController,
    drawingId: Int?,
    viewModel: CustomViewModel = viewModel()
) {


    var localBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var localColor by remember { mutableStateOf(Color.Black) }
    var localBrushSize by remember { mutableStateOf(10f) }

    // the update logic when Viewmodel values changes
    LaunchedEffect(drawingId) {
        if (drawingId != null) {
            viewModel.loadDrawingFromDatabase(drawingId).observeForever { drawingEntity ->
                drawingEntity?.let {
                    localBitmap = BitmapFactory.decodeFile(it.filePath)
                    localColor = Color(it.color)
                    localBrushSize = it.brushSize
                }
            }
        }
    }


    BackHandler(true) {
        localBitmap?.let {
            viewModel.saveDrawingToDatabase(
                navController.context,
                it,
                localColor.toArgb(),
                localBrushSize
            )
        }
        Log.d("BACK", "OnBackPressed and Drawing Saved: " + viewModel.getSavedDrawing().toString())
        navController.popBackStack()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(modifier = Modifier.fillMaxSize(),
            factory = { context ->
                CustomView(context = context, attrs = null).apply {
                    localBitmap?.let { setBitmap(it) }
                    setColor(localColor.toArgb())
                    setBrushSize(localBrushSize)
                }
            },
            update = { customView ->
                customView.setColor(localColor.toArgb())
                customView.setBrushSize(localBrushSize)
            }
        )
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            DrawingUI(viewModel, localColor, localBrushSize)
        }
    }
}

@Composable
fun DrawingUI(viewModel: CustomViewModel, currentColor: Color, currentBrushSize: Float) {

//    var brushSize by remember { mutableStateOf(10f) }
//    var currentColor by remember { mutableStateOf(Color.Black) }
//
//    val savedBrushSize by viewModel.brushSize.collectAsState(initial = 10f)
//    val savedColor by viewModel.colorP.collectAsState(initial = Color.Black.toArgb())
//
//    LaunchedEffect(savedBrushSize, savedColor) {
//        brushSize = savedBrushSize
//        currentColor = Color(savedColor)
//    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {}) {
                Text(text = "Round Brush")
            }
            Button(onClick = {}) {
                Text(text = "Square Brush")
            }
        }
        Slider(
            value = currentBrushSize,
            onValueChange = { newSize ->
                viewModel.saveBrushSize(newSize)
            }, valueRange = 1f..50f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ColorButton(color = Color.Blue) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
            }
            ColorButton(color = Color.Red) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
            }
            ColorButton(color = Color.Black) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
            }
            ColorButton(color = Color.White) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
            }
        }
    }
}

@Composable
fun ColorButton(color: Color, onClick: (Color) -> Unit) {
    IconButton(onClick = { onClick(color) }, modifier = Modifier.size(50.dp)) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color)
        )
    }
}
//    ====== =
//    import androidx . core . graphics . PathParser
//            import androidx . fragment . app . activityViewModels
//            import com . example . lab2 . databinding . FragmentSecondBinding
//            import java . io . File
//            import java . io . FileOutputStream
//            import java . io . IOException
//
//    class SecondFragment : Fragment() {
//
//        private val viewModel: CustomViewModel by activityViewModels()
//        private lateinit var binding: FragmentSecondBinding
//
//        override fun onCreateView(
//            inflater: LayoutInflater, container: ViewGroup?,
//            savedInstanceState: Bundle?
//        ): View? {
//            binding = DataBindingUtil.inflate(
//                inflater, R.layout.fragment_second, container, false
//            )
//
//            val drawingId = arguments?.getInt("drawing_id") ?: 0
//            Log.d("Navigation", "Received drawing_id: $drawingId")
//
//            if (drawingId != 0) {
//                viewModel.loadDrawingFromDatabase(drawingId)
//                    .observe(viewLifecycleOwner) { drawingEntity ->
//                        drawingEntity?.let {
//                            Log.d(
//                                "Database",
//                                "Loaded drawing with ID: ${it.id}, file path: ${it.filePath}"
//                            )
//                            val bitmap = BitmapFactory.decodeFile(it.filePath)
//                            if (bitmap != null) {
//                                binding.customView.setBitmap(bitmap)
//                            }
//                            binding.customView.setColor(it.color)
//                            binding.customView.setBrushSize(it.brushSize)
//                        } ?: run {
//                            Log.d("Database", "No drawing found for ID: $drawingId")
//                        }
//                    }
//            } else {
//                Toast.makeText(context, "Starting a new drawing.", Toast.LENGTH_SHORT).show()
//            }
//
//            setupBrushAndColorListeners()
//
//            return binding.root
//        }
//
//        private fun setupBrushAndColorListeners() {
//            val customView = binding.customView
//
//            binding.roundBrushButton.setOnClickListener {
//                customView.setBrushShape(BrushShape.ROUND)
//            }
//
//            binding.squareBrushButton.setOnClickListener {
//                customView.setBrushShape(BrushShape.SQUARE)
//            }
//
//            binding.blueColor.setOnClickListener {
//                binding.customView.setColor(Color.BLUE)
//            }
//
//            binding.redColor.setOnClickListener {
//                binding.customView.setColor(Color.RED)
//            }
//
//            binding.whiteColor.setOnClickListener {
//                binding.customView.setColor(Color.WHITE)
//            }
//
//            binding.blackColor.setOnClickListener {
//                binding.customView.setColor(Color.BLACK)
//            }
//
//            binding.brushSizeSeekbar.setOnSeekBarChangeListener(object :
//                SeekBar.OnSeekBarChangeListener {
//                override fun onProgressChanged(
//                    seekBar: SeekBar?,
//                    progress: Int,
//                    fromUser: Boolean
//                ) {
//                    binding.customView.setBrushSize(progress.toFloat())
//                }
//
//                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//
//                override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                    viewModel.saveBrushSize(binding.customView.getBrushSize())
//                }
//            })
//        }
//
//        override fun onPause() {
//            super.onPause()
//            val currentBitmap = binding.customView.getBitmap()
//            val currentColor = binding.customView.getCurrentColor()
//            val currentBrushSize = binding.customView.getBrushSize()
//
//            if (currentBitmap != null) {
//
//                viewModel.saveDrawingToDatabase(
//                    requireContext(),
//                    currentBitmap,
//                    currentColor,
//                    currentBrushSize
//                )
//            }
//        }
//
//
//
//
//        >>>>>>> dc51a976b27044c4248bb7221803114ce4b3ff16
//    }
