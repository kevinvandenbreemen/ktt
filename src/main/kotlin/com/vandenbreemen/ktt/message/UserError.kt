package com.vandenbreemen.ktt.message

/**
 * An error resulting from a user's actions
 */
class UserError(message: String): Exception(message) {
}