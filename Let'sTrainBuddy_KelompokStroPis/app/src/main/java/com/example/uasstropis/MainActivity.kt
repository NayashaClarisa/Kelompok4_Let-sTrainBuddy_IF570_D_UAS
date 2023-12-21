package com.example.uasstropis

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import android.view.View
import android.widget.Button

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        if(user == null) {
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            Toast.makeText(this, "Welcome, ${user.email}", Toast.LENGTH_SHORT).show()
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    as NavHostFragment
        val navController = navHostFragment.navController

        //Creating top level destinations and adding them to the draw
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.listFragment, R.id.favoritesFragment, R.id.sportFragment, R.id.planFragment, R.id.workoutFragment, R.id.detailFragment
            ), findViewById(R.id.drawer_layout)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view)?.setupWithNavController(navController)

        // Added this part only
        findViewById<BottomNavigationView>(R.id.bottom_nav)?.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_user -> {
                val username = "StroPis"
                Toast.makeText(this, "Halo, $username", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                auth.signOut()
                val intent = Intent(this, LandingActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
