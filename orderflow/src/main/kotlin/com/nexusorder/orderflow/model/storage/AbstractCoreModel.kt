package com.nexusorder.orderflow.model.storage

import com.nexusorder.orderflow.util.DataTimeUtil
import com.nexusorder.orderflow.util.UUIDUtil

abstract class AbstractCoreModel(
    open var id: String = UUIDUtil.generateUuid(),
    open var version: Long = 0L,
    open var createdAt: String = DataTimeUtil.getCurrentDatetime(),
    open var updatedAt: String = DataTimeUtil.getCurrentDatetime()
)
