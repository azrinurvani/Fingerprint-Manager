package com.mobile.azrinurvani.fingerprintauthentication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat


//TODO 12
@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.M)
class FingerprintHelper(private val context : Context) : FingerprintManager.AuthenticationCallback() {

    //TODO 14 - Create cancellationSignal variabel and startAuth method with public modifier
    lateinit var cancellationSignal : CancellationSignal
    fun startAuth(manager : FingerprintManager,cryptoObject: FingerprintManager.CryptoObject){
        cancellationSignal = CancellationSignal()

        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.USE_FINGERPRINT)!= PackageManager.PERMISSION_GRANTED){
            return
        }
        manager.authenticate(cryptoObject,cancellationSignal,0,this,null)
    }

/*TODO 13 - Ovverride 4 method from FingerprintManager.AuthenticationCallback()*/
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        super.onAuthenticationError(errorCode, errString)

        Toast.makeText(context,"Authentication Error ",Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        super.onAuthenticationSucceeded(result)
        Toast.makeText(context,"Authentication Success ",Toast.LENGTH_LONG).show()
        context.startActivity(Intent(context,ResultActivity::class.java))
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        super.onAuthenticationHelp(helpCode, helpString)
        Toast.makeText(context,"Authentication Help ",Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        Toast.makeText(context,"Authentication Failed ",Toast.LENGTH_LONG).show()
    }
}