package com.mobile.azrinurvani.fingerprintauthentication

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {

    //TODO 1
    lateinit var fm : FingerprintManager
    lateinit var km : KeyguardManager

    lateinit var keyStore : KeyStore
    lateinit var keyGenerator: KeyGenerator
    var KEY_NAME = "my_key"

    lateinit var cipher : Cipher
    lateinit var cryptoObject : FingerprintManager.CryptoObject

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO 2
        fm = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        //TODO 3
        if (!km.isDeviceSecure){
            Toast.makeText(this,"Lock screen security not enabled in settings",Toast.LENGTH_LONG).show()
            return
        }
        if (!fm.hasEnrolledFingerprints()){
            Toast.makeText(this,"Register at least one fingerprint in settings",Toast.LENGTH_LONG).show()
            return
        }

        //TODO 4
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.USE_FINGERPRINT),111)
        }else{
            validateFingerprint()
        }

    }

    //TODO 5
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            validateFingerprint()
        }
    }
    //TODO 6
    private fun validateFingerprint() {
        //generate key
        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore")
            keyStore.load(null)
            keyGenerator.init(KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()
            )
            keyGenerator.generateKey()
        }catch (e : Exception){
            Log.e("ADX","MainActivity.validateFingerprint Exception : $e")
        }


        //initialization of Cryptography
        if (initChiper()){
            //TODO 10
            cipher.let {
                cryptoObject = FingerprintManager.CryptoObject(it)
            }
        }

        //TODO 11
        var helper = FingerprintHelper(this)
        if (fm!=null && km!=null){
            helper.startAuth(fm,cryptoObject)
        }
    }
    //TODO 7
    private fun initChiper(): Boolean {
        //TODO 8
        try{
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"
                    +KeyProperties.BLOCK_MODE_CBC+"/"
                    +KeyProperties.ENCRYPTION_PADDING_PKCS7)
        }catch (e : Exception){
            Log.v("ADX","MainActivity.initCipher exception $e")
        }

        //TODO 9
        try{
            keyStore.load(null)
            var key = keyStore.getKey(KEY_NAME,null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE,key)
            return true
        }catch (e : Exception){
            Log.v("ADX","MainActivity.initCipher.secretKey exception $e")
            return false
        }


    }
}
