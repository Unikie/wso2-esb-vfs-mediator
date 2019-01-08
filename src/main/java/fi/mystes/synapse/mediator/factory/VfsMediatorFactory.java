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

import java.text.MessageFormat;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractMediatorFactory;
import org.apache.synapse.config.xml.SynapseXPathFactory;
import org.jaxen.JaxenException;

import fi.mystes.synapse.mediator.VfsMediator;
import fi.mystes.synapse.mediator.config.VfsMediatorConfigConstants;

public class VfsMediatorFactory extends AbstractMediatorFactory {

    /**
     * The QName of wrapper mediator element in the XML config
     * 
     * @return QName of wrapper mediator
     */
    @Override
    public QName getTagQName() {
        return VfsMediatorConfigConstants.ROOT_TAG;
    }

    /**
     * Specific mediator factory implementation to build the
     * org.apache.synapse.Mediator by the given XML configuration
     * 
     * @param omElement
     *            element configuration element describing the properties of the
     *            mediator
     * @param properties
     *            bag of properties to pass in any information to the factory
     * 
     * @return built custom VFS mediator
     */
    @SuppressWarnings("deprecation")
    @Override
    protected Mediator createSpecificMediator(OMElement omElement, Properties properties) {
        VfsMediator mediator = new VfsMediator();

        processTraceState(mediator, omElement);

        handleSourceDirectoryElement(omElement, mediator);

        handleArchiveDirectoryElement(omElement, mediator);

        handleTargetDirectoryElement(omElement, mediator);

        handleOperationElement(omElement, mediator);

        handleFilePatternElement(omElement, mediator);

        handleCreateMissingDirectoriesElement(omElement, mediator);

        handleLockEnabledElement(omElement, mediator);

        handleUserDirIsRootElement(omElement, mediator);

        handleStreamingTransferElement(omElement, mediator);

        handleStreamingBlockSizeElement(omElement, mediator);

        handleRetryElement(omElement, mediator);

        handleSftpTimeoutElement(omElement, mediator);

        handleTargetFilenamePrefixElement(omElement, mediator);

        handleTargetFilenameSuffixElement(omElement, mediator);

        handleArchiveFilenamePrefixElement(omElement, mediator);

        handleArchiveFilenameSuffixElement(omElement, mediator);

        return mediator;
    }

    /**
     * Retrieves targetFilenamePrefix -element from omElement and configures mediator accordingly.
     *
     * @param omElement element to search in
     * @param mediator mediator to configure
     */
    private void handleTargetFilenamePrefixElement(OMElement omElement, VfsMediator mediator) {
        OMElement elem = omElement.getFirstChildWithName(VfsMediatorConfigConstants.ELEM_TARGET_FILENAME_PREFIX);
        if(elem == null) return;

        if(elem.getAttributeValue(ATT_VALUE) != null) {
            mediator.setTargetFilenamePrefixValue(elem.getAttributeValue(ATT_VALUE));
        } else if(elem.getAttributeValue(ATT_EXPRN) != null) {
            try {
                mediator.setTargetFilenamePrefixXpath(SynapseXPathFactory.getSynapseXPath(elem, ATT_EXPRN));
            } catch (JaxenException e) {
                handleXpathException(VfsMediatorConfigConstants.ELEM_TARGET_FILENAME_PREFIX, e);
            }
        }
    }

    /**
     * Retrieves targetFilenameSuffix -element from omElement and configures mediator accordingly.
     *
     * @param omElement element to search in
     * @param mediator mediator to configure
     */
    private void handleTargetFilenameSuffixElement(OMElement omElement, VfsMediator mediator) {
        OMElement elem = omElement.getFirstChildWithName(VfsMediatorConfigConstants.ELEM_TARGET_FILENAME_SUFFIX);
        if(elem == null) return;

        if(elem.getAttributeValue(ATT_VALUE) != null) {
            mediator.setTargetFilenameSuffixValue(elem.getAttributeValue(ATT_VALUE));
        } else if(elem.getAttributeValue(ATT_EXPRN) != null) {
            try {
                mediator.setTargetFilenameSuffixXpath(SynapseXPathFactory.getSynapseXPath(elem, ATT_EXPRN));
            } catch (JaxenException e) {
                handleXpathException(VfsMediatorConfigConstants.ELEM_TARGET_FILENAME_SUFFIX, e);
            }
        }
    }

    /**
     * Retrieves archiveFilenamePrefix -element from omElement and configures mediator accordingly.
     *
     * @param omElement element to search in
     * @param mediator mediator to configure
     */
    private void handleArchiveFilenamePrefixElement(OMElement omElement, VfsMediator mediator) {
        OMElement elem = omElement.getFirstChildWithName(VfsMediatorConfigConstants.ELEM_ARCHIVE_FILENAME_PREFIX);
        if(elem == null) return;

        if(elem.getAttributeValue(ATT_VALUE) != null) {
            mediator.setArchiveFilenamePrefixValue(elem.getAttributeValue(ATT_VALUE));
        } else if(elem.getAttributeValue(ATT_EXPRN) != null) {
            try {
                mediator.setArchiveFilenamePrefixXpath(SynapseXPathFactory.getSynapseXPath(elem, ATT_EXPRN));
            } catch (JaxenException e) {
                handleXpathException(VfsMediatorConfigConstants.ELEM_ARCHIVE_FILENAME_PREFIX, e);
            }
        }
    }

    /**
     * Retrieves archiveFilenameSuffix -element from omElement and configures mediator accordingly.
     *
     * @param omElement element to search in
     * @param mediator mediator to configure
     */
    private void handleArchiveFilenameSuffixElement(OMElement omElement, VfsMediator mediator) {
        OMElement elem = omElement.getFirstChildWithName(VfsMediatorConfigConstants.ELEM_ARCHIVE_FILENAME_SUFFIX);
        if(elem == null) return;

        if(elem.getAttributeValue(ATT_VALUE) != null) {
            mediator.setArchiveFilenameSuffixValue(elem.getAttributeValue(ATT_VALUE));
        } else if(elem.getAttributeValue(ATT_EXPRN) != null) {
            try {
                mediator.setArchiveFilenameSuffixXpath(SynapseXPathFactory.getSynapseXPath(elem, ATT_EXPRN));
            } catch (JaxenException e) {
                handleXpathException(VfsMediatorConfigConstants.ELEM_ARCHIVE_FILENAME_SUFFIX, e);
            }
        }
    }

    /**
     * Helper method for handleException.
     * Formats the message based on element.
     * @param element element with exception thrown
     * @param e exception thrown
     */
    private void handleXpathException(QName element, Exception e) {
        handleException(MessageFormat.format("Invalid XPath expression given to VfsMediator [{0}] element", element), e);
    }


    /**
     * Retrieves 'lockEnabled' from given OMElement and sets it to given
     * mediator.
     * 
     * @param omElement
     *            To read 'lockEnabled' element from
     * @param mediator
     *            To set 'lockEnabled' boolean true/false value to
     */
    private void handleLockEnabledElement(OMElement omElement, VfsMediator mediator) {
        OMElement lockEnabledElement = omElement.getFirstChildWithName(VfsMediatorConfigConstants.ATT_LOCK_ENABLED);
        if (lockEnabledElement != null) {
            if (lockEnabledElement.getAttributeValue(ATT_EXPRN) != null) {
                try {
                    mediator.setLockEnabledXpath(SynapseXPathFactory.getSynapseXPath(lockEnabledElement, ATT_EXPRN));
                } catch (JaxenException e) {
                    handleException(MessageFormat.format(
                            "An invalid xPath expression has been given to a VfsMediator [{0}] element",
                            VfsMediatorConfigConstants.ATT_LOCK_ENABLED), e);
                }
            } else {
                String value = lockEnabledElement.getAttributeValue(ATT_VALUE);
                mediator.setLockEnabledValue(Boolean.valueOf(value));
            }
        }
    }

    /**
     * Retrieves 'userDirIsRoot' from given OMElement and sets it to given
     * mediator.
     *
     * @param omElement
     *            To read 'userDirIsRoot' element from
     * @param mediator
     *            To set 'userDirIsRoot' boolean true/false value to
     */
    private void handleUserDirIsRootElement(OMElement omElement, VfsMediator mediator) {
        OMElement userDirIsRootElement = omElement.getFirstChildWithName(VfsMediatorConfigConstants.ATT_USER_DIR_ROOT);
        if (userDirIsRootElement != null) {
            if (userDirIsRootElement.getAttributeValue(ATT_EXPRN) != null) {
                try {
                    mediator.setUserDirIsRootXpath(SynapseXPathFactory.getSynapseXPath(userDirIsRootElement, ATT_EXPRN));
                } catch (JaxenException e) {
                    handleException(MessageFormat.format(
                            "An invalid xPath expression has been given to a VfsMediator [{0}] element",
                            VfsMediatorConfigConstants.ATT_USER_DIR_ROOT), e);
                }
            } else {
                String value = userDirIsRootElement.getAttributeValue(ATT_VALUE);
                mediator.setUserDirIsRootValue(Boolean.valueOf(value));
            }
        }
    }

    private void handleRetryElement(OMElement omElement, VfsMediator mediator) {
        OMElement retryElement = omElement.getFirstChildWithName(VfsMediatorConfigConstants.ELEM_RETRY);

        if(retryElement == null) {
            mediator.setRetryCount(VfsMediatorConfigConstants.DEFAULT_RETRY_COUNT);
            mediator.setRetryWait(VfsMediatorConfigConstants.DEFAULT_RETRY_WAIT);

            return;
        }

        try {
            int retryCount = Integer.parseInt(retryElement.getAttributeValue(VfsMediatorConfigConstants.ATT_RETRY_COUNT));
            int retryWait = Integer.parseInt(retryElement.getAttributeValue(VfsMediatorConfigConstants.ATT_RETRY_WAIT));

            mediator.setRetryCount(retryCount);
            mediator.setRetryWait(retryWait);
        } catch (Exception e) {
            handleException("Failed to parse retry count or wait attribute.", e);
        }
    }

    /**
     * Retrieves 'createMissingDirectories' from given OMElement and sets it to
     * given mediator.
     * 
     * @param omElement
     *            To read 'createMissingDirectories' element from
     * @param mediator
     *            To set 'createMissingDirectories' boolean true/false value to
     */
    private void handleCreateMissingDirectoriesElement(OMElement omElement, VfsMediator mediator) {
        OMElement createMissingDirectoriesElement = omElement
                .getFirstChildWithName(VfsMediatorConfigConstants.ATT_CREATE_MISSING_DIRECTORY);
        if (createMissingDirectoriesElement != null) {
            if (createMissingDirectoriesElement.getAttributeValue(ATT_EXPRN) != null) {
                try {
                    mediator.setCreateMissingDirectoriesXpath(
                            SynapseXPathFactory.getSynapseXPath(createMissingDirectoriesElement, ATT_EXPRN));
                } catch (JaxenException e) {
                    handleException(MessageFormat.format(
                            "An invalid xPath expression has been given to a VfsMediator [{0}] element",
                            VfsMediatorConfigConstants.ATT_CREATE_MISSING_DIRECTORY), e);
                }
            } else {
                String value = createMissingDirectoriesElement.getAttributeValue(ATT_VALUE);
                mediator.setCreateMissingDirectoriesValue(Boolean.valueOf(value));
            }
        }
    }

    /**
     * Retrieves 'filePattern' from given OMElement and sets it to given
     * mediator.
     * 
     * @param omElement
     *            To read 'filePattern' element from
     * @param mediator
     *            To set 'filePattern' value to
     */
    private void handleFilePatternElement(OMElement omElement, VfsMediator mediator) {
        OMElement filePatternElement = omElement.getFirstChildWithName(VfsMediatorConfigConstants.ATT_FILE_PATTERN);
        if (filePatternElement != null) {
            if (filePatternElement.getAttributeValue(ATT_EXPRN) != null) {
                try {
                    mediator.setFilePatternXpath(SynapseXPathFactory.getSynapseXPath(filePatternElement, ATT_EXPRN));
                } catch (JaxenException e) {
                    handleException(MessageFormat.format(
                            "An invalid xPath expression has been given to a VfsMediator [{0}] element",
                            VfsMediatorConfigConstants.ATT_FILE_PATTERN), e);
                }
            } else {
                String value = filePatternElement.getAttributeValue(ATT_VALUE);
                mediator.setFilePatternValue(value);
            }
        }
    }

    /**
     * Retrieves 'operation' element from given OMElement and sets it to given
     * mediator.
     * 
     * @param omElement
     *            To read 'operation' element from
     * @param mediator
     *            To set 'operation' (copy/move) value to
     */
    private void handleOperationElement(OMElement omElement, VfsMediator mediator) {
        OMElement operationElement = omElement.getFirstChildWithName(VfsMediatorConfigConstants.ATT_OPERATION);
        if (operationElement == null) {
            handleException(MessageFormat.format("The [{0}] element is required by the VfsMediator.",
                    VfsMediatorConfigConstants.ATT_OPERATION.getLocalPart()));
        } else {
            if (operationElement.getAttributeValue(ATT_EXPRN) != null) {
                try {
                    mediator.setOperationXPath(SynapseXPathFactory.getSynapseXPath(operationElement, ATT_EXPRN));
                } catch (JaxenException e) {
                    handleException(MessageFormat.format(
                            "An invalid xPath expression has been given to a VfsMediator [{0}] element",
                            VfsMediatorConfigConstants.ATT_OPERATION), e);
                }
            } else {
                String value = operationElement.getAttributeValue(ATT_VALUE);
                mediator.setOperationValue(value);
            }
        }
    }

    /**
     * Retrieves 'targetDirectory' element from given OMElement and sets it to
     * given mediator.
     * 
     * @param omElement
     *            To read 'targetDirectory' element from
     * @param mediator
     *            To set 'targetDirectory' value to
     */
    private void handleTargetDirectoryElement(OMElement omElement, VfsMediator mediator) {
        OMElement targetDirectoryElement = omElement
                .getFirstChildWithName(VfsMediatorConfigConstants.ATT_TARGET_DIRECTORY);
        if (targetDirectoryElement == null) {
            handleException(MessageFormat.format("The [{0}] element is required by the VfsMediator.",
                    VfsMediatorConfigConstants.ATT_TARGET_DIRECTORY.getLocalPart()));
        } else {
            if (targetDirectoryElement.getAttributeValue(ATT_EXPRN) != null) {
                try {
                    mediator.setTargetDirectoryXpath(
                            SynapseXPathFactory.getSynapseXPath(targetDirectoryElement, ATT_EXPRN));
                } catch (JaxenException e) {
                    handleException(MessageFormat.format(
                            "An invalid xPath expression has been given to a VfsMediator [{0}] element",
                            VfsMediatorConfigConstants.ATT_TARGET_DIRECTORY), e);
                }
            } else {
                String value = targetDirectoryElement.getAttributeValue(ATT_VALUE);
                mediator.setTargetDirectoryValue(value);
            }
        }
    }

    /**
     * Retrieves 'archiveDirectory' element from given OMElement and sets it to
     * given mediator.
     * 
     * @param omElement
     *            To read 'archiveDirectory' element from
     * @param mediator
     *            To set 'archiveDirectory' (directory where be moved to) value
     *            to
     */
    private void handleArchiveDirectoryElement(OMElement omElement, VfsMediator mediator) {
        OMElement archiveDirectoryElement = omElement
                .getFirstChildWithName(VfsMediatorConfigConstants.ATT_ARCHIVE_DIRECTORY);
        if (archiveDirectoryElement != null) {
            if (archiveDirectoryElement.getAttributeValue(ATT_EXPRN) != null) {
                try {
                    mediator.setArchiveDirectoryXpath(
                            SynapseXPathFactory.getSynapseXPath(archiveDirectoryElement, ATT_EXPRN));
                } catch (JaxenException e) {
                    handleException(MessageFormat.format(
                            "An invalid xPath expression has been given to a VfsMediator [{0}] element",
                            VfsMediatorConfigConstants.ATT_ARCHIVE_DIRECTORY), e);
                }
            } else {
                String value = archiveDirectoryElement.getAttributeValue(ATT_VALUE);
                mediator.setArchiveDirectoryValue(value);
            }
        }
    }

    /**
     * Retrieves 'streamingTransfer' element from given OMElement and sets it to
     * given mediator.
     *
     * @param omElement
     *            To read 'streamingTransfer' element from
     * @param mediator
     *            To set 'streamingTransfer' (transfer is made by streaming in blocks) value
     *            to
     */
    private void handleStreamingTransferElement(OMElement omElement, VfsMediator mediator) {
        OMElement streamingTransferElement = omElement
                .getFirstChildWithName(VfsMediatorConfigConstants.ATT_STREAMING_TRANSFER);
        if (streamingTransferElement != null) {
            if (streamingTransferElement.getAttributeValue(ATT_EXPRN) != null) {
                try {
                    mediator.setStreamingTransferXpath(
                            SynapseXPathFactory.getSynapseXPath(streamingTransferElement, ATT_EXPRN));
                } catch (JaxenException e) {
                    handleException(MessageFormat.format(
                            "An invalid xPath expression has been given to a VfsMediator [{0}] element",
                            VfsMediatorConfigConstants.ATT_STREAMING_TRANSFER), e);
                }
            } else {
                String value = streamingTransferElement.getAttributeValue(ATT_VALUE);
                mediator.setStreamingTransferValue(Boolean.valueOf(value));
            }
        }
    }

    /**
     * Retrieves 'streamingBlockSize' element from given OMElement and sets it to
     * given mediator.
     *
     * @param omElement
     *            To read 'streamingBlockSize' element from
     * @param mediator
     *            To set 'streamingBlockSize' (transfer is made in specified blockSize) value
     *            to
     */
    private void handleStreamingBlockSizeElement(OMElement omElement, VfsMediator mediator) {
        OMElement streamingBlockSizeElement = omElement
                .getFirstChildWithName(VfsMediatorConfigConstants.ATT_STREAMING_BLOCK_SIZE);
        if (streamingBlockSizeElement != null) {
            if (streamingBlockSizeElement.getAttributeValue(ATT_EXPRN) != null) {
                try {
                    mediator.setStreamingBlockSizeXpath(
                            SynapseXPathFactory.getSynapseXPath(streamingBlockSizeElement, ATT_EXPRN));
                } catch (JaxenException e) {
                    handleException(MessageFormat.format(
                            "An invalid xPath expression has been given to a VfsMediator [{0}] element",
                            VfsMediatorConfigConstants.ATT_STREAMING_BLOCK_SIZE), e);
                }
            } else {
                String value = streamingBlockSizeElement.getAttributeValue(ATT_VALUE);
                mediator.setStreamingBlockSizeValue(value);
            }
        }
    }

    /**
     * Retrieves 'sourceDirectory' element from given OMElement and sets it to
     * given mediator.
     * 
     * @param omElement
     *            To read 'sourceDirectory' element from
     * @param mediator
     *            To set 'sourceDirectory' (directory where to read files from)
     *            value to
     */
    private void handleSourceDirectoryElement(OMElement omElement, VfsMediator mediator) {
        OMElement sourceDirectoryElement = omElement
                .getFirstChildWithName(VfsMediatorConfigConstants.ATT_SOURCE_DIRECTORY);
        if (sourceDirectoryElement == null) {
            handleException(MessageFormat.format("The [{0}] element is required by the VfsMediator.",
                    VfsMediatorConfigConstants.ATT_SOURCE_DIRECTORY.getLocalPart()));
        } else {
            if (sourceDirectoryElement.getAttributeValue(ATT_EXPRN) != null) {
                try {
                    mediator.setSourceDirectoryXpath(
                            SynapseXPathFactory.getSynapseXPath(sourceDirectoryElement, ATT_EXPRN));
                } catch (JaxenException e) {
                    handleException(MessageFormat.format(
                            "An invalid xPath expression has been given to a VfsMediator [{0}] element",
                            VfsMediatorConfigConstants.ATT_SOURCE_DIRECTORY), e);
                }
            } else {
                String value = sourceDirectoryElement.getAttributeValue(ATT_VALUE);
                mediator.setSourceDirectoryValue(value);
            }
        }
    }

    private void handleSftpTimeoutElement(OMElement element, VfsMediator mediator) {
        OMElement sftpTimeoutElement = element.getFirstChildWithName(VfsMediatorConfigConstants.ATT_SFTP_TIMEOUT);

        if(sftpTimeoutElement == null) return;

        String timeoutValue = sftpTimeoutElement.getAttributeValue(ATT_VALUE);

        if(timeoutValue != null) {
            try {
                int valueAsInt = Integer.parseInt(timeoutValue);
                mediator.setSftpTimeoutValue(valueAsInt);
            } catch (NumberFormatException e) {
                handleException("Could not read sftp timeout value from: " + timeoutValue, e);
            }
        }
    }
}
