/**
 * Copyright 2016 Mystes Oy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.mystes.synapse.mediator.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.synapse.SynapseException;
import org.apache.synapse.config.xml.XMLConfigConstants;
import org.jaxen.JaxenException;
import org.junit.Test;

import fi.mystes.synapse.mediator.VfsMediator;

import static org.junit.Assert.*;

public class VfsMediatorFactoryTest {

    private static String TARGET_DIRECTORY_RESULT = "tmp://blaa";
    private static String SOURCE_DIRECTORY_RESULT = "tmp://bar";

    private static String SYNAPSE_NAMESPACE = XMLConfigConstants.SYNAPSE_NAMESPACE;

    private static VfsMediatorFactory factory = new VfsMediatorFactory();
    private final AXIOMXPath vfsMediator;

    public VfsMediatorFactoryTest() throws FileNotFoundException, XMLStreamException, JaxenException {
        vfsMediator = new AXIOMXPath("//syn:vfs");
        vfsMediator.addNamespace("syn", SYNAPSE_NAMESPACE);
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereOperationCopyAndFilePatternMissing()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorOperationCopyFilePatternMissingVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("Target directory is not what it should be", TARGET_DIRECTORY_RESULT, m.getTargetDirectoryValue());
        assertEquals("Source directory is not what it should be", SOURCE_DIRECTORY_RESULT, m.getSourceDirectoryValue());
        assertEquals("Wrong operation", "copy", m.getOperationValue());
        assertNull("File pattern should be null", m.getFilePatternValue());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereOperationCopyAndFilePatternExists()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorOperationCopyFilePatternExistsVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("Target directory is not what it should be", TARGET_DIRECTORY_RESULT, m.getTargetDirectoryValue());
        assertEquals("Source directory is not what it should be", SOURCE_DIRECTORY_RESULT, m.getSourceDirectoryValue());
        assertEquals("Wrong operation", "copy", m.getOperationValue());
        assertEquals("File pattern should not be null", ".*.xml", m.getFilePatternValue());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereOperationMoveAndFilePatternIsExpression()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorOperationMoveFilePatternExpressionVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("Target directory is not what it should be", TARGET_DIRECTORY_RESULT, m.getTargetDirectoryValue());
        assertEquals("Source directory is not what it should be", SOURCE_DIRECTORY_RESULT, m.getSourceDirectoryValue());
        assertEquals("Wrong operation", "move", m.getOperationValue());
        assertEquals("File pattern should not be null", "//FileName", m.getFilePatternXpath().toString());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereOperationIsExpression()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorOperationExpressionVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("Operation should not be null", "//operation", m.getOperationXPath().toString());
    }

    @Test
    public void testCreateMediatorFromInvalidXMLWhereSourceDirectoryMissing()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorSourceDirectoryMissingINVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        try {
            VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
            fail("moving should've failed");
        } catch (SynapseException expected) {
            assertEquals(expectedError("sourceDirectory"), expected.getMessage());
        }
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereSourceDirectoryIsExpression()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorSourceDirectoryExpressionVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("Source directory should not be null", "//sourceDirectory",
                m.getSourceDirectoryXPath().toString());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereTargetDirectoryIsExpression()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorTargetDirectoryExpressionVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("Target directory should not be null", "//targetDirectory",
                m.getTargetDirectoryXPath().toString());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereArchiveDirectoryIsExpression()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorArchiveDirectoryExpressionVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("Archive directory should not be null", "//archiveDirectory",
                m.getArchiveDirectoryXpath().toString());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereCreateMissingDirectoriesIsValue()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorCreateMissingDirectoriesValueVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("CreateMissingDirectories should not be null", true, m.getCreateMissingDirectoriesValue());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereCreateMissingDirectoriesIsExpression()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorCreateMissingDirectoriesExpressionVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("CreateMissingDirectories should not be null", "//dynamicFolder",
                m.getCreateMissingDirectoriesXpath().toString());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereLockEnabledIsValue()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorLockEnabledValueVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertFalse(m.getLockEnabledValue());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereLockEnabledIsExpression()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorLockEnabledExpressionVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("LockEnabled XPath should not be null", "//dynamicLockEnabled",
                m.getLockEnabledXpath().toString());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereStreamingTransferEnabledIsValue()
        throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorStreamingTransferEnabledValueVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertTrue(m.getStreamingTransferValue());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereStreamingTransferEnabledIsExpression()
        throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorStreamingTransferEnabledExpressionVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("StreamingEnabled XPath should not be null", "//streamingTransferEnabled/text()",
            m.getStreamingTransferXpath().toString());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereStreamingTransferEnabledWithBlockSizeIsValue()
        throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorStreamingTransferEnabledWithBlockSizeValueVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("StreamingBlockSize should be the same as in xml configuration","1024", m.getStreamingBlockSizeValue());
    }

    @Test
    public void testCreateMediatorFromValidXMLWhereStreamingTransferEnabledithBlockSizeIsExpression()
        throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorStreamingTransferEnabledWithBlockSizeExpressionVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
        assertNotNull(m);
        assertEquals("StreamingBlockSize XPath should not be null", "//streamingBlockSize/text()",
            m.getStreamingBlockSizeXpath().toString());
    }

    @Test
    public void testCreateMediatorFromInvalidXMLWhereTargetDirectoryMissing()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorTargetDirectoryMissingINVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        try {
            VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
            fail("moving should've failed");
        } catch (SynapseException expected) {
            assertEquals(expectedError("targetDirectory"), expected.getMessage());
        }
    }

    @Test
    public void testCreateMediatorFromInvalidXMLWhereOperationMissing()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        OMElement proxy = getDocumentElementFromResourcePath("/vfsMediatorOperationMissingINVALID.xml");
        List<OMElement> vfsMediatorConfigs = (List<OMElement>) vfsMediator.evaluate(proxy);
        try {
            VfsMediator m = (VfsMediator) factory.createMediator(vfsMediatorConfigs.get(0), null);
            fail("moving should've failed");
        } catch (SynapseException expected) {
            assertEquals(expectedError("operation"), expected.getMessage());
        }
    }

    private static OMElement getDocumentElementFromResourcePath(String path)
            throws FileNotFoundException, XMLStreamException {
        return new StAXOMBuilder(
                new FileInputStream(new File(VfsMediatorFactoryTest.class.getResource(path).getPath())))
                        .getDocumentElement();
    }

    private String expectedError(String field) {
        return MessageFormat.format("The [{0}] element is required by the VfsMediator.", field);
    }
}
