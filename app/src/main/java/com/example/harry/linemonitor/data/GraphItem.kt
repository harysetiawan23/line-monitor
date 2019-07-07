package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName


data class GraphItem(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("lineId")
	val lineId: Int? = null
)