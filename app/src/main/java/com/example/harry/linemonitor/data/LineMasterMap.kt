package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class LineMasterMap(

	@field:SerializedName("distance")
	var distance: Int? = null,

	@field:SerializedName("startNodeLng")
	var startNodeLng: String? = null,

	@field:SerializedName("flow_leakage_treshold")
	var flowLeakageTreshold: Double? = null,

	@field:SerializedName("startNodePhone")
	var startNodePhone: String? = null,

	@field:SerializedName("end_node_id")
	var endNodeId: Int? = null,

	@field:SerializedName("created_at")
	var createdAt: String? = null,

	@field:SerializedName("start_node_id")
	var startNodeId: Int? = null,

	@field:SerializedName("diameter")
	var diameter: Int? = null,

	@field:SerializedName("updated_at")
	var updatedAt: String? = null,

	@field:SerializedName("endNodePhone")
	var endNodePhone: String? = null,

	@field:SerializedName("end")
	var end: String? = null,

	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("pressure_check_duration")
	var pressureCheckDuration: Int? = null,

	@field:SerializedName("start")
	var start: String? = null,

	@field:SerializedName("startNodeSN")
	var startNodeSN: String? = null,

	@field:SerializedName("manufacture")
	var manufacture: String? = null,

	@field:SerializedName("user_id")
	var userId: Int? = null,

	@field:SerializedName("endNodeLat")
	var endNodeLat: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("column_16")
	var column16: Any? = null,

	@field:SerializedName("thicknes")
	var thicknes: Int? = null,

	@field:SerializedName("pressure_leakage")
	var pressureLeakage: Double? = null,

	@field:SerializedName("EndNodeSN")
	var endNodeSN: String? = null,

	@field:SerializedName("startNodeLat")
	var startNodeLat: String? = null,

	@field:SerializedName("endNodeLng")
	var endNodeLng: String? = null
):Serializable