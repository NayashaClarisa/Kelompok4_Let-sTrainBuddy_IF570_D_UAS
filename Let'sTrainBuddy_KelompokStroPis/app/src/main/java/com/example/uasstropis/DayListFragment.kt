package com.example.uasstropis

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class DayListFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbReferences: DatabaseReference
    private lateinit var dbReferences_exercise: DatabaseReference
    private lateinit var moveListView: ListView
    private lateinit var moveList: MutableList<String>

    data class ExerciseMove(
        val exerciseName: String,
        val description: String,
        val reps: Int,
        val sets: Int
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_day_list, container, false)


        val dayName = arguments?.getString(DAY_NAME, "") ?: ""
        val sportName = arguments?.getString(SPORT_NAME, "") ?: ""
        Log.d("Firebase", "Chosen exercise program: $sportName")

        moveListView = view.findViewById(R.id.exercise_move_list)
        moveList = mutableListOf()

        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid
        dbReferences = FirebaseDatabase.getInstance().reference.child("userdata").child(userId ?: "").child("exerciseProgram")
        dbReferences.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val chosenExerciseProgram = dataSnapshot.getValue(String::class.java) ?: ""
                    val exercisePrograms = listOf("Bigger", "Leaner", "Healthier", "Stronger")
                    if (exercisePrograms.contains(chosenExerciseProgram)) {
                        Log.d("Firebase", "Chosen exercise program: $chosenExerciseProgram")
                        fetchAndDisplayExerciseMoves(chosenExerciseProgram,dayName,sportName)
                    } else {
                        // ChosenExerciseProgram doesn't match any available programs
                        // Handle this scenario as needed
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Error fetching exercise program: ${error.message}") // Logging Firebase error
                    // Handle error
                }
            })


        return view
    }

    private fun fetchAndDisplayExerciseMoves(chosenProgram: String, dayName: String,sportName: String) {
        // Fetch the exercise moves for the chosen program and display them in the ListView
        dbReferences_exercise = FirebaseDatabase.getInstance().reference.child("exerciseProgram").child(chosenProgram).child(sportName).child(dayName)

        dbReferences_exercise
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val dayDesc = dataSnapshot.child("description").getValue(String::class.java) ?: ""
                    val exerciseListSnapshot = dataSnapshot.child("exercises")
                    val exerciseMovesList = mutableListOf<ExerciseMove>()
                    exerciseMovesList.clear() // Clear the list before adding new items

                    for (exerciseSnapshot in exerciseListSnapshot.children) {
                        val exerciseName = exerciseSnapshot.key ?: ""
                        val description = exerciseSnapshot.child("description").getValue(String::class.java) ?: ""
                        val reps = exerciseSnapshot.child("reps").getValue(Int::class.java) ?: 0
                        val set = exerciseSnapshot.child("set").getValue(Int::class.java) ?: 0

                        val day_title = view?.findViewById<TextView>(R.id.day_title)
                        val workout_title = view?.findViewById<TextView>(R.id.workout_title)
                        val workout_desc = view?.findViewById<TextView>(R.id.workout_desc)

                        var newDayTitle = ""
                        var newWorkoutTitle = ""

                        if(dayName == "day1"){
                            newDayTitle = "Day 1"
                        }else if(dayName == "day2"){
                            newDayTitle = "Day 2"
                        }else if(dayName == "day3"){
                            newDayTitle = "Day 3"
                        }else if(dayName == "day4"){
                            newDayTitle = "Day 4"
                        }else if(dayName == "day5"){
                            newDayTitle = "Day 5"
                        }else if(dayName == "day6"){
                            newDayTitle = "Day 6"
                        }else if(dayName == "day7"){
                            newDayTitle = "Day 7"
                        }

                        if(sportName == "leg"){
                            newWorkoutTitle = "Lower Body Workout"
                        }else if(sportName == "glute"){
                            newWorkoutTitle = "Core Workout"
                        }else if(sportName == "arm"){
                            newWorkoutTitle = "Upper Body Workout"
                        }


                        day_title?.text = newDayTitle
                        workout_title?.text = newWorkoutTitle
                        workout_desc?.text = dayDesc

                        val exerciseMove = ExerciseMove(exerciseName, description, reps, set)
                        exerciseMovesList.add(exerciseMove)
                    }
                    val adapter = ExerciseListAdapter(requireContext(), exerciseMovesList)
                    moveListView.adapter = adapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle potential errors
                }
            })
    }

    // Add this inner class within your DayListFragment class
    private inner class ExerciseListAdapter(
        context: Context,
        private val exerciseMoves: List<ExerciseMove>
    ) : ArrayAdapter<ExerciseMove>(context, R.layout.item_move_list, exerciseMoves) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var listItemView = convertView
            if (listItemView == null) {
                listItemView = LayoutInflater.from(context).inflate(R.layout.item_move_list, parent, false)
            }

            val currentExerciseMove = exerciseMoves[position]

            val moveNameTextView = listItemView?.findViewById<TextView>(R.id.move_name)
            val moveDescTextView = listItemView?.findViewById<TextView>(R.id.move_desc)
            val repsTextView = listItemView?.findViewById<TextView>(R.id.reps)
            val setsTextView = listItemView?.findViewById<TextView>(R.id.sets)

            moveNameTextView?.text = currentExerciseMove.exerciseName
            moveDescTextView?.text = currentExerciseMove.description
            repsTextView?.text = "Reps: ${currentExerciseMove.reps}"
            setsTextView?.text = "Sets: ${currentExerciseMove.sets}"

            return listItemView!!
        }
    }


    companion object {
        const val SPORT_ID = "SPORT_ID"
        const val SPORT_NAME = "SPORT_NAME"
        const val DAY_NAME = "DAY_NAME"
    }
}


