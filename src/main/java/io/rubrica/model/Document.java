package io.rubrica.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Representa un documento.
 */
public interface Document {

    /** Obtener un InputStream del documento */
    InputStream openStream();

    /** Escribir un documento a un OutputStream */
    void writeTo(OutputStream stream) throws IOException;

    /** Obtener un nombre */
    String getName();

    /** Establecer un nombre */
    void setName(String name);

    /** Grabar el documento */
    void save(final String filePath) throws IOException;
}
