package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName


data class DataItem(

	@field:SerializedName("hours")
	val hours: Int? = null,

	@field:SerializedName("endFlow")
	val endFlow: Double? = null,

	@field:SerializedName("startPressure")
	val startPressure: Double? = null,

	@field:SerializedName("minutes")
	val minutes: Int? = null,

	@field:SerializedName("days")
	val days: String? = null,

	@field:SerializedName("endPressure")
	val endPressure: Double? = null,

	@field:SerializedName("lineId")
	val lineId: Int? = null,

	@field:SerializedName("startFlow")
	val startFlow: Double? = null
)