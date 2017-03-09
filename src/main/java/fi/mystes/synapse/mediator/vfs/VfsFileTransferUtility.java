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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.synapse.SynapseException;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class providing methods for VFS operations (copy/move).
 */
public class VfsFileTransferUtility {

    private static final Log log = LogFactory.getLog(VfsFileTransferUtility.class);
    private static final String LOCK_FILE_SUFFIX = ".lock";
    private static final String DEFAULT_STREAMING_BLOCK_SIZE = "1024";

    private final VfsOperationOptions options;
    private final FileSystemOptions fsOptions;

    /**
     * Constructor.
     * 
     * @param options
     *            VFS operation options
     * @throws NullPointerException
     *             If given VFS operation options instance is not initiated
     */
    public VfsFileTransferUtility(VfsOperationOptions options) {
        if (options == null) {
            throw new NullPointerException("options cannot be null");
        }
        this.options = options;
        fsOptions = new FileSystemOptions();
        if (options.isFtpPassiveModeEnabled()) {
            FtpFileSystemConfigBuilder.getInstance().setPassiveMode(fsOptions, true);
        }

        SftpFileSystemConfigBuilder.getInstance().setTimeout(fsOptions, options.getSftpTimeout());
    }

    /**
     * Performs copy VFS operation.
     * 
     * @return Amount of files being copied
     * @throws FileSystemException
     *             If file copying fails
     */
    public int copyFiles() throws FileSystemException {
        return doOperation(Operation.COPY);
    }

    /**
     * Performs move VFS operation.
     * 
     * @return Amount of files being moved
     * @throws FileSystemException
     *             If file moving fails
     */
    public int moveFiles() throws FileSystemException {
        return doOperation(Operation.MOVE);
    }

    /**
     * Helper method for file listing.
     * 
     * @param sourceDirectoryPath
     *            Source directory path to list files from
     * @param filePatternRegex
     *            File pattern regex for file listing
     * @return Array containing file object listed by given file pattern regex
     * @throws FileSystemException
     *             If file listing fails
     */
    private FileObject[] listFiles(final FileSystemManager manager, final String sourceDirectoryPath, final String filePatternRegex) throws FileSystemException {

        final FileObject fromDirectory = resolveFile(manager, sourceDirectoryPath);
        FileObject[] fileList = null;

        try {
            log.debug("Source directory: " + fileObjectNameForDebug(fromDirectory));
            // check that both of the parameters are folders
            isFolder(fromDirectory);

            Retrier<FileObject[]> retrier = new Retrier<FileObject[]>() {
                @Override
                public FileObject[] operation() throws FileSystemException {
                    FileObject[] fileListRet = null;
                    if (filePatternRegex != null) {
                        log.debug("Applying file pattern " + filePatternRegex);
                        FileFilter ff = initFileFilter(filePatternRegex);
                        fileListRet = fromDirectory.findFiles(new FileFilterSelector(ff));
                    } else {
                        // List all the files in that directory and copy each
                        fileListRet = fromDirectory.getChildren();
                    }
                    log.debug("Found " + fileListRet.length + " files in source directory");

                    return fileListRet;
                }
            };

            fileList = retrier.doWithRetry(options.getRetryCount(), options.getRetryWait());

        } finally {
            try {
                fromDirectory.close();
            } catch (Exception ignored) {}
        }

        return fileList;
    }

    /**
     * Helper method to execute file copy operation.
     * 
     * @param file
     *            FileObject of the file to be copied
     * @param targetDirectoryPath
     *            FileObject of the target directory where the file will be
     *            copied
     * @return true if give file was a file and it was successfully copied to
     *         destination directory. False if given file is not a file or given
     *         destination directory is not a directory
     * @throws FileSystemException
     *             If file copy operation fails
     */
    private boolean copyFile(final FileSystemManager manager, final FileObject file, String targetDirectoryPath, boolean lockEnabled, TargetType targetType)
            throws FileSystemException {
        if (file.getType() == FileType.FILE) {
            final String targetPath = targetDirectoryPath + "/" +
                    getPrefix(targetType) +
                    FilenameUtils.removeExtension(file.getName().getBaseName()) +
                    getSuffix(targetType) +
                    (FilenameUtils.getExtension(file.getName().getBaseName()).isEmpty() ? "" : ("." + FilenameUtils.getExtension(file.getName().getBaseName())));

            String lockFilePath = null;
            if (lockEnabled) {
                lockFilePath = createLockFile(manager, targetPath);
            }

            final FileObject newLocation = resolveFile(manager, targetPath);

            try {
                log.debug(
                        "About to copy " + fileObjectNameForDebug(file) + " to " + fileObjectNameForDebug(newLocation));

                if(options.isStreamingTransferEnabled()) {
                    // TODO: Should we use the retrier for this?
                    streamFromFileToFile(file, resolveFile(manager, targetPath));
                }
                else{
                    new Retrier<Object>() {
                        @Override
                        public Object operation() throws FileSystemException {
                            newLocation.copyFrom(file, Selectors.SELECT_SELF);
                            return null;
                        }
                    }.doWithRetry(options.getRetryCount(), options.getRetryWait());
                }

                newLocation.close();
                file.close();
                log.debug("File copied to " + fileObjectNameForDebug(newLocation));
            } finally {
                if (lockFilePath != null) {
                    deleteLockFile(manager, lockFilePath);
                }
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * Creates File filter that matches file names to given regex
     *
     * @param regex
     * @return
     */
    private FileFilter initFileFilter(final String regex) {
        FileFilter ff = new FileFilter() {
            public boolean accept(FileSelectInfo fileInfo) {
                FileObject fo = fileInfo.getFile();
                return fo.getName().getBaseName().matches(regex);
            }

        };
        return ff;
    }

    /**
     * Helper method to execute given operation.
     * 
     * @param operation
     *            Operation to execute
     * @return Amount of files operated
     * @throws FileSystemException
     *             If given file operation fails
     */
    private int doOperation(Operation operation) throws FileSystemException {
        FileSystemManager manager = null;
        try {
            manager = initManager();

            FileObject toDirectory = resolveFile(manager, options.getTargetDirectory());
            log.debug("Starting operation " + operation + ", target directory: " + fileObjectNameForDebug(toDirectory));
            validateFolder(toDirectory, options.isCreateMissingDirectories());

            if (options.getArchiveDirectory() != null) {
                FileObject archiveDir = resolveFile(manager, options.getArchiveDirectory());
                log.debug("Using archive directory: " + fileObjectNameForDebug(archiveDir));
                validateFolder(archiveDir, options.isCreateMissingDirectories());
                archiveDir.close();
            }

            FileObject[] children = listFiles(manager, options.getSourceDirectory(), options.getFilePatternRegex());
            int fileProcessed = 0;
            for (int i = 0; i < children.length; i++) {
                log.debug("Processing file #" + (i + 1));
                // archive file here first before processing it
                if (options.getArchiveDirectory() != null) {
                    log.debug("Copying file to archive directory");
                    copyFile(manager, children[i], options.getArchiveDirectory(), options.isLockEnabled(), TargetType.ARCHIVE);
                }
                if (operation == Operation.MOVE) {
                    if (moveFile(manager, children[i], options.getTargetDirectory(), options.isLockEnabled())) {
                        // FileObject was copied successfully
                        fileProcessed++;
                    }
                } else if (operation == Operation.COPY) {
                    if (copyFile(manager, children[i], options.getTargetDirectory(), options.isLockEnabled(), TargetType.TARGET)) {
                        // FileObject was copied successfully
                        fileProcessed++;
                    }
                } else {
                    // unsupported operation
                }

            }
            toDirectory.close();

            return fileProcessed;
        } finally {
            if(manager != null) {
                ((StandardFileSystemManager) manager).close();
            }
        }
    }

    /**
     * Helper method to execute file move operation.
     * 
     * @param file
     *            FileObject of the file to be moved
     * @param toDirectoryPath
     * @return true if give file was a file and it was successfully moved to
     *         destination directory. False if given file is not a file or given
     *         destination directory is not a directory
     * @throws FileSystemException
     *             If file move operation fails
     */
    private boolean moveFile(final FileSystemManager manager, final FileObject file, String toDirectoryPath, boolean lockEnabled) throws FileSystemException {
        if (file.getType() == FileType.FILE) {
            final String targetPath = toDirectoryPath + "/" +
                    getPrefix(TargetType.TARGET) +
                    FilenameUtils.removeExtension(file.getName().getBaseName()) +
                    getSuffix(TargetType.TARGET) +
                    (FilenameUtils.getExtension(file.getName().getBaseName()).isEmpty() ? "" : ("." + FilenameUtils.getExtension(file.getName().getBaseName())));

            String lockFilePath = null;
            if (lockEnabled) {
                lockFilePath = createLockFile(manager, targetPath);
            }

            final FileObject newLocation = resolveFile(manager, targetPath);

            try {
                log.debug(
                        "About to move " + fileObjectNameForDebug(file) + " to " + fileObjectNameForDebug(newLocation));

                if(options.isStreamingTransferEnabled()) {
                    streamFromFileToFile(file, resolveFile(manager, targetPath)); // TODO TODO could fail. What if we fail during streaming and continue? Do we get targetFile which has the first streamed fragment and then the whole file again?
                }
                else{
                    new Retrier<Object>() {
                        @Override
                        public Object operation() throws FileSystemException {
                            newLocation.copyFrom(file, Selectors.SELECT_SELF); // could fail
                            return null;
                        }
                    }.doWithRetry(options.getRetryCount(), options.getRetryWait());
                }

                newLocation.close();
                file.delete();
                file.close();
                log.debug("File moved to " + fileObjectNameForDebug(newLocation));
            } finally {
                if (lockFilePath != null) {
                    deleteLockFile(manager, lockFilePath);
                }
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * Helper method to create lock file for multithreading.
     * 
     * @param targetPath
     *            Target file path
     * @return URI of lock file
     * @throws FileSystemException
     *             If lock file creation fails
     */
    private String createLockFile(final FileSystemManager manager, final String targetPath) throws FileSystemException {
        FileObject lockFile = resolveFile(manager, lockFilePath(targetPath));;
        log.debug("About to create lock file: " + fileObjectNameForDebug(lockFile));
        if (lockFile.exists()) {
            throw new FileSystemException(
                    "Lock file " + fileObjectNameForDebug(lockFile) + " already exists, refusing to create lock file");
        }
        lockFile.createFile();
        log.debug("Created lock file " + fileObjectNameForDebug(lockFile));

        String uri = lockFile.getName().getURI();

        lockFile.close();

        return uri;
    }

    /**
     * Helper method to stream file from source to destination folder.
     *
     * @param inputFile
     *            FileObject of input file
     * @param outputFile
     *            FileObject of output file
     * @return Boolean value of success. It is based on file length comparison.
     * @throws FileSystemException
     *             If given file operation fails
     */
    private void streamFromFileToFile(FileObject inputFile, FileObject outputFile) {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = inputFile.getContent().getInputStream();

            outputStream = outputFile.getContent().getOutputStream();

            String blockSize = DEFAULT_STREAMING_BLOCK_SIZE;

            if(options.getStreamingBlockSize() == null || !options.getStreamingBlockSize().matches("^\\d+$")) {
                blockSize = DEFAULT_STREAMING_BLOCK_SIZE;
                log.warn("Streaming block size not numeric, using default value of " + DEFAULT_STREAMING_BLOCK_SIZE);
            }
            else{
                blockSize = options.getStreamingBlockSize();
            }

            int length;
            byte[] buffer = new byte[new Integer(blockSize)];
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new SynapseException("Unexpected error during the file transfer", e);
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch(IOException ex){
                // making sure that output streams were closed in error situation
            }

            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch(IOException ex) {
                // making sure that inputstream is closed
            }

        }

    }

    /**
     * Helper method to delete lock file.
     * 
     * @param lockFilePath
     *            Lock file to be deleted
     * @throws FileSystemException
     *             If lock file deletion fails
     */
    private void deleteLockFile(final FileSystemManager manager, final String lockFilePath) throws FileSystemException {
        new Retrier<Object>() {
            @Override
            public Object operation() throws FileSystemException {
                FileObject lockFile = resolveFile(manager, lockFilePath);
                log.debug("Deleting lock file: " + fileObjectNameForDebug(lockFile));
                lockFile.delete();
                lockFile.close();

                return null;
            }
        }.doWithRetry(options.getRetryCount(), options.getRetryWait());
    }

    /**
     * Helper method to get lock file with file suffix.
     * 
     * @param targetPath
     *            Lock file path
     * @return Absolute file (with suffix) path
     */
    private String lockFilePath(String targetPath) {
        return targetPath + LOCK_FILE_SUFFIX;
    }

    /**
     * Helper method to check whether given path is folder.
     * 
     * @param path
     *            File path to be checked
     * @throws FileSystemException
     *             If given path is not a filder
     */
    private void isFolder(FileObject path) throws FileSystemException {
        if (path.getType() != FileType.FOLDER) {
            throw new FileSystemException("Path " + fileObjectNameForDebug(path) + " is not a folder!");
        }
    }

    /**
     * Helper method to validate existence of given directory. Will create
     * missing directory if createMissingDirectories flag is set to true.
     * 
     * @param path
     *            To be validated
     * @param createMissingDirectories
     *            Boolean flag to indicate whether to create missing directory
     * @throws FileSystemException
     *             If given path is not directory or directory creation fails
     */
    private void validateFolder(FileObject path, boolean createMissingDirectories) throws FileSystemException {
        if (createMissingDirectories) {
            createFolder(path);
        } else {
            isFolder(path);
        }
    }

    /**
     * Helper method to create directory by given path.
     * 
     * @param path
     *            Path object to create directory
     * @throws FileSystemException
     *             If directory creation fails
     */
    private void createFolder(FileObject path) throws FileSystemException {
        path.createFolder();
    }

    /**
     * Helper method to retrieve given file object's friendly URI.
     * 
     * @param fileObject
     *            To retrieve friendly URI from
     * @return Friendly URI or null
     */
    private String fileObjectNameForDebug(FileObject fileObject) {
        return fileObject == null ? null : fileObject.getName().getFriendlyURI();
    }

    /**
     * Helper method to resolve file by given path.
     * 
     * @param path
     *            To resolve file with
     * @return Resolved file
     * @throws FileSystemException
     *             If resolving file fails
     */
    private FileObject resolveFile(final FileSystemManager manager, final String path) throws FileSystemException {
        return new Retrier<FileObject>() {
            @Override
            public FileObject operation() throws FileSystemException {
                return manager.resolveFile(path, fsOptions);
            }
        }.doWithRetry(options.getRetryCount(), options.getRetryWait());
    }

    /**
     * Helper method to get the suffix for file operations.
     * @param targetType target folder or archive folder
     * @return resolved suffix
     */
    private String getSuffix(TargetType targetType) {
        switch (targetType) {
            case TARGET:
                return options.getTargetFileSuffix() == null ? "" : options.getTargetFileSuffix();
            case ARCHIVE:
                return options.getArchiveFileSuffix() == null ? "" : options.getArchiveFileSuffix();
            default:
                return "";
        }
    }

    /**
     * Helper method to get the prefix for file operations.
     * @param targetType target folder or archive folder
     * @return resolved prefix
     */
    private String getPrefix(TargetType targetType) {
        switch (targetType) {
            case TARGET:
                return options.getTargetFilePrefix() == null ? "" : options.getTargetFilePrefix();
            case ARCHIVE:
                return options.getArchiveFilePrefix() == null ? "" : options.getArchiveFilePrefix();
            default:
                return "";
        }
    }

    /**
     * Supported VFS operation enumerator.
     */
    private enum Operation {
        MOVE, COPY
    }


    /**
     * Target folder type.
     */
    private enum TargetType {
        TARGET, ARCHIVE
    }



    private FileSystemManager initManager() throws FileSystemException {
        StandardFileSystemManager manager = new StandardFileSystemManager();
        manager.init();

        return manager;
    }


    abstract static class Retrier<T> {

        private static final Log log = LogFactory.getLog(Retrier.class);

        public T doWithRetry(int retryCount, int retryWait) throws FileSystemException {
            boolean retry = false;
            int retries = 0;
            T ret = null;
            do {
                try {
                    ret = operation();
                    retry = false;
                } catch (FileSystemException e) {
                    log.debug("Connection failed! Retry count: " + retryCount + ", Retry wait: " + retryWait + ", Retries left: " + (retryCount - retries));
                    if (retries >= retryCount) {
                        throw e;
                    }
                    retry = true;
                    retries++;
                    try {
                        Thread.sleep(retryWait);
                    } catch (InterruptedException ignored) {
                        retry = false;
                    }
                }
            } while (retry);

            return ret;
        }

        public abstract T operation() throws FileSystemException;

    }
}
