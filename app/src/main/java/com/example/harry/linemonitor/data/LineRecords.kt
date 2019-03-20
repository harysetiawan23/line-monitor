package com.example.harry.linemonitor.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class LineRecords(

	@field:SerializedName("lineRecords")
	val lineRecords: List<LineRecordsItem?>? = null
):Serializable