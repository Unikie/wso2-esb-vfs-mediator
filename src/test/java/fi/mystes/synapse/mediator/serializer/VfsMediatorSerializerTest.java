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
package fi.mystes.synapse.mediator.serializer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.jaxen.JaxenException;
import org.junit.Test;

import fi.mystes.synapse.mediator.VfsMediator;

public class VfsMediatorSerializerTest {

    @Test
    public void serializeWithStringFilePattern() throws FileNotFoundException, XMLStreamException, JaxenException {
        VfsMediatorSerializer serializer = getDefaultSerializer();
        VfsMediator mediator = getDefaultMediator();
        mediator.setFilePatternValue(".*.xml");
        OMElement element = serializer.serializeSpecificMediator(mediator);
        OMElement expected = getDocumentElementFromResourcePath("/serializedVfsMediatorWithFilePatternValue.xml");
        assertEquals(expected.toString(), element.toString());
    }

    @Test
    public void serializeWithXPathFilePattern() throws FileNotFoundException, XMLStreamException, JaxenException {
        VfsMediatorSerializer serializer = getDefaultSerializer();
        VfsMediator mediator = getDefaultMediator();
        mediator.setFilePatternXpath(new SynapseXPath("//FileName"));
        OMElement element = serializer.serializeSpecificMediator(mediator);
        OMElement expected = getDocumentElementFromResourcePath("/serializedVfsMediatorWithFilePatternExpression.xml");
        assertEquals(expected.toString(), element.toString());
    }

    @Test
    public void serializeWithoutFilePattern() throws FileNotFoundException, XMLStreamException, JaxenException {
        VfsMediatorSerializer serializer = getDefaultSerializer();
        VfsMediator mediator = getDefaultMediator();
        OMElement element = serializer.serializeSpecificMediator(mediator);
        OMElement expected = getDocumentElementFromResourcePath("/serializedVfsMediatorWithoutFilePattern.xml");
        assertEquals(expected.toString(), element.toString());
    }

    @Test
    public void serializeWithoutCreateMissingDirectories()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        VfsMediatorSerializer serializer = getDefaultSerializer();
        VfsMediator mediator = getDefaultMediator();
        OMElement element = serializer.serializeSpecificMediator(mediator);
        OMElement expected = getDocumentElementFromResourcePath(
                "/serializedVfsMediatorWithoutCreateMissingDirectories.xml");
        assertEquals(expected.toString(), element.toString());
    }

    @Test
    public void serializeWithCreateMissingDirectories()
            throws FileNotFoundException, XMLStreamException, JaxenException {
        VfsMediatorSerializer serializer = getDefaultSerializer();
        VfsMediator mediator = getDefaultMediator();
        mediator.setCreateMissingDirectoriesValue(true);
        OMElement element = serializer.serializeSpecificMediator(mediator);
        OMElement expected = getDocumentElementFromResourcePath(
                "/serializedVfsMediatorWithCreateMissingDirectories.xml");
        assertEquals(expected.toString(), element.toString());
    }

    @Test
    public void serializeWithoutLockEnabled() throws FileNotFoundException, XMLStreamException, JaxenException {
        VfsMediatorSerializer serializer = getDefaultSerializer();
        VfsMediator mediator = getDefaultMediator();
        OMElement element = serializer.serializeSpecificMediator(mediator);
        OMElement expected = getDocumentElementFromResourcePath("/serializedVfsMediatorWithoutLockEnabled.xml");
        assertEquals(expected.toString(), element.toString());
    }

    @Test
    public void serializeWithLockEnabledDirectories() throws FileNotFoundException, XMLStreamException, JaxenException {
        VfsMediatorSerializer serializer = getDefaultSerializer();
        VfsMediator mediator = getDefaultMediator();
        mediator.setLockEnabledValue(false);
        OMElement element = serializer.serializeSpecificMediator(mediator);
        OMElement expected = getDocumentElementFromResourcePath("/serializedVfsMediatorWithLockEnabled.xml");
        assertEquals(expected.toString(), element.toString());
    }

    private VfsMediator getDefaultMediator() {
        VfsMediator mediator = new VfsMediator();
        mediator.setOperationValue("copy");
        mediator.setSourceDirectoryValue("tmp://bar");
        mediator.setTargetDirectoryValue("tmp://blaa");
        return mediator;
    }

    private VfsMediatorSerializer getDefaultSerializer() {
        return new VfsMediatorSerializer();
    }

    private static OMElement getDocumentElementFromResourcePath(String path)
            throws FileNotFoundException, XMLStreamException {
        return new StAXOMBuilder(
                new FileInputStream(new File(VfsMediatorSerializerTest.class.getResource(path).getPath())))
                        .getDocumentElement();
    }
}
