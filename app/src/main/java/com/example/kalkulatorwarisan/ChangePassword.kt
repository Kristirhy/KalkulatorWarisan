package com.example.kalkulatorwarisan

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.change_password.*

class ChangePassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)

        auth = FirebaseAuth.getInstance()
        btnChangePass.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        if (etCurrentPass.text.isNotEmpty() &&
            etNewtPass.text.isNotEmpty() &&
            etConfirmPass.text.isNotEmpty()){

            if (etNewtPass.text.toString().equals(etConfirmPass.text.toString())) {
                val user : FirebaseUser? = auth.currentUser
                if (user != null && user.email !=null){
                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, etCurrentPass.text.toString())

// Prompt the user to re-provide their sign-in credentials
                    user?.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Re-Authentication Success.", Toast.LENGTH_SHORT).show()

                                user?.updatePassword(etNewtPass.text.toString())
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Password Change successfully", Toast.LENGTH_SHORT).show()

                                            auth.signOut()
                                            startActivity(Intent(this,ProfileActivity::class.java))
                                            finish()
                                        }
                                    }
                            } else{
                                Toast.makeText(this, "Re-Authentication Failed.", Toast.LENGTH_SHORT).show()
                            }
                        }

                }else{
                    startActivity(Intent(this,ProfileActivity::class.java))
                    finish()
                }

            }else{
                Toast.makeText(this, "Password mismatching.",Toast.LENGTH_SHORT).show()
            }
        } else{
            Toast.makeText(this, "Please enter all the fields.",Toast.LENGTH_SHORT).show()
        }
    }
}


