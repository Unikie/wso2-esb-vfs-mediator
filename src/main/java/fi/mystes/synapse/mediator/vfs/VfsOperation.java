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
package fi.mystes.synapse.mediator.vfs;

import org.apache.commons.vfs2.FileSystemException;

/**
 * VFS operation interface providing implementable methods.
 *
 */
public interface VfsOperation {

    /**
     * Method for executing implemented operation.
     * 
     * @return number of handled files
     * 
     * @throws FileSystemException
     *             If operation fails
     */
    int execute() throws FileSystemException;

    /**
     * Sets operation delete.
     * 
     * @param delegate
     *            Operation delegate to set
     */
    void setOperationDelegate(VfsOperationDelegate delegate);

    /**
     * Sets source directory.
     * 
     * @param sourceDirectory
     *            Path to source directory
     */
    void setSourceDirectory(String sourceDirectory);

    /**
     * Sets target directory.
     * 
     * @param targetDirectory
     *            Path to target directory
     */
    void setTargetDirectory(String targetDirectory);

    /**
     * Sets the target file prefix.
     * @param prefix
     */
    void setTargetFilePrefix(String prefix);

    /**
     * Sets the target file suffix.
     * @param suffix
     */
    void setTargetFileSuffix(String suffix);

    /**
     * Sets archive directory where files will be archived.
     * 
     * @param targetDirectory
     *            Path to archive directory
     */
    void setArchiveDirectory(String targetDirectory);

    /**
     * Sets the archive file prefix.
     * @param prefix
     */
    void setArchiveFilePrefix(String prefix);

    /**
     * Sets the archive file suffix.
     * @param suffix
     */
    void setArchiveFileSuffix(String suffix);

    /**
     * Sets file pattern for selecting files by operation.
     * 
     * @param filePattern
     *            File pattern to select files
     */
    void setFilePattern(String filePattern);

    /**
     * Sets create missing directories boolean flag.
     * 
     * @param createMissingDirectories
     *            True/false whether to create missing directories
     */
    void setCreateMissingDirectories(boolean createMissingDirectories);

    /**
     * Sets lock enabled boolean flag.
     * 
     * @param lockEnabled
     *            True/false whether lock is enabled
     */
    void setLockEnabled(boolean lockEnabled);

    /**
     * Sets streaming transfer flag.
     * 
     * @param streamingTransfer
     *            True to use passive mode, false to use active mode
     */
    void setStreamingTransfer(boolean streamingTransfer);

    /**
     * Sets streaming block size.
     *
     * @param streamingBlockSize
     *            True to use passive mode, false to use active mode
     */
    void setStreamingBlockSize(String streamingBlockSize);

    /**
     * Sets FTP passive mode flag.
     *
     * @param ftpPassiveMode
     *            True to use passive mode, false to use active mode
     */
    void setFtpPassiveMode(boolean ftpPassiveMode);

}
