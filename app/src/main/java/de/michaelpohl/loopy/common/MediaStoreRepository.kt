package de.michaelpohl.loopy.common

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import timber.log.Timber

class MediaStoreRepository(val context: Context) {

    private var mediaStoreEntries = listOf<AudioModel>()

    fun getAlbumTitles(): MutableList<String> {
        val list: MutableList<String> = mutableListOf()

        val projection =
            arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS
            )

        val selection: String? = null
        val selectionArgs: Array<String>? = null
        val sortOrder = MediaStore.Audio.Media.ALBUM + " ASC"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        if (cursor.moveToFirst()) {
            val albumTitle = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
            do {
                list.add(cursor.getString(albumTitle))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return list
    }

    // if we've already done that, we'll just return the field. TODO check if that's sufficient!
    fun getMediaStoreEntries(): List<AudioModel> {
        if (mediaStoreEntries.isEmpty()) {
            mediaStoreEntries = readMediaStore(context)
        }
        return mediaStoreEntries
    }

    private fun readMediaStore(context: Context): List<AudioModel> {
        val list: MutableList<AudioModel> = mutableListOf()

        // Get the external storage media store audio uri
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        //val uri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI

        // IS_MUSIC : Non-zero if the audio file is music
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"

        // Sort the musics
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        //val sortOrder = MediaStore.Audio.Media.TITLE + " DESC"

        // Query the external storage for music files
        val cursor: Cursor = context.contentResolver.query(
            uri,
            null,
            selection,
            null,
            sortOrder
        )

        if (cursor.moveToFirst()) {
            val id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val data: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            do {
                val audioId: Long = cursor.getLong(id)
                val audioTitle: String = cursor.getString(title)
                val albumName: String = cursor.getString(album)
                val albumData = cursor.getString(data)
                Timber.d("Music file: %s, %s, %s", audioId, audioTitle, albumName)
                val c = context.contentResolver
                val extension =
                    MimeTypeMap.getSingleton().getExtensionFromMimeType(c.getType(uri))
                Timber.d("My file extension is: %s", extension)
                list.add(AudioModel(audioTitle, audioId, albumName, albumData, extension ?: ""))
            } while (cursor.moveToNext())
        }
        return list
    }

}
