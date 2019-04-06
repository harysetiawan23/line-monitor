package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName


data class LineRealtimeData(

	@field:SerializedName("lineRecords")
	val lineRecords: List<LineRecordsItem?>? = null
)