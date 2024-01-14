package pw.react.backend.dao.specification

import au.com.console.jpaspecificationdsl.equal
import pw.react.backend.models.entity.FlatEntity

object FlatEntitySpecification {

    fun hasBedrooms(bedrooms: Int) = FlatEntity::bedrooms.equal(bedrooms)

    fun hasBathrooms(bathrooms: Int) = FlatEntity::bathrooms.equal(bathrooms)

    fun hasCapacity(capacity: Int) = FlatEntity::capacity.equal(capacity)
}
