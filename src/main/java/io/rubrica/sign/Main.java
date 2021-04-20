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
package io.rubrica.sign;

import io.rubrica.certificate.CertEcUtils;
import static io.rubrica.certificate.CertUtils.seleccionarAlias;
import io.rubrica.certificate.to.Certificado;
import io.rubrica.certificate.to.Documento;
import io.rubrica.exceptions.InvalidFormatException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import io.rubrica.exceptions.SignatureVerificationException;
import io.rubrica.keystore.FileKeyStoreProvider;
import io.rubrica.keystore.KeyStoreProvider;
import io.rubrica.keystore.KeyStoreProviderFactory;
import io.rubrica.sign.pdf.PDFSignerItext;
import io.rubrica.sign.pdf.RectanguloUtil;
import io.rubrica.utils.FileUtils;
import io.rubrica.utils.TiempoUtils;
import io.rubrica.utils.Utils;
import io.rubrica.utils.UtilsCrlOcsp;
import io.rubrica.utils.X509CertificateUtils;
import io.rubrica.validaciones.DocumentoUtils;
import java.io.File;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
//NFC
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

/**
 * Metodo de pruebas funcionales
 *
 * @author mfernandez
 */
public class Main {

    // ARCHIVO
    private static final String ARCHIVO = "/home/mfernandez/prueba.p12";
    private static final String PASSWORD = "123456";
//    private static final String FILE = "/home/mfernandez/Descargas/1111201901099252674200120430030017228925486271312_A.xml";
//    private static final String FILE_XML = "/home/mfernandez/Descargas/1111201901099252674200120430030017228925486271312_A-signed.txt.xml";
//    private static final String FILE_XML = "/home/mfernandez/Test/facturaMovistar.xml";
//    private static final String FILE = "/home/mfernandez/Test/hello_encrypted2.pdf";
//    private static final String FILE = "/home/mfernandez/Test/Caballero.pdf";
//    private static final String FILE = "/home/mfernandez/CompartidoWindows/Aplicaciones Windows/caso firma/1.7_Porcentaje_Cero_Papeles_Quipux_Agosto_2020_editado_3.pdf";
//    private static final String FILE = "/home/mfernandez/CompartidoWindows/Aplicaciones Windows/caso firma/1.7_Porcentaje_Cero_Papeles_Quipux_Agosto_2020_ok.pdf";
//    private static final String FILE = "/home/mfernandez/Test/Editados/Paz y salvo - rige el 2020 Diego Saud DF-signed-signed.pdf";
//    private static final String FILE = "/home/mfernandez/Test/CasosDePrueba/Caso 09 - PDF Firmado - Tres firmas - CJ - CJ - CJ.pdf";
    private static final String FILE = "/home/mfernandez/documento_blanco.pdf";
//    private static final String FILE = "/home/mfernandez/Test/1.pdf";
//    private static final String FILE = "/home/mfernandez/Test/firmadoEditado.pdf";
//    private static final String FILE = "/home/mfernandez/Test/documento_blanco.pdf";
//    private static final String FILE = "/home/mfernandez/Test/quipux_xls.p7m";

    public static void main(String args[]) throws KeyStoreException, Exception {
//        fechaHora(240);//espera en segundos
//        firmarDocumento(FILE);
//        validarCertificado();
        verificarDocumento(FILE);
//        leerNFC();
    }

    private static Properties parametros() throws IOException {
        //PageSize.A4.getWidth();//595.0
        //PageSize.A4.getHeight();//842.0
        //QR
        //SUPERIOR IZQUIERDA
        String llx = "10";
        String lly = "830";
        //INFERIOR IZQUIERDA
//        String llx = "100";
//        String lly = "91";
        //INFERIOR DERECHA
        //String llx = "419";
        //String lly = "91";
        //INFERIOR CENTRADO
        //String llx = "260";
        //String lly = "91";
        //QR
        //SUPERIOR IZQUIERDA
        //String llx = "10";
        //String lly = "830";
        //String urx = String.valueOf(Integer.parseInt(llx) + 110);
        //String ury = String.valueOf(Integer.parseInt(lly) - 36);
        //INFERIOR CENTRADO
        //String llx = "190";
        //String lly = "85";
        //String urx = String.valueOf(Integer.parseInt(llx) + 260);
        //String ury = String.valueOf(Integer.parseInt(lly) - 36);
        //INFERIOR CENTRADO (ancho pie pagina)
        //String llx = "100";
        //String lly = "80";&
        //String urx = String.valueOf(Integer.parseInt(llx) + 430);
        //String ury = String.valueOf(Integer.parseInt(lly) - 25);
        //INFERIOR DERECHA
        //String llx = "10";
        //String lly = "85";
        //String urx = String.valueOf(Integer.parseInt(llx) + 260);
        //String ury = String.valueOf(Integer.parseInt(lly) - 36);

        Properties params = new Properties();
        params.setProperty(PDFSignerItext.SIGNING_LOCATION, "Teletrabajo");
        params.setProperty(PDFSignerItext.SIGNING_REASON, "Firmado digitalmente con RUBRICA");
        params.setProperty(PDFSignerItext.SIGN_TIME, TiempoUtils.getFechaHoraServidor(null));
        params.setProperty(PDFSignerItext.LAST_PAGE, "1");
        params.setProperty(PDFSignerItext.TYPE_SIG, "QR");
        params.setProperty(PDFSignerItext.INFO_QR, "Firmado digitalmente con RUBRICA\nhttps://minka.gob.ec/rubrica/rubrica");
        //params.setProperty(PDFSigner.TYPE_SIG, "information2");
        //params.setProperty(PDFSigner.FONT_SIZE, "4.5");
        // Posicion firma
        params.setProperty(RectanguloUtil.POSITION_ON_PAGE_LOWER_LEFT_X, llx);
        params.setProperty(RectanguloUtil.POSITION_ON_PAGE_LOWER_LEFT_Y, lly);
        //params.setProperty(PdfUtil.POSITION_ON_PAGE_UPPER_RIGHT_X, urx);
        //params.setProperty(PdfUtil.POSITION_ON_PAGE_UPPER_RIGHT_Y, ury);
        return params;
    }

    private static void firmarDocumento(String file) throws KeyStoreException, Exception {
        ////// LEER PDF:
        byte[] docByteArry = DocumentoUtils.loadFile(file);

        // ARCHIVO
        KeyStoreProvider ksp = new FileKeyStoreProvider(ARCHIVO);
        KeyStore keyStore = ksp.getKeystore(PASSWORD.toCharArray());
        // TOKEN
        //KeyStore keyStore = KeyStoreProviderFactory.getKeyStore(PASSWORD);

        byte[] signed = null;
        Signer signer = Utils.documentSigner(new File(file));
        String alias = seleccionarAlias(keyStore);
        PrivateKey key = (PrivateKey) keyStore.getKey(alias, PASSWORD.toCharArray());

        X509CertificateUtils x509CertificateUtils = new X509CertificateUtils();
        if (x509CertificateUtils.validarX509Certificate((X509Certificate) keyStore.getCertificate(alias), null)) {//validación de firmaEC
            Certificate[] certChain = keyStore.getCertificateChain(alias);
            Properties properties = parametros();
            properties.setProperty(PDFSignerItext.PATH, file);
            PDFSignerItext pDFSignerItext = new PDFSignerItext();
            signed = pDFSignerItext.sign(docByteArry, "SHA256", key, certChain, properties);
            System.out.println("final firma\n-------");
            ////// Permite guardar el archivo en el equipo y luego lo abre
            String nombreDocumento = FileUtils.crearNombreFirmado(new File(file), FileUtils.getExtension(signed));
            java.io.FileOutputStream fos = new java.io.FileOutputStream(nombreDocumento);
            //Abrir documento
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    try {
                        FileUtils.abrirDocumento(nombreDocumento);
                        System.out.println(nombreDocumento);
                        verificarDocumento(nombreDocumento);
                    } catch (java.lang.Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        System.exit(0);
                    }
                }
            }, 3000); //espera 3 segundos
            fos.write(signed);
            fos.close();
            //Abrir documento
        } else {
            System.out.println("Entidad Certificadora no reconocida");
        }
    }

    private static void validarCertificado() throws IOException, KeyStoreException, Exception {
        // ARCHIVO
        KeyStoreProvider ksp = new FileKeyStoreProvider(ARCHIVO);
        KeyStore keyStore = ksp.getKeystore(PASSWORD.toCharArray());
        // TOKEN
        //KeyStore keyStore = KeyStoreProviderFactory.getKeyStore(PASSWORD);

        String alias = seleccionarAlias(keyStore);
        X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
        System.out.println("UID: " + Utils.getUID(x509Certificate));
        System.out.println("CN: " + Utils.getCN(x509Certificate));
        System.out.println("emisión: " + CertEcUtils.getNombreCA(x509Certificate));
        System.out.println("fecha emisión: " + x509Certificate.getNotBefore());
        System.out.println("fecha expiración: " + x509Certificate.getNotAfter());
        System.out.println("ISSUER: " + x509Certificate.getIssuerX500Principal().getName());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        TemporalAccessor accessor = dateTimeFormatter.parse(TiempoUtils.getFechaHoraServidor(null));
        Date fechaHoraISO = Date.from(Instant.from(accessor));

        //Validad certificado revocado
        Date fechaRevocado = UtilsCrlOcsp.validarFechaRevocado(x509Certificate, null);
        if (fechaRevocado != null && fechaRevocado.compareTo(fechaHoraISO) <= 0) {
            System.out.println("Certificado revocado: " + fechaRevocado);
        }
        if (fechaHoraISO.compareTo(x509Certificate.getNotBefore()) <= 0 || fechaHoraISO.compareTo(x509Certificate.getNotAfter()) >= 0) {
            System.out.println("Certificado caducado");
        }
        System.out.println("Certificado emitido por entidad certificadora acreditada? " + Utils.verifySignature(x509Certificate));
    }

    private static void verificarDocumento(String file) throws IOException, SignatureVerificationException, Exception {
        File document = new File(file);
        Documento documento = Utils.verificarDocumento(document);
        System.out.println("Documento: " + documento);
        if (documento.getCertificados() != null) {
            documento.getCertificados().forEach((certificado) -> {
                System.out.println(certificado.toString());
            });
        } else {
            throw new InvalidFormatException("Documento no soportado");
        }
    }

    private static void leerNFC() throws Exception {
        TerminalFactory tf = TerminalFactory.getDefault();
        List< CardTerminal> terminals = tf.terminals().list();
        System.out.println("Available Readers:");
        System.out.println(terminals + "\n");
        CardTerminal terminal = null;

        int i = 0;
        for (Object next : terminals) {
            terminal = (CardTerminal) next;
            if (terminal.isCardPresent()) {
                System.out.println("i: " + i);
                break;
            }
            i++;
        }

        System.out.println("keyStore: " + KeyStoreProviderFactory.getKeyStore("11111111"));

        if (terminal.waitForCardPresent(5000)) {
            Card card = terminal.connect("*");
            CommandAPDU getAts = new CommandAPDU(0x00, 0xa4, 0x04, 0x00, new byte[]{(byte) 0xa0, 0x00, 0x00, 0x00, 0x62, 0x03, 0x01, 0x08, 0x01}, 0x7f);
            CardChannel channel = card.getBasicChannel();
            ResponseAPDU response = channel.transmit(getAts);

            System.out.println(response.getSW1());
            System.out.println(response.getSW2());

            System.out.println(" LE=" + getAts.getNc() + " LE=" + getAts.getNe());
        }
    }
    
    //pruebas de fecha-hora
    private static void fechaHora(int segundos) throws KeyStoreException, Exception {
        tiempo(segundos);//espera en segundos
        do {
            try {
                System.out.println("getFechaHora() " + TiempoUtils.getFechaHora(null));
                System.out.println("getFechaHoraServidor() " + TiempoUtils.getFechaHoraServidor(null));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } while (tiempo);
        System.exit(0);
    }

    private static boolean tiempo = true;

    private static void tiempo(int segundos) {
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                tiempo = false;
            }
        }, segundos * 1000); //espera 3 segundos
    }
    //pruebas de fecha-hora
}
