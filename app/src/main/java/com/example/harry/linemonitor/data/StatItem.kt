package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName


data class StatItem(

	@field:SerializedName("lastStartNodePressure")
	val lastStartNodePressure: Double? = null,

	@field:SerializedName("distance")
	val distance: Int? = null,

	@field:SerializedName("lastStartNodeFlow")
	val lastStartNodeFlow: Double? = null,


	@field:SerializedName("created_at")
	val createdAt: String? = null,


	@field:SerializedName("endNodeSN")
	val endNodeSN: String? = null,

	@field:SerializedName("startNodeId")
	val startNodeId: Int? = null,

	@field:SerializedName("diameter")
	val diameter: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("pressureRatio")
	val pressureRatio: Double? = null,

	@field:SerializedName("end")
	val end: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("lastEndNodePressure")
	val lastEndNodePressure: Double? = null,

	@field:SerializedName("timestamp")
	val timestamp: Any? = null,

	@field:SerializedName("startNodeLiters")
	val startNodeLiters: Any? = null,

	@field:SerializedName("flowRatio")
	val flowRatio: Double? = null,

	@field:SerializedName("start")
	val start: String? = null,

	@field:SerializedName("endNodeId")
	val endNodeId: Int? = null,

	@field:SerializedName("endNodeLiters")
	val endNodeLiters: Any? = null,

	@field:SerializedName("startNodeSN")
	val startNodeSN: String? = null,

	@field:SerializedName("manufacture")
	val manufacture: String? = null,

	@field:SerializedName("lastEndNodeFlow")
	val lastEndNodeFlow: Double? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("thicknes")
	val thicknes: Int? = null
)