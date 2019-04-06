package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Max(

	@field:SerializedName("startRecord")
	val startRecord: List<StartRecordItem?>? = null,

	@field:SerializedName("endRecord")
	val endRecord: List<EndRecordItem?>? = null
): Serializable