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
 * Abstract class for VFS operations. All VFS operations should inherit this
 * class.
 *
 */
public abstract class AbstractVfsOperation implements VfsOperation {
    private VfsOperationDelegate delegate;
    private String sourceDirectory;
    private String targetDirectory;
    private String archiveDirectory;
    private String filePattern;
    private String streamingBlockSize;
    private boolean createMissingDirectories;
    private boolean streamingTransfer;
    private boolean lockEnabled;
    private boolean ftpPassiveMode;

    /**
     * Setter for operation delegate instance.
     */
    @Override
    public void setOperationDelegate(VfsOperationDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Setter for source directory path.
     */
    @Override
    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    /**
     * Setter for target directory path.
     */
    @Override
    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    /**
     * Setter for file archiving directory path.
     */
    @Override
    public void setArchiveDirectory(String archiveDirectory) {
        this.archiveDirectory = archiveDirectory;
    }

    /**
     * Setter for create missing directories boolean flag.
     */
    @Override
    public void setCreateMissingDirectories(boolean createMissingDirectories) {
        this.createMissingDirectories = createMissingDirectories;
    }

    /**
     * Setter for file pattern regex for file selection from source directory.
     */
    @Override
    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    /**
     * Setter for lock enabled boolean flag.
     */
    @Override
    public void setLockEnabled(boolean lockEnabled) {
        this.lockEnabled = lockEnabled;
    }

    /**
     * Setter for streaming transfer boolean flag.
     */
    @Override
    public void setStreamingTransfer(boolean streamingTransfer) {
        this.streamingTransfer = streamingTransfer;
    }

    /**
     * Setter for streaming block size.
     */
    @Override
    public void setStreamingBlockSize(String streamingBlockSize) {
        this.streamingBlockSize = streamingBlockSize;
    }

    /**
     * Method to execute the VFS operation (copy/move/other).
     */
    @Override
    public int execute() throws FileSystemException {
        VfsOperationOptions options = parseOperationOptions();
        validateOptions(options);
        return executeImpl(parseOperationOptions());
    }

    /**
     * Setter for FTP passive mode boolean flag.
     */
    @Override
    public void setFtpPassiveMode(boolean ftpPassiveMode) {
        this.ftpPassiveMode = ftpPassiveMode;
    }

    /**
     * Interface method to be implemented by subclasses.
     * 
     * @param options
     *            VFS operation options
     * @return Amount of files handled
     * @throws FileSystemException
     *             If VFS operation (copy/move/other) fails
     */
    protected abstract int executeImpl(VfsOperationOptions options) throws FileSystemException;

    /**
     * Interface method to be implemented by subclasses.
     * 
     * @param options
     *            VFS operation options
     */
    protected abstract void validateOptions(VfsOperationOptions options);

    /**
     * Getter for operation delegate instance.
     * 
     * @return VFS operation delegate instance
     */
    protected VfsOperationDelegate getOperationDelegate() {
        return delegate;
    }

    /**
     * Helper method initiate VFS operation options.
     * 
     * @return Initiated VFS operation options
     */
    private VfsOperationOptions parseOperationOptions() {
        return VfsOperationOptions.with().sourceDirectory(sourceDirectory).targetDirectory(targetDirectory)
                .filePatternRegex(filePattern).archiveDirectory(archiveDirectory)
                .createMissingDirectories(createMissingDirectories).lockEnabled(lockEnabled)
                .ftpPassiveModeEnabled(ftpPassiveMode).streamingTransferEnabled(streamingTransfer)
                .streamingBlockSize(streamingBlockSize).build();
    }
}
