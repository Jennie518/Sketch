package com.example.lab2

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab2.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {

    private val viewModel: CustomViewModel by viewModels()
    private lateinit var binding: FragmentSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 使用泛型形式初始化 DataBinding
        binding = DataBindingUtil.inflate<FragmentSecondBinding>(
            inflater, R.layout.fragment_second, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        // 观察 ViewModel 中的 LiveData
        viewModel.drawingPath.observe(viewLifecycleOwner) { savedPath ->
            // 更新 CustomView，当绘制路径发生变化时
            savedPath?.let { binding.customView.setPath(it) }
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        // 保存当前 CustomView 的绘制内容到 ViewModel
        val currentPath = binding.customView.getCurrentPath()
        viewModel.saveDrawing(currentPath)
    }
}