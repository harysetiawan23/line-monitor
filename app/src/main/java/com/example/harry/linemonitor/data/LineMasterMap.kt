package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class LineMasterMap(

	@field:SerializedName("distance")
	val distance: Int? = null,

	@field:SerializedName("startNodeLng")
	val startNodeLng: String? = null,

	@field:SerializedName("startNodePhone")
	val startNodePhone: String? = null,

	@field:SerializedName("start")
	val start: String? = null,

	@field:SerializedName("end_node_id")
	val endNodeId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("start_node_id")
	val startNodeId: Int? = null,

	@field:SerializedName("startNodeSN")
	val startNodeSN: String? = null,

	@field:SerializedName("manufacture")
	val manufacture: String? = null,

	@field:SerializedName("diameter")
	val diameter: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("endNodeLat")
	val endNodeLat: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("endNodePhone")
	val endNodePhone: String? = null,

	@field:SerializedName("end")
	val end: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("thicknes")
	val thicknes: Int? = null,

	@field:SerializedName("EndNodeSN")
	val endNodeSN: String? = null,

	@field:SerializedName("startNodeLat")
	val startNodeLat: String? = null,

	@field:SerializedName("endNodeLng")
	val endNodeLng: String? = null
):Serializable