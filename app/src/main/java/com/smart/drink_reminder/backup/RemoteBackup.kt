package com.smart.drink_reminder.backup

import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.*
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.SearchableField
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.smart.drink_reminder.Backup
import com.smart.drink_reminder.Backup.Companion.REQUEST_CODE_CREATION
import com.smart.drink_reminder.Backup.Companion.REQUEST_CODE_OPENING
import com.smart.drink_reminder.Backup.Companion.REQUEST_CODE_SIGN_IN
import java.io.*

class RemoteBackup(backup: Backup) {
    private var mDriveClient: DriveClient? = null
    private var mDriveResourceClient: DriveResourceClient? = null
    var mOpenItemTaskSource: TaskCompletionSource<DriveId>? = null
    private val activity: Backup = backup
    private var DATABASE_NAME="DRINK_WATER"
    private val MIME_TYPE = "application/x-sqlite-3"
    lateinit var  mGoogleApiClient: GoogleApiClient

    fun connectToDrive(backup: Boolean) {
        val account = GoogleSignIn.getLastSignedInAccount(activity)

        Log.e("TAG", "connectToDrive: $account ${account?.displayName}" )
        if (account == null) {
            signIn()
        } else {
            Log.e("TAG", "connectToDrive:Already have Account " )
            //Initialize the drive api
            mDriveClient = Drive.getDriveClient(activity, account)
            // Build a drive resource client.
            mDriveResourceClient = Drive.getDriveResourceClient(activity, account)
            if (backup) startDriveBackup() else startDriveRestore()
        }
    }
    private fun signIn() {
        Log.e("TAG", "Start sign in")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Drive.SCOPE_FILE)
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(activity, signInOptions)
        //val GoogleSignInClient: GoogleSignInClient = buildGoogleSignInClient()!!

        Log.e("TAG", "GoogleSignInClient:$client ")
        activity.startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }
    private fun buildGoogleSignInClient(): GoogleSignInClient? {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Drive.SCOPE_FILE)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, signInOptions)
    }
    private fun startDriveBackup() {
        Log.e("TAG", "startDriveBackup:$mDriveResourceClient " )
        mDriveResourceClient
            ?.createContents()
            ?.continueWithTask(
                Continuation<DriveContents?, Task<Void?>?> { task ->
                    Log.e("TAG", "startDriveBackup:Start" )
                    createFileIntentSender(
                        task.result)
                })

            ?.addOnFailureListener { e -> Log.e("TAG", "Failed to create new contents.", e) }
        Log.e("TAG", "startDriveBackup: " )

    }

    private fun createFileIntentSender(driveContents: DriveContents?): Task<Void?>? {
        Log.e("TAG", "createFileIntentSender: " )
        val inFileName = activity.getDatabasePath(DATABASE_NAME).toString()
        try {
            val dbFile = File(inFileName)
            val fis = FileInputStream(dbFile)
            val outputStream: OutputStream = driveContents!!.outputStream
            // Transfer bytes from the inputfile to the outputfile
            val buffer = ByteArray(1024)
            var length: Int
            while (fis.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val metadataChangeSet = MetadataChangeSet.Builder()
            .setTitle("database_backup.db")
            .setMimeType("application/db")
            .build()
        val createFileActivityOptions = CreateFileActivityOptions.Builder()
            .setInitialMetadata(metadataChangeSet)
            .setInitialDriveContents(driveContents)
            .build()
        return mDriveClient
            ?.newCreateFileActivityIntentSender(createFileActivityOptions)
            ?.continueWith { task: Task<IntentSender?> ->
                activity.startIntentSenderForResult(
                    task.result,
                    REQUEST_CODE_CREATION,
                    null,
                    0,
                    0,
                    0
                )
                null
            }
    }
    private fun startDriveRestore() {
        Log.e("TAG", "startDriveRestore: " )
        pickFile()
            ?.addOnSuccessListener(
                activity
            ) { driveId -> retrieveContents(driveId!!.asDriveFile()) }
            ?.addOnFailureListener(activity) { e -> Log.e("TAG", "No file selected", e) }
    }
    private fun retrieveContents(file: DriveFile) {
        Log.e("TAG", "retrieveContents: " )
        //DB Path
        val inFileName = activity.getDatabasePath(DATABASE_NAME).toString()
        val openFileTask = mDriveResourceClient!!.openFile(file, DriveFile.MODE_READ_ONLY)
        openFileTask
            .continueWithTask { task: Task<DriveContents?> ->
                val contents = task.result
                try {
                    val parcelFileDescriptor = contents!!.parcelFileDescriptor
                    val fileInputStream =
                        FileInputStream(parcelFileDescriptor.fileDescriptor)
                    // Open the empty db as the output stream
                    val output: OutputStream = FileOutputStream(inFileName)
                    // Transfer bytes from the inputfile to the outputfile
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (fileInputStream.read(buffer).also { length = it } > 0) {
                        output.write(buffer, 0, length)
                    }
                    // Close the streams
                    output.flush()
                    output.close()
                    fileInputStream.close()
                    Toast.makeText(activity, "Import completed", Toast.LENGTH_SHORT).show()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Toast.makeText(activity, "Error on import", Toast.LENGTH_SHORT).show()
                }
                mDriveResourceClient!!.discardContents(contents!!)
            }
            .addOnFailureListener { e: java.lang.Exception? ->
                Log.e("TAG", "Unable to read contents", e)
                Toast.makeText(activity, "Error on import", Toast.LENGTH_SHORT).show()
            }
    }
    private fun pickItem(openOptions: OpenFileActivityOptions): Task<DriveId?>? {
        Log.e("TAG", "pickItem: " )
        mOpenItemTaskSource = TaskCompletionSource()
        mDriveClient
            ?.newOpenFileActivityIntentSender(openOptions)
            ?.continueWith(Continuation<IntentSender?, Void?> { task: Task<IntentSender?> ->
                activity.startIntentSenderForResult(
                    task.result, REQUEST_CODE_OPENING, null, 0, 0, 0
                )
                null
            })
        return mOpenItemTaskSource!!.task
    }
    private fun pickFile(): Task<DriveId?>? {
        Log.e("TAG", "pickFile: " )
        val openOptions = OpenFileActivityOptions.Builder()
            .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "application/db"))
            .setActivityTitle("Select DB File")
            .build()
        return pickItem(openOptions)
    }

}