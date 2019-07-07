package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName


data class UserData(

        @field:SerializedName("settings")
        val settings: List<Any?>? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("role_id")
        val roleId: Any? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("email_verified_at")
        val emailVerifiedAt: Any? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("avatar")
        val avatar: String? = null,

        @field:SerializedName("email")
        val email: String? = null



)