package de.michaelpohl.loopy.model

import android.content.Context
import android.os.Environment
import de.michaelpohl.loopy.common.FileHelper
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ExternalStorageManager(val context: Context) {

    private val appStorageFolder: File by lazy {
        context.getExternalFilesDir(null)
    }

    private val isExternalStorageReadOnly: Boolean
        get() {
            val extStorageState = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)
        }
    private val isExternalStorageAvailable: Boolean
        get() {
            val extStorageState = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED.equals(extStorageState)
        }

    fun saveFile(fileToSave: File, path: String): Boolean {
        var externalFile = File(path, fileToSave.name.toString())
        return try {
            val fileOutPutStream = FileOutputStream(externalFile)
            fileOutPutStream.write(fileToSave.readBytes())
            fileOutPutStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    //    fun readFile(): File {
    //        var myExternalFile = File(getExternalFilesDir(filepath), fileName.text.toString())
    //
    //        val filename = myExternalFile.name.toString()
    //        myExternalFile = File(getExternalFilesDir(filepath),filename)
    //        if(filename.toString()!=null && filename.toString().trim()!=""){
    //            var fileInputStream = FileInputStream(myExternalFile)
    //            var inputStreamReader = InputStreamReader(fileInputStream)
    //            val bufferedReader = BufferedReader(inputStreamReader)
    //            val stringBuilder: StringBuilder = StringBuilder()
    //            var text: String? = null
    //            while ({ text = bufferedReader.readLine(); text }() != null) {
    //                stringBuilder.append(text)
    //            }
    //            fileInputStream.close()
    //            //Displaying data on EditText
    //            Toast.makeText(applicationContext,stringBuilder.toString(),Toast.LENGTH_SHORT).show()
    //    }
    //    })  }

    fun createSetFolder(folderName: String? = STANDARD_SET_FOLDER_NAME): Boolean {
        val folder = File(
            "$appStorageFolder",
            folderName
        )
        return if (!folder.exists()) {
            folder.mkdirs()
        } else true
    }

    //TODO change this to handle a number of files
    fun copyStandardFilesToSdCard(): Boolean {
        Timber.d("Is external storage available: $isExternalStorageAvailable, read only: $isExternalStorageReadOnly")
        val outputPath = "${appStorageFolder.path}/$STANDARD_SET_FOLDER_NAME/"

        return try {

            listAssetFiles().forEach {
                copySingleFileFromAssetsToStandardSet(outputPath, context.assets.open(it), it)
            }
            true
        } catch (e: IOException) {
            Timber.e("Copying of files to SD card (Location: ${appStorageFolder.path}/$STANDARD_SET_FOLDER_NAME) failed")
            e.printStackTrace()
            false
        }
    }

    private fun copySingleFileFromAssetsToStandardSet(
        outputPath: String,
        input: InputStream,
        fileName: String
    ) {
        FileOutputStream(File(outputPath, fileName)).use { out ->
            input.use {
                it.copyTo(out)
            }
            out.close()

        }
    }

    fun listAssetFiles(): Set<String> {
        val list = mutableSetOf<String>()
        try {
            context.assets.list("")?.let { filesList ->
                filesList.filter { FileHelper.isValidAudioFile(it) }.forEach { fileName ->
                    if (FileHelper.isValidAudioFile(fileName)) {
                        Timber.d("Found this file: $fileName")
                        list.add(fileName)
                    }
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return list
    }

    companion object {
        const val STANDARD_SET_FOLDER_NAME = "standard"
    }
}
