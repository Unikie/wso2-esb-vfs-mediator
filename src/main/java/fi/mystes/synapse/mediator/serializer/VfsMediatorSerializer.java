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

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractMediatorSerializer;

import fi.mystes.synapse.mediator.VfsMediator;
import fi.mystes.synapse.mediator.config.VfsMediatorConfigConstants;

/**
 * Mediator serializer class to transform mediator instance to OMElement
 * instance.
 * 
 */
public class VfsMediatorSerializer extends AbstractMediatorSerializer {

    private static final String ATT_VALUE = "value";
    private static final String ATT_EXPR = "expression";

    /**
     * Performs the mediator serialization by transforming mediator instance
     * into OMElement instance.
     */
    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {
        OMElement element = fac.createOMElement(VfsMediatorConfigConstants.ROOT_TAG_NAME, synNS);

        VfsMediator mediator = (VfsMediator) m;

        if (mediator.getOperationXPath() != null) {
            OMElement operation = fac.createOMElement(VfsMediatorConfigConstants.ATT_OPERATION.getLocalPart(), synNS);
            operation.addAttribute(ATT_EXPR, mediator.getOperationXPath().toString(), nullNS);
            element.addChild(operation);
        } else if (mediator.getOperationValue() != null) {
            OMElement operation = fac.createOMElement(VfsMediatorConfigConstants.ATT_OPERATION.getLocalPart(), synNS);
            operation.addAttribute(ATT_VALUE, mediator.getOperationValue(), nullNS);
            element.addChild(operation);
        }

        if (mediator.getSourceDirectoryXPath() != null) {
            OMElement sourceDirectory = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_SOURCE_DIRECTORY.getLocalPart(), synNS);
            sourceDirectory.addAttribute(ATT_EXPR, mediator.getSourceDirectoryXPath().toString(), nullNS);
            element.addChild(sourceDirectory);
        } else if (mediator.getSourceDirectoryValue() != null) {
            OMElement sourceDirectory = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_SOURCE_DIRECTORY.getLocalPart(), synNS);
            sourceDirectory.addAttribute(ATT_VALUE, mediator.getSourceDirectoryValue(), nullNS);
            element.addChild(sourceDirectory);
        }

        if (mediator.getTargetDirectoryXPath() != null) {
            OMElement targetDirectory = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_TARGET_DIRECTORY.getLocalPart(), synNS);
            targetDirectory.addAttribute(ATT_EXPR, mediator.getTargetDirectoryXPath().toString(), nullNS);
            element.addChild(targetDirectory);
        } else if (mediator.getTargetDirectoryValue() != null) {
            OMElement targetDirectory = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_TARGET_DIRECTORY.getLocalPart(), synNS);
            targetDirectory.addAttribute(ATT_VALUE, mediator.getTargetDirectoryValue(), nullNS);
            element.addChild(targetDirectory);
        }

        if (mediator.getFilePatternXpath() != null) {
            OMElement filePatternDirectory = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_FILE_PATTERN.getLocalPart(), synNS);
            filePatternDirectory.addAttribute(ATT_EXPR, mediator.getFilePatternXpath().toString(), nullNS);
            element.addChild(filePatternDirectory);
        } else if (mediator.getFilePatternValue() != null) {
            OMElement filePatternDirectory = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_FILE_PATTERN.getLocalPart(), synNS);
            filePatternDirectory.addAttribute(ATT_VALUE, mediator.getFilePatternValue(), nullNS);
            element.addChild(filePatternDirectory);
        }

        if (mediator.getArchiveDirectoryXpath() != null) {
            OMElement archiveDirectory = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_ARCHIVE_DIRECTORY.getLocalPart(), synNS);
            archiveDirectory.addAttribute(ATT_EXPR, mediator.getArchiveDirectoryXpath().toString(), nullNS);
            element.addChild(archiveDirectory);
        } else if (mediator.getArchiveDirectoryValue() != null) {
            OMElement archiveDirectory = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_ARCHIVE_DIRECTORY.getLocalPart(), synNS);
            archiveDirectory.addAttribute(ATT_VALUE, mediator.getArchiveDirectoryValue(), nullNS);
            element.addChild(archiveDirectory);
        }

        if (mediator.getCreateMissingDirectoriesValue()) {
            OMElement createMissingDirectories = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_CREATE_MISSING_DIRECTORY.getLocalPart(), synNS);
            createMissingDirectories.addAttribute(ATT_VALUE,
                    String.valueOf(mediator.getCreateMissingDirectoriesValue()), nullNS);
            element.addChild(createMissingDirectories);
        }

        if (mediator.getStreamingTransferXpath() != null) {
            OMElement streamingTransfer = fac
                .createOMElement(VfsMediatorConfigConstants.ATT_STREAMING_TRANSFER.getLocalPart(), synNS);
            streamingTransfer.addAttribute(ATT_EXPR, mediator.getStreamingTransferXpath().toString(), nullNS);
            element.addChild(streamingTransfer);
        } else if (mediator.getStreamingTransferValue()) {
            OMElement streamingTransfer = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_STREAMING_TRANSFER.getLocalPart(), synNS);
            streamingTransfer.addAttribute(ATT_VALUE,
                    String.valueOf(mediator.getStreamingTransferValue()), nullNS);
            element.addChild(streamingTransfer);
        }

        if (mediator.getStreamingBlockSizeXpath() != null) {
            OMElement streamingBlockSize = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_STREAMING_BLOCK_SIZE.getLocalPart(), synNS);
            streamingBlockSize.addAttribute(ATT_EXPR, mediator.getStreamingBlockSizeXpath().toString(), nullNS);
            element.addChild(streamingBlockSize);
        } else if (mediator.getStreamingBlockSizeValue() != null) {
            OMElement streamingBlockSize = fac
                    .createOMElement(VfsMediatorConfigConstants.ATT_STREAMING_BLOCK_SIZE.getLocalPart(), synNS);
            streamingBlockSize.addAttribute(ATT_VALUE, mediator.getStreamingBlockSizeValue(), nullNS);
            element.addChild(streamingBlockSize);
        }

        if (mediator.getLockEnabledValue() != null) {
            OMElement lockEnabled = fac.createOMElement(VfsMediatorConfigConstants.ATT_LOCK_ENABLED.getLocalPart(),
                    synNS);
            lockEnabled.addAttribute(ATT_VALUE, String.valueOf(mediator.getLockEnabledValue()), nullNS);
            element.addChild(lockEnabled);
        }

        if(mediator.getSftpTimeoutValue() != null && mediator.getSftpTimeoutValue() != VfsMediatorConfigConstants.DEFAULT_SFTP_TIMEOUT) {
            OMElement sftpTimeout = fac.createOMElement(VfsMediatorConfigConstants.ATT_SFTP_TIMEOUT.getLocalPart(), synNS);
            sftpTimeout.addAttribute(ATT_VALUE, mediator.getSftpTimeoutValue().toString(), nullNS);
            element.addChild(sftpTimeout);
        }


        handleRetry(mediator, element);

        return element;
    }

    private void handleRetry(VfsMediator mediator, OMElement element) {
        if(mediator.getRetryCount() == VfsMediatorConfigConstants.DEFAULT_RETRY_COUNT &&
                mediator.getRetryWait() == VfsMediatorConfigConstants.DEFAULT_RETRY_WAIT) {
            return;
        }

        OMElement retryElement = fac.createOMElement(VfsMediatorConfigConstants.ELEM_RETRY);
        retryElement.addAttribute(VfsMediatorConfigConstants.ATT_RETRY_COUNT.getLocalPart(), Integer.toString(mediator.getRetryCount()), nullNS);
        retryElement.addAttribute(VfsMediatorConfigConstants.ATT_RETRY_WAIT.getLocalPart(), Integer.toString(mediator.getRetryWait()), nullNS);

        element.addChild(retryElement);
    }

    /**
     * Get Wrapper Mediator class name
     */
    @Override
    public String getMediatorClassName() {
        return VfsMediator.class.getName();
    }
}
