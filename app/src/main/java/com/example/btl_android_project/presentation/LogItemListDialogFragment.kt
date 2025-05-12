package com.example.btl_android_project.presentation

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentItemListDialogListDialogItemBinding
import com.example.btl_android_project.databinding.FragmentItemListDialogListDialogBinding
import dagger.hilt.android.AndroidEntryPoint

const val ARG_ITEM_COUNT = 4

@AndroidEntryPoint
class LogItemListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentItemListDialogListDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = binding.list
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = LogItemAdapter(ARG_ITEM_COUNT)
    }

    private inner class ViewHolder(binding: FragmentItemListDialogListDialogItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val text: TextView = binding.tvLogItem

    }

    private inner class LogItemAdapter(private val mItemCount: Int) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentItemListDialogListDialogItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when (position) {
                0 -> {
                    holder.text.text = getString(R.string.log_food)
                    holder.itemView.setOnClickListener {
                        val action = LogItemListDialogFragmentDirections.actionLogItemListDialogFragmentToLogAllFragment()
                        findNavController().navigate(action)
                    }
                }
                1 -> {
                    holder.text.text = getString(R.string.water)
                    holder.itemView.setOnClickListener {
                        val action = LogItemListDialogFragmentDirections.actionLogItemListDialogFragmentToLogWaterFragment()
                        findNavController().navigate(action)
                    }
                }
                2 -> {
                    holder.text.text = getString(R.string.weight)
                    holder.itemView.setOnClickListener {
                        val action = LogItemListDialogFragmentDirections.actionLogItemListDialogFragmentToLogWeightFragment()
                        findNavController().navigate(action)
                    }
                }
                3 -> holder.text.text = getString(R.string.exercise)
            }

        }

        override fun getItemCount(): Int {
            return mItemCount
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}