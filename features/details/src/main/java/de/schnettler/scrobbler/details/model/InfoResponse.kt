package de.schnettler.scrobbler.details.model

interface InfoResponse {
    val tags: TagListResponse?
        get() = null
    val wiki: WikiResponse?
        get() = null
    val duration: Long
        get() = 0
    val userloved: Long
        get() = 0
}