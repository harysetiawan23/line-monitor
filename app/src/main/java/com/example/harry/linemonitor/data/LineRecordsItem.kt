package com.example.harry.linemonitor.data

import com.google.gson.annotations.SerializedName


data class LineRecordsItem(

	@field:SerializedName("lastStartNodePressure")
	val lastStartNodePressure: Double? = null,

	@field:SerializedName("startNodeLiters")
	val startNodeLiters: Double? = null,

	@field:SerializedName("flowRatio")
	val flowRatio: Double? = null,

	@field:SerializedName("lastStartNodeFlow")
	val lastStartNodeFlow: Double? = null,

	@field:SerializedName("endNodeId")
	val endNodeId: Int? = null,

	@field:SerializedName("endNodeLiters")
	val endNodeLiters: Double? = null,

	@field:SerializedName("startNodeId")
	val startNodeId: Int? = null,

	@field:SerializedName("lastEndNodeFlow")
	val lastEndNodeFlow: Double? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("pressureRatio")
	val pressureRatio: Double? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("lastEndNodePressure")
	val lastEndNodePressure: Double? = null,

	@field:SerializedName("timestamp")
	val timestamp: Any? = null
)