package com.smart.drink_reminder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.drive.DriveId
import com.google.android.gms.drive.OpenFileActivityOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.smart.drink_reminder.backup.DriveServiceHelper
import com.smart.drink_reminder.backup.RemoteBackup
import java.util.*

class Backup : AppCompatActivity(){
    lateinit var toolbar: Toolbar
    lateinit var googleCardview:CardView
    private var remoteBackup: RemoteBackup? = null
    private var mDriveServiceHelper: DriveServiceHelper? = null

    private val isBackup = true
    companion object {
        val REQUEST_CODE_SIGN_IN = 0
        val REQUEST_CODE_OPENING = 1
        val REQUEST_CODE_CREATION = 2
        val REQUEST_CODE_PERMISSIONS = 2
        private const val REQUEST_CODE_OPEN_DOCUMENT = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)
        toolbar = findViewById(R.id.backup_toolbar)
        googleCardview = findViewById(R.id.googleDrive)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        remoteBackup= RemoteBackup(this)
        googleCardview.setOnClickListener(){
            //requestSignIn()
            remoteBackup!!.connectToDrive(true)
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            REQUEST_CODE_SIGN_IN -> {
//               val getaccont= GoogleSignIn.getSignedInAccountFromIntent(data)
//                val account = GoogleSignIn.getLastSignedInAccount(this)
//                Log.e("TAG", "Sign in request code:$account")
//               // val account = GoogleSignIn.getLastSignedInAccount(this)
//                Log.e("TAG", " account: $getaccont" )
//
//                if (resultCode == Activity.RESULT_OK && data != null) {
//                    handleSignInResult(data)
//                }
//
//                // Called after user is signed in.
//
////                if (resultCode == RESULT_OK) {
////                    remoteBackup!!.connectToDrive(isBackup)
////                    Log.e("TAG", "again Sign in request code")
////                }
//            }
//
//            REQUEST_CODE_CREATION ->{
//
//            // Called after a file is saved to Drive.
//                if (resultCode == RESULT_OK) {
//                    Log.e("TAG", "Backup successfully saved.")
//                    Toast.makeText(this, "Backup successufly loaded!", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//
//            REQUEST_CODE_OPENING -> {
//                if (resultCode == RESULT_OK) {
//                    val driveId: DriveId = data!!.getParcelableExtra(
//                        OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID
//                    )!!
//                    remoteBackup!!.mOpenItemTaskSource!!.setResult(driveId)
//                } else {
//                    remoteBackup!!.mOpenItemTaskSource!!.setException(RuntimeException("Unable to open file"))
//                }
//            }
//             REQUEST_CODE_OPEN_DOCUMENT->{
//                 if (resultCode == Activity.RESULT_OK && data != null) {
//                    val uri:Uri  = data.data!!
//                     openFileFromFilePicker(uri)
//                 }
//             }
//
//        }
//    }
//    private fun requestSignIn() {
//        Log.e("TAG", "Requesting sign-in")
//        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
//            .build()
//        val client = GoogleSignIn.getClient(this, signInOptions)
//        // The result of the sign-in Intent is handled in onActivityResult.
//        startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
//    }
//    private fun openFileFromFilePicker(uri: Uri) {
//        if (mDriveServiceHelper != null) {
//            Log.e("TAG", "Opening " + uri.path)
//            mDriveServiceHelper!!.openFileUsingStorageAccessFramework(contentResolver, uri)
//                ?.addOnSuccessListener(OnSuccessListener { nameAndContent: Pair<String, String> ->
//                    val name = nameAndContent.first
//                    val content = nameAndContent.second
//                    Log.e("TAG", "openFileFromFilePicker name:$name ")
//                    Log.e("TAG", "openFileFromFilePicker content:$content " )
//                    //mFileTitleEditText.setText(name)
//                   // mDocContentEditText.setText(content)
//                    // Files opened through SAF cannot be modified.
//                    setReadOnlyMode()
//                })
//                ?.addOnFailureListener { exception: Exception? ->
//                    Log.e(
//                        "TAG",
//                        "Unable to open file from picker.",
//                        exception
//                    )
//                }
//        }
//    }
//    private fun createFile() {
//        if (mDriveServiceHelper != null) {
//            Log.e("TAG", "Creating a file.")
//            mDriveServiceHelper!!.createFile()
//                ?.addOnSuccessListener(OnSuccessListener { fileId: String? ->
//                    readFile(
//                        fileId!!
//                    )
//                })
//                ?.addOnFailureListener { exception: java.lang.Exception? ->
//                    Log.e(
//                        "TAG",
//                        "Couldn't create file.",
//                        exception
//                    )
//                }
//        }
//    }
//    private fun readFile(fileId: String) {
//        if (mDriveServiceHelper != null) {
//            Log.e("TAG", "Reading file $fileId")
//            mDriveServiceHelper!!.readFile(fileId)
//                ?.addOnSuccessListener(OnSuccessListener { nameAndContent: Pair<String, String> ->
//                    val name = nameAndContent.first
//                    val content = nameAndContent.second
//                    Log.e("TAG", "readFile name:$name ")
//                    Log.e("TAG", "readFile content:$content " )
//                 //   mFileTitleEditText.setText(name)
//                  //  mDocContentEditText.setText(content)
//                    setReadWriteMode(fileId)
//                })
//                ?.addOnFailureListener { exception: java.lang.Exception? ->
//                    Log.e(
//                        "TAG",
//                        "Couldn't read file.",
//                        exception
//                    )
//                }
//        }
//    }
//    private fun setReadOnlyMode() {
//        Log.e("TAG", "setReadOnlyMode: " )
//    }
//    /**
//     * Updates the UI to read/write mode on the document identified by `fileId`.
//     */
//    private fun setReadWriteMode(fileId: String) {
//        Log.e("TAG", "setReadWriteMode: " )
//    }
//    override fun onStart() {
//        super.onStart()
//        val mPrefs = getSharedPreferences(
//            "theme", Context.MODE_PRIVATE
//        ) //add key
//        val getTheme =
//            mPrefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_NO)
//        AppCompatDelegate.setDefaultNightMode(getTheme)
//    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//
//            android.R.id.home -> {
//                startActivity(Intent(this,MainActivity::class.java))
//                return true
//            }
//
//            else -> return false
//        }
//    }
//    private fun query() {
//        if (mDriveServiceHelper != null) {
//            Log.e("TAG", "Querying for files.")
//            mDriveServiceHelper!!.queryFiles()
//                ?.addOnSuccessListener(OnSuccessListener { fileList: FileList ->
//                    val builder = StringBuilder()
//                    for (file in fileList.files) {
//                        builder.append(file.name).append("\n")
//                    }
//                    val fileNames = builder.toString()
////                    mFileTitleEditText.setText("File List")
////                    mDocContentEditText.setText(fileNames)
//                    setReadOnlyMode()
//                })
//                ?.addOnFailureListener { exception: java.lang.Exception? ->
//                    Log.e(
//                        "TAG",
//                        "Unable to query files.",
//                        exception
//                    )
//                }
//        }
//    }
//
//    private fun handleSignInResult(result: Intent) {
//        GoogleSignIn.getSignedInAccountFromIntent(result)
//            .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
//                Log.e("TAG", "Signed in as " + googleAccount.email)
//                // Use the authenticated account to sign in to the Drive service.
//                val credential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
//                    this, Collections.singleton(DriveScopes.DRIVE_FILE)
//                )
//                credential.selectedAccount = googleAccount.account
//                val googleDriveService: Drive = Drive.Builder(
//                    AndroidHttp.newCompatibleTransport(),
//                    GsonFactory(),
//                    credential
//                )
//                    .setApplicationName("Drive API Migration")
//                    .build()
//                // The DriveServiceHelper encapsulates all REST API and SAF functionality.
//                // Its instantiation is required before handling any onClick actions.
//                mDriveServiceHelper = DriveServiceHelper(googleDriveService)
//            }
//            .addOnFailureListener { exception: java.lang.Exception? ->
//                Log.e(
//                    "TAG",
//                    "Unable to sign in.",
//                    exception
//                )
//            }
//    }

}

