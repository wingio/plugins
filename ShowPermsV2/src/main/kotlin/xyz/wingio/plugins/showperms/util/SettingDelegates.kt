package xyz.wingio.plugins.showperms.util

import com.aliucord.api.SettingsAPI
import kotlin.reflect.KProperty

class Setting<T>(
    private val getter: (String) -> T,
    private val setter: (String, T) -> Unit,
    private val key: String? = null
) {

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: T) {
        setter(key ?: prop.name, value)
    }

    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        return getter(key ?: prop.name)
    }

}

fun SettingsAPI.boolean(default: Boolean, key: String? = null) = Setting(
    getter = { k -> getBool(k, default) },
    setter = { k, v -> setBool(k, v) },
    key = key
)

inline fun <reified E: Enum<E>> SettingsAPI.enum(default: E, key: String? = null) = Setting(
    getter = { k -> enumValues<E>()[getInt(k, default.ordinal)] },
    setter = { k, v -> setInt(k, v.ordinal) },
    key = key
)