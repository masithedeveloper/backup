package com.money.randing.domain.repository

import com.money.randing.error.Failure
import com.money.randing.util.Either
import com.money.randing.util.left

abstract class BaseRepository {

    protected suspend fun <T> runCatching(
        block: suspend () -> Either<Failure, T>
    ): Either<Failure, T> {
        return try {
            block()
        } catch (e: Exception) {
            left(Failure.UnexpectedFailure)
        }
    }
}