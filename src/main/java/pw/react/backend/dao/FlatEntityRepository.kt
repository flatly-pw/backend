package pw.react.backend.dao

import org.springframework.data.repository.PagingAndSortingRepository
import pw.react.backend.models.entity.FlatEntity

interface FlatEntityRepository : PagingAndSortingRepository<FlatEntity, String>
