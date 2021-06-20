package com.michaelpohl.loopyplayer2.model

import android.content.Context
import android.os.Environment
import com.michaelpohl.loopyplayer2.common.AudioModel
import com.michaelpohl.loopyplayer2.common.FileModel
import com.michaelpohl.loopyplayer2.common.jni.JniBridge
import com.michaelpohl.loopyplayer2.common.toFileModels
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ExternalStorageManager(val context: Context) {

    private val appStorageFolder = context.getExternalFilesDir(null)

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

    fun listSets(): List<File> {
        return getPathContent(path = appStorageFolder!!.path, onlyFolders = true)
    }

    fun getAudioModelsInSet(setFolderName: String): List<AudioModel> {
        val audioModels = mutableListOf<AudioModel>()

        getPathContent( "${appStorageFolder?.path}/$setFolderName")
            .toFileModels(setOf(AppStateRepository.Companion.AudioFileType.PCM)) // we store only pcm in the set folders
            .filterIsInstance<FileModel.AudioFile>()
            .forEach {
                audioModels.add(it.toAudioModel())
            }
        return audioModels
    }

    fun saveFile(fileToSave: File, path: String): Boolean {
        val externalFile = File(path, fileToSave.name.toString())
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

    fun getPathContent(
        path: String,
        showHiddenFiles: Boolean = false,
        onlyFolders: Boolean = false
    ): List<File> {
        val file = File(path)
        Timber.d("My file should be: $path")
        Timber.d("what's in my path: ${file.listFiles().map { it.name }}")

        return file.listFiles()
            .filter { showHiddenFiles || !it.name.startsWith(".") }
            .filter { !onlyFolders || it.isDirectory }
            .toList()
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

    fun getFullPath(path: String): String {
        return "${appStorageFolder?.path}/$path/"
    }

    fun copyStandardFilesToSdCard(): Boolean {
        Timber.d("Is external storage available: $isExternalStorageAvailable, read only: $isExternalStorageReadOnly")
        val outputPath = "${appStorageFolder?.path}/$STANDARD_SET_FOLDER_NAME/"

        return try {

            listAssetFiles(AppStateRepository.Companion.AudioFileType.MP3.suffix).forEach {
                Timber.d("File from assets: $it")
                copySingleFileFromAssetsTo(outputPath, context.assets.open(it), it)
            }
            val conversionResult = JniBridge.convertFilesInFolder(outputPath);
            Timber.d("conversion result: $conversionResult")
            true
        } catch (e: IOException) {
            Timber.e("Copying of files to SD card (Location: ${appStorageFolder?.path}/$STANDARD_SET_FOLDER_NAME) failed")
            e.printStackTrace()
            false
        }
    }

    private fun copySingleFileFromAssetsTo(
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

    private fun listAssetFiles(extension: String): Set<String> {
        val list = mutableSetOf<String>()
        try {
            context.assets.list("")?.let { filesList ->
                filesList.filter {
                    it.endsWith(extension)
                }.forEach { fileName ->
                    Timber.d("Found this file: $fileName")
                    list.add(fileName)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return list
    }

    fun deleteFromSet(path: String?, model: AudioModel) {

        val content = getPathContent("${appStorageFolder?.path}/${path ?: STANDARD_SET_FOLDER_NAME}")
        Timber.d("name: ${model.name}")
        content.forEach {
            Timber.d("name: ${it.path}")
        }
        val found = content.find { it.path == model.name }
        found?.let {
            it.delete()
        }
    }

    fun copyMiscFiles(): Boolean {
        val outputPath = "${appStorageFolder?.path}"
        return try {

            listAssetFiles("html").forEach {
                copySingleFileFromAssetsTo(outputPath, context.assets.open(it), it)
            }
            true
        } catch (e: IOException) {
            Timber.e("Copying of files to SD card (Location: ${appStorageFolder?.path}) failed")
            e.printStackTrace()
            false
        }
    }

    companion object {
        const val STANDARD_SET_FOLDER_NAME = "standard"
    }
}

