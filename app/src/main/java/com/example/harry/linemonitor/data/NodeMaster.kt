package com.example.harry.linemonitor.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class NodeMaster(

	@field:SerializedName("lng")
	val lng: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("isStartNode")
	val isStartNode: Int? = null,

	@field:SerializedName("isOnline")
	val isOnline: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("sn")
	val sn: String? = null,

	@field:SerializedName("lat")
	val lat: String? = null
):Serializable