// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.common

/** Outcome of a repository read. */
sealed interface DataResult<out T> {
    /** @param stale served from cache because the network was unreachable. */
    data class Success<T>(val data: T, val stale: Boolean = false) : DataResult<T>

    data class Failure(val error: LoadError) : DataResult<Nothing>
}

/** Why a read failed, as a type; turning it into words is the UI's job. */
enum class LoadError {
    /** No usable network and nothing cached to fall back on. */
    OFFLINE,

    /** Reached the host, but it did not return a usable response. */
    SERVER,

    /** Got a response we could not parse. */
    MALFORMED,
}
