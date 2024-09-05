package com.example.lab2
import com.example.lab2.CustomView

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab2.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {

    private var customView: CustomView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view = inflater.inflate(R.layout.fragment_second, container, false)
        val binding = FragmentSecondBinding.inflate(layoutInflater);
        customView = binding.customView

        // Restore the previous drawing (if any) when navigating back to the second fragment
        customView?.restoreSavedDrawing()

        return view
    }

    override fun onPause() {
        super.onPause()
        // Save the current drawing before navigating away
        customView?.saveCurrentDrawing()
    }
}