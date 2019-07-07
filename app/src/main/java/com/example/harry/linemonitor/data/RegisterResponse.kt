package com.example.harry.linemonitor.data


import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("token")
	val token: String? = null
)