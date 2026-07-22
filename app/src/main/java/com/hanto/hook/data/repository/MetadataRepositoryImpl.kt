package com.hanto.hook.data.repository

import com.hanto.hook.data.remote.MetadataDataSource
import com.hanto.hook.domain.repository.MetadataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetadataRepositoryImpl @Inject constructor(
    private val metadataDataSource: MetadataDataSource
) : MetadataRepository {

    override suspend fun fetchOgImageUrl(url: String?): String? =
        metadataDataSource.fetchOgImageUrl(url)
}
