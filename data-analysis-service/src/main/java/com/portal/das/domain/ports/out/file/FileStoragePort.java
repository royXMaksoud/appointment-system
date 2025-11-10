package com.portal.das.domain.ports.out.file;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Output port for file storage operations
 * Abstracts the physical storage mechanism (filesystem, S3, etc.)
 */
public interface FileStoragePort {
    /**
     * Store a file in the storage system
     *
     * @param inputStream File content
     * @param filename Target filename
     * @return Path where file was stored
     */
    Path store(InputStream inputStream, String filename);

    /**
     * Retrieve a file from storage
     *
     * @param filename File to retrieve
     * @return InputStream of file content
     */
    InputStream retrieve(String filename);

    /**
     * Delete a file from storage
     *
     * @param filename File to delete
     * @return true if deleted successfully
     */
    boolean delete(String filename);

    /**
     * Check if a file exists in storage
     *
     * @param filename File to check
     * @return true if file exists
     */
    boolean exists(String filename);

    /**
     * Get the full path for a file
     *
     * @param filename Filename
     * @return Full path to file
     */
    Path getPath(String filename);
}

