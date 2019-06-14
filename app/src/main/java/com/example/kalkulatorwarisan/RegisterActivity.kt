package com.example.kalkulatorwarisan

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            register()

        }


    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            Toast.makeText(this, "Hello ${currentUser.email}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }


    private fun register() {

        val namaTxt: EditText = findViewById(R.id.etUsername)
        var nama = namaTxt.text.toString()
        val emailTxt : EditText = findViewById(R.id.etEmail)
        var email = emailTxt.text.toString()
        val passwordTxt : EditText = findViewById(R.id.etPassword)
        var password = passwordTxt.text.toString()
        var passwordConfirm = etConfirm.text.toString()

        if (!email.isEmpty() && !nama.isEmpty() && !password.isEmpty()&& !passwordConfirm.isEmpty()) {
            if(password.equals(passwordConfirm)){
                this.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("REGISTER", "Successfully registered ")
                        val user = auth.currentUser
                        updateUI(user)
                    }

                    else {
                        // If sign in fails, display a message to the user.
                        Log.w("REGISTER", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
            } else{
                Toast.makeText(this,"Password don't match :|", Toast.LENGTH_LONG).show()
            }

        }
        else {
            Toast.makeText(this,"Please fill up Username, Email, and Password :|", Toast.LENGTH_LONG).show()
        }

    }


}
