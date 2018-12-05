package edu.uw.oazeemi.trashcache

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth



import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        val auth = FirebaseAuth.getInstance()
        if (auth.getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        login_button_login.setOnClickListener {
            performLogin()
        }


        back_to_register_textview.setOnClickListener {
            Log.d("Register Activity", "Show register activity")

            // launch register activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private val SIGN_IN_RESPONSE_CODE = 100

    private fun performLogin() {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        Log.d("Login", "Attempt login with email/pw: $email/****")

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in e-mail/password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (!it.isSuccessful) {
                        return@addOnCompleteListener
                    } else {
                        var intent = Intent(this, ChoiceActivity::class.java)
                        intent.putExtra("id", FirebaseAuth.getInstance().currentUser?.email)
                        startActivity(intent)
                    }
                    Log.d("Login", "Successfully logged in with user with uid: ${it.result!!.user.uid}")
                }
                .addOnFailureListener {
                    Log.d("Register", "Failed to login user: ${it.message}")
                    Toast.makeText(this, "Failed to login user: ${it.message}", Toast.LENGTH_SHORT).show()

                }
    }
}
