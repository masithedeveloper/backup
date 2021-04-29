package com.money.randing.data.db.converter

import androidx.room.TypeConverter
import com.money.randing.data.model.MovementTypeDto

class MovementTypeConverter {

    @TypeConverter
    fun toMovementType(value: Int): MovementTypeDto {
        return MovementTypeDto(value)
    }

    @TypeConverter
    fun fromMovementType(value: MovementTypeDto): Int {
        return value.id
    }
}