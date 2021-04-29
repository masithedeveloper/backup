/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package za.co.absa.networking

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.NullNode

object ResponseHelper {
    fun removeNullsFromJsonNode(jsonNode: JsonNode?) {
        if (jsonNode != null) {
            val iterator = jsonNode.fieldNames()
            while (iterator.hasNext()) {
                val key = iterator.next()
                val node: Any = jsonNode[key]
                if (node is NullNode) {
                    iterator.remove()
                } else {
                    removeNullsFrom(node)
                }
            }
        }
    }

    private fun removeNullsFromArrayNode(arrayNode: ArrayNode) {
        val arrayNodeIterator = arrayNode.iterator()
        while (arrayNodeIterator.hasNext()) {
            val arrayNodeIteratorItem = arrayNodeIterator.next()
            if (arrayNodeIteratorItem is NullNode) {
                arrayNodeIterator.remove()
            } else {
                removeNullsFrom(arrayNodeIteratorItem)
            }
        }
    }

    private fun removeNullsFrom(node: Any) {
        if (node is ArrayNode) {
            removeNullsFromArrayNode(node)
        } else if (node is JsonNode) {
            removeNullsFromJsonNode(node)
        }
    }
}