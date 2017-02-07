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

import java.text.MessageFormat;
import java.util.List;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.jaxen.JaxenException;

import fi.mystes.synapse.mediator.vfs.DefaultVfsOperationDelegate;
import fi.mystes.synapse.mediator.vfs.VfsCopy;
import fi.mystes.synapse.mediator.vfs.VfsMove;
import fi.mystes.synapse.mediator.vfs.VfsOperation;
import fi.mystes.synapse.mediator.vfs.VfsOperationDelegate;

/**
 * Virtual File System (VFS) mediator class for file copying and moving.
 * 
 */
public class VfsMediator extends AbstractMediator {
    private static final boolean DEFAULT_LOCK_ENABLED = true;

    private String operationValue;
    private String sourceDirectoryValue;
    private String targetDirectoryValue;
    private String archiveDirectoryValue;
    private String filePatternValue;
    private boolean createMissingDirectoriesValue;
    private Boolean lockEnabledValue;
    private boolean streamingTransferValue;
    private String streamingBlockSizeValue;
    private int retryCount;
    private int retryWait;

    private SynapseXPath filePatternXpath;
    private SynapseXPath operationXpath;
    private SynapseXPath sourceDirectoryXpath;
    private SynapseXPath targetDirectoryXpath;
    private SynapseXPath archiveDirectoryXpath;
    private SynapseXPath createMissingDirectoriesXpath;
    private SynapseXPath lockEnabledXpath;
    private SynapseXPath streamingTransferXpath;
    private SynapseXPath streamingBlockSizeXpath;
    private final static String FILE_COUNT_PROPERTY_NAME = "vfs.fileCount";
    static final String FTP_PASSIVE_MODE_PROPERTY_NAME = "vfs.ftp.passiveMode";

    private VfsOperationDelegate delegate = new DefaultVfsOperationDelegate();

    /**
     * Inherited method that will be invoked by passing the current message for
     * mediation. Each mediator performs its mediation action, and returns true
     * if mediation should continue, or false if further mediation should be
     * aborted.
     *
     * @param messageContext
     *            Current message context for mediation
     * @return true if further mediation should continue, otherwise false
     */
    @Override
    public boolean mediate(MessageContext messageContext) {
        if (messageContext == null) {
            throw new NullPointerException("Refusing to mediate with null message context");
        }

        VfsOperation op = initOperation(messageContext);
        op.setOperationDelegate(delegate);

        try {
            int processedFileCount = op.execute();
            messageContext.setProperty(FILE_COUNT_PROPERTY_NAME, processedFileCount);
        } catch (FileSystemException e) {
            throw new SynapseException(e);
        }

        return true;
    }

    /**
     * Setter for VFS operation delegate.
     * 
     * @param delegate
     *            VFS operation delegate
     */
    public void setDelegate(VfsOperationDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Getter for target directory path.
     * 
     * @return Target directory path
     */
    public String getTargetDirectoryValue() {
        return targetDirectoryValue;
    }

    /**
     * Setter for target directory path.
     * 
     * @param targetDirectoryValue
     *            Target directory path
     */
    public void setTargetDirectoryValue(String targetDirectoryValue) {
        this.targetDirectoryValue = targetDirectoryValue;
    }

    /**
     * Getter for operation.
     * 
     * @return Operation (copy/move/other)
     */
    public String getOperationValue() {
        return operationValue;
    }

    /**
     * Setter for operation.
     * 
     * @param operationValue
     *            Operation (copy/move/other)
     */
    public void setOperationValue(String operationValue) {
        this.operationValue = operationValue;
    }

    /**
     * Getter for source directory path.
     * 
     * @return Source directory path
     */
    public String getSourceDirectoryValue() {
        return sourceDirectoryValue;
    }

    /**
     * Setter for source directory path.
     * 
     * @param sourceDirectoryValue
     *            Source directory path
     */
    public void setSourceDirectoryValue(String sourceDirectoryValue) {
        this.sourceDirectoryValue = sourceDirectoryValue;
    }

    /**
     * Getter for file pattern XPath for file selection. SynapseXPath possibles
     * reading value either 'expression' or 'value' attributes.
     * 
     * @return File pattern XPath for file selection
     */
    public SynapseXPath getFilePatternXpath() {
        return filePatternXpath;
    }

    /**
     * Getter for boolean flag indicating whether to create missing directories.
     * 
     * @return True to create missing directories, otherwise false
     */
    public boolean getCreateMissingDirectoriesValue() {
        return createMissingDirectoriesValue;
    }

    /**
     * Getter for file archiving directory path.
     * 
     * @return Archive directory path
     */
    public String getArchiveDirectoryValue() {
        return archiveDirectoryValue;
    }

    /**
     * Setter for file archiving directory path.
     * 
     * @param archiveDirectoryValue
     *            Archive directory path
     */
    public void setArchiveDirectoryValue(String archiveDirectoryValue) {
        this.archiveDirectoryValue = archiveDirectoryValue;
    }

    /**
     * Setter for file pattern XPath for file selection from source directory.
     * SynapseXPath possibles reading value either 'expression' or 'value'
     * attributes.
     * 
     * @param filePatternXPath
     *            File pattern XPath
     */
    public void setFilePatternXpath(SynapseXPath filePatternXPath) {
        this.filePatternXpath = filePatternXPath;
    }

    /**
     * Setter for file pattern regex for file selection from source directory.
     * 
     * @param filePatternValue
     *            File pattern regex
     */
    public void setFilePatternValue(String filePatternValue) {
        this.filePatternValue = filePatternValue;
    }

    /**
     * Getter for file pattern regex for file selection from source directory.
     * 
     * @return File pattern regex
     */
    public String getFilePatternValue() {
        return this.filePatternValue;
    }

    /**
     * Getter for operation XPath. SynapseXPath possibles reading value either
     * 'expression' or 'value' attributes.
     * 
     * @return
     */
    public SynapseXPath getOperationXPath() {
        return operationXpath;
    }

    /**
     * Setter for operation XPath. SynapseXPath possibles reading value either
     * 'expression' or 'value' attributes.
     * 
     * @param operationXpath
     */
    public void setOperationXPath(SynapseXPath operationXpath) {
        this.operationXpath = operationXpath;
    }

    /**
     * Getter for source directory XPath. SynapseXPath possibles reading value
     * either 'expression' or 'value' attributes.
     * 
     * @return
     */
    public SynapseXPath getSourceDirectoryXPath() {
        return sourceDirectoryXpath;
    }

    /**
     * Setter for source directory XPath. SynapseXPath possibles reading value
     * either 'expression' or 'value' attributes.
     * 
     * @param xpath
     */
    public void setSourceDirectoryXpath(SynapseXPath xpath) {
        this.sourceDirectoryXpath = xpath;
    }

    /**
     * Getter for target directory XPath. SynapseXPath possibles reading value
     * either 'expression' or 'value' attributes.
     * 
     * @return
     */
    public SynapseXPath getTargetDirectoryXPath() {
        return targetDirectoryXpath;
    }

    /**
     * Setter for target directory XPath. SynapseXPath possibles reading value
     * either 'expression' or 'value' attributes.
     * 
     * @param xpath
     */
    public void setTargetDirectoryXpath(SynapseXPath xpath) {
        this.targetDirectoryXpath = xpath;
    }

    /**
     * Getter for archive directory XPath. SynapseXPath possibles reading value
     * either 'expression' or 'value' attributes.
     * 
     * @return
     */
    public SynapseXPath getArchiveDirectoryXpath() {
        return this.archiveDirectoryXpath;
    }

    /**
     * Setter for archive directory XPath. SynapseXPath possibles reading value
     * either 'expression' or 'value' attributes.
     * 
     * @param archiveDirectoryXpath
     */
    public void setArchiveDirectoryXpath(SynapseXPath archiveDirectoryXpath) {
        this.archiveDirectoryXpath = archiveDirectoryXpath;
    }

    /**
     * Setter for create missing directories XPath. SynapseXPath possibles
     * reading value either 'expression' or 'value' attributes.
     * 
     * @param createMissingDirectoriesXpath
     */
    public void setCreateMissingDirectoriesXpath(SynapseXPath createMissingDirectoriesXpath) {
        this.createMissingDirectoriesXpath = createMissingDirectoriesXpath;
    }

    /**
     * Setter for boolean flag indicating whether to create missing directories.
     * 
     * @param createMissingDirectoriesValue
     *            True to create missing directories, otherwise false
     */
    public void setCreateMissingDirectoriesValue(boolean createMissingDirectoriesValue) {
        this.createMissingDirectoriesValue = createMissingDirectoriesValue;
    }

    /**
     * Getter for create missing directory XPath. SynapseXPath possibles reading
     * value either 'expression' or 'value' attributes.
     * 
     * @return
     */
    public SynapseXPath getCreateMissingDirectoriesXpath() {
        return createMissingDirectoriesXpath;
    }

    /**
     * Setter for boolean flag indicating whether to use streaming for file transfer.
     *
     * @param streamingTransferValue
     *            True to use lock file, otherwise false
     */
    public void setStreamingTransferValue(boolean streamingTransferValue) {
        this.streamingTransferValue = streamingTransferValue;
    }

    /**
     * Getter for boolean flag indicating whether to use streaming for
     * file transfer.
     * 
     * @return
     */
    public Boolean getStreamingTransferValue() {
        return streamingTransferValue;
    }

    /**
     * Setter for streaming transfer XPath. SynapseXPath possibles reading value either
     * 'expression' or 'value' attributes.
     * 
     * @param xpath
     */
    public void setStreamingTransferXpath(SynapseXPath xpath) {
        this.streamingTransferXpath = xpath;
    }

    /**
     * Getter for streaming transfer XPath. SynapseXPath possibles reading value
     * either 'expression' or 'value' attributes.
     * 
     * @return
     */
    public SynapseXPath getStreamingTransferXpath() {
        return this.streamingTransferXpath;
    }

    /**
     * Setter for String indicating block size the streaming file transfer uses.
     *
     * @param streamingBlockSizeValue
     *            True to use lock file, otherwise false
     */
    public void setStreamingBlockSizeValue(String streamingBlockSizeValue) {
        this.streamingBlockSizeValue = streamingBlockSizeValue;
    }

    /**
     * Getter for string indicating block size for streaming
     * file transfer.
     *
     * @return
     */
    public String getStreamingBlockSizeValue() {
        return streamingBlockSizeValue;
    }

    /**
     * Setter for streaming block size XPath. SynapseXPath possibles reading value either
     * 'expression' or 'value' attributes.
     *
     * @param xpath
     */
    public void setStreamingBlockSizeXpath(SynapseXPath xpath) {
        this.streamingBlockSizeXpath = xpath;
    }

    /**
     * Getter for streaming block size XPath. SynapseXPath possibles reading value
     * either 'expression' or 'value' attributes.
     *
     * @return
     */
    public SynapseXPath getStreamingBlockSizeXpath() {
        return this.streamingBlockSizeXpath;
    }

    /**
     * Setter for boolean flag indicating whether to use lock file for
     * multithread orchestration.
     *
     * @param lockEnabledValue
     *            True to use lock file, otherwise false
     */
    public void setLockEnabledValue(boolean lockEnabledValue) {
        this.lockEnabledValue = lockEnabledValue;
    }

    /**
     * Getter for boolean flag indicating whether to use lock file for
     * multithreading.
     *
     * @return
     */
    public Boolean getLockEnabledValue() {
        return lockEnabledValue;
    }

    /**
     * Setter for lock enable XPath. SynapseXPath possibles reading value either
     * 'expression' or 'value' attributes.
     *
     * @param xpath
     */
    public void setLockEnabledXpath(SynapseXPath xpath) {
        this.lockEnabledXpath = xpath;
    }

    /**
     * Getter for lock enabled XPath. SynapseXPath possibles reading value
     * either 'expression' or 'value' attributes.
     *
     * @return
     */
    public SynapseXPath getLockEnabledXpath() {
        return this.lockEnabledXpath;
    }

    /**
     * Helper method for operation initiation.
     * 
     * @param messageContext
     *            Contains all necessary data do initiate VfsOperation
     * @return Initiated VfsOperation instance.
     * @throws SynapseException
     *             If operation not supported
     */
    private VfsOperation initOperation(MessageContext messageContext) {
        VfsOperation op;
        String operation = resolveOperation(messageContext);
        if ("copy".equalsIgnoreCase(operation)) {
            op = new VfsCopy();
        } else if ("move".equalsIgnoreCase(operation)) {
            op = new VfsMove();
        } else {
            handleException("Not supported operation: " + operation, messageContext);
            return null;
        }

        op.setSourceDirectory(resolveSourceDirectory(messageContext));
        op.setTargetDirectory(resolveTargetDirectory(messageContext));
        op.setFilePattern(resolveFilePattern(messageContext));
        op.setArchiveDirectory(resolveArchiveDirectory(messageContext));
        op.setCreateMissingDirectories(resolveCreateMissingDirectories(messageContext));
        op.setLockEnabled(resolveLockEnabled(messageContext));
        op.setStreamingTransfer(resolveStreamingTransfer(messageContext));
        op.setStreamingBlockSize(resolveStreamingBlockSize(messageContext));
        boolean ftpPassiveMode = isFtpPassiveModeEnabled(messageContext);
        op.setFtpPassiveMode(ftpPassiveMode);
        op.setRetryCount(this.retryCount);
        op.setRetryWait(this.retryWait);

        return op;
    }

    /**
     * Helper method indicating whether passive mode is set in message context.
     * 
     * @param messageContext
     *            Contains FTP passive mode data
     * @return True if FTP passive mode is enabled, otherwise false
     */
    private boolean isFtpPassiveModeEnabled(MessageContext messageContext) {
        Object property = messageContext.getProperty(FTP_PASSIVE_MODE_PROPERTY_NAME);
        if (property != null) {
            return Boolean.valueOf(property.toString());
        }

        return false;
    }

    /**
     * Helper method to resolve file pattern regex.
     * 
     * @param messageContext
     *            Contains file pattern regex data.
     * @return Resulved file pattern regex.
     */
    private String resolveFilePattern(MessageContext messageContext) {
        if (filePatternXpath != null) {
            return resolvePayloadValue(filePatternXpath, messageContext);
        }
        return getFilePatternValue();
    }

    /**
     * Helper method to resolve operation (copy/move/other).
     * 
     * @param messageContext
     *            Contains operation data.
     * @return Resolved operation
     */
    private String resolveOperation(MessageContext messageContext) {
        if (operationXpath != null) {
            return resolvePayloadValue(operationXpath, messageContext);
        }
        return getOperationValue();
    }

    /**
     * Helper method to resolve source directory path.
     * 
     * @param messageContext
     *            Contains source directory path data
     * @return Resolved source directory path
     */
    private String resolveSourceDirectory(MessageContext messageContext) {
        if (sourceDirectoryXpath != null) {
            return resolvePayloadValue(sourceDirectoryXpath, messageContext);
        }
        return getSourceDirectoryValue();
    }

    /**
     * Helper method to resolve target directory path.
     * 
     * @param messageContext
     *            Contains target directory path data
     * @return Resolved target directory path
     */
    private String resolveTargetDirectory(MessageContext messageContext) {
        if (targetDirectoryXpath != null) {
            return resolvePayloadValue(targetDirectoryXpath, messageContext);
        }
        return getTargetDirectoryValue();
    }

    /**
     * Helper method to resolve archive directory path.
     * 
     * @param messageContext
     *            Contains archive directory path data
     * @return Resolved archive directory path
     */
    private String resolveArchiveDirectory(MessageContext messageContext) {
        if (archiveDirectoryXpath != null) {
            return resolvePayloadValue(archiveDirectoryXpath, messageContext);
        }
        return getArchiveDirectoryValue();
    }

    /**
     * Helper method indicating whether create missing directories flag is
     * true/false.
     * 
     * @param messageContext
     *            Contains create missing directories data
     * @return Resolved boolean value indicating whether to create missing
     *         directories
     */
    private boolean resolveCreateMissingDirectories(MessageContext messageContext) {
        if (createMissingDirectoriesXpath != null) {
            return Boolean.valueOf(resolvePayloadValue(createMissingDirectoriesXpath, messageContext));
        }
        return createMissingDirectoriesValue;
    }

    /**
     * Helper method indicating whether create missing directories flag is
     * true/false.
     *
     * @param messageContext
     *            Contains create missing directories data
     * @return Resolved boolean value indicating whether to create missing
     *         directories
     */
    private boolean resolveStreamingTransfer(MessageContext messageContext) {
        if (streamingTransferXpath != null) {
            return Boolean.valueOf(resolvePayloadValue(streamingTransferXpath, messageContext));
        }
        return streamingTransferValue;
    }

    /**
     * Helper method to resolve streaming block size.
     *
     * @param messageContext
     *            Contains streaming block size data
     * @return Resolved streaming block size
     */
    private String resolveStreamingBlockSize(MessageContext messageContext) {
        if (streamingBlockSizeXpath != null) {
            return resolvePayloadValue(getStreamingBlockSizeXpath(), messageContext);
        }
        return getStreamingBlockSizeValue();
    }

    /**
     * Helper method indicating whether lock file is enabled.
     * 
     * @param messageContext
     *            Contains lock enabled data
     * @return True if lock file is enabled, otherwise false
     */
    private boolean resolveLockEnabled(MessageContext messageContext) {
        if (lockEnabledXpath != null) {
            String result = resolvePayloadValue(lockEnabledXpath, messageContext);
            if (result != null) {
                return Boolean.valueOf(result);
            }
        }
        return lockEnabledValue == null ? DEFAULT_LOCK_ENABLED : lockEnabledValue;
    }

    /**
     * Helper method to resolve file pattern XPath from given message context.
     * 
     * @param xpath
     *            Evaluates initiated XPath within message context
     * @param messageContext
     *            Contains paydload
     * @return Resolved payload value, otherwise null
     */
    private String resolvePayloadValue(SynapseXPath xpath, MessageContext messageContext) {
        try {
            Object evaluationResult = xpath.evaluate(messageContext);
            return extractValueFromPayload(xpath, messageContext, evaluationResult);
        } catch (JaxenException e) {
            String errorString = MessageFormat.format(
                    "Error while resolving the file path XPath expression: [{0}]. Exception message: [{0}].",
                    filePatternXpath.toString(), e.getMessage());
            log.warn(errorString, e);
            handleException(errorString, messageContext);
        }
        return null;
    }

    /**
     * Helper method to extract value from payload within given message context.
     * 
     * @param xpath
     *            Evaluated XPath
     * @param messageContext
     *            Contains payload
     * @param evaluationResult
     *            Already evaluated XPath result
     * @return Extracted value from payload, otherwise null
     */
    private String extractValueFromPayload(SynapseXPath xpath, MessageContext messageContext, Object evaluationResult) {
        if (evaluationResult instanceof String) {
            return (String) evaluationResult;
        } else if (evaluationResult instanceof OMElement) {
            return ((OMElement) evaluationResult).getText();
        } else if (evaluationResult instanceof OMAttribute) {
            return ((OMAttribute) evaluationResult).getAttributeValue();
        } else if (evaluationResult instanceof OMText) {
            return ((OMText) evaluationResult).getText();
        } else if (evaluationResult != null) {
            if (evaluationResult instanceof List) {
                List<?> resultList = (List<?>) evaluationResult;
                if (resultList.size() > 1) {
                    handleException("More than one result found with xpath " + xpath + ", refusing to proceed.",
                            messageContext);
                } else {
                    if (!resultList.isEmpty()) {
                        return extractValueFromPayload(xpath, messageContext, resultList.get(0));
                    } else {
                        getLog(messageContext).traceOrDebug("VfsMediator: Resolving resulted in null");
                        return null;
                    }
                }
            }
            handleException(
                    "Unsupported result type for " + xpath + ": " + evaluationResult.getClass().getCanonicalName(),
                    messageContext);
            return null;
        }
        getLog(messageContext).traceOrDebug("VfsMediator: Resolving resulted in null");
        return null;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryWait() {
        return retryWait;
    }

    public void setRetryWait(int retryWait) {
        this.retryWait = retryWait;
    }
}
