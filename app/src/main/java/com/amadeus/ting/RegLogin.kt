package com.amadeus.ting

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class RegLogin : AppCompatActivity() {

    // Authentication object to handle Firebase authentication
    private lateinit var auth : FirebaseAuth

    // Client object for handling Google Sign-in
    private lateinit var googleSignInClient : GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg_login)

        // Initialize the FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // Configure the Google Sign-In Options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        // Create the GoogleSignInClient instance
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        findViewById<Button>(R.id.google_login_button).setOnClickListener {
            signInGoogle()
        }
    }

    // Function to initiate Google Sign-in flow
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    // Handle the result of the Google Sign-in flow
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleResults(task)

                }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account : GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent : Intent = Intent(this, HomePage::class.java)
                intent.putExtra("email",account.email)
                intent.putExtra("name",account.displayName)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}