package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName


data class LineRecords(

	@field:SerializedName("stat")
	val stat: List<StatItem?>? = null,

	@field:SerializedName("graph")
	val graph: List<GraphItem?>? = null
)