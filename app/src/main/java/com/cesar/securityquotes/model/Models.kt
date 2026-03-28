package com.cesar.securityquotes.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val message: String,
    val userId: Int,
    val imageUrl: String? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val imageUrl: String? = null
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserDto
)

@Serializable
data class DocumentRequest(
    val clientName: String,
    val documentType: String,
    val services: List<String>,
    val materials: List<String>,
    val total: Double
)

@Serializable
data class DocumentResponse(
    val message: String,
    val documentId: Int? = null,
    val pdfUrl: String
)

@Serializable
data class ApiErrorResponse(
    val message: String? = null,
    val details: Map<String, String>? = null
)
