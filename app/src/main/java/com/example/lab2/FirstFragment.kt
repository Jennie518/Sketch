package com.example.lab2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.motion.widget.MotionScene.Transition.TransitionOnClick
import com.example.lab2.databinding.FragmentFirstBinding
import androidx.fragment.app.setFragmentResultListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [FirstFragment.newInstance] factory method to
// * create an instance of this fragment.
// */
//class FirstFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        val composeView = ComposeView(requireContext())
//
//        val binding = FragmentFirstBinding.inflate(layoutInflater, container, false)
//
//        binding.navigateButton.apply {
//            // Dispose of the Composition when the view's LifecycleOwner
//            // is destroyed
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                Text("Let's draw something!")
//            }
//        }
//
//        return composeView
//
//        //val navigateButton = view.findViewById<Button>(R.id.navigate_button)
//
//        // Navigate to SecondFragment when the button is clicked
////        binding.navigateButton.setOnClickListener {
////            val bundle = Bundle().apply {
////                putLong("drawing_id", id.toLong())
////            }
////            val secondFragment = SecondFragment()
////            secondFragment.arguments = bundle
////
////            parentFragmentManager.beginTransaction()
////                .replace(R.id.fragment_container, SecondFragment())
////                .addToBackStack(null)
////                .commit()
////        }
//
//
////        return binding.root
//    }
//
//
//}
//
//class StartFragment : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentFirstBinding.inflate(layoutInflater, container, false)
//
//        return ComposeView(requireContext()).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                NavBtm()
//            }
//        }
//    }
//}

@Composable
fun StartScreen(
    onNavigate: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { onNavigate() }) {
            Text(text = "Let's draw something!")
        }
    }
}

//@Composable
//private fun NavBtm(onNavigate: (SecondFragment) -> Unit) {
//    val secondFragment = SecondFragment()
//    FilledTonalButton(onClick = { onNavigate(secondFragment) }) {
//        Text("Let's draw something!")
//    }
//}
//
//@Preview
//@Composable
//private fun NavBtmPreview() {
//    MaterialTheme {
//        NavBtm({})
//    }
//}
//
