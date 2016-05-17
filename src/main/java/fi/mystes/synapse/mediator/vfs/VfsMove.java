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
import org.apache.synapse.SynapseException;

/**
 * VFS operation to move files from source to target directory.
 *
 */
public class VfsMove extends AbstractVfsOperation {

    /**
     * Performs move operation with delegation.
     * 
     * @throws FileSystemException
     *             if move operation fails
     */
    @Override
    protected int executeImpl(VfsOperationOptions options) throws FileSystemException {
        return getOperationDelegate().move(options);
    }

    /**
     * Validates source and target directory values.
     * 
     * @throws SynapseException
     *             source and/or target directory not given
     */
    @Override
    protected void validateOptions(VfsOperationOptions options) {
        if (options.getSourceDirectory() == null) {
            throw new SynapseException("Source directory not set");
        }
        if (options.getTargetDirectory() == null) {
            throw new SynapseException("Target directory not set");
        }
    }
}
