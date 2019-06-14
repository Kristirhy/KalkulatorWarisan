package com.example.kalkulatorwarisan

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity(),
    GoogleApiClient.OnConnectionFailedListener,
    View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginActivity"
    private val RC_SIGN_IN = 9001
    private var mSignInButton: SignInButton? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    //firebase instance variables
    private var mFirebaseAuth: FirebaseAuth? = null

    val callbackManager = CallbackManager.Factory.create();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()


        var btn: Button = findViewById(R.id.btnLogin)
        btn.setOnClickListener {
            login()
        }

        var register: TextView = findViewById(R.id.tvRegister)
        register.setOnClickListener {
            register()
        }

        //Assign fields
        mSignInButton = findViewById<View>(R.id.sign_in_button) as SignInButton
        // Set Click listeners
        mSignInButton!!.setOnClickListener(this)
        //Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                getString(
                    R.string.default_web_client_id
                )
            )
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(
                this /*FragmentActivity */,
                this /* OnConnectionFailedListener */
            )
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        //Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance()
    }

    private fun register() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(
            TAG, "onConnectionFailed:" +
                    "$p0"
        )
        Toast.makeText(
            this, "Google Play " +
                    "Services error.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sign_in_button -> signIn()
        }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                Log.e(TAG, "Google Sign-In failed.")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebasAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mFirebaseAuth!!.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
            if (!task.isSuccessful) {
                Log.w(TAG, "signInWithCredential", task.exception)
                Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(
                    Intent(this@LoginActivity, RegisterActivity::class.java)
                )
                finish()
            }
        }

        val loginButton = findViewById<View>(R.id.login_button) as LoginButton
        loginButton.setReadPermissions("email")
        // If using in a fragment
//        loginButton.setFragment(this)

        // Callback registration
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })

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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
    private fun login() {

        val emailTxt: EditText = findViewById(R.id.email)
        var email = emailTxt.text.toString()
        val passwordTxt: EditText = findViewById(R.id.password)
        var password = passwordTxt.text.toString()

        this.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LOGIN", "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LOGIN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }
}