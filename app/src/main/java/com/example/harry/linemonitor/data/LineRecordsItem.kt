package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class LineRecordsItem(

	@field:SerializedName("lastStartNodePressure")
	val lastStartNodePressure: Any? = null,

	@field:SerializedName("lastEndNodeFlow")
	val lastEndNodeFlow: Any? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("flowRatio")
	val flowRatio: Double? = null,

	@field:SerializedName("lastStartNodeFlow")
	val lastStartNodeFlow: Any? = null,

	@field:SerializedName("pressureRatio")
	val pressureRatio: Double? = null,

	@field:SerializedName("lastEndNodePressure")
	val lastEndNodePressure: Any? = null,

	@field:SerializedName("line_id")
	val lineId: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
): Serializable