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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

public class VFSTestHelper {

    private VFSTestHelper() {
        // static utility methods only - prevent instantiation
    }

    public static void deleteDirectory(String dirPath) throws FileSystemException {
        FileObject dir = VFS.getManager().resolveFile(dirPath);
        dir.delete(new AllFileSelector());
        dir.delete();
    }

    public static void createDirectory(String dirPath) throws FileSystemException {
        FileObject dir = VFS.getManager().resolveFile(dirPath);
        if (!dir.exists()) {
            dir.createFolder();
        }
    }

    public static String filePath(String dirPath, String fileName) {
        return dirPath + "/" + fileName;
    }

    public static TestFile createFile(String path, String content) throws IOException {
        FileSystemManager fsManager = VFS.getManager();
        FileObject file = fsManager.resolveFile(path);
        file.getContent().getOutputStream().write(content.getBytes());
        file.getContent().close();
        return new TestFileImpl(path, content, file.getContent().getSize());
    }

    public static void assertFileDoesNotExist(String path) throws FileSystemException {
        assertFalse(path + " should not exist", fileObjectExists(path));
    }

    public static boolean fileObjectExists(String path) throws FileSystemException {
        return VFS.getManager().resolveFile(path).exists();
    }

    public static void assertFileExists(String path) throws FileSystemException {
        assertTrue(path + " should exist", fileObjectExists(path));
    }

    public static void assertFilesExists(String directory, int expectedCount) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();
        FileObject file = fsManager.resolveFile(directory);
        FileObject[] children = file.getChildren();
        assertEquals("File count doesn't match", expectedCount, children.length);
    }

    public static void assertFileContentEquals(String path, String expectedContent) throws IOException {
        FileObject file = VFS.getManager().resolveFile(path);
        InputStream in = file.getContent().getInputStream();
        int length = 0;
        byte[] buffer = new byte[256];
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }

        assertEquals("Unexpected file content", expectedContent, out.toString());
    }

    public static List<TestFile> createTestFiles(String dirPath, int count) throws IOException {
        return createTestFiles(dirPath, count, 0);
    }

    public static List<TestFile> createTestFiles(String dirPath, int count, int nameOffset) throws IOException {
        List<TestFile> files = new ArrayList<TestFile>();

        for (int i = 0; i < count; i++) {
            int index = i + nameOffset;
            files.add(createFile(filePath(dirPath, "test" + index + ".txt"), "fileContent" + index));
        }

        return files;
    }

    public static interface TestFile {
        String getPath();

        String getName();

        String getContent();

        long getSize();
    }

    private static final class TestFileImpl implements TestFile {
        private final String path;
        private final String content;
        private final long size;
        private final String name;

        TestFileImpl(String path, String content, long size) throws FileSystemException {
            if (path == null) {
                throw new NullPointerException("path cannot be null");
            }
            if (content == null) {
                throw new NullPointerException("content cannot be null");
            }
            if (size < 0) {
                throw new IllegalArgumentException("invalid size: " + size);
            }
            this.path = path;
            this.content = content;
            this.size = size;
            this.name = VFS.getManager().resolveFile(path).getName().getBaseName();
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getContent() {
            return content;
        }

        @Override
        public long getSize() {
            return size;
        }
    }
}
