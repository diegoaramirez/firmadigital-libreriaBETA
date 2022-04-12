/*
 * Copyright (C) 2021 
 * Authors: Ricardo Arguello
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.*
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.rubrica.sign.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.Properties;
import java.util.logging.Logger;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.TSAClientBouncyCastle;

import io.rubrica.sign.RubricaSigner;

/**
 * PAdES-LTV
 */
public class PadesLtvSigner extends PadesEnhancedSigner {

    private static final Logger logger = Logger.getLogger(PadesLtvSigner.class.getName());

    public PadesLtvSigner(RubricaSigner signer) {
        super(signer);
    }

    @Override
    protected byte[] signInternal(ByteArrayOutputStream os, com.itextpdf.signatures.PdfSigner pdfSigner,
            RubricaSigner signer, Certificate[] certChain, Properties params) throws IOException {
        byte[] signedBytes = super.signInternal(os, pdfSigner, signer, certChain, params);
        ByteArrayInputStream bis = new ByteArrayInputStream(signedBytes);

        ICrlClient crlClient = new CrlClientOnline();
        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        IOcspClient ocspClient = new OcspClientBouncyCastle(ocspVerifier);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ITSAClient tsc = null;

        if (params.getProperty("tsaUrl") != null && params.getProperty("tsaUsername") != null
                && params.getProperty("tsaPassword") != null) {
            tsc = new TSAClientBouncyCastle(params.getProperty("tsaUrl"), params.getProperty("tsaUsername"),
                    params.getProperty("tsaPassword"));
        }

        ltvEnable(pdfSigner, bis, baos, pdfSigner.getFieldName(), ocspClient, crlClient, tsc);
        return baos.toByteArray();
    }

    private void ltvEnable(PdfSigner signer, ByteArrayInputStream signedPdfInput, ByteArrayOutputStream baos,
            String name, IOcspClient ocspClient, ICrlClient crlClient, ITSAClient tsc) {
        try (PdfReader pdfReader = new PdfReader(signedPdfInput)) {
            PdfDocument document = new PdfDocument(pdfReader, new PdfWriter(baos),
                    new StampingProperties().useAppendMode());

            LtvVerification ltvVerification = new LtvVerification(document, "BC");
            crlClient = null;
            ltvVerification.addVerification(name, ocspClient, crlClient, LtvVerification.CertificateOption.WHOLE_CHAIN,
                    LtvVerification.Level.OCSP, LtvVerification.CertificateInclusion.YES);

            ltvVerification.merge();
            document.close();
        } catch (IOException | GeneralSecurityException e) {
            logger.severe("Error while making signature ltv enabled");
            throw new RuntimeException(e);
        }
    }
}
