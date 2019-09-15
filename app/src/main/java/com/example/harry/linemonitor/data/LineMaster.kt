package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class LineMaster(

	@field:SerializedName("distance")
	var distance: String? = null,

	@field:SerializedName("flow_leakage_treshold")
	var flowLeakageTreshold: String? = null,

	@field:SerializedName("pressure_check_duration")
	var pressureCheckDuration: String? = null,

	@field:SerializedName("start")
	var start: String? = null,

	@field:SerializedName("end_node_id")
	var endNodeId: String? = null,

	@field:SerializedName("created_at")
	var createdAt: String? = null,

	@field:SerializedName("start_node_id")
	var startNodeId: String? = null,

	@field:SerializedName("manufacture")
	var manufacture: String? = null,

	@field:SerializedName("diameter")
	var diameter: String? = null,

	@field:SerializedName("updated_at")
	var updatedAt: String? = null,

	@field:SerializedName("user_id")
	var userId: Int? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("end")
	var end: String? = null,

	@field:SerializedName("column_16")
	var column16: Any? = null,

	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("thicknes")
	var thicknes: String? = null,

	@field:SerializedName("pressure_leakage")
	var pressureLeakage: String? = null
):Serializable