package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName


data class LeakageMaster(

        @field:SerializedName("informed")
        val informed: Int? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("user_id")
        val userId: Int? = null,

        @field:SerializedName("solved")
        val solved: Int? = null,

        @field:SerializedName("created_at")
        val createdAt: Any? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("line_id")
        val lineId: Int? = null
)