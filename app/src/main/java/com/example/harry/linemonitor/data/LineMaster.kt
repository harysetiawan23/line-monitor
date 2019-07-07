package com.example.harry.linemonitor.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class LineMaster(

	@field:SerializedName("distance")
	var distance: String? = null,

	@field:SerializedName("start")
	var start: String? = null,

	@field:SerializedName("end_node_id")
	var endNodeId: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("start_node_id")
    var startNodeId: String? = null,

	@field:SerializedName("manufacture")
	var manufacture: String? = null,

	@field:SerializedName("diameter")
	var diameter: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("end")
	var end: String? = null,

	@field:SerializedName("id")
    var id: Int? = null,

	@field:SerializedName("thicknes")
	var thicknes: String? = null
):Serializable