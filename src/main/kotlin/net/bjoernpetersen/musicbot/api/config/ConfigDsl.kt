package net.bjoernpetersen.musicbot.api.config

import net.bjoernpetersen.musicbot.spi.config.ConfigChecker
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Experimental
annotation class ExperimentalConfigDsl

@ExperimentalConfigDsl
class StringConfiguration(val key: String) {

    /**
     * A description of what the entry does.
     */
    lateinit var description: String

    private lateinit var configChecker: ConfigChecker<in String>
    /**
     * A visual representation of the config entry, not needed for state entries.
     */
    var uiNode: UiNode<in String>? = null

    private var default: String? = null

    /**
     * Set a default value, optional.
     */
    fun default(default: String) {
        this.default = default
    }

    /**
     * Set a validator for entry values.
     */
    fun check(configChecker: ConfigChecker<in String>) {
        this.configChecker = configChecker
    }

    internal fun toEntry(config: Config): Config.StringEntry {
        if (!::description.isInitialized)
            throw IllegalStateException("description is not set")
        if (!::configChecker.isInitialized)
            throw IllegalStateException("configChecker is not set")

        return config.StringEntry(
            key = key,
            description = description,
            configChecker = configChecker,
            uiNode = uiNode,
            default = default
        )
    }
}

/**
 * Creates a String config entry.
 * @param key the unique (in scope) entry key
 */
@ExperimentalConfigDsl
fun Config.string(key: String, configure: StringConfiguration.() -> Unit): Config.StringEntry {
    val config = StringConfiguration(key)
    config.configure()
    return config.toEntry(this)
}

@ExperimentalConfigDsl
class StringDelegate(
    private val config: Config,
    private val configure: StringConfiguration.() -> Unit
) : ReadOnlyProperty<Any?, Config.StringEntry> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Config.StringEntry {
        val key = property.name
        val config = StringConfiguration(key)
        config.configure()
        return config.toEntry(this.config)
    }
}

/**
 * Delegate to create a String config entry.
 */
@ExperimentalConfigDsl
fun Config.string(
    configure: StringConfiguration.() -> Unit
): StringDelegate = StringDelegate(this, configure)

@ExperimentalConfigDsl
class BooleanConfiguration(val key: String) {
    /**
     * A description of what the entry does.
     */
    lateinit var description: String
    private var defaultSet = false
    /**
     * A default value.
     */
    var default: Boolean = false
        get() = if (defaultSet) field else throw IllegalStateException()
        set(value) {
            defaultSet = true
            field = value
        }

    internal fun toEntry(config: Config): Config.BooleanEntry {
        if (!::description.isInitialized)
            throw IllegalStateException("description is not set")
        if (!defaultSet)
            throw IllegalStateException("default not set")

        return config.BooleanEntry(
            key = this.key,
            description = description,
            default = default
        )
    }
}

/**
 * Creates a boolean config entry.
 * @param key the unique (in scope) entry key
 */
@ExperimentalConfigDsl
fun Config.boolean(key: String, configure: BooleanConfiguration.() -> Unit): Config.BooleanEntry {
    val config = BooleanConfiguration(key)
    config.configure()
    return config.toEntry(this)
}

@ExperimentalConfigDsl
class BooleanDelegate(
    private val config: Config,
    private val configure: BooleanConfiguration.() -> Unit
) : ReadOnlyProperty<Any?, Config.BooleanEntry> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Config.BooleanEntry {
        val key = property.name
        val config = BooleanConfiguration(key)
        config.configure()
        return config.toEntry(this.config)
    }
}

/**
 * Delegate to create a boolean config entry.
 */
@ExperimentalConfigDsl
fun Config.boolean(
    configure: BooleanConfiguration.() -> Unit
): BooleanDelegate = BooleanDelegate(this, configure)

@ExperimentalConfigDsl
class SerializationConfiguration<T> {
    private lateinit var serializer: (T) -> String
    private lateinit var deserializer: (String) -> T

    fun serialize(action: (T) -> String) {
        this.serializer = action
    }

    fun deserialize(action: (String) -> T) {
        this.deserializer = action
    }

    internal fun toSerializer(): ConfigSerializer<T> {
        val exceptionMessage = when {
            !::serializer.isInitialized -> "serializer is not set"
            !::deserializer.isInitialized -> "deserializer not set"
            else -> null
        }
        if (exceptionMessage != null) throw IllegalStateException(exceptionMessage)

        return object : ConfigSerializer<T> {
            override fun serialize(obj: T): String {
                return serializer(obj)
            }

            override fun deserialize(string: String): T {
                return deserialize(string)
            }
        }
    }
}

/**
 * Configure config serialization using a DSL.
 */
@ExperimentalConfigDsl
fun <T> serialization(configure: SerializationConfiguration<T>.() -> Unit): ConfigSerializer<T> {
    val config = SerializationConfiguration<T>()
    config.configure()
    return config.toSerializer()
}

/**
 * Configure and use config serialization using a DSL.
 */
@ExperimentalConfigDsl
fun <T> SerializedConfiguration<T>.serialization(
    configure: SerializationConfiguration<T>.() -> Unit
) {
    val config = SerializationConfiguration<T>()
    config.configure()
    serializer = config.toSerializer()
}

@ExperimentalConfigDsl
class SerializedConfiguration<T>(val key: String) {
    /**
     * A description of what the entry does.
     */
    lateinit var description: String
    /**
     * A config serializer to convert from/to string.
     */
    lateinit var serializer: ConfigSerializer<T>
    private lateinit var configChecker: ConfigChecker<in T>
    /**
     * A visual representation of the config entry, not needed for state.
     */
    var uiNode: UiNode<in T>? = null
    private var default: T? = null

    /**
     * Set a default value, optional.
     */
    fun default(default: T) {
        this.default = default
    }

    /**
     * Set a validator for entry values.
     */
    fun check(configChecker: ConfigChecker<in T>) {
        this.configChecker = configChecker
    }

    internal fun toEntry(config: Config): Config.SerializedEntry<T> {
        val exceptionMessage = when {
            !::description.isInitialized -> "description is not set"
            !::serializer.isInitialized -> "serializer is not set"
            !::configChecker.isInitialized -> "configChecker is not set"
            else -> null
        }
        if (exceptionMessage != null) throw IllegalStateException(exceptionMessage)

        return config.SerializedEntry(
            key = this.key,
            description = description,
            serializer = serializer,
            configChecker = configChecker,
            uiNode = uiNode,
            default = default
        )
    }
}

/**
 * Creates a config entry accepting a non-string type.
 *
 * @param key the unique (in scope) entry key
 */
@ExperimentalConfigDsl
fun <T> Config.serialized(
    key: String,
    configure: SerializedConfiguration<T>.() -> Unit
): Config.SerializedEntry<T> {
    val config = SerializedConfiguration<T>(key)
    config.configure()
    return config.toEntry(this)
}

@ExperimentalConfigDsl
class SerializedDelegate<T>(
    private val config: Config,
    private val configure: SerializedConfiguration<T>.() -> Unit
) : ReadOnlyProperty<Any?, Config.SerializedEntry<T>> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Config.SerializedEntry<T> {
        val key = property.name
        val config = SerializedConfiguration<T>(key)
        config.configure()
        return config.toEntry(this.config)
    }
}

/**
 * Delegate to create a config entry accepting a non-string type.
 */
@ExperimentalConfigDsl
fun <T> Config.serialized(
    configure: SerializedConfiguration<T>.() -> Unit
): SerializedDelegate<T> = SerializedDelegate(this, configure)
