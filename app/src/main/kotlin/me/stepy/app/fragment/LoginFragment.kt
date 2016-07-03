package me.stepy.app.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.parts_list_check_text.*
import me.stepy.app.R
import me.stepy.app.activity.MainActivity
import me.stepy.app.util.tracking.SharedPreferencesWrap

class LoginFragment : Fragment() {

    val TAG = "LoginFragment"
    val RC_SIGN_IN = 200

    val mainActivity: MainActivity
        get() = activity as MainActivity

    var googleApiClient: GoogleApiClient? = null
    lateinit var gso: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleApiClient = GoogleApiClient.Builder(activity)
                .enableAutoManage(activity) { p0 -> Log.e(TAG, p0.errorCode.toString()) }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        google_sign_in_button.apply {
            setSize(SignInButton.SIZE_WIDE)
            setScopes(gso.scopeArray)
        }
        sign_in_button.apply {
            setOnClickListener {
                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
        sign_out_button.apply {
            setOnClickListener {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback {
                    Log.d(TAG, "Sign Out")
                }
            }
        }
        tool_bar.apply {
            setNavigationOnClickListener { activity.supportFragmentManager.popBackStack() }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                Log.d(TAG, "GoogleSignInResult:" + result.isSuccess)
                val acct = result.signInAccount ?: return
                // TODO send server google token
                SharedPreferencesWrap.setBoolean(SharedPreferencesWrap.KEY_LOGIN, true)
                mainActivity.applyTopMainFragment()
            } else {
                Log.d(TAG, "handleSignInResult: Error")
                SharedPreferencesWrap.setBoolean(SharedPreferencesWrap.KEY_LOGIN, false)
            }
        }
    }

}