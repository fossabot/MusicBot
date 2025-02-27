package net.bjoernpetersen.musicbot.api.module

import com.google.inject.AbstractModule
import net.bjoernpetersen.musicbot.spi.util.FileStorage
import kotlin.reflect.KClass

class FileStorageModule(private val type: Class<out FileStorage>) : AbstractModule() {
    constructor(type: KClass<out FileStorage>) : this(type.java)

    override fun configure() {
        bind(FileStorage::class.java).to(type)
    }
}
