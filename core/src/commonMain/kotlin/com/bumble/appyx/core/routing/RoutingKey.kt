package com.bumble.appyx.core.routing

import com.bumble.appyx.CommonParcelize
import com.bumble.appyx.CommonRawValue
import com.bumble.appyx.CommonParcelable
import androidx.compose.runtime.Immutable
import java.util.UUID

@CommonParcelize
@Immutable
class RoutingKey<Routing> private constructor(
    val routing: @CommonRawValue Routing,
    val id: String
) : CommonParcelable {

    constructor(routing: @CommonRawValue Routing) : this(
        routing = routing,
        id = UUID.randomUUID().toString()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoutingKey<*>

        if (routing != other.routing) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = routing?.hashCode() ?: 0
        result = 31 * result + id.hashCode()
        return result
    }

}
