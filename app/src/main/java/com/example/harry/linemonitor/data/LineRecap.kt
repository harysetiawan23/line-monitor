package com.example.harry.linemonitor.data

import com.google.gson.annotations.SerializedName


data class LineRecap(

	@field:SerializedName("hours")
	val hours: Int? = null,

	@field:SerializedName("endFlow")
	val endFlow: Int? = null,

	@field:SerializedName("startPressure")
	val startPressure: Float? = null,

	@field:SerializedName("minutes")
	val minutes: Int? = null,

	@field:SerializedName("days")
	val days: String? = null,

	@field:SerializedName("endPressure")
	val endPressure: Float? = null,

	@field:SerializedName("line_id")
	val lineId: Int? = null,

	@field:SerializedName("startFlow")
	val startFlow: Int? = null
)