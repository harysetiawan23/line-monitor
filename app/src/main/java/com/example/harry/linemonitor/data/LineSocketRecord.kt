package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class LineSocketRecord(

	@field:SerializedName("hours")
	val hours: String? = null,

	@field:SerializedName("endFlow")
	val endFlow: String? = null,

	@field:SerializedName("minutes")
	val minutes: String? = null,

	@field:SerializedName("startPressure")
	val startPressure: String? = null,

	@field:SerializedName("days")
	val days: String? = null,

	@field:SerializedName("lineId")
	val lineId: String? = null,

	@field:SerializedName("endPressure")
	val endPressure: String? = null,

	@field:SerializedName("startFlow")
	val startFlow: String? = null
):Serializable