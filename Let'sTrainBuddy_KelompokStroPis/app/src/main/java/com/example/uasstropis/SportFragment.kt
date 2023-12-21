package com.example.uasstropis


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.*
import org.w3c.dom.Text

class SportFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbReferences: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sport, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid
        dbReferences = FirebaseDatabase.getInstance().reference.child("userdata").child(userId ?: "")

        val content_description = view.findViewById<TextView>(R.id.content_description)
        val user_email = view.findViewById<TextView>(R.id.user_email)
        val user_name = view.findViewById<TextView>(R.id.user_name)

        dbReferences.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val email = snapshot.child("email").getValue(String::class.java) ?: ""
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val exerciseProgram = snapshot.child("exerciseProgram").getValue(String::class.java) ?: ""

                content_description.text = "You workout because you want to get $exerciseProgram"
                user_email.text = email
                user_name.text = name
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching email: ${error.message}") // Logging Firebase error
                // Handle error
            }
        })

        val addButton = view.findViewById<Button>(R.id.update_button)
        addButton.setOnClickListener {
            showPreferencesInputDialogFragment()
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
}