package com.cesar.securityquotes.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cesar.securityquotes.data.NetworkModule
import com.cesar.securityquotes.data.SessionStore
import com.cesar.securityquotes.model.ApiErrorResponse
import com.cesar.securityquotes.model.DocumentRequest
import com.cesar.securityquotes.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

data class UiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val token: String? = null,
    val username: String = "",
    val email: String = "",
    val imageUrl: String? = null,
    val pdfUrl: String? = null
)

class SecurityQuotesViewModel(private val context: Context) : ViewModel() {
    private val sessionStore = SessionStore(context)
    private val json = Json { ignoreUnknownKeys = true }
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            sessionStore.tokenFlow.collect { token ->
                _uiState.value = _uiState.value.copy(token = token)
            }
        }
        viewModelScope.launch {
            sessionStore.usernameFlow.collect { username ->
                _uiState.value = _uiState.value.copy(username = username ?: "")
            }
        }
        viewModelScope.launch {
            sessionStore.emailFlow.collect { email ->
                _uiState.value = _uiState.value.copy(email = email ?: "")
            }
        }
        viewModelScope.launch {
            sessionStore.imageUrlFlow.collect { image ->
                _uiState.value = _uiState.value.copy(imageUrl = image)
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, success = null)
    }

    fun register(username: String, email: String, password: String, imageUri: Uri?) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Completa nombre, correo y contraseña")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, success = null)
            runCatching {
                val usernameBody = username.trim().toRequestBody("text/plain".toMediaTypeOrNull())
                val emailBody = email.trim().toRequestBody("text/plain".toMediaTypeOrNull())
                val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
                val imagePart = imageUri?.let { uri ->
                    val input = context.contentResolver.openInputStream(uri) ?: throw IllegalStateException("No se pudo abrir la imagen")
                    val tempFile = File.createTempFile("installer_", ".jpg", context.cacheDir)
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                    MultipartBody.Part.createFormData(
                        "image",
                        tempFile.name,
                        tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }
                NetworkModule.api.register(usernameBody, emailBody, passwordBody, imagePart)
            }.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    success = "Registro exitoso. Ahora inicia sesión.",
                    imageUrl = response.imageUrl
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = readableError(e)
                )
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Ingresa correo y contraseña")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, success = null)
            runCatching {
                NetworkModule.api.login(LoginRequest(email.trim(), password))
            }.onSuccess { response ->
                sessionStore.saveSession(
                    token = response.token,
                    username = response.user.username,
                    email = response.user.email,
                    imageUrl = response.user.imageUrl
                )
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    token = response.token,
                    username = response.user.username,
                    email = response.user.email,
                    imageUrl = response.user.imageUrl,
                    success = "Inicio de sesión correcto"
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = readableError(e)
                )
            }
        }
    }

    fun createDocument(
        clientName: String,
        documentType: String,
        servicesText: String,
        materialsText: String,
        totalText: String
    ) {
        val token = _uiState.value.token
        if (token.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Tu sesión no está disponible. Vuelve a iniciar sesión")
            return
        }

        if (clientName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Ingresa el nombre del cliente antes de generar el PDF")
            return
        }

        if (documentType.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Selecciona el tipo de documento")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, success = null, pdfUrl = null)
            runCatching {
                val request = DocumentRequest(
                    clientName = clientName.trim(),
                    documentType = documentType.trim(),
                    services = splitItems(servicesText),
                    materials = splitItems(materialsText),
                    total = parseAmount(totalText)
                )
                NetworkModule.api.createDocument("Bearer $token", request)
            }.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    success = response.message,
                    pdfUrl = response.pdfUrl
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = readableError(e)
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionStore.clear()
            _uiState.value = UiState()
        }
    }

    private fun splitItems(text: String): List<String> {
        return text
            .split("\n", ",", ";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    private fun parseAmount(totalText: String): Double {
        val normalized = totalText.trim().replace(",", ".")
        return normalized.toDoubleOrNull() ?: 0.0
    }

    private fun readableError(error: Throwable): String {
        return when (error) {
            is HttpException -> parseHttpError(error)
            is IOException -> "No se pudo conectar con la API. Revisa que el VPS esté activo y accesible"
            else -> error.message ?: "Ocurrió un error inesperado"
        }
    }

    private fun parseHttpError(error: HttpException): String {
        val body = try {
            error.response()?.errorBody()?.string()
        } catch (_: Exception) {
            null
        }

        val apiMessage = body?.let {
            runCatching { json.decodeFromString(ApiErrorResponse.serializer(), it) }
                .getOrNull()
        }

        val detailText = apiMessage?.details
            ?.entries
            ?.joinToString(" | ") { (key, value) -> "$key: $value" }
            ?.takeIf { it.isNotBlank() }

        return listOfNotNull(
            apiMessage?.message?.takeIf { it.isNotBlank() },
            detailText
        ).joinToString(". ").ifBlank {
            "Error HTTP ${error.code()} al comunicarse con la API"
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SecurityQuotesViewModel(context.applicationContext) as T
        }
    }
}
