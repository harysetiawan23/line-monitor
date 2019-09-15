package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName


data class LineLeakageResponse(

        @field: SerializedName("leakId")
        val leakId: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("solved")
        val solved: Int? = null,

        @field:SerializedName("lineData")
        val lineData: LineData? = null
)