package com.geronso.pearler.pearl.viewmodel

import android.util.Log
import com.geronso.pearler.base.BaseEventManager
import com.geronso.pearler.base.ImageUtils
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.pearl.model.PearlInteractor
import com.geronso.pearler.profile.viewmodel.ProfileEventManager
import com.google.gson.Gson
import io.reactivex.ObservableSource
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import javax.inject.Inject

class PearlEventManager @Inject constructor(
    private val pearlInteractor: PearlInteractor
) : BaseEventManager<PearlViewModelEvent, PearlViewState>() {

    private val gson = Gson()

    override fun onEvent(event: PearlViewModelEvent): ObservableSource<out PearlViewState> =
        when (event) {
            is PearlViewModelEvent.PublishPearl -> {
                val data = gson.toJson(event.publishPearl)
                pearlInteractor.publishPearl(
                    imageData = ImageUtils.compressImageToMultipart(
                        context = event.context,
                        bitmap = event.bitmap
                    ),
                    pearlData = MultipartBody.Part.createFormData(DATA_MULTIPART_TAG, data),
                    fileExtensionData = MultipartBody.Part.createFormData(
                        FILE_EXTENSION_MULTIPART_TAG,
                        event.fileExtension
                    )
                )
            }
        }.subscribeOn(Schedulers.io())

    override fun onNext(value: PearlViewState?) {
        viewEventObservable.postValue(value)
    }

    override fun onError(e: Throwable?) {
        Log.e(Logger.PEARL, e.toString())
    }

    override fun onComplete() {}

    private companion object {
        const val DATA_MULTIPART_TAG = "data"
        const val FILE_EXTENSION_MULTIPART_TAG = "file_extension"
    }
}