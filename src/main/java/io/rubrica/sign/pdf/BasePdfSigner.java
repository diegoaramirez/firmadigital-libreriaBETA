/*
 * Copyright (C) 2021 
 * Authors: Ricardo Arguello, Misael Fernández
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
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Logger;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.PdfSignatureAppearance;

import io.rubrica.certificate.CertEcUtils;
import io.rubrica.certificate.to.DatosUsuario;
import io.rubrica.sign.RubricaSigner;
import io.rubrica.sign.pdf.appearance.CustomAppearance;
import io.rubrica.sign.pdf.appearance.Information1Appearance;
import io.rubrica.sign.pdf.appearance.Information2Appearance;
import io.rubrica.sign.pdf.appearance.QrAppereance;
import io.rubrica.utils.BouncyCastleUtils;
import io.rubrica.utils.Utils;

public abstract class BasePdfSigner implements PdfSigner {

    private static final Logger logger = Logger.getLogger(BasePdfSigner.class.getName());

    public static final String SIGNING_REASON = "signingReason";
    public static final String SIGNING_LOCATION = "signingLocation";
    public static final String SIGN_TIME = "signTime";
    public static final String SIGNATURE_PAGE = "signingPage";
    public static final String LAST_PAGE = "page";
    public static final String FONT_SIZE = "font";
    public static final String TYPE_SIG = "typeSignature";
    public static final String INFO_QR = "infoQR";
    public static final String PATH = "path";

    public static final String TSA_URL = "tsaUrl";
    public static final String TSA_USERNAME = "tsaUsername";
    public static final String TSA_PASSWORD = "tsaPassword";
    // private Provider provider;

    static {
        BouncyCastleUtils.initializeBouncyCastle();
    }

    // ETSI TS 102 778-1 V1.1.1 (2009-07)
    // PAdES Basic - Profile based on ISO 32000-1
    /**
     * @param is
     * @param signer
     * @param params
     * @param certChain
     * @return
     * @throws java.io.IOException
     */
    @Override
    public byte[] sign(InputStream is, RubricaSigner signer, Certificate[] certChain, Properties params)
            throws IOException {
        try (PdfReader pdfReader = new PdfReader(is) {
            @Override
            public boolean hasRebuiltXref() {
                return false;
            }
        };
                ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            StampingProperties stampingProperties = new StampingProperties();
            //TODO Edison Lomas Almeida: La línea siguiente genera error PdfException: Append mode requires a document without errors, even if recovery is possible.
            stampingProperties.useAppendMode();

            com.itextpdf.signatures.PdfSigner pdfSigner = new com.itextpdf.signatures.PdfSigner(pdfReader, os,
                    stampingProperties);

            X509Certificate x509Certificate = (X509Certificate) certChain[0];

            Properties extraParams = params != null ? params : new Properties();

            // Motivo de la firma
            String reason = extraParams.getProperty(SIGNING_REASON);

            // Lugar de realizacion de la firma
            String location = extraParams.getProperty(SIGNING_LOCATION);

            // Fecha y hora de la firma, en formato ISO-8601
            String signTime = extraParams.getProperty(SIGN_TIME);

            // Tamaño letra
            float fontSize = 3;

            try {
                if (extraParams.getProperty(FONT_SIZE) == null) {
                    fontSize = 3;
                } else {
                    fontSize = Float.parseFloat(extraParams.getProperty(FONT_SIZE).trim());
                }
            } catch (Exception e) {
                logger.warning("Se ha indicado un tamaño de letra invalida ('" + extraParams.getProperty(FONT_SIZE)
                        + "'), se usara el tamaño por defecto: " + fontSize + " " + e);
            }

            // Tipo de firma (Información, QR)
            String typeSig = extraParams.getProperty(TYPE_SIG, "information1");

            if (typeSig.equals("QR") && extraParams.getProperty(FONT_SIZE) == null) {
                fontSize = 4.5f;
            }

            // Información QR
            String infoQR = extraParams.getProperty(INFO_QR, "").trim();

            // Pagina donde situar la firma visible
            int page = 0;

            try {
                if (extraParams.getProperty(LAST_PAGE) == null) {
                    page = 0;
                } else {
                    page = Integer.parseInt(extraParams.getProperty(LAST_PAGE).trim());
                }
            } catch (Exception e) {
                logger.warning("Se ha indicado un numero de pagina invalido ('" + extraParams.getProperty(LAST_PAGE)
                        + "'), se usara la ultima pagina: " + e);
            }

            if (page == 0 || page < 0 || page > pdfSigner.getDocument().getNumberOfPages()) {
                page = pdfSigner.getDocument().getNumberOfPages();
            }

            Rectangle signaturePositionOnPage = RectanguloUtil.getPositionOnPage(extraParams);

            StampingProperties properties = new StampingProperties();
            properties.useAppendMode();

            PdfSignatureAppearance signatureAppearance = pdfSigner.getSignatureAppearance();
            signatureAppearance.setPageRect(signaturePositionOnPage).setPageNumber(page);

            if (signaturePositionOnPage != null) {
                DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(x509Certificate);
                String nombreFirmante = (datosUsuario.getNombre() + " " + datosUsuario.getApellido()).toUpperCase();
                String informacionCertificado = x509Certificate.getSubjectDN().getName();

//                logger.info("datosUsuario: " + datosUsuario);
//                logger.info("Nombre firmante: " + nombreFirmante);
//                logger.info("Informacion certificado: " + informacionCertificado);
                PdfDocument pdfDocument = pdfSigner.getDocument();

                CustomAppearance customAppearance;

                switch (typeSig) {
                    case "QR": {
                        customAppearance = new QrAppereance(nombreFirmante, reason, location, signTime, infoQR);
                        break;
                    }
                    case "information1": {
                        customAppearance = new Information1Appearance(nombreFirmante, informacionCertificado, reason,
                                location, signTime);
                        break;
                    }
                    case "information2": {
                        customAppearance = new Information2Appearance(nombreFirmante, reason, location, signTime);
                        break;
                    }
                    default: {
                        throw new RuntimeException("typeSign unknown");
                    }
                }

                customAppearance.createCustomAppearance(signatureAppearance, page, pdfDocument,
                        signaturePositionOnPage);
            }

            // Razon de firma
            if (reason != null) {
                signatureAppearance.setReason(reason);
            }

            // Localización en donde se produce la firma
            if (location != null) {
                signatureAppearance.setLocation(location);
            }

            signatureAppearance.setSignatureCreator("Rubrica 3.0");

            // Fecha y hora de la firma
            if (signTime != null) {
                Date date = Utils.getSignTime(signTime);
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                pdfSigner.setSignDate(calendar);
            }

            return signInternal(os, pdfSigner, signer, certChain, params);
        }
    }

    protected abstract byte[] signInternal(ByteArrayOutputStream os, com.itextpdf.signatures.PdfSigner pdfSigner,
            RubricaSigner signer, Certificate[] certChain, Properties params) throws IOException;
}
