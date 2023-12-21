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
import android.widget.RadioButton
import android.widget.RadioGroup
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

class PreferenceInputDialogFragment : DialogFragment() {

    private lateinit var dbreferences: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var mAuth: FirebaseAuth

    interface InputFormListener {
        fun onSubmitClicked(inputText: String)
    }

    private lateinit var inputFormListener: InputFormListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preferences_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance();
        dbreferences = FirebaseDatabase.getInstance().getReference().child("userdata")

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        val btnSelect = view.findViewById<Button>(R.id.btnSelect)

        btnSelect.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val currentUser = mAuth.currentUser
            val uid = currentUser?.uid

            if (selectedId != -1) {
                val selectedRadioButton = view.findViewById<RadioButton>(selectedId)
                val reason = selectedRadioButton.text.toString()
                uid?.let { userId ->
                    val userRef = dbreferences.child(userId).child("exerciseProgram")

                    userRef.setValue(reason)
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "Successfully added exercise program")
                        }
                        .addOnFailureListener {
                            Log.d(ContentValues.TAG, "Failed to add exercise program")
                        }


                }
                inputFormListener.onSubmitClicked(reason)
                dismiss()
            }
        }

    }

    fun setOnSubmitListener(listener: InputFormListener) {
        this.inputFormListener = listener
    }
}
