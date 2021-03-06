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

/**
 * Bean class containing all necessary settings for VFS operations.
 *
 */
public final class VfsOperationOptions {
    private final String sourceDirectory;
    private final String targetDirectory;
    private final String targetFileSuffix;
    private final String targetFilePrefix;
    private final String filePatternRegex;
    private final String archiveDirectory;
    private final String archiveFileSuffix;
    private final String archiveFilePrefix;
    private final String streamingBlockSize;
    private final boolean createMissingDirectories;
    private final boolean lockEnabled;
    private final boolean ftpPassiveMode;
    private final boolean streamingTransfer;
    private final String userDirIsRoot;
    private final int retryCount;
    private final int retryWait;
    private final int sftpTimeout;
    private final String sftpAuthKeyPath;

    /**
     * Class constructor.
     * 
     * @param sourceDirectory
     *            To copy/move/other files from
     * @param targetDirectory
     *            To copy/move/other files to
     * @param filePatternRegex
     *            File pattern regex for selecting files from source directory
     * @param archiveDirectory
     *            Directory to archive files to
     * @param createMissingDirectories
     *            Boolean flag indicating whether to create missing directories
     * @param lockEnabled
     *            Boolean flag indicating whether to use lock file to
     *            orchestrate multi-threading
     * @param ftpPassiveMode
     *            Boolean flag indicating whether FTP operation should be in
     *            passive mode
     * @param streamingTransfer
     *            Boolean flag indicating whether file transfer use streaming
     * @param userDirIsRoot
     *            Boolean flag indicating whether to use user directory as root
     * @param streamingBlockSize
     *            Block size used with streaming transfer
     * @param archiveFilePrefix
     *            Prefix to use when archiving files
     * @param archiveFileSuffix
     *            Suffix to use when archiving files
     * @param targetFilePrefix
     *            Prefix to use when doing file operations
     * @param targetFileSuffix
     *            Suffix to use when doing file operations
     * @param sftpAuthKeyPath
     *            Path to private key to be used in SFTP authentication
     */
    public VfsOperationOptions(String sourceDirectory, String targetDirectory,
                               String filePatternRegex, String archiveDirectory,
                               boolean createMissingDirectories, boolean lockEnabled,
                               boolean ftpPassiveMode, boolean streamingTransfer, String userDirIsRoot,
                               String streamingBlockSize, int retryCount,
                               int retryWait, int sftpTimeout,
                               String archiveFilePrefix, String archiveFileSuffix,
                               String targetFilePrefix, String targetFileSuffix, String sftpAuthKeyPath) {
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.filePatternRegex = filePatternRegex;
        this.archiveDirectory = archiveDirectory;
        this.createMissingDirectories = createMissingDirectories;
        this.lockEnabled = lockEnabled;
        this.ftpPassiveMode = ftpPassiveMode;
        this.streamingTransfer = streamingTransfer;
        this.userDirIsRoot = userDirIsRoot;
        this.streamingBlockSize = streamingBlockSize;
        this.retryCount = retryCount;
        this.retryWait = retryWait;
        this.sftpTimeout = sftpTimeout;
        this.archiveFilePrefix = archiveFilePrefix;
        this.archiveFileSuffix = archiveFileSuffix;
        this.targetFilePrefix = targetFilePrefix;
        this.targetFileSuffix = targetFileSuffix;
        this.sftpAuthKeyPath = sftpAuthKeyPath;
    }

    /**
     * Returns source directory path.
     * 
     * @return Path for source directory
     */
    public String getSourceDirectory() {
        return sourceDirectory;
    }

    /**
     * Returns target directory path.
     * 
     * @return Path for target directory
     */
    public String getTargetDirectory() {
        return targetDirectory;
    }

    public String getTargetFileSuffix() {
        return this.targetFileSuffix;
    }

    public String getTargetFilePrefix() {
        return this.targetFilePrefix;
    }

    /**
     * Returns file pattern regular expression for selecting files from source
     * directory.
     * 
     * @return File pattern regex for selecting files
     */
    public String getFilePatternRegex() {
        return filePatternRegex;
    }

    /**
     * Returns archive directory path.
     * 
     * @return
     */
    public String getArchiveDirectory() {
        return archiveDirectory;
    }

    public String getArchiveFileSuffix() {
        return this.archiveFileSuffix;
    }

    public String getArchiveFilePrefix() {
        return this.archiveFilePrefix;
    }

    /**
     * Returns streaming block size.
     *
     * @return
     */
    public String getStreamingBlockSize() {
        return streamingBlockSize;
    }

    /**
     * Returns boolean flag indicating whether to create missing directories.
     * 
     * @return True/false whether to create missing directories
     */
    public boolean isCreateMissingDirectories() {
        return createMissingDirectories;
    }

    /**
     * Returns boolean flag indicating whether to use lock file to orchestrate
     * multi-threading.
     * 
     * @return True/false whether to use lock file for multi-threading
     *         orchestration
     */
    public boolean isLockEnabled() {
        return lockEnabled;
    }

    /**
     * Returns the retry count.
     * @return
     */
    public int getRetryCount() {
        return this.retryCount;
    }

    /**
     * Returns the retry wait.
     *
     * @return
     */
    public int getRetryWait() {
        return this.retryWait;
    }

    /**
     * Returns boolean flag indicating whether to use FTP passive mode.
     * 
     * @return True/false whether to use passive mode for FTP connection
     */
    public boolean isFtpPassiveModeEnabled() {
        return ftpPassiveMode;
    }

    /**
     * Returns boolean flag indicating whether to use streaming for file transfers.
     *
     * @return True/false whether to use streaming for file transfers
     */
    public boolean isStreamingTransferEnabled() {
        return streamingTransfer;
    }

    /**
     * Returns boolean flag indicating whether to use user directory as root.
     *
     * @return True/false whether to use user directory as root
     */
    public String getUserDirIsRootOption() {
        return this.userDirIsRoot;
    }

    /**
     * Returns integer value of SFTP timeout in milliseconds.
     *
     * @return SFTP timeout value in milliseconds
     */
    public int getSftpTimeout() {
        return this.sftpTimeout;
    }

    /**
     * Return the path to the private key to use for authenticating to a SFTP endpoint.
     *
     * @return Key path
     */
    public String getSftpAuthKeyPath() {
        return this.sftpAuthKeyPath;
    }

    /**
     * Returns builder instance to build VfsOperationOptions bean.
     * 
     * @return Builder for building VfsOperationOptions bean
     */
    public static Builder with() {
        return new BuilderImpl();
    }

    /**
     * Inherited method to perform equality instance check.
     * 
     * @return True if instance equals to this one, otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        VfsOperationOptions that = (VfsOperationOptions) o;

        if (createMissingDirectories != that.createMissingDirectories)
            return false;
        if (lockEnabled != that.lockEnabled)
            return false;
        if (ftpPassiveMode != that.ftpPassiveMode)
            return false;
        if (streamingTransfer != that.streamingTransfer)
            return false;
        if (sourceDirectory != null ? !sourceDirectory.equals(that.sourceDirectory) : that.sourceDirectory != null)
            return false;
        if (targetDirectory != null ? !targetDirectory.equals(that.targetDirectory) : that.targetDirectory != null)
            return false;
        if (filePatternRegex != null ? !filePatternRegex.equals(that.filePatternRegex) : that.filePatternRegex != null)
            return false;
        if (streamingBlockSize != null ? !streamingBlockSize.equals(that.streamingBlockSize) : that.streamingBlockSize != null)
            return false;
        if(targetFilePrefix != null ? !targetFilePrefix.equals(that.targetFilePrefix) : that.targetFilePrefix != null)
            return false;
        if(targetFileSuffix != null ? !targetFileSuffix.equals(that.targetFileSuffix) : that.targetFileSuffix != null)
            return false;
        if(archiveFilePrefix != null ? !archiveFilePrefix.equals(that.archiveFilePrefix) : that.archiveFilePrefix != null)
            return false;
        if(archiveFileSuffix != null ? !archiveFileSuffix.equals(that.archiveFileSuffix) : that.archiveFileSuffix != null)
            return false;
        return !(archiveDirectory != null ? !archiveDirectory.equals(that.archiveDirectory)
                : that.archiveDirectory != null);

    }

    /**
     * Inherited method to individualize hash code for this instance.
     * 
     * return Integer value calculated for this class instance.
     */
    @Override
    public int hashCode() {
        int result = sourceDirectory != null ? sourceDirectory.hashCode() : 0;
        result = 31 * result + (targetDirectory != null ? targetDirectory.hashCode() : 0);
        result = 31 * result + (filePatternRegex != null ? filePatternRegex.hashCode() : 0);
        result = 31 * result + (archiveDirectory != null ? archiveDirectory.hashCode() : 0);
        result = 31 * result + (streamingBlockSize != null ? streamingBlockSize.hashCode() : 0);
        result = 31 * result + (createMissingDirectories ? 1 : 0);
        result = 31 * result + (lockEnabled ? 1 : 0);
        result = 31 * result + (ftpPassiveMode ? 1 : 0);
        result = 31 * result + (streamingTransfer ? 1 : 0);
        return result;
    }

    /**
     * Interface class for VfsOperationOptions builder.
     *
     */
    public interface Builder {
        /**
         * Method to build VfsOperationOptions instance.
         * 
         * @return New VfpOprationOptions instance
         */
        VfsOperationOptions build();

        /**
         * Setter for source directory path.
         * 
         * @param sourceDirectory
         *            Source directory path
         * @return This builder instance
         */
        Builder sourceDirectory(String sourceDirectory);

        /**
         * Setter for target directory path.
         * 
         * @param targetDirectory
         *            Target directory path
         * @return This builder instance
         */
        Builder targetDirectory(String targetDirectory);

        /**
         * Setter for prefix for filename after file operation.
         * @param prefix
         * @return
         */
        Builder targetFilePrefix(String prefix);

        /**
         * Setter for suffix for filename after file operation.
         * @param suffix
         * @return
         */
        Builder targetFileSuffix(String suffix);

        /**
         * Setter for file patter regular expression for file selecting.
         * 
         * @param filePatternRegex
         *            File pattern regular expression
         * @return This builder instance
         */
        Builder filePatternRegex(String filePatternRegex);

        /**
         * Setter for file archiving directory path.
         * 
         * @param archiveDirectory
         *            Archive directory path
         * @return This builder instance
         */
        Builder archiveDirectory(String archiveDirectory);

        /**
         * Setter for file prefix for archived files.
         * @param prefix
         * @return
         */
        Builder archiveFilePrefix(String prefix);

        /**
         * Setter for file suffix for archived files.
         * @param suffix
         * @return
         */
        Builder archiveFileSuffix(String suffix);

        /**
         * Setter for boolean flag whether to create missing directories.
         * 
         * @param createMissingDirectories
         *            Boolean value indicating whether to create missing
         *            directories
         * @return This builder instance
         */
        Builder createMissingDirectories(boolean createMissingDirectories);

        /**
         * Setter for boolean flag whether to use lock file to orchestrate
         * multi-threading.
         * 
         * @param lockEnabled
         *            Boolean value indicating whether to use lock file
         * @return This builder instance
         */
        Builder lockEnabled(boolean lockEnabled);

        /**
         * Setter for boolean flag whether to use passive mode with FTP
         * connection.
         * 
         * @param ftpPassiveMode
         *            Boolean value indicating whether to use FTP passive mode
         * @return This builder instance
         */
        Builder ftpPassiveModeEnabled(boolean ftpPassiveMode);

        /**
         * Setter for boolean flag whether to use streaming for file transfer.
         *
         * @param streamingTransfer
         *            Boolean value indicating whether to use streaming transfer
         * @return This builder instance
         */
        Builder streamingTransferEnabled(boolean streamingTransfer);

        /**
         * Setter for boolean flag whether to use user directory as root.
         *
         * @param userDirIsRoot
         *            Boolean value indicating whether to use user directory as root
         * @return This builder instance
         */
        Builder userDirIsRootEnabled(String userDirIsRoot);

        /**
         * Setter for block size of streaming transfer.
         *
         * @param streamingBlockSize
         *            Value for block size of streaming transfer.
         * @return This builder instance
         */
        Builder streamingBlockSize(String streamingBlockSize);

        /**
         * Setter for retry count.
         *
         * @param retryCount
         * @return
         */
        Builder retryCount(int retryCount);

        /**
         * Setter for retry wait time.
         *
         * @param retryWait
         * @return
         */
        Builder retryWait(int retryWait);

         /** Setter for SFTP timeout.
         *
         * @param timeout Value of the timeout in milliseconds.
         * @return This builder instance
         */
        Builder sftpTimeout(int timeout);

        Builder sftpAuthKeyPath(String path);
    }

    /**
     * Builder implementation class.
     *
     */
    private static final class BuilderImpl implements Builder {
        private String sourceDirectory;
        private String targetDirectory;
        private String filePatternRegex;
        private String archiveDirectory;
        private String streamingBlockSize;
        private boolean createMissingDirectories;
        private boolean lockEnabled;
        private boolean ftpPassiveMode;
        private boolean streamingTransfer;
        private String userDirIsRoot;
        private int retryCount;
        private int retryWait;
        private int sftpTimeout;
        private String archiveFileSuffix;
        private String archiveFilePrefix;
        private String targetFileSuffix;
        private String targetFilePrefix;
        private String sftpKeyPath;

        @Override
        public VfsOperationOptions build() {
            return new VfsOperationOptions(sourceDirectory, targetDirectory, filePatternRegex, archiveDirectory,
                    createMissingDirectories, lockEnabled, ftpPassiveMode, streamingTransfer, userDirIsRoot,
                    streamingBlockSize, retryCount, retryWait, sftpTimeout, archiveFilePrefix, archiveFileSuffix,
                    targetFilePrefix, targetFileSuffix, sftpKeyPath);
        }

        @Override
        public Builder sourceDirectory(String sourceDirectory) {
            this.sourceDirectory = sourceDirectory;

            return this;
        }

        @Override
        public Builder targetDirectory(String targetDirectory) {
            this.targetDirectory = targetDirectory;

            return this;
        }

        @Override
        public Builder targetFilePrefix(String prefix) {
            this.targetFilePrefix = prefix;

            return this;
        }

        @Override
        public Builder targetFileSuffix(String suffix) {
            this.targetFileSuffix = suffix;

            return this;
        }

        @Override
        public Builder filePatternRegex(String filePatternRegex) {
            this.filePatternRegex = filePatternRegex;

            return this;
        }

        @Override
        public Builder archiveDirectory(String archiveDirectory) {
            this.archiveDirectory = archiveDirectory;

            return this;
        }

        @Override
        public Builder archiveFilePrefix(String prefix) {
            this.archiveFilePrefix = prefix;

            return this;
        }

        @Override
        public Builder archiveFileSuffix(String suffix) {
            this.archiveFileSuffix = suffix;

            return this;
        }

        @Override
        public Builder streamingBlockSize(String streamingBlockSize) {
            this.streamingBlockSize = streamingBlockSize;

            return this;
        }

        @Override
        public Builder retryCount(int retryCount) {
            this.retryCount = retryCount;

            return this;
        }

        @Override
        public Builder retryWait(int retryWait) {
            this.retryWait = retryWait;

            return this;
        }

        @Override
        public Builder createMissingDirectories(boolean createMissingDirectories) {
            this.createMissingDirectories = createMissingDirectories;

            return this;
        }

        @Override
        public Builder lockEnabled(boolean lockEnabled) {
            this.lockEnabled = lockEnabled;

            return this;
        }

        @Override
        public Builder ftpPassiveModeEnabled(boolean ftpPassiveMode) {
            this.ftpPassiveMode = ftpPassiveMode;

            return this;
        }

        @Override
        public Builder streamingTransferEnabled(boolean streamingTransfer) {
            this.streamingTransfer = streamingTransfer;

            return this;
        }

        @Override
        public Builder userDirIsRootEnabled(String userDirIsRoot) {
            this.userDirIsRoot = userDirIsRoot;

            return this;
        }

        @Override
        public Builder sftpTimeout(int timeout) {
            this.sftpTimeout = timeout;

            return this;
        }

        @Override
        public Builder sftpAuthKeyPath(String path) {
            this.sftpKeyPath = (path != null && path.trim().length() > 0) ? path : null;

            return this;
        }
    }
}
