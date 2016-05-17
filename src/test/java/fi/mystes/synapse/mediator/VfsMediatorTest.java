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

package fi.mystes.synapse.mediator;

import fi.mystes.synapse.mediator.vfs.VfsOperationDelegate;
import fi.mystes.synapse.mediator.vfs.VfsOperationOptions;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.jaxen.JaxenException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class VfsMediatorTest {

    private static final String COPY_OPERATION = "copy";
    private static final String MOVE_OPERATION = "move";
    private static final String SOURCE_DIRECTORY = "vfs:file:///tmp";
    private static final String TARGET_DIRECTORY = "vfs:file:///tmp/out";
    private static final String ARCHIVE_DIRECTORY = "vfs:file:///tmp/archive";
    private static final String FILE_PATTERN = ".txt";
    private static final boolean DEFAULT_CREATE_MISSING_DIRECTORIES = false;
    private static final boolean DEFAULT_LOCK_ENABLED = true;
    private static final boolean DEFAULT_FTP_PASSIVE_MODE_ENABLED = false;

    @Mock
    private SynapseXPath filePatternXpath;

    @Mock
    private SynapseXPath sourceDirectoryXpath;

    @Mock
    private SynapseXPath targetDirectoryXpath;

    @Mock
    private SynapseXPath archiveDirectoryXpath;

    @Mock
    private SynapseXPath operationXpath;

    @Mock
    private SynapseXPath createMissingDirectoriesXpath;

    @Mock
    private SynapseXPath lockEnabledXpath;

    @Mock
    private MessageContext mc;

    @Mock
    private VfsOperationDelegate operationDelegate;

    private VfsMediator mediator;

    @Mock
    private SOAPEnvelope envelope;

    @Mock
    private SOAPBody body;

    @Before
    public void setUp() throws JaxenException {
        MockitoAnnotations.initMocks(this);

        mediator = new VfsMediator();
        mediator.setDelegate(operationDelegate);
        when(mc.getEnvelope()).thenReturn(envelope);
        when(envelope.getBody()).thenReturn(body);

        when(operationXpath.evaluate(anyObject())).thenReturn(MOVE_OPERATION);
        when(sourceDirectoryXpath.evaluate(anyObject())).thenReturn(SOURCE_DIRECTORY);
        when(targetDirectoryXpath.evaluate(anyObject())).thenReturn(TARGET_DIRECTORY);
        when(filePatternXpath.evaluate(anyObject())).thenReturn(FILE_PATTERN);
        when(archiveDirectoryXpath.evaluate(anyObject())).thenReturn(ARCHIVE_DIRECTORY);
        when(createMissingDirectoriesXpath.evaluate(anyObject())).thenReturn(String.valueOf(DEFAULT_CREATE_MISSING_DIRECTORIES));
        when(lockEnabledXpath.evaluate(anyObject())).thenReturn(String.valueOf(DEFAULT_LOCK_ENABLED));

        mediator.setArchiveDirectoryXpath(archiveDirectoryXpath);
        mediator.setSourceDirectoryXpath(sourceDirectoryXpath);
        mediator.setTargetDirectoryXpath(targetDirectoryXpath);
        mediator.setOperationXPath(operationXpath);
        mediator.setFilePatternXpath(filePatternXpath);
        mediator.setCreateMissingDirectoriesXpath(createMissingDirectoriesXpath);
        mediator.setLockEnabledXpath(lockEnabledXpath);
    }

    @Test(expected = NullPointerException.class)
    public void mediationFailsIfMessageContextNull() {
        mediator.mediate(null);
    }

    @Test(expected = SynapseException.class)
    public void mediationFailsIfOperationNotSpecified() throws JaxenException {
        when(operationXpath.evaluate(anyObject())).thenReturn(null);
        mediator.mediate(mc);
    }

    @Test(expected = SynapseException.class)
    public void mediationFailsIfUnsupportedOperationSpecified() throws JaxenException {
        when(operationXpath.evaluate(anyObject())).thenReturn("blaa");
        mediator.mediate(mc);
    }

    @Test(expected = SynapseException.class)
    public void mediationFailsForCopyIfSourceDirectoryNotSpecified() throws JaxenException {
        when(sourceDirectoryXpath.evaluate(anyObject())).thenReturn(null);
        when(operationXpath.evaluate(anyObject())).thenReturn(COPY_OPERATION);
        mediator.mediate(mc);
    }

    @Test(expected = SynapseException.class)
    public void mediationFailsForCopyIfTargetDirectoryNotSpecified() throws JaxenException {
        when(targetDirectoryXpath.evaluate(anyObject())).thenReturn(null);
        when(operationXpath.evaluate(anyObject())).thenReturn(COPY_OPERATION);
        mediator.mediate(mc);
    }

    @Test
    public void mediationReturnsTrueForCopyIfValidConfigurationSpecified() throws JaxenException {
        when(operationXpath.evaluate(anyObject())).thenReturn(COPY_OPERATION);
        assertTrue(mediator.mediate(mc));
    }

    @Test
    public void mediationDelegatesCopyOperationCorrectlyWhenFilePatternNotSpecified() throws JaxenException, FileSystemException {
        when(operationXpath.evaluate(anyObject())).thenReturn(COPY_OPERATION);
        when(filePatternXpath.evaluate(anyObject())).thenReturn(null);
        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).copy(eq(defaultOptions().filePatternRegex(null).build()));
    }

    @Test
    public void mediationDelegatesCopyOperationCorrectlyWhenFilePatternSpecified() throws JaxenException, FileSystemException {
        when(operationXpath.evaluate(anyObject())).thenReturn(COPY_OPERATION);
        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).copy(eq(defaultOptions().build()));
    }

    @Test
    public void mediationSupportsMoveOperation() {
        assertTrue(mediator.mediate(mc));
    }

    @Test(expected = SynapseException.class)
    public void mediationFailsForMoveIfSourceDirectoryNotSpecified() throws JaxenException {
        when(sourceDirectoryXpath.evaluate(anyObject())).thenReturn(null);
        mediator.mediate(mc);
    }

    @Test(expected = SynapseException.class)
    public void mediationFailsForMoveIfTargetDirectoryNotSpecified() throws JaxenException {
        when(targetDirectoryXpath.evaluate(anyObject())).thenReturn(null);
        mediator.mediate(mc);
    }

    @Test
    public void mediationDelegatesMoveOperationCorrectlyWhenFilePatternNotSpecified() throws JaxenException, FileSystemException {
        when(filePatternXpath.evaluate(anyObject())).thenReturn(null);
        when(archiveDirectoryXpath.evaluate(anyObject())).thenReturn(null);
        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().archiveDirectory(null).filePatternRegex(null).build()));
    }

    @Test
    public void mediationDelegatesMoveOperationCorrectlyWhenFilePatternSpecified() throws JaxenException, FileSystemException {
        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().build()));
    }

    @Test
    public void mediationSetsProcessedFileCountPropertyWhenFileOperationSuccessful() throws JaxenException, FileSystemException {
        when(operationDelegate.move(any(VfsOperationOptions.class))).thenReturn(2);
        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().build()));
        verify(mc).setProperty("vfs.fileCount", 2);
    }

    @Test
    public void mediationDelegatesMoveOperationCorrectlyWhenFilePatternXpathSpecified() throws JaxenException, FileSystemException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement fileName = fac.createOMElement("FileName", "", "");
        fileName.setText(".*.xml");

        when(filePatternXpath.evaluate(anyObject())).thenReturn(fileName);
        mediator.setFilePatternXpath(filePatternXpath);
        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().filePatternRegex(".*.xml").build()));
    }

    @Test
    public void mediationDelegatesMoveOperationCorrectlyAndArchivesSourceFiles() throws JaxenException, FileSystemException {
        when(operationDelegate.move(any(VfsOperationOptions.class))).thenReturn(2);
        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().build()));
        verify(mc).setProperty("vfs.fileCount", 2);
    }

    @Test
    public void mediationDelegatesCopyOperationCorrectlyWhenValuesUsed() throws JaxenException, FileSystemException {
        resetXpaths();
        mediator.setFilePatternValue(FILE_PATTERN);
        mediator.setOperationValue(COPY_OPERATION);
        mediator.setSourceDirectoryValue(SOURCE_DIRECTORY);
        mediator.setTargetDirectoryValue(TARGET_DIRECTORY);
        mediator.setArchiveDirectoryValue(ARCHIVE_DIRECTORY);
        mediator.setCreateMissingDirectoriesValue(true);
        mediator.setLockEnabledValue(false);

        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).copy(eq(defaultOptions().createMissingDirectories(true).lockEnabled(false).build()));
    }

    @Test
    public void mediationDelegatesMoveOperationCorrectlyWhenValuesUsed() throws JaxenException, FileSystemException {
        resetXpaths();
        mediator.setFilePatternValue(FILE_PATTERN);
        mediator.setOperationValue(MOVE_OPERATION);
        mediator.setSourceDirectoryValue(SOURCE_DIRECTORY);
        mediator.setTargetDirectoryValue(TARGET_DIRECTORY);
        mediator.setArchiveDirectoryValue(ARCHIVE_DIRECTORY);
        mediator.setCreateMissingDirectoriesValue(true);
        mediator.setLockEnabledValue(false);

        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().createMissingDirectories(true).lockEnabled(false).build()));
    }

    @Test
    public void mediationDelegatesWithCorrectDefaultValueWhenCreateMissingDirectoriesFlagNotSpecified() throws JaxenException, FileSystemException {
        when(createMissingDirectoriesXpath.evaluate(anyObject())).thenReturn(null);

        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().build()));
    }

    @Test
    public void mediationDelegatesWithCorrectValueWhenCreateMissingDirectoriesFlagSpecified() throws JaxenException, FileSystemException {
        when(createMissingDirectoriesXpath.evaluate(anyObject())).thenReturn(String.valueOf("true"));

        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().createMissingDirectories(true).build()));
    }

    @Test
    public void mediationDelegatesWithCorrectDefaultValueWhenLockEnabledFlagNotSpecified() throws JaxenException, FileSystemException {
        when(lockEnabledXpath.evaluate(anyObject())).thenReturn(null);

        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().lockEnabled(true).build()));
    }

    @Test
    public void mediationDelegatesWithCorrectValueWhenLockEnabledFlagSpecified() throws JaxenException, FileSystemException {
        when(lockEnabledXpath.evaluate(anyObject())).thenReturn(String.valueOf("false"));

        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().lockEnabled(false).build()));
    }

    @Test
    public void mediationDeleagtesWithCorrectValueWhenFtpPassiveModeNotEnabled() throws FileSystemException {
        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().ftpPassiveModeEnabled(false).build()));
    }

    @Test
    public void mediationDeleagtesWithCorrectValueWhenFtpPassiveModeEnabled() throws FileSystemException {
        when(mc.getProperty(VfsMediator.FTP_PASSIVE_MODE_PROPERTY_NAME)).thenReturn("true");
        assertTrue(mediator.mediate(mc));
        verify(operationDelegate).move(eq(defaultOptions().ftpPassiveModeEnabled(true).build()));
    }

    private void resetXpaths() {
        mediator.setArchiveDirectoryXpath(null);
        mediator.setSourceDirectoryXpath(null);
        mediator.setTargetDirectoryXpath(null);
        mediator.setOperationXPath(null);
        mediator.setFilePatternXpath(null);
        mediator.setCreateMissingDirectoriesXpath(null);
        mediator.setLockEnabledXpath(null);
    }

    private VfsOperationOptions.Builder defaultOptions() {
        return VfsOperationOptions.with().sourceDirectory(SOURCE_DIRECTORY).targetDirectory(TARGET_DIRECTORY).filePatternRegex(FILE_PATTERN).archiveDirectory(ARCHIVE_DIRECTORY).createMissingDirectories(DEFAULT_CREATE_MISSING_DIRECTORIES).lockEnabled(DEFAULT_LOCK_ENABLED).ftpPassiveModeEnabled(DEFAULT_FTP_PASSIVE_MODE_ENABLED);
    }

}
