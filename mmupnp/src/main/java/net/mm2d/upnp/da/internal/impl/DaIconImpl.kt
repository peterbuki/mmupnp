/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.upnp.da.internal.impl

import net.mm2d.upnp.common.internal.property.IconProperty
import net.mm2d.upnp.common.util.appendNewElement
import net.mm2d.upnp.da.DaIcon
import org.w3c.dom.Element

class DaIconImpl(
    property: IconProperty
) : DaIcon, DescriptionAppendable {
    override val mimeType: String = property.mimeType
    override val width: Int = property.width
    override val height: Int = property.height
    override val depth: Int = property.depth
    override val url: String = property.url
    override val binary: ByteArray? = null

    override fun appendDescriptionTo(parent: Element) {
        parent.appendNewElement("icon").apply {
            appendNewElement("mimeType", mimeType)
            appendNewElement("width", width.toString())
            appendNewElement("height", height.toString())
            appendNewElement("depth", depth.toString())
            appendNewElement("url", url)
        }
    }

    companion object {
        fun create(property: IconProperty): DaIconImpl =
            DaIconImpl(property)
    }
}