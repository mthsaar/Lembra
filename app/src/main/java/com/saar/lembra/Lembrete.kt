package com.saar.lembra

data class Lembrete(
    val id: String = "",
    val titulo: String,
    val descricao: String,
    val horario: Long,
    val remetenteId: String,
    val destinatarioId: String,
    val status: String = "pendente"
)
