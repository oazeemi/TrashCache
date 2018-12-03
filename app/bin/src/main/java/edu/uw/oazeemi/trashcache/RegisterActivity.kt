package edu.uw.oazeemi.trashcache

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar)


        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_text_view.setOnClickListener {
            Log.d("Register Activity", "Show login activity")

            // launch login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in e-mail/password", Toast.LENGTH_SHORT).show()
        }

        Log.d("RegisterActivity", "Email is: " + email)
        Log.d("RegisterActivity", "Password: $password")

        // Firebase Authetication to create a uers with e-mail and password.
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if(!it.isSuccessful) return@addOnCompleteListener

                    // else if successful
                    Log.d("Register", "Successfully created user with uid: ${it.result!!.user.uid}")
                }
                .addOnFailureListener{
                    Log.d("Register", "Failed to create user: ${it.message}")
                    Toast.makeText(this, "Failed to create user: \${it.message", Toast.LENGTH_SHORT).show()
                }
    }
}
