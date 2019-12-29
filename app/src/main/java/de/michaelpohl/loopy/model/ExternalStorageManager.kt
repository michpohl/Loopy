package de.michaelpohl.loopy.model

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExternalStorageManager(val context: Context) {


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

    fun createAppFolder(): Boolean {
        val folder = File(Environment.getExternalStorageDirectory(), APP_FILES_FOLDER_NAME)
        return if (!folder.exists()) {
            folder.mkdirs()
        } else true
    }

    fun createLoopFolder(folderName: String): Boolean {
        val folder = File(
            "${Environment.getExternalStorageDirectory()}/$APP_FILES_FOLDER_NAME",
            folderName
        )
        return if (!folder.exists()) {
            folder.mkdirs()
        } else true
    }

    companion object {
        const val APP_FILES_FOLDER_NAME = "loopy"
    }
}
