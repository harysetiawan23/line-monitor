package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class NodeMaster(

	@field:SerializedName("liquidFlowKonstanta")
	var liquidFlowKonstanta: Double? = null,

	@field:SerializedName("lng")
	var lng: String? = null,

	@field:SerializedName("pressOffset")
	var pressOffset: Double? = null,

	@field:SerializedName("pressure_tranducer_model")
	var pressureTranducerModel: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("isStartNode")
	var isStartNode: Int? = null,

	@field:SerializedName("isOnline")
	val isOnline: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("flow_rate_model")
	var flowRateModel: Any? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("phone_number")
	var phoneNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("sn")
	var sn: String? = null,

	@field:SerializedName("lat")
    var lat: String? = null
):Serializable