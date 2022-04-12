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
import java.util.Properties;
import java.util.logging.Logger;

import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;

import io.rubrica.sign.RubricaSigner;
import io.rubrica.sign.pdf.itext.ITextSignerAdapter;

/**
 * PaDES Basic Signer
 */
public class PadesBasicSigner extends BasePdfSigner {

    private IExternalSignature externalSignature;

    private static final Logger logger = Logger.getLogger(PadesBasicSigner.class.getName());

    public PadesBasicSigner(RubricaSigner signer) {
        this.externalSignature = new ITextSignerAdapter(signer);
    }

    @Override
    protected byte[] signInternal(ByteArrayOutputStream os, com.itextpdf.signatures.PdfSigner pdfSigner, RubricaSigner signer,
            Certificate[] certChain, Properties params) throws IOException {
        try {
            pdfSigner.signDetached(new BouncyCastleDigest(), externalSignature, certChain, null, null, null, 0,
                    CryptoStandard.CMS);
            return os.toByteArray();
        } catch (GeneralSecurityException e) {
            logger.severe("Error al firmar:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
