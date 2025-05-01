package com.example.btl_android_project.presentation.log_food

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.FragmentLogFoodBinding
import com.example.btl_android_project.presentation.log_all.LogAllFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogFoodFragment : Fragment() {
    private var _binding: FragmentLogFoodBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LogFoodViewModel by viewModels()
    private lateinit var foodAdapter: FoodAdapter

    private var isFromCreateMeal: Boolean = false

    companion object {
        private const val ARG_IS_FROM_CREATE_MEAL = "isFromCreateMeal"

        fun newInstance(isFromCreateMeal: Boolean = false): LogFoodFragment {
            val fragment = LogFoodFragment()
            val args = Bundle()
            args.putBoolean(ARG_IS_FROM_CREATE_MEAL, isFromCreateMeal)
            fragment.arguments = args
            return fragment
        }
    }

    val onSearchQueryChanged : (String) -> Unit = { query ->
        viewModel.searchFoods(query)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFromCreateMeal = arguments?.getBoolean(ARG_IS_FROM_CREATE_MEAL, false) ?: false

        viewModel.syncFoodsFromFirestore()
        viewModel.loadFoods()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreateAFood.tvLogItem.text = "Create New Food"
        setupRecyclerView()
        setupSwipeToDelete()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(
            foods = emptyList(),
            onItemClick = { food ->
                if(isFromCreateMeal){
                    val action = LogAllFragmentDirections.actionLogAllFragmentToLogFoodDiaryFragment(
                        foodId = food.id,
                        isFromCreateMeal = isFromCreateMeal
                    )
                    findNavController().navigate(action)
                }
                else{
                    val action = LogAllFragmentDirections.actionLogAllFragmentToCreateFoodInformationFragment(food.id)
                    findNavController().navigate(action)
                }
            },
            onAddToDiaryClick = { food ->
                if(isFromCreateMeal){

                }
                else{
                    viewModel.addFoodToDiary(
                        foodId = food.id,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Food added to diary", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        )
        binding.rvFoods.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.foods.collectLatest { foods ->
                foodAdapter.updateFoods(foods)
            }
        }
    }

    private fun setupListeners() {
        binding.btnCreateAFood.cvLogItem.setOnClickListener {
            val action =
                LogAllFragmentDirections.actionLogAllFragmentToCreateFoodInformationFragment();
            findNavController().navigate(action)
        }

    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val food = foodAdapter.getFoods()[position]
                    viewModel.deleteFood(food)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView

                    val cornerRadius = 40f
                    val background = RectF(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )

                    val paint = Paint().apply {
                        color = Color.RED
                        isAntiAlias = true
                    }

                    c.drawRoundRect(background, cornerRadius, cornerRadius, paint)

                    val textPaint = Paint().apply {
                        color = Color.WHITE
                        textSize = 60f  // to hơn
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }

                    val textX = itemView.right - 200f
                    val textY =
                        itemView.top + (itemView.height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f)
                    c.drawText("DELETE", textX, textY, textPaint)

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            })

        itemTouchHelper.attachToRecyclerView(binding.rvFoods)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}