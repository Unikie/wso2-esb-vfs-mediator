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

import fi.mystes.synapse.mediator.vfs.VFSTestHelper.*;
import org.apache.commons.logging.Log;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static fi.mystes.synapse.mediator.vfs.VFSTestHelper.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

public class VfsFileTransferUtilityTest {
    private static final String SOURCE_DIR = "file:///tmp/foo/bar/dir";
    private static final String TARGET_DIR = "file:///tmp/bar/foo/dir";
    private static final String ARCHIVE_DIR = "file:///tmp/bar/foo/archive";

    @After
    public void tearDown() throws FileSystemException {
        deleteDirectory(SOURCE_DIR);
        deleteDirectory(TARGET_DIR);
        deleteDirectory(ARCHIVE_DIR);
    }

    @Before
    public void setUp() throws FileSystemException {
        createDirectory(SOURCE_DIR);
        createDirectory(TARGET_DIR);
        createDirectory(ARCHIVE_DIR);
    }

    @Test(expected = FileSystemException.class)
    public void copyFilesFailsWhenSourceDirectoryNotDirectory() throws FileSystemException {
        new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory("tmp:///not/existing/directory").targetDirectory(TARGET_DIR).build()).copyFiles();
    }

    @Test(expected = FileSystemException.class)
    public void copyFilesFailsWhenTargetDirectoryNotDirectory() throws FileSystemException {
        new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory("tmp:///not/existing/directory").build()).copyFiles();
    }

    @Test
    public void copyFilesSuccessfulWhenSourceAndTargetDirectoryValid() throws FileSystemException {
        int copyCount = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).build()).copyFiles();
        assertEquals("Utility return wrong file copied count", 0, copyCount);
    }


    @Test
    public void copiesOneFileWhenValidSourceAndTargetDirectorySpecified() throws FileSystemException, IOException {
        // create test file set
        createTestFiles(SOURCE_DIR, 1);

        // copy files
        int copyCount = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).build()).copyFiles();

        // check that files were copied
        assertFilesExists(SOURCE_DIR, 1);
        assertFilesExists(TARGET_DIR, 1);
        assertEquals("Utility returned false file copied count", 1, copyCount);
    }

    @Test
    public void createsLockFileWhenCopyingFileIfLockEnabled() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestFile fileToCopy = createTestFiles(SOURCE_DIR, 1).get(0);
        String targetPath = filePath(TARGET_DIR, fileToCopy.getName());
        String lockFilePath = lockFilePath(targetPath);
        LockFileVerifier verifier = lockFileVerifierFor(lockFilePath);
        Log log = applyMockLog(verifier);
        new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).filePatternRegex(fileToCopy.getName()).lockEnabled(true).build()).copyFiles();
        assertFilesExists(TARGET_DIR, 1);
        verify(log).debug(expectedLockFileDebugString(verifier));
        verify(log).debug(expectedFileCopiedDebugString(fileToCopy, targetPath));
    }

    @Test
    public void doesNotCreateLockFileWhenCopyingFileIfLockNotEnabled() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestFile fileToCopy = createTestFiles(SOURCE_DIR, 1).get(0);
        String targetPath = filePath(TARGET_DIR, fileToCopy.getName());
        String lockFilePath = lockFilePath(targetPath);
        LockFileVerifier verifier = lockFileVerifierFor(lockFilePath);
        Log log = applyMockLog(verifier);
        new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).filePatternRegex(fileToCopy.getName()).lockEnabled(false).build()).copyFiles();
        assertFilesExists(TARGET_DIR, 1);
        expectCommonDebugLogging(log);
        verify(log).debug(expectedFileCopiedDebugString(fileToCopy, targetPath));
        Mockito.verifyNoMoreInteractions(log);
    }

    @Test
    public void copyTenFilesWhenValidSourceAndTargetAndArchiveDirectorySpecified() throws IOException {
        // create test file set
        createTestFiles(SOURCE_DIR, 10);

        // copy files
        int copyCount = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).archiveDirectory(ARCHIVE_DIR).build()).copyFiles();

        // check that files were copied
        assertFilesExists(SOURCE_DIR, 10);
        assertFilesExists(TARGET_DIR, 10);
        assertFilesExists(ARCHIVE_DIR, 10);
        assertEquals("Utility returned false file copied count", 10, copyCount);
    }

    @Test
    public void copyOneOfTenFilesWhenFileNamePatternSpecified() throws IOException {
        // create test file set
        createTestFiles(SOURCE_DIR, 9);
        createFile(SOURCE_DIR + "/theOne.txt", "this is file content");

        // copy files
        int copyCount = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).filePatternRegex("theOne.txt").build()).copyFiles();

        // check that files were copied
        assertFilesExists(SOURCE_DIR, 10);
        assertFilesExists(TARGET_DIR, 1);
        assertEquals("Utility returned false file copied count", 1, copyCount);
    }

    @Test
    public void copyTwoOfFiveFilesWhenFileSuffixPatternSpecified() throws IOException {
        // create three files with .txt suffix
        createTestFiles(SOURCE_DIR, 3);
        // create two files with .xml suffix
        createFile(SOURCE_DIR + "/one.xml", "this is file content for test file 1");
        createFile(SOURCE_DIR + "/two.XML", "this is file content for test file 2");

        int copyCount = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).filePatternRegex(".*.xml|.*.XML").build()).copyFiles();

        // check that files were copied
        assertFilesExists(SOURCE_DIR, 5);
        assertFilesExists(TARGET_DIR, 2);
        assertEquals("Utility returned false file copied count", 2, copyCount);
    }

    @Test(expected = FileSystemException.class)
    public void moveFilesFailsWhenSourceDirectoryNotDirectory() throws FileSystemException {
        new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory("tmp:///not/existing/directory").targetDirectory(TARGET_DIR).build()).moveFiles();
    }

    @Test(expected = FileSystemException.class)
    public void moveFilesFailsWhenTargetDirectoryNotDirectory() throws FileSystemException {
        new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory("tmp:///not/existing/directory").build()).moveFiles();
    }

    @Test
    public void moveFilesSuccessfulWhenSourceAndTargetDirectoryValid() throws FileSystemException {
        int moveCount = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).build()).moveFiles();
        assertEquals("Utility return wrong file move count", 0, moveCount);
    }

    @Test
    public void moveTenFilesWhenValidSourceAndTargetDirectorySpecified() throws FileSystemException, IOException {

        // create test file set
        createTestFiles(SOURCE_DIR, 10);

        // move files
        int moveCount = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).build()).moveFiles();

        // check that files were moved
        assertFilesExists(TARGET_DIR, 10);
        assertEquals("Utility return wrong file moved count", 10, moveCount);
    }

    @Test
    public void moveTenFilesWhenValidSourceAndTargetAndArchiveDirectorySpecified() throws FileSystemException, IOException {

        // create test file set
        createTestFiles(SOURCE_DIR, 10);

        // move files
        int moveCount = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).archiveDirectory(ARCHIVE_DIR).build()).moveFiles();

        // check that files were moved
        assertFilesExists(TARGET_DIR, 10);
        assertFilesExists(ARCHIVE_DIR, 10);
        assertEquals("Utility return wrong file moved count", 10, moveCount);
    }

    @Test
    public void createsLockFileWhenMovingFileIfLockEnabled() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestFile fileToMove = createTestFiles(SOURCE_DIR, 1).get(0);
        String targetPath = filePath(TARGET_DIR, fileToMove.getName());
        String lockFilePath = lockFilePath(targetPath);
        LockFileVerifier verifier = lockFileVerifierFor(lockFilePath);
        Log log = applyMockLog(verifier);
        new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).lockEnabled(true).build()).moveFiles();
        assertFilesExists(TARGET_DIR, 1);
        verify(log).debug(expectedLockFileDebugString(verifier));
        verify(log).debug(expectedFileMovedDebugString(fileToMove, targetPath));
    }

    @Test
    public void doesNotCreateLockFileWhenMovingFilesIfLockNotEnabled() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestFile fileToMove = createTestFiles(SOURCE_DIR, 1).get(0);
        String targetPath = filePath(TARGET_DIR, fileToMove.getName());
        String lockFilePath = lockFilePath(targetPath);
        LockFileVerifier verifier = lockFileVerifierFor(lockFilePath);
        Log log = applyMockLog(verifier);
        new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).filePatternRegex(fileToMove.getName()).lockEnabled(false).build()).moveFiles();
        assertFilesExists(TARGET_DIR, 1);
        expectCommonDebugLogging(log);
        verify(log).debug(expectedFileMovedDebugString(fileToMove, targetPath));
        Mockito.verifyNoMoreInteractions(log);
    }

    @Test
    public void moveOneOfTenFilesWhenFileNamePatternSpecified() throws IOException {
        // create test file set
        createTestFiles(SOURCE_DIR, 9);
        createFile(SOURCE_DIR + "/theOne.txt", "this is file content");

        // move files
        int count = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).filePatternRegex("theOne.txt").build()).moveFiles();

        // check that files were copied
        assertFilesExists(SOURCE_DIR, 9);
        assertFilesExists(TARGET_DIR, 1);
        assertEquals("Utility returned false file copied count", 1, count);
    }

    @Test
    public void moveTwoOfFiveFilesWhenFileSuffixPatternSpecified() throws IOException {
        // create three files with .txt suffix
        createTestFiles(SOURCE_DIR, 3);
        // create two files with .xml suffix
        createFile(SOURCE_DIR + "/one.xml", "this is file content for test file 1");
        createFile(SOURCE_DIR + "/two.XML", "this is file content for test file 2");

        // move files
        int count = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).filePatternRegex(".*.xml|.*.XML").build()).moveFiles();

        // check that files were copied
        assertFilesExists(SOURCE_DIR, 3);
        assertFilesExists(TARGET_DIR, 2);
        assertEquals("Utility returned false file copied count", 2, count);
    }

    @Test
    public void archiveTenFilesWhenDynamicArchiveDirectorySpecifiedWithCreateMissingDirectoriesFlag() throws IOException {
        // create test file set
        createTestFiles(SOURCE_DIR, 10);

        String dynamicArchive = ARCHIVE_DIR + "/how/deep/is/the/rabbit/hole";

        int copyCount = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).archiveDirectory(dynamicArchive).createMissingDirectories(true).build()).copyFiles();

        // check that files were copied
        assertFilesExists(SOURCE_DIR, 10);
        assertFilesExists(TARGET_DIR, 10);
        assertFilesExists(dynamicArchive, 10);
        assertEquals("Utility returned false file copied count", 10, copyCount);
    }

    @Test
    public void copyTenFilesWhenDynamicTargetDirectorySpecifiedWithCreateMissingDirectoriesFlag() throws IOException {
        // create test file set
        createTestFiles(SOURCE_DIR, 10);

        String dynamicTarget = TARGET_DIR + "/how/deep/is/the/rabbit/hole";

        // copy files
        int copyCount = new VfsFileTransferUtility(VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(dynamicTarget).archiveDirectory(ARCHIVE_DIR).createMissingDirectories(true).build()).copyFiles();

        // check that files were copied
        assertFilesExists(SOURCE_DIR, 10);
        assertFilesExists(dynamicTarget, 10);
        assertFilesExists(ARCHIVE_DIR, 10);
        assertEquals("Utility returned false file copied count", 10, copyCount);
    }

    //@Test // FIXME: java.lang.NoClassDefFoundError: org/apache/commons/net/ftp/parser/FTPFileEntryParserFactory
    public void testFtpPassiveFlag() throws Exception {
        createTestFiles(SOURCE_DIR, 3);

        VfsOperationOptions opts = VfsOperationOptions.with().sourceDirectory(SOURCE_DIR).targetDirectory(TARGET_DIR).archiveDirectory(ARCHIVE_DIR).createMissingDirectories(true).ftpPassiveModeEnabled(true).build();
        int copyCount = new VfsFileTransferUtility((opts)).copyFiles();

        assertFilesExists(SOURCE_DIR, 3);
        assertFilesExists(TARGET_DIR, 3);
        assertFilesExists(ARCHIVE_DIR, 3);
        assertEquals("Utility returned wrong file copied count", 3, copyCount);
    }

    private String expectedFolderNotExistsErrorString(String folder) {
        return "Path " + folder + " is not a folder!";
    }

    private String expectedFileMovedDebugString(TestFile testFile, String targetPath) {
        return "File moved to " + targetPath;
    }

    private String lockFilePath(String targetFilePath) {
        return targetFilePath + ".lock";
    }

    private LockFileVerifier lockFileVerifierFor(final String lockFilePath) {
        return new LockFileVerifier() {

            @Override
            public void verify() throws FileSystemException {
                assertFileExists(lockFilePath);
            }

            @Override
            public String getLockFilePath() {
                return lockFilePath;
            }
        };
    }

    private Log applyMockLog(final LockFileVerifier verifier) throws NoSuchFieldException, IllegalAccessException {
        Log log = mock(Log.class);

        if (verifier != null) {
            doAnswer(new Answer<Void>() {

                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    verifier.verify();
                    return null;
                }
            }).when(log).debug(expectedLockFileDebugString(verifier));
        }
        Field field = VfsFileTransferUtility.class.getDeclaredField("log");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, log);
        return log;
    }

    private static interface LockFileVerifier {
        void verify() throws IOException;

        String getLockFilePath();
    }

    private String expectedLockFileDebugString(final LockFileVerifier verifier) {
        return "Created lock file " + verifier.getLockFilePath();
    }

    private String expectedFileCopiedDebugString(TestFile testFile, String targetPath) {
        return "File copied to " + targetPath;
    }

    private void expectCommonDebugLogging(Log log) {
        verify(log).debug(startsWith("Starting operation"));
        verify(log).debug(startsWith("Source directory"));
        verify(log).debug(startsWith("Applying file pattern"));
        verify(log).debug(startsWith("Found 1 files"));
        verify(log).debug(startsWith("Processing file"));
        verify(log).debug(startsWith("About to"));
    }
}