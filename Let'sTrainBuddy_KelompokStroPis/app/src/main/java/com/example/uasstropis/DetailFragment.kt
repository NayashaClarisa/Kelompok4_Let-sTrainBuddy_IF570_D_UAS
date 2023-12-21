package com.example.uasstropis

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.findNavController

class DetailFragment : Fragment() {
    private val sportTitle: TextView?
        get() = view?.findViewById(R.id.sport_title)
    private val sportDesc: TextView?
        get() = view?.findViewById(R.id.sport_desc)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sportId = arguments?.getInt(SPORT_ID, 0) ?: 0
        setSportData(sportId)

        val backButton = view.findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener{
            Navigation.findNavController(it).navigateUp()
        }

        val DayList = listOf<View>(
            view.findViewById(R.id.day1),
            view.findViewById(R.id.day2),
            view.findViewById(R.id.day3),
            view.findViewById(R.id.day4),
            view.findViewById(R.id.day5),
            view.findViewById(R.id.day6),
            view.findViewById(R.id.day7)
        )

        // Menambahkan listener untuk setiap TextView di DayList
        DayList.forEach { day ->
            day.setOnClickListener { view ->
                onDayClick(view)
            }
        }
    }
    fun setSportData(id: Int){
        when(id){
            R.id.sport1 -> {
                sportTitle?.text = getString(R.string.sport1_title)
                sportDesc?.text = getString(R.string.sport1_desc)
            }
            R.id.sport2 -> {
                sportTitle?.text = getString(R.string.sport2_title)
                sportDesc?.text = getString(R.string.sport2_desc)
            }
            R.id.sport3-> {
                sportTitle?.text = getString(R.string.sport3_title)
                sportDesc?.text = getString(R.string.sport3_desc)
            }
        }
    }
    fun onDayClick(view: View) {
        val dayName = when (view.id) {
            R.id.day1 -> "day1"
            R.id.day2 -> "day2"
            R.id.day3 -> "day3"
            R.id.day4 -> "day4"
            R.id.day5 -> "day5"
            R.id.day6 -> "day6"
            R.id.day7 -> "day7"
            else -> ""
        }

        val sportName = arguments?.getString(SPORT_NAME, "") ?: ""
        val fragmentBundle = Bundle()
        fragmentBundle.putString(DAY_NAME, dayName)
        fragmentBundle.putInt(SPORT_ID, view.id)
        fragmentBundle.putString(SPORT_NAME, sportName)
        Navigation.findNavController(view).navigate(
            R.id.action_detailFragment_to_daylistFragment,
            fragmentBundle
        )
    }



    companion object {
        private const val DAY_NAME = "DAY_NAME"
        private const val SPORT_ID = "SPORT_ID"
        private const val SPORT_NAME = "SPORT_NAME"
    }
}