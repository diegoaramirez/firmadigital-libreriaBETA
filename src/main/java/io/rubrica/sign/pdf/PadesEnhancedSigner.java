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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.TSAClientBouncyCastle;

import io.rubrica.sign.RubricaSigner;
import io.rubrica.sign.pdf.itext.ITextSignerAdapter;

/**
 * PAdES Enhanced - PAdES-BES
 */
public class PadesEnhancedSigner extends BasePdfSigner {

    private IExternalSignature externalSignature;

    private static final Logger logger = Logger.getLogger(PadesBasicSigner.class.getName());

    public PadesEnhancedSigner(RubricaSigner signer) {
        this.externalSignature = new ITextSignerAdapter(signer);
    }

    @Override
    protected byte[] signInternal(ByteArrayOutputStream os, PdfSigner pdfSigner, RubricaSigner signer,
            Certificate[] certChain, Properties params) throws IOException {
        try {
            ICrlClient crlClient = new CrlClientOnline();
            Collection<ICrlClient> crlClients = Collections.singleton(crlClient);

            OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
            IOcspClient ocspClient = new OcspClientBouncyCastle(ocspVerifier);
            ITSAClient tsc = null;

            if (params.getProperty("tsaUrl") != null && params.getProperty("tsaUsername") != null
                    && params.getProperty("tsaPassword") != null) {
                tsc = new TSAClientBouncyCastle(params.getProperty("tsaUrl"), params.getProperty("tsaUsername"),
                        params.getProperty("tsaPassword"));
            }

            // Sign!
            pdfSigner.signDetached(new BouncyCastleDigest(), externalSignature, certChain, crlClients, ocspClient, tsc,
                    0, PdfSigner.CryptoStandard.CADES);

            return os.toByteArray();
        } catch (GeneralSecurityException e) {
            logger.severe("Error al firmar:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
