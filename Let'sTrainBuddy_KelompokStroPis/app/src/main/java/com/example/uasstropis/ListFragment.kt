package com.example.uasstropis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sportList = listOf<View>(
            view.findViewById(R.id.sport1),
            view.findViewById(R.id.sport2),
            view.findViewById(R.id.sport3)
        )
        sportList.forEach{ sport ->
            val fragmentBundle = Bundle()
            fragmentBundle.putInt(SPORT_ID, sport.id)
            sport.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_listFragment_to_detailFragment,
                    fragmentBundle)
            )
        }
    }
    companion object {
        const val SPORT_ID = "SPORT_ID"
    }
}
