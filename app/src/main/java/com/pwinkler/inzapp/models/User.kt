package com.pwinkler.inzapp.models

/**
 * Prosta klasa reprezentująca użytkownika
 *
 * @property uid - identyfikator Firebase
 * @property email - adres email użytkownika
 * @property name - nazwa użytkownika
 * @property invites - lista zaproszeń użytkownika
 */

data class User (
    val uid: String,
    val email: String,
    val name: String,
    val invites: List<String>
)