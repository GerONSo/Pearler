package com.geronso.pearler.login.model.data

import com.google.gson.annotations.SerializedName


data class LocalIdData(
    @SerializedName("id")
    val id: String?,
    @SerializedName("metadata")
    val existsMetadata: ExistsMetadata
) {
    inner class ExistsMetadata(
        @SerializedName("exists")
        val exists: Boolean,
    )
}
