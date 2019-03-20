package com.example.harry.linemonitor.data

import com.google.gson.annotations.SerializedName


data class LineHistory(

	@field:SerializedName("avg_flow")
	val avgFlow: Float? = null,

	@field:SerializedName("hour")
	val hour: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("isStartNode")
	val isStartNode: Int? = null,

	@field:SerializedName("avg_pressure")
	val avgPressure: Float? = null,

	@field:SerializedName("line_id")
	val lineId: Int? = null,

	@field:SerializedName("minute")
	val minute: Int? = null
)