package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName


data class LoginResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)