package com.example.uasstropis

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PlanFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbReferences: DatabaseReference
    private lateinit var planListView: ListView
    private lateinit var planList: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plan, container, false)
        planListView = view.findViewById(R.id.plan_list_view)
        planList = mutableListOf()

        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid
        dbReferences = FirebaseDatabase.getInstance().reference.child("userdata").child(userId ?: "")

        setupPlanListView()

        return view
    }

    private fun setupPlanListView() {
        val adapter = CustomAdapter(requireContext(), planList)
        planListView.adapter = adapter

        dbReferences.child("plan").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPlanList = mutableListOf<String>()
                for (postSnapshot in snapshot.children) {
                    val planName = postSnapshot.getValue(String::class.java)
                    planName?.let {
                        newPlanList.add(it)
                    }
                }
                adapter.updatePlanList(newPlanList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching plans: ${error.message}") // Logging Firebase error
                // Handle error
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val addButton = view.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            showPlanInputDialogFragment()
        }
    }

    private fun showPlanInputDialogFragment() {
        val planInputDialogFragment = PlanInputDialogFragment()
        planInputDialogFragment.setOnSubmitListener(object : PlanInputDialogFragment.InputFormListener {
            override fun onSubmitClicked(inputText: String) {
            }
        })
        planInputDialogFragment.show(childFragmentManager, "PlanInputDialogFragment")
    }

    inner class CustomAdapter(context: Context, private val planList: MutableList<String>) : ArrayAdapter<String>(
        context,
        R.layout.item_plan_card,
        R.id.plan_item,
        planList
    ) {
        override fun getCount(): Int {
            return planList.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_plan_card, parent, false)

            val planItem = planList[position]
            val deleteButton = view.findViewById<Button>(R.id.delete_button1)

            val planItemTextView = view.findViewById<TextView>(R.id.plan_item) // Assuming plan_item is a TextView

            planItemTextView.text = planItem // Set the plan name to the TextView


            deleteButton.setOnClickListener {
                val planToDelete = planList[position]
                val userId = mAuth.currentUser?.uid
                userId?.let { uid ->
                    dbReferences.child("plan").orderByValue().equalTo(planToDelete)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (childSnapshot in snapshot.children) {
                                    childSnapshot.ref.removeValue()
                                        .addOnSuccessListener {
                                            Log.d("Firebase", "Successfully deleted plan: $planToDelete")
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("Firebase", "Failed to delete plan: $exception")
                                        }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error deleting plan: ${error.message}")
                            }
                        })
                }
            }

            // Set other views or data for the item as needed

            return view
        }

        fun updatePlanList(newPlanList: MutableList<String>) {
            planList.clear()
            planList.addAll(newPlanList)
            notifyDataSetChanged()
        }
    }

    companion object {
        const val SPORT_ID = "SPORT_ID"
    }
}
