package com.knasirayaz.recorder.utils

import android.content.Context
import android.content.res.loader.ResourcesProvider
import android.provider.Settings.Global.getString
import android.provider.Settings.Secure.getString
import androidx.fragment.app.FragmentManager
import com.filepickersample.bottomsheet.FilePicker
import com.filepickersample.enumeration.FileSelectionType
import com.filepickersample.listener.FilePickerCallback
import com.filepickersample.model.Media
import com.knasirayaz.recorder.R

object FilePickerHelper {

    fun getImageOrDocumentPdf(
        context: Context,
        supportFragmentManager: FragmentManager,
        callback: (success: Boolean, media: Media, message: String) -> Unit,
    ) {
        FilePicker.with(context)
            .type(FileSelectionType.IMAGE_DOCUMENT)
            .pickDocumentType(arrayOf("application/pdf"))
            .enableDocumentWithOriginalName()
            .callBack(object : FilePickerCallback {
                override fun onSuccess(media: Media) {
                    callback.invoke(true, media, context.getString(R.string.success))
                }

                override fun onSuccess(mediaList: ArrayList<Media>) {}

                override fun onError(error: String) {
                    callback.invoke(false, Media(), error)
                }
            })
            .start(supportFragmentManager)
    }

    fun getVideoOrDocumentPdf(
        context: Context,
        supportFragmentManager: FragmentManager,
        callback: (success: Boolean, media: Media, message: String) -> Unit,
    ) {
        FilePicker.with(context)
            .type(FileSelectionType.PICK_DOCUMENT)
            .pickDocumentType(arrayOf("application/pdf,video/*"))
            .enableDocumentWithOriginalName()
            .callBack(object : FilePickerCallback {
                override fun onSuccess(media: Media) {
                    callback.invoke(true, media, context.getString(R.string.success))
                }

                override fun onSuccess(mediaList: ArrayList<Media>) {}

                override fun onError(error: String) {
                    callback.invoke(false, Media(), error)
                }
            })
            .start(supportFragmentManager)
    }


    fun getAudio(
        context: Context,
        supportFragmentManager: FragmentManager,
        callback: (success: Boolean, media: Media, message: String) -> Unit,
    ) {
        FilePicker.with(context)
            .type(FileSelectionType.AUDIO)
            .pickDocumentType(arrayOf("audio/*"))
            .enableDocumentWithOriginalName()
            .callBack(object : FilePickerCallback {
                override fun onSuccess(media: Media) {
                    callback.invoke(true, media, context.getString(R.string.success))
                }

                override fun onSuccess(mediaList: ArrayList<Media>) {}

                override fun onError(error: String) {
                    callback.invoke(false, Media(), error)
                }
            })
            .start(supportFragmentManager)
    }


    fun getProfileImage(
        context: Context,
        supportFragmentManager: FragmentManager,
        callback: (success: Boolean, media: Media, message: String) -> Unit,
    ) {
        FilePicker.with(context)
            .type(FileSelectionType.IMAGE)
            .cropSquare()
            .callBack(object : FilePickerCallback {
                override fun onSuccess(media: Media) {
                    callback.invoke(true, media, context.getString(R.string.success))
                }

                override fun onSuccess(mediaList: ArrayList<Media>) {}

                override fun onError(error: String) {
                    callback.invoke(false, Media(), error)
                }
            })
            .start(supportFragmentManager)
    }

    fun getVideo(
        context: Context,
        supportFragmentManager: FragmentManager,
        callback: (success: Boolean, media: Media, message: String) -> Unit,
    ) {
        FilePicker.with(context)
            .type(FileSelectionType.VIDEO)
            .callBack(object : FilePickerCallback {
                override fun onSuccess(media: Media) {
                    callback.invoke(true, media, context.getString(R.string.success))
                }

                override fun onSuccess(mediaList: ArrayList<Media>) {}

                override fun onError(error: String) {
                    callback.invoke(false, Media(), error)
                }
            })
            .start(supportFragmentManager)
    }

    fun getImage(
        context: Context,
        supportFragmentManager: FragmentManager,
        callback: (success: Boolean, media: Media, message: String) -> Unit,
    ) {
        FilePicker.with(context)
            .type(FileSelectionType.IMAGE)
            .callBack(object : FilePickerCallback {
                override fun onSuccess(media: Media) {
                    callback.invoke(true, media, context.getString(R.string.success))
                }

                override fun onSuccess(mediaList: ArrayList<Media>) {}

                override fun onError(error: String) {
                    callback.invoke(false, Media(), error)
                }
            })
            .start(supportFragmentManager)
    }

}