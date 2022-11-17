/*
 * Copyright (C) 2020 
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ExternalBlankSignatureContainer;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignatureUtil;

import io.rubrica.certificate.CertEcUtils;
import io.rubrica.certificate.to.DatosUsuario;
import io.rubrica.exceptions.HoraServidorException;
import io.rubrica.exceptions.InvalidFormatException;
import io.rubrica.exceptions.RubricaException;
import io.rubrica.model.Document;
import io.rubrica.model.InMemoryDocument;
import io.rubrica.sign.SignInfo;
import io.rubrica.sign.Signer;
import io.rubrica.utils.BouncyCastleUtils;
import io.rubrica.utils.FileUtils;
import io.rubrica.utils.Utils;
import java.util.logging.Level;

@Deprecated
public class PDFSignerItext implements Signer {

    private static final Logger logger = Logger.getLogger(PDFSignerItext.class.getName());

    public static final String SIGNING_REASON = "signingReason";
    public static final String SIGNING_LOCATION = "signingLocation";
    public static final String SIGN_TIME = "signTime";
    public static final String SIGNATURE_PAGE = "signingPage";
    public static final String LAST_PAGE = "page";
    public static final String FONT_SIZE = "font";
    public static final String TYPE_SIG = "typeSignature";
    public static final String INFO_QR = "infoQR";
    public static final String PATH = "path";
    private Provider provider;

    static {
        BouncyCastleUtils.initializeBouncyCastle();
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    // ETSI TS 102 778-1 V1.1.1 (2009-07)
    // PAdES Basic - Profile based on ISO 32000-1
    /**
     * @param data
     * @param algorithm
     * @param key
     * @param certChain
     * @param xParams
     * @param base64
     * @return
     * @throws java.io.IOException
     * @throws io.rubrica.exceptions.RubricaException
     */
    @Override
    public byte[] sign(byte[] data, String algorithm, PrivateKey key, Certificate[] certChain, Properties xParams, String base64)
            throws IOException, RubricaException {
        byte[] documentoFirmado = null;
        try {
            File file = new File(xParams.getProperty(PATH));
            file.mkdirs();

            String rutaDocumentoTemporal = FileUtils.crearNombreTemporal(file, ".tmp", base64);
            String rutaDocumentoFirmado = FileUtils.crearNombreFirmado(file, ".pdf");

            try {
                String fieldName = emptySignature(file.getPath(), rutaDocumentoTemporal, certChain, xParams);
                documentoFirmado = createSignature(rutaDocumentoTemporal, rutaDocumentoFirmado, fieldName, key, certChain,
                        algorithm);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                new File(rutaDocumentoTemporal).delete();
                new File(rutaDocumentoFirmado).delete();
                // eliminar temporales
                FileUtils.eliminarPorConstante(System.getProperty("java.io.tmpdir"), "firmaec.rubrica.firmadigital.temp");
            }
        } catch (HoraServidorException ex) {
            Logger.getLogger(PDFSignerItext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return documentoFirmado;
    }

    public String emptySignature(String src, String dest, Certificate[] certChain, Properties xParams)
            throws IOException, GeneralSecurityException, RubricaException {
        Properties extraParams = xParams != null ? xParams : new Properties();
        X509Certificate x509Certificate = (X509Certificate) certChain[0];

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
        } catch (final Exception e) {
            logger.warning("Se ha indicado un tamaño de letra invalida ('" + extraParams.getProperty(FONT_SIZE)
                    + "'), se usara el tamaño por defecto: " + fontSize + " " + e);
        }

        // Tipo de firma (Información, QR)
        String typeSig = extraParams.getProperty(TYPE_SIG);
        if (typeSig == null) {
            typeSig = "information1";
        }

        if (typeSig.equals("QR") && extraParams.getProperty(FONT_SIZE) == null) {
            fontSize = 4.5f;
        }

        // Información QR
        String infoQR = "";
        if (extraParams.getProperty(INFO_QR) == null) {
            infoQR = "";
        } else {
            infoQR = extraParams.getProperty(INFO_QR).trim();
        }

        // Pagina donde situar la firma visible
        int page = 0;
        try {
            if (extraParams.getProperty(LAST_PAGE) == null) {
                page = 0;
            } else {
                page = Integer.parseInt(extraParams.getProperty(LAST_PAGE).trim());
            }
        } catch (final Exception e) {
            logger.warning("Se ha indicado un numero de pagina invalido ('" + extraParams.getProperty(LAST_PAGE)
                    + "'), se usara la ultima pagina: " + e);
        }

        // Leer el PDF
        PdfReader pdfReader = new PdfReader(src) {
            @Override
            public boolean hasRebuiltXref() {
                return false;
            }
        };
        // if (pdfReader.isEncrypted()) {
        // logger.severe("Documento encriptado");
        // throw new RubricaException("Documento encriptado");
        // }
        Rectangle signaturePositionOnPage = getSignaturePositionOnPage(extraParams);
        StampingProperties properties = new StampingProperties();
        properties.useAppendMode();
        PdfSigner pdfSigner = new PdfSigner(pdfReader, new FileOutputStream(dest), properties);
        if (page == 0 || page < 0 || page > pdfSigner.getDocument().getNumberOfPages()) {
            page = pdfSigner.getDocument().getNumberOfPages();
        }
        PdfSignatureAppearance signatureAppearance = pdfSigner.getSignatureAppearance();
        signatureAppearance.setPageRect(signaturePositionOnPage).setPageNumber(page);

        if (signaturePositionOnPage != null) {
            String informacionCertificado = x509Certificate.getSubjectDN().getName();
            DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(x509Certificate);
            String nombreFirmante = (datosUsuario.getNombre() + " " + datosUsuario.getApellido()).toUpperCase();

            PdfFormXObject layer2 = signatureAppearance.getLayer2();
            PdfCanvas canvas = new PdfCanvas(layer2, pdfSigner.getDocument());

            PdfFont fontCourier = PdfFontFactory.createFont(FontConstants.COURIER);
            PdfFont fontCourierBold = PdfFontFactory.createFont(FontConstants.COURIER_BOLD);
            PdfFont fontHelvetica = PdfFontFactory.createFont(FontConstants.HELVETICA);

            switch (typeSig) {
                case "QR": {
                    // Imagen
                    byte[] byteQR = null;
                    // QR
                    String text = "FIRMADO POR: " + nombreFirmante.trim() + "\n";
                    text = text + "RAZON: " + reason + "\n";
                    text = text + "LOCALIZACION: " + location + "\n";
                    text = text + "FECHA: " + signTime + "\n";
                    text = text + infoQR;
                    try {
                        byteQR = io.rubrica.utils.QRCode.generateQR(text,
                                (int) signaturePositionOnPage.getHeight(), (int) signaturePositionOnPage.getHeight());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // QR
                    Rectangle dataRect = new Rectangle(0, 0, signaturePositionOnPage.getWidth(),
                            signaturePositionOnPage.getHeight());
                    Rectangle signatureRect = new Rectangle(signaturePositionOnPage.getWidth() / 3, 0,
                            signaturePositionOnPage.getWidth(), signaturePositionOnPage.getHeight());
                    // <editor-fold defaultstate="collapsed" desc="Tested Code">
                    // Signature at left and image at right
                    // Rectangle dataRect = new Rectangle(signaturePositionOnPage.getWidth() / 2 +
                    // MARGIN / 2, MARGIN, signaturePositionOnPage.getWidth() / 2 - MARGIN,
                    // signaturePositionOnPage.getHeight() - 2 * MARGIN);
                    // Rectangle signatureRect = new Rectangle(MARGIN, MARGIN,
                    // signaturePositionOnPage.getWidth() / 2 - 2 * MARGIN,
                    // signaturePositionOnPage.getHeight() - 2 * MARGIN);
                    // Signature at right and image at left
                    // Rectangle dataRect = new Rectangle(MARGIN, MARGIN,
                    // signaturePositionOnPage.getWidth() / 2 - MARGIN,
                    // signaturePositionOnPage.getHeight() - 2 * MARGIN);
                    // Rectangle signatureRect = new Rectangle(signaturePositionOnPage.getWidth() /
                    // 2 + MARGIN / 2, MARGIN, signaturePositionOnPage.getWidth() / 2 - 2 * MARGIN,
                    // signaturePositionOnPage.getHeight() - 2 * MARGIN);
                    // Signature at top and image at bottom
                    // Rectangle dataRect = new Rectangle(MARGIN, MARGIN,
                    // signaturePositionOnPage.getWidth() - 2 * MARGIN,
                    // signaturePositionOnPage.getHeight() / 2 - MARGIN);
                    // Rectangle signatureRect = new Rectangle(MARGIN,
                    // signaturePositionOnPage.getHeight() / 2 + MARGIN,
                    // signaturePositionOnPage.getWidth() - 2 * MARGIN,
                    // signaturePositionOnPage.getHeight() / 2 - MARGIN);
                    // Signature at bottom and image at top
                    // Rectangle dataRect = new Rectangle(MARGIN,
                    // signaturePositionOnPage.getHeight() / 2 + MARGIN,
                    // signaturePositionOnPage.getWidth() - 2 * MARGIN,
                    // signaturePositionOnPage.getHeight() / 2 - MARGIN);
                    // Rectangle signatureRect = new Rectangle(MARGIN, MARGIN,
                    // signaturePositionOnPage.getWidth() - 2 * MARGIN,
                    // signaturePositionOnPage.getHeight() / 2 - MARGIN);
                    // </editor-fold>
                    Div imageDiv = new Div();
                    imageDiv.setHeight(dataRect.getHeight());
                    imageDiv.setWidth(dataRect.getWidth());
                    imageDiv.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    imageDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    Image image = new Image(ImageDataFactory.create(byteQR));
                    image.setAutoScale(true);
                    imageDiv.add(image);
                    Canvas imageLayoutCanvas = new Canvas(canvas, pdfSigner.getDocument(), dataRect);
                    imageLayoutCanvas.add(imageDiv);
                    imageLayoutCanvas.close();
                    Div textDiv = new Div();
                    textDiv.setHeight(signatureRect.getHeight());
                    textDiv.setWidth(signatureRect.getWidth() - signaturePositionOnPage.getWidth() / 3);
                    textDiv.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    textDiv.setHorizontalAlignment(HorizontalAlignment.LEFT);
                    Text texto = new Text("Firmado electrónicamente por:\n");
                    // Text contenido = new Text("Simón José Antonio de la Santísima Trinidad
                    // Bolívar y Palacios Ponte-Andrade y Blanco");
                    Paragraph paragraph = new Paragraph().add(texto).setFont(fontCourier).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(3.25f);
                    textDiv.add(paragraph);
                    Text contenido = new Text(nombreFirmante.trim());
                    paragraph = new Paragraph().add(contenido).setFont(fontCourierBold).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(6.25f);
                    // paragraph.setBackgroundColor(DeviceGray.BLACK);
                    textDiv.add(paragraph);
                    Canvas textLayoutCanvas = new Canvas(canvas, pdfSigner.getDocument(), signatureRect);
                    textLayoutCanvas.add(textDiv);
                    textLayoutCanvas.close();
                    break;
                }
                case "information1": {
                    Rectangle signatureRect = new Rectangle(0, 0, signaturePositionOnPage.getWidth(),
                            signaturePositionOnPage.getHeight());
                    Div textDiv = new Div();
                    textDiv.setHeight(signatureRect.getHeight());
                    textDiv.setWidth(signatureRect.getWidth());
                    textDiv.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    textDiv.setHorizontalAlignment(HorizontalAlignment.LEFT);
                    Text contenido = new Text(nombreFirmante.trim());
                    Paragraph paragraph = new Paragraph().add(contenido).setFont(fontHelvetica).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(6.25f);
                    textDiv.add(paragraph);
                    contenido = new Text("Nombre de reconocimiento " + informacionCertificado.trim());
                    paragraph = new Paragraph().add(contenido).setFont(fontHelvetica).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(3.25f);
                    textDiv.add(paragraph);
                    contenido = new Text("Razón: " + reason);
                    paragraph = new Paragraph().add(contenido).setFont(fontHelvetica).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(3.25f);
                    textDiv.add(paragraph);
                    contenido = new Text("Localización: " + location);
                    paragraph = new Paragraph().add(contenido).setFont(fontHelvetica).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(3.25f);
                    textDiv.add(paragraph);
                    contenido = new Text("Fecha: " + signTime);
                    paragraph = new Paragraph().add(contenido).setFont(fontHelvetica).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(3.25f);
                    textDiv.add(paragraph);
                    Canvas textLayoutCanvas = new Canvas(canvas, pdfSigner.getDocument(), signatureRect);
                    textLayoutCanvas.add(textDiv);
                    textLayoutCanvas.close();
                    break;
                }
                case "information2": {
                    // Creating the appearance for layer 2
                    // ETSI TS 102 778-6 V1.1.1 (2010-07)
                    Rectangle signatureRect = new Rectangle(0, 0, signaturePositionOnPage.getWidth(),
                            signaturePositionOnPage.getHeight());
                    Div textDiv = new Div();
                    textDiv.setHeight(signatureRect.getHeight());
                    textDiv.setWidth(signatureRect.getWidth());
                    textDiv.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    textDiv.setHorizontalAlignment(HorizontalAlignment.LEFT);
                    Text texto = new Text("Firmado electrónicamente por:\n");
                    Paragraph paragraph = new Paragraph().add(texto).setFont(fontHelvetica).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(3.25f);
                    textDiv.add(paragraph);
                    Text contenido = new Text(nombreFirmante.trim());
                    paragraph = new Paragraph().add(contenido).setFont(fontHelvetica).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(6.25f);
                    textDiv.add(paragraph);
                    contenido = new Text("Razón: " + reason);
                    paragraph = new Paragraph().add(contenido).setFont(fontHelvetica).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(4.25f);
                    textDiv.add(paragraph);
                    contenido = new Text("Localización: " + location);
                    paragraph = new Paragraph().add(contenido).setFont(fontHelvetica).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(4.25f);
                    textDiv.add(paragraph);
                    contenido = new Text("Fecha: " + signTime);
                    paragraph = new Paragraph().add(contenido).setFont(fontHelvetica).setMargin(0)
                            .setMultipliedLeading(0.9f).setFontSize(4.25f);
                    textDiv.add(paragraph);
                    Canvas textLayoutCanvas = new Canvas(canvas, pdfSigner.getDocument(), signatureRect);
                    textLayoutCanvas.add(textDiv);
                    textLayoutCanvas.close();
                    break;
                }
                default: {
                }
            }
        }

        // Razon de firma
        if (reason != null) {
            signatureAppearance.setReason(reason);
        }

        // Localización en donde se produce la firma
        if (location != null) { // no puede ser null
            signatureAppearance.setLocation(location);
        }

        // Fecha y hora de la firma
        if (signTime != null) {
            Date date = Utils.getSignTime(signTime);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            pdfSigner.setSignDate(calendar);
        }

        try {
            /*
             * ExternalBlankSignatureContainer constructor will create the PdfDictionary for
             * the signature information and will insert the /Filter and /SubFilter values
             * into this dictionary. It will leave just a blank placeholder for the
             * signature that is to be inserted later.
             */
            IExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached);

            // Sign the document using an external container.
            // 8192 is the size of the empty signature placeholder.
            pdfSigner.signExternalContainer(external, 8192);
            // } catch (ExceptionConverter ec) {
            // logger.severe("Problemas con el driver\n" + ec);
            // throw new
            // RubricaException(io.rubrica.utils.PropertiesUtils.getMessages().getProperty("mensaje.error.driver_problemas")
            // + "\n", ec);
            // } catch (DocumentException | InvalidPdfException de) {
            // logger.severe("Error al estampar la firma\n" + de);
            // throw new RubricaException("Error al estampar la firma\n", de);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdfSigner.getFieldName();
    }

    public byte[] createSignature(String src, String dest, String fieldName, PrivateKey pk, Certificate[] chain,
            String algorithm) throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(src);
        try (FileOutputStream os = new FileOutputStream(dest)) {
            PdfSigner signer = new PdfSigner(reader, os, new StampingProperties());
            IExternalSignatureContainer external = new PDFSignerItext.MyExternalSignatureContainer(pk, chain,
                    algorithm);
            // Signs a PDF where space was already reserved. The field must cover the whole
            // document.
            signer.signDeferred(signer.getDocument(), fieldName, os, external);
            reader.close();
            return FileUtils.fileConvertToByteArray(new File(dest));
        }
    }

    public List<SignInfo> getSigners(byte[] sign) throws InvalidFormatException, IOException {
        PdfReader pdfReader;
        try {
            Document document = new InMemoryDocument(sign);
            try (InputStream is = document.openStream()) {
                pdfReader = new PdfReader(is);
            }
        } catch (Exception e) {
            logger.severe("No se ha podido leer el PDF: " + e);
            throw new InvalidFormatException("No se ha podido leer el PDF", e);
        }
        SignatureUtil signatureUtil;
        try {
            PdfDocument pdfDocument = new PdfDocument(pdfReader);
            signatureUtil = new com.itextpdf.signatures.SignatureUtil(pdfDocument);
        } catch (Exception e) {
            logger.severe(
                    "No se ha podido obtener la informacion de los firmantes del PDF, se devolvera un arbol vacio: "
                    + e);
            throw new InvalidFormatException("No se ha podido obtener la informacion de los firmantes del PDF", e);
        }

        @SuppressWarnings("unchecked")
        List<String> names = signatureUtil.getSignatureNames();
        List<SignInfo> signInfos = new ArrayList<>();
        for (String signatureName : names) {
            com.itextpdf.signatures.PdfPKCS7 pdfPKCS7;
            try {
                pdfPKCS7 = signatureUtil.readSignatureData(signatureName);
            } catch (Exception e) {
                e.printStackTrace();
                logger.severe("El PDF contiene una firma corrupta o con un formato desconocido (" + signatureName
                        + "), se continua con las siguientes si las hubiese: " + e);
                continue;
            }
            Certificate[] signCertificateChain = pdfPKCS7.getSignCertificateChain();
            X509Certificate[] certChain = new X509Certificate[signCertificateChain.length];
            for (int i = 0; i < certChain.length; i++) {
                certChain[i] = (X509Certificate) signCertificateChain[i];
            }
            SignInfo signInfo = new SignInfo(certChain, pdfPKCS7.getSignDate().getTime());
            signInfos.add(signInfo);
        }
        return signInfos;
    }

    // private static final String PDF_FILE_HEADER = "%PDF-";
    // private boolean isPdfFile(final byte[] data) {
    // byte[] buffer = new byte[PDF_FILE_HEADER.length()];
    // try {
    // new ByteArrayInputStream(data).read(buffer);
    // } catch (Exception e) {
    // buffer = null;
    // }
    // // Comprobamos que cuente con una cabecera PDF
    // if (buffer != null && !PDF_FILE_HEADER.equals(new String(buffer))) {
    // return false;
    // }
    // try {
    // // Si lanza una excepcion al crear la instancia, no es un fichero
    // // PDF
    // new PdfReader(data);
    // } catch (final Exception e) {
    // return false;
    // }
    // return true;
    // }
    private static Rectangle getSignaturePositionOnPage(Properties extraParams) {
        return RectanguloUtil.getPositionOnPage(extraParams);
    }

    class MyExternalSignatureContainer implements IExternalSignatureContainer {

        protected PrivateKey pk;
        protected Certificate[] chain;
        protected String algorithm;

        public MyExternalSignatureContainer(PrivateKey pk, Certificate[] chain, String algorithm) {
            this.pk = pk;
            this.chain = chain;
            this.algorithm = algorithm;
        }

        @Override
        public byte[] sign(InputStream is) throws GeneralSecurityException {
            try {
                PrivateKeySignature signature = new PrivateKeySignature(pk, algorithm, "BC");
//                PrivateKeySignature signature = new PrivateKeySignature(pk, algorithm,
//                        provider == null ? "BC" : provider.getName());
                String hashAlgorithm = signature.getHashAlgorithm();
                BouncyCastleDigest digest = new BouncyCastleDigest();

                PdfPKCS7 sgn = new PdfPKCS7(null, chain, hashAlgorithm, null, digest, false);
                byte hash[] = DigestAlgorithms.digest(is, digest.getMessageDigest(hashAlgorithm));
                byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, PdfSigner.CryptoStandard.CMS, null, null);
                byte[] extSignature = signature.sign(sh);
                sgn.setExternalDigest(extSignature, null, signature.getEncryptionAlgorithm());

                return sgn.getEncodedPKCS7(hash, PdfSigner.CryptoStandard.CMS, null, null, null);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }

        @Override
        public void modifySigningDictionary(PdfDictionary signDic) {
        }
    }
}
