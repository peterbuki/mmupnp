/*
 * Copyright (c) 2016 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.upnp.util

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * Provide XML utility methods.
 *
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
object XmlUtils {
    private val documentBuilder: DocumentBuilder by lazy {
        newDocumentBuilder(false)
    }
    private val documentBuilderNs: DocumentBuilder by lazy {
        newDocumentBuilder(true)
    }

    @Throws(ParserConfigurationException::class)
    private fun newDocumentBuilder(awareness: Boolean): DocumentBuilder {
        return DocumentBuilderFactory.newInstance().also {
            it.isNamespaceAware = awareness
        }.newDocumentBuilder()
    }

    @Throws(ParserConfigurationException::class)
    private fun getDocumentBuilder(awareness: Boolean): DocumentBuilder {
        return if (awareness) documentBuilderNs else documentBuilder
    }

    /**
     * Create an empty Document.
     *
     * @param awareness if true, XML namespace aware
     * @return Document
     * @throws ParserConfigurationException If there is a problem with instantiation
     */
    @Throws(ParserConfigurationException::class)
    @JvmStatic
    fun newDocument(awareness: Boolean): Document {
        return getDocumentBuilder(awareness).newDocument()
    }

    /**
     * Create new Document that contains the argument string.
     *
     * @param awareness if true, XML namespace aware
     * @param xml       XML string
     * @return Document
     * @throws SAXException                 if an parse error occurs.
     * @throws IOException                  if an I/O error occurs.
     * @throws ParserConfigurationException If there is a problem with instantiation
     */
    @Throws(SAXException::class, IOException::class, ParserConfigurationException::class)
    @JvmStatic
    fun newDocument(awareness: Boolean, xml: String): Document {
        val reader = StringReader(xml)
        return getDocumentBuilder(awareness).parse(InputSource(reader))
    }

    /**
     * Returns the first element node with a specific name below the parent node.
     *
     * @param parent    parent node
     * @param localName local name to search
     * @return Element node found, null if not found
     */
    @JvmStatic
    fun findChildElementByLocalName(parent: Node, localName: String): Element? {
        parent.firstChild?.forEachElement {
            if (localName == it.localName) {
                return it
            }
        }
        return null
    }
}

/**
 * Iterate over all sibling nodes of the specified node.
 *
 * @receiver Node performing iteration
 * @param action Action to be performed on each element
 */
inline fun Node.forEachElement(action: (Element) -> Unit) {
    var node: Node? = this
    while (node != null) {
        if (node is Element) {
            action(node)
        }
        node = node.nextSibling
    }
}

/**
 * Search for a node with the specified local name among the child nodes of the receiver node.
 *
 * @receiver Search target node
 * @param localName local name
 */
fun Node.findChildElementByLocalName(localName: String): Element? {
    firstChild?.forEachElement {
        if (localName == it.localName) {
            return it
        }
    }
    return null
}

/**
 * Iterate the node list.
 *
 * @receiver node list
 * @param action Action to be performed on each node
 */
inline fun NodeList.forEach(action: (Node) -> Unit) {
    for (i in 0 until length) action(item(i))
}