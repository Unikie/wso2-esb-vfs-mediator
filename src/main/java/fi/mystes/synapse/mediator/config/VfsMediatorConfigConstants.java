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
package fi.mystes.synapse.mediator.config;

import org.apache.synapse.config.xml.XMLConfigConstants;

import javax.xml.namespace.QName;

public class VfsMediatorConfigConstants {

    public static final String NAMESPACE_STRING = XMLConfigConstants.SYNAPSE_NAMESPACE;
    public static final String ROOT_TAG_NAME = "vfs";
    public static final QName ROOT_TAG = new QName(NAMESPACE_STRING, ROOT_TAG_NAME);
    public static final QName ATT_SOURCE_DIRECTORY = new QName(NAMESPACE_STRING,"sourceDirectory");
    public static final QName ATT_TARGET_DIRECTORY = new QName(NAMESPACE_STRING,"targetDirectory");
    public static final QName ATT_ARCHIVE_DIRECTORY = new QName(NAMESPACE_STRING,"archiveDirectory");
    public static final QName ATT_OPERATION = new QName(NAMESPACE_STRING,"operation");
    public static final QName ATT_FILE_PATTERN = new QName(NAMESPACE_STRING, "filePattern");
    public static final QName ATT_CREATE_MISSING_DIRECTORY = new QName(NAMESPACE_STRING, "createMissingDirectories");
    public static final QName ATT_LOCK_ENABLED = new QName(NAMESPACE_STRING, "lockEnabled");
    public static final QName ATT_STREAMING_TRANSFER = new QName(NAMESPACE_STRING, "streamingTransfer");
    public static final QName ATT_STREAMING_BLOCK_SIZE = new QName(NAMESPACE_STRING, "streamingBlockSize");
    public static final QName ATT_SFTP_TIMEOUT = new QName(NAMESPACE_STRING, "sftpTimeout");
}
