package com.smart.drink_reminder.backup


import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import android.util.Pair as UtilPair

class DriveServiceHelper//code
    (driveService: Drive) {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    private var mDriveService: Drive? = driveService


    fun createFile(): Task<String?>? {
        return Tasks.call(mExecutor, Callable {
            val metadata = File()
                .setParents(Collections.singletonList("root"))
                .setMimeType("text/plain")
                .setName("Untitled file")
            val googleFile = mDriveService!!.files().create(metadata).execute()
                ?: throw IOException("Null result when requesting file creation.")
            googleFile.id
        })
    }

    fun readFile(fileId: String?): Task<android.util.Pair<String, String>>? {
        return Tasks.call(mExecutor, Callable {
            // Retrieve the metadata as a File object.
            val metadata =
                mDriveService!!.files()[fileId].execute()
            val name = metadata.name
            mDriveService!!.files()[fileId].executeMediaAsInputStream().use { `is` ->
                BufferedReader(InputStreamReader(`is`)).use { reader ->
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    val contents = stringBuilder.toString()
                    val myPair = UtilPair.create<String?, String?>(name, contents)
                    return@Callable myPair
                }
            }
        })
    }
    fun saveFile(fileId: String?, name: String?, content: String?): Task<Void?>? {
        return Tasks.call(mExecutor, {
            // Create a File containing any metadata changes.
            val metadata =
                File().setName(name)
            // Convert content to an AbstractInputStreamContent instance.
            val contentStream = ByteArrayContent.fromString("text/plain", content)
            // Update the metadata and contents.
            mDriveService!!.files().update(fileId, metadata, contentStream).execute()
            null
        })
    }
    fun queryFiles(): Task<FileList>? {
        return Tasks.call(mExecutor,
            {
                mDriveService!!.files().list().setSpaces("drive").execute()
            })
    }
    fun createFilePickerIntent(): Intent? {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        return intent
    }
    fun openFileUsingStorageAccessFramework(
        contentResolver: ContentResolver, uri: Uri?
    ): Task<android.util.Pair<String, String>>? {
        return Tasks.call(mExecutor, Callable {
            // Retrieve the document's display name from its metadata.
            var name: String
            contentResolver.query(uri!!, null, null, null, null).use { cursor ->
                name = if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.getString(nameIndex)
                } else {
                    throw IOException("Empty cursor returned for file.")
                }
            }
            // Read the document's contents as a String.
            var content: String
            contentResolver.openInputStream(uri).use { `is` ->
                BufferedReader(InputStreamReader(`is`)).use { reader ->
                    val stringBuilder = java.lang.StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    content = stringBuilder.toString()
                }
            }

            val myPair = UtilPair.create<String?, String?>(name, content)
            return@Callable myPair
        })
    }

}