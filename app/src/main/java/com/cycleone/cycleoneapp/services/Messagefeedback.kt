package com.cycleone.cycleoneapp.services


data class Feedback(
    var message: String = "",
    var uri: String? = null,
    var timestamp: Long = System.currentTimeMillis()
)






fun mapMessageToFirestoreMap(message: String, uri: String? = null): Map<String, Any?> {
    return mapOf(
        "message" to message,
        "uri" to uri,
        "timestamp" to System.currentTimeMillis()
    )
}
