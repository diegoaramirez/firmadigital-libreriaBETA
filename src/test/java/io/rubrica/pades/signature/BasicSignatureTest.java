package io.rubrica.pades.signature;

import org.junit.jupiter.api.Test;

import io.rubrica.model.Document;
import io.rubrica.model.FileDocument;

public class BasicSignatureTest {

    private final static String test1 = "src/main/resources/test1.pdf";

    @Test
    void sign() {
        Document document = new FileDocument(test1);
        
     //   BasicSignature bs= new BasicSignature(rubricaSigner);
    }
}
