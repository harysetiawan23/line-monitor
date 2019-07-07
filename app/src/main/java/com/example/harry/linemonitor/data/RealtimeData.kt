package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName


data class RealtimeData(

	@field:SerializedName("lineRecords")
	val lineRecords: LineRecords? = null
)