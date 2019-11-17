package com.pwinkler.inzapp.models

/**
 * Prosta klasa reprezentująca użytkownika
 *
 * @property uid - identyfikator Firebase
 * @property email - adres email użytkownika
 */

data class User (
    val uid: String,
    val email: String,
    val invites: List<String>
)