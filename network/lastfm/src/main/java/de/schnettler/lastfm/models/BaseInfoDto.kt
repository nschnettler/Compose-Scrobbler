package de.schnettler.lastfm.models

interface BaseInfoDto {
    val tags: TagsDto?
        get() = null
    val wiki: WikiDto?
        get() = null
    val duration: Long
        get() = 0
    val userloved: Long
        get() = 0
}