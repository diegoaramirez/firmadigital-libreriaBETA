package io.rubrica.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

/**
 * Implementación base de Document.
 */
public abstract class CommonDocument implements Document {

    protected String name;

    @Override
    public void writeTo(OutputStream stream) throws IOException {
        byte[] buffer = new byte[1024];
        int count = -1;
        try (InputStream inStream = openStream()) {
            while ((count = inStream.read(buffer)) > 0) {
                stream.write(buffer, 0, count);
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void save(String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            writeTo(fos);
        }
    }

    @Override
    public String toString() {
        final StringWriter stringWriter = new StringWriter();
        stringWriter.append("Name: ").append(getName());
        return stringWriter.toString();
    }
}
