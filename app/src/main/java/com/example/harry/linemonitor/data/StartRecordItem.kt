package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class StartRecordItem(

	@field:SerializedName("hours")
	val hours: Int? = null,

	@field:SerializedName("startPressure")
	val startPressure: Double? = null,

	@field:SerializedName("minutes")
	val minutes: Int? = null,

	@field:SerializedName("days")
	val days: String? = null,

	@field:SerializedName("line_id")
	val lineId: Int? = null,

	@field:SerializedName("startFlow")
	val startFlow: Int? = null
):Serializable