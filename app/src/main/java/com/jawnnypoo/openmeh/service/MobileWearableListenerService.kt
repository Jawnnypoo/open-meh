package com.jawnnypoo.openmeh.service

import android.content.Intent
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.*
import com.google.gson.Gson
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.activity.MehActivity
import com.jawnnypoo.openmeh.shared.api.MehResponse
import com.jawnnypoo.openmeh.shared.communication.DataValues
import com.jawnnypoo.openmeh.shared.communication.MessageType
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

/**
 * Listens for messages from the wearable device
 */
class MobileWearableListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent?) {
        when (messageEvent!!.path) {
            MessageType.TYPE_FETCH_MEH -> loadMehAndSendItToWearable(messageEvent.sourceNodeId)
            MessageType.TYPE_OPEN_ON_PHONE -> {
                val intent = MehActivity.newIntent(this)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            MessageType.TYPE_BUY_ON_PHONE -> {
                val buyIntent = MehActivity.newIntentForInstaBuy(this)
                buyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(buyIntent)
            }
        }
    }

    private fun loadMehAndSendItToWearable(nodeId: String) {
        Timber.d("loading meh to send to wearable")
        val googleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build()

        val connectionResult = googleApiClient.blockingConnect(30, TimeUnit.SECONDS)

        if (!connectionResult.isSuccess) {
            Timber.d("Failed to connect to Google API on the mobile device")
        }

        var mehResponse: MehResponse? = null
        try {
            mehResponse = App.get().meh.getMeh()
                    .blockingGet()
        } catch (any: Exception) {
            Timber.e(any)
        }

        if (mehResponse == null || mehResponse.deal == null) {
            Timber.e("The meh response was null. Lame")
            sendError(googleApiClient, nodeId)
            return
        }

        val tinyMehResponse = TinyMehResponse.create(mehResponse)
        val tinyMehResponseJson = Gson().toJson(tinyMehResponse)

        var icon: Bitmap? = null
        try {
            icon = Glide.with(applicationContext)
                    .load(mehResponse.deal.photos[0])
                    .asBitmap()
                    .into(300, 300)
                    .get()
        } catch (e: Exception) {
            Timber.e(e)
        }

        val putDataMapReq = PutDataMapRequest.create(DataValues.DATA_PATH_MEH_RESPONSE + "/" + DataValues.getDataMehPathForToday())
        putDataMapReq.dataMap.putString(DataValues.DATA_KEY_MEH_RESPONSE, tinyMehResponseJson)
        if (icon != null) {
            val asset = createAssetFromBitmap(icon)
            putDataMapReq.dataMap.putAsset(DataValues.DATA_KEY_MEH_IMAGE, asset)
        }
        var putDataReq = putDataMapReq.asPutDataRequest()
        //https://developers.google.com/android/reference/com/google/android/gms/wearable/PutDataRequest.html#setUrgent()
        putDataReq = putDataReq.setUrgent()
        val result = Wearable.DataApi.putDataItem(googleApiClient, putDataReq).await()
        if (result.status.isSuccess) {
            Timber.d("Successfully placed the data!")
        } else {
            Timber.e("Failed to put data item for some reason")
            sendError(googleApiClient, nodeId)
        }
    }

    private fun sendError(client: GoogleApiClient, nodeId: String) {
        Wearable.MessageApi.sendMessage(client, nodeId, MessageType.TYPE_FETCH_FAILED, null)
    }

    /**
     * https://developer.android.com/training/wearables/data-layer/assets.html
     */
    private fun createAssetFromBitmap(bitmap: Bitmap): Asset {
        val byteStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
        return Asset.createFromBytes(byteStream.toByteArray())
    }
}
