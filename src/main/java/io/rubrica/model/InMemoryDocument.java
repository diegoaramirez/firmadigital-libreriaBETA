package io.rubrica.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;

/**
 * Implementacion de Document en memoria.
 */
public class InMemoryDocument extends CommonDocument {

    /* Contenido del documento */
    private byte[] bytes;

    public InMemoryDocument() {
    }

    public InMemoryDocument(final byte[] bytes) {
        this(bytes, null);
    }

    public InMemoryDocument(final byte[] bytes, final String name) {
        Objects.requireNonNull(bytes, "Bytes cannot be null");
        this.bytes = bytes;
        this.name = name;
    }

    public InMemoryDocument(final InputStream inputStream) {
        this(toByteArray(inputStream), null);
    }

    public InMemoryDocument(final InputStream inputStream, final String name) {
        this(toByteArray(inputStream), name);
    }

    public InputStream openStream() {
        Objects.requireNonNull(bytes, "Bytes are null");
        return new ByteArrayInputStream(bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getBase64Encoded() {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] toByteArray(InputStream inputStream) {
        Objects.requireNonNull(inputStream, "The InputStream is null");
        try (InputStream is = inputStream; ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[2048];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                baos.write(data, 0, nRead);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RubricaRuntimeException("Unable to fully read the InputStream", e);
        }
    }
}
