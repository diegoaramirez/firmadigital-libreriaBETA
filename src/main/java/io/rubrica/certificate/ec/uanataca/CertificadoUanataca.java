/* 
 * AppsHandler © 2021
 * development@appshandler.com
 * http://www.appshandler.com
 */
package io.rubrica.certificate.ec.uanataca;

import java.io.IOException;
import java.security.cert.X509Certificate;

import io.rubrica.certificate.CertUtils;

/**
 * @author Edison Lomas Almeida
 */
public class CertificadoUanataca {
	
	 // OIDs de tipo de certificado:
	public static final String OID_CERTIFICADO_PERSONA_NATURAL = "1.3.6.1.4.1.47286.102.2.1";
	public static final String OID_CERTIFICADO_MIEMBRO_EMPRESA = "1.3.6.1.4.1.47286.102.2.2";
	public static final String OID_CERTIFICADO_REPRESENTANTE_EMPRESA = "1.3.6.1.4.1.47286.102.2.3";
	public static final String OID_CERTIFICADO_PERSONA_JURIDICA = "1.3.6.1.4.1.47286.102.2.4";
	//public static final String OID_CERTIFICADO_EMPRESA = "1.3.6.1.4.1.43745.1.2.2.1";
    public static final String OID_SELLADO_TIEMPO = "1.3.6.1.4.1.47286.102.2.5";

    // OIDs de Campos del Certificado:
    public static final String OID_CEDULA_PASAPORTE = "1.3.6.1.4.1.47286.102.3.1";
    public static final String OID_NOMBRES = "1.3.6.1.4.1.47286.102.3.2";
    public static final String OID_APELLIDO_1 = "1.3.6.1.4.1.47286.102.3.3";
    public static final String OID_APELLIDO_2 = "1.3.6.1.4.1.47286.102.3.4";
    public static final String OID_CARGO = "1.3.6.1.4.1.47286.102.3.5";
    public static final String OID_DIRECCION = "1.3.6.1.4.1.47286.102.3.7";
    public static final String OID_TELEFONO = "1.3.6.1.4.1.47286.102.3.8";
    public static final String OID_CIUDAD = "1.3.6.1.4.1.47286.102.3.9";
    public static final String OID_RAZON_SOCIAL = "1.3.6.1.4.1.47286.102.3.10 ";
    public static final String OID_RUC = "1.3.6.1.4.1.47286.102.3.11";
    public static final String OID_PAIS = "1.3.6.1.4.1.47286.102.3.12";
    public static final String OID_CERTIFICADO = "1.3.6.1.4.1.47286.102.3.50";
    public static final String OID_CONTENEDOR = "1.3.6.1.4.1.47286.102.3.51";
    public static final String OID_RUP = "1.3.6.1.4.1.47286.102.3.52";
  //TODO Subject del sujeto
    public static final String OID_PROFESION = "2.5.4.12";
    public static final String OID_DEPARTAMENTO = "2.5.4.11";

    /**
     * Certificado a analizar
     */
    private final X509Certificate certificado;

    public CertificadoUanataca(X509Certificate certificado) {
        this.certificado = certificado;
	}

    /**
     * Retorna el valor de la extension, y una cadena vacia si no existe.
     *
     * @param oid
     * @return
     */
    protected String obtenerExtension(String oid) {
        try {
            String valor = CertUtils.getExtensionValueSubjectAlternativeNames(certificado, oid);
            return (valor != null) ? valor : "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
