package com.example.uasstropis
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PlanInputDialogFragment : DialogFragment() {

    private lateinit var dbreferences: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var mAuth: FirebaseAuth

    interface InputFormListener {
        fun onSubmitClicked(inputText: String)
    }

    private lateinit var inputFormListener: InputFormListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_plan_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance();
        dbreferences = FirebaseDatabase.getInstance().getReference().child("userdata")

        val editText = view.findViewById<EditText>(R.id.input_new_plan)
        val submitButton = view.findViewById<Button>(R.id.confirm_input)

        submitButton.setOnClickListener {
            val inputText = editText.text.toString().trim()
            val currentUser = mAuth.currentUser
            val uid = currentUser?.uid

            uid?.let { userId ->
                val userRef = dbreferences.child(userId).child("plan")

                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val count = snapshot.childrenCount.toInt()

                        val newPlanRef = userRef.push()
                        newPlanRef.setValue(inputText)
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "Successfully added plan")
                            }
                            .addOnFailureListener { exception ->
                                Log.e(ContentValues.TAG, "Failed to add plan: $exception")
                            }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e(ContentValues.TAG, "Cancelled operation: $error")
                    }
            })
            }

            inputFormListener.onSubmitClicked(inputText)
            dismiss()
        }
    }

    fun setOnSubmitListener(listener: InputFormListener) {
        this.inputFormListener = listener
    }
}
