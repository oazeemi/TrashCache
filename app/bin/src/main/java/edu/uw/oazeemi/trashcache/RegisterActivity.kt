package edu.uw.oazeemi.trashcache

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    data class User(val uid: String, val username: String)
    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
//        setSupportActionBar(toolbar)

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
            return
        }

        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authetication to create a uers with e-mail and password.
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if(!it.isSuccessful) {
                        return@addOnCompleteListener
                    } else {
                        var intent = Intent(this, ChoiceActivity::class.java)
                        intent.putExtra("id", FirebaseAuth.getInstance().currentUser?.email)
                        startActivity(intent)
                    }

                    // else if successful
                    Log.d(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")
                    saveUserToFirebaseDatabase(it.toString())
                }
                .addOnFailureListener{
                    Log.d("Register", "Failed to create user: ${it.message}")
                    Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString())

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Finally we saved the user to Firebase Database")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to set value to database: ${it.message}")
                }
    }
}

