package com.example.uasstropis

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WorkoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkoutFragment : Fragment() {


    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbReferences: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workout, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid
        dbReferences = FirebaseDatabase.getInstance().reference.child("userdata").child(userId ?: "").child("exerciseProgram")


        val sportList = listOf<View>(
            view.findViewById(R.id.sport1),
            view.findViewById(R.id.sport2),
            view.findViewById(R.id.sport3)
        )
        sportList.forEach{ sport ->
            val fragmentBundle = Bundle()
            val sportString = when (sport.id) {
                R.id.sport1 -> "leg"
                R.id.sport2 -> "glute"
                R.id.sport3 -> "arm"
                else -> ""
            }
            fragmentBundle.putString(SPORT_NAME, sportString)
            fragmentBundle.putInt(SPORT_ID, sport.id)
            sport.setOnClickListener{
                dbReferences.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val exerciseProgram = snapshot.getValue(String::class.java)
                        if (exerciseProgram == "none") {
                            showPreferencesInputDialogFragment()

                        } else {
                            Log.d("Firebase", "Exercise program: $exerciseProgram")
                            Navigation.createNavigateOnClickListener(R.id.action_workoutFragment_to_detailFragment, fragmentBundle).onClick(sport)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseError", "Error fetching exercise program: ${error.message}") // Logging Firebase error
                        // Handle error
                    }
                })
            }
        }
    }

    private fun showPreferencesInputDialogFragment() {
        val preferencesDialog = PreferenceInputDialogFragment()
        preferencesDialog.setOnSubmitListener ( object : PreferenceInputDialogFragment.InputFormListener {
            override fun onSubmitClicked(inputText: String) {
                Log.d("PreferenceInputDialog", "Input: $inputText")
            }
        } )
        preferencesDialog.show(childFragmentManager, "PreferenceInputDialogFragment")
    }

    companion object {
        const val SPORT_ID = "SPORT_ID"
        const val SPORT_NAME = "SPORT_NAME"
    }
}