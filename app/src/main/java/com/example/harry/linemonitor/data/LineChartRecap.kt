package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class LineChartRecap(

	@field:SerializedName("avg")
	val avg: Avg? = null,

	@field:SerializedName("median")
	val median: List<MedianItem?>? = null,

	@field:SerializedName("max")
	val max: Max? = null
): Serializable