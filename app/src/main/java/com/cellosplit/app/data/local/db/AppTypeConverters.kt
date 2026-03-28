package com.cellosplit.app.data.local.db

import androidx.room.TypeConverter

/**
 * Room TypeConverters for types that Room cannot store natively.
 *
 * Currently we only need converters for nullable primitives and the
 * split-mode String — Room handles Long, Boolean, String natively.
 * Custom share maps are already stored as JSON Strings in the entity,
 * so no converter is needed for those.
 */
class AppTypeConverters {

    // Room stores Boolean as Int (0/1) natively in newer versions,
    // but explicit converters ensure compatibility across API levels.
    @TypeConverter
    fun fromBoolean(value: Boolean): Int = if (value) 1 else 0

    @TypeConverter
    fun toBoolean(value: Int): Boolean = value != 0
}
