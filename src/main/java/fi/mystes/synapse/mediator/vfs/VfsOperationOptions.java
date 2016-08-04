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
    private final String filePatternRegex;
    private final String archiveDirectory;
    private final String streamingBlockSize;
    private final boolean createMissingDirectories;
    private final boolean lockEnabled;
    private final boolean ftpPassiveMode;
    private final boolean streamingTransfer;

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
     * @param streamingBlockSize
     *            Block size used with streaming transfer
     */
    public VfsOperationOptions(String sourceDirectory, String targetDirectory, String filePatternRegex,
            String archiveDirectory, boolean createMissingDirectories, boolean lockEnabled, boolean ftpPassiveMode,
            boolean streamingTransfer, String streamingBlockSize) {
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.filePatternRegex = filePatternRegex;
        this.archiveDirectory = archiveDirectory;
        this.createMissingDirectories = createMissingDirectories;
        this.lockEnabled = lockEnabled;
        this.ftpPassiveMode = ftpPassiveMode;
        this.streamingTransfer = streamingTransfer;
        this.streamingBlockSize = streamingBlockSize;
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
         * Setter for block size of streaming transfer.
         *
         * @param streamingBlockSize
         *            Value for block size of streaming transfer.
         * @return This builder instance
         */
        Builder streamingBlockSize(String streamingBlockSize);

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

        @Override
        public VfsOperationOptions build() {
            return new VfsOperationOptions(sourceDirectory, targetDirectory, filePatternRegex, archiveDirectory,
                    createMissingDirectories, lockEnabled, ftpPassiveMode, streamingTransfer, streamingBlockSize);
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
        public Builder streamingBlockSize(String streamingBlockSize) {
            this.streamingBlockSize = streamingBlockSize;

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

    }
}
