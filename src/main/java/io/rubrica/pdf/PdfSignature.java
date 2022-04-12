package io.rubrica.pdf;

import io.rubrica.model.Document;

public interface PdfSignature {

	Document sign(final Document pdfData, final byte[] signatureValue, final SignatureParameters parameters);
}
