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

import static io.rubrica.certificate.CertUtils.seleccionarAlias;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Properties;

import io.rubrica.certificate.CertEcUtils;
import io.rubrica.certificate.to.Documento;
import io.rubrica.exceptions.InvalidFormatException;
import io.rubrica.exceptions.SignatureVerificationException;
import io.rubrica.keystore.Bit4IdGenericAppleKeyStoreProvider;
import io.rubrica.keystore.FileKeyStoreProvider;
import io.rubrica.keystore.KeyStoreProvider;
import io.rubrica.keystore.UKCGenericAppleKeyStoreProvider;
import io.rubrica.sign.pdf.PDFSigner;
import io.rubrica.sign.pdf.PdfUtil;
import io.rubrica.utils.FileUtils;
import io.rubrica.utils.TiempoUtils;
import io.rubrica.utils.Utils;
import io.rubrica.utils.UtilsCrlOcsp;
import io.rubrica.utils.X509CertificateUtils;
import io.rubrica.validaciones.DocumentoUtils;

/**
 * Metodo de pruebas funcionales
 * 
 * @author mfernandez
 */
public class MainUanataca {

	// ARCHIVO
	private static final String ARCHIVO = "/Users/elomas/Desktop/firma/elomas.p12";
	private static String password;
	private static final String FILE = "/Users/elomas/Desktop/firma/Document.pdf";
	private static final String FILE_FIRMADO = "/Users/elomas/Desktop/firma/Document-signed.pdf";
	private static final int CONTAINER = 0; // Archivo: 0, UKC: 1, Token: 2

	public static void main(String args[]) throws KeyStoreException, Exception {
		password = System.getenv("UANATAKA_PIN");
		firmarDocumento(FILE);
//		validarCertificado();
//		verificarDocumento(FILE_FIRMADO);
	}

	private static Properties parametros() throws IOException {
		String llx = "100";
		String lly = "50";

		Properties params = new Properties();
		params.setProperty(PDFSigner.SIGNING_LOCATION, "Atuntaqui - Imbabura");
		params.setProperty(PDFSigner.SIGNING_REASON, "Firmado digitalmente con RUBRICA");
		params.setProperty(PDFSigner.SIGN_TIME, TiempoUtils.getFechaHoraServidor());
		params.setProperty(PDFSigner.LAST_PAGE, "1");
//        params.setProperty(PDFSigner.TYPE_SIG, "QR");
//        params.setProperty(PDFSigner.INFO_QR, "Firmado digitalmente con RUBRICA\nhttps://minka.gob.ec/rubrica/rubrica");
		params.setProperty(PDFSigner.TYPE_SIG, "information2");
		// params.setProperty(PDFSigner.FONT_SIZE, "4.5");
		// Posicion firma
		params.setProperty(PdfUtil.POSITION_ON_PAGE_LOWER_LEFT_X, llx);
		params.setProperty(PdfUtil.POSITION_ON_PAGE_LOWER_LEFT_Y, lly);
		// params.setProperty(PdfUtil.POSITION_ON_PAGE_UPPER_RIGHT_X, urx);
		// params.setProperty(PdfUtil.POSITION_ON_PAGE_UPPER_RIGHT_Y, ury);
		return params;
	}

	private static void firmarDocumento(String file) throws KeyStoreException, Exception {
		////// LEER PDF:
		byte[] docByteArry = DocumentoUtils.loadFile(file);
		KeyStore keyStore = instancesKeyStore();
		byte[] signed = null;
		Signer signer = Utils.documentSigner(new File(file));
		String alias = seleccionarAlias(keyStore);
		PrivateKey key = (PrivateKey) keyStore.getKey(alias, password.toCharArray());

		X509CertificateUtils x509CertificateUtils = new X509CertificateUtils();
		if (x509CertificateUtils.validarX509Certificate((X509Certificate) keyStore.getCertificate(alias))) {// validación de firmaEC
			Certificate[] certChain = keyStore.getCertificateChain(alias);
			signed = signer.sign(docByteArry, SignConstants.SIGN_ALGORITHM_SHA1WITHRSA, key, certChain, parametros());
			System.out.println("final firma\n-------");
			////// Permite guardar el archivo en el equipo y luego lo abre
			String nombreDocumento = FileUtils.crearNombreFirmado(new File(file), FileUtils.getExtension(signed));
			java.io.FileOutputStream fos = new java.io.FileOutputStream(nombreDocumento);
			// Abrir documento
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
			}, 3000); // espera 3 segundos
			fos.write(signed);
			fos.close();
			// Abrir documento
		} else {
			System.out.println("Entidad Certificadora no reconocida");
		}
	}

	private static void validarCertificado() throws IOException, KeyStoreException, Exception {
		KeyStore keyStore = instancesKeyStore();

		String alias = seleccionarAlias(keyStore);
		X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
		System.out.println("UID: " + Utils.getUID(x509Certificate));
		System.out.println("CN: " + Utils.getCN(x509Certificate));
		System.out.println("emisión: " + CertEcUtils.getNombreCA(x509Certificate));
		System.out.println("fecha emisión: " + x509Certificate.getNotBefore());
		System.out.println("fecha expiración: " + x509Certificate.getNotAfter());
		System.out.println("ISSUER: " + x509Certificate.getIssuerX500Principal().getName());

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		TemporalAccessor accessor = dateTimeFormatter.parse(TiempoUtils.getFechaHoraServidor());
		Date fechaHoraISO = Date.from(Instant.from(accessor));

		// Validad certificado revocado
		Date fechaRevocado = UtilsCrlOcsp.validarFechaRevocado(x509Certificate);
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

	// pruebas de fecha-hora
	private static void fechaHora(int segundos) throws KeyStoreException, Exception {
		tiempo(segundos);// espera en segundos
		do {
			try {
				System.out.println("getFechaHora() " + TiempoUtils.getFechaHora());
				System.out.println("getFechaHoraServidor() " + TiempoUtils.getFechaHoraServidor());
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
		}, segundos * 1000); // espera 3 segundos
	}

	private static KeyStore instancesKeyStore() throws KeyStoreException {
		KeyStoreProvider keyStoreProvider;
		switch (CONTAINER) {
		case 0:
			keyStoreProvider = new FileKeyStoreProvider(ARCHIVO);
			break;
		case 1:
			keyStoreProvider = new UKCGenericAppleKeyStoreProvider();
			break;
		case 2:
			keyStoreProvider = new Bit4IdGenericAppleKeyStoreProvider();
			break;

		default:
			throw new RuntimeException("No se identifica el contenedor");
		}
		return keyStoreProvider.getKeystore(password.toCharArray());
	}
}
