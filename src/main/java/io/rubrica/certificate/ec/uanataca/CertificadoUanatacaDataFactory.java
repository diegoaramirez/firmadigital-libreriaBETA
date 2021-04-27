/* 
 * AppsHandler © 2021
 * development@appshandler.com
 * http://www.appshandler.com
 */
package io.rubrica.certificate.ec.uanataca;

import static io.rubrica.certificate.ec.uanataca.CertificadoUanataca.OID_CERTIFICADO_MIEMBRO_EMPRESA;
import static io.rubrica.certificate.ec.uanataca.CertificadoUanataca.OID_CERTIFICADO_PERSONA_JURIDICA;
import static io.rubrica.certificate.ec.uanataca.CertificadoUanataca.OID_CERTIFICADO_PERSONA_NATURAL;
import static io.rubrica.certificate.ec.uanataca.CertificadoUanataca.OID_CERTIFICADO_REPRESENTANTE_EMPRESA;
import static io.rubrica.utils.BouncyCastleUtils.certificateHasPolicy;

import java.security.cert.X509Certificate;

/**
 * @author Edison Lomas Almeida
 */
public class CertificadoUanatacaDataFactory {

	public static boolean esCertificadoUanataca(X509Certificate certificado) {
		return (certificateHasPolicy(certificado, OID_CERTIFICADO_PERSONA_NATURAL) || 
				certificateHasPolicy(certificado, OID_CERTIFICADO_PERSONA_JURIDICA) || 
				certificateHasPolicy(certificado, OID_CERTIFICADO_MIEMBRO_EMPRESA) || 
				certificateHasPolicy(certificado, OID_CERTIFICADO_REPRESENTANTE_EMPRESA));
	}

	public static CertificadoUanataca construir(X509Certificate certificado) {
		if (certificateHasPolicy(certificado, OID_CERTIFICADO_PERSONA_NATURAL)) {
			return new CertificadoPersonaNaturalUanataca(certificado);
		} else if (certificateHasPolicy(certificado, OID_CERTIFICADO_PERSONA_JURIDICA)) {
			return new CertificadoPersonaJuridicaPrivadaUanataca(certificado);
		} else if (certificateHasPolicy(certificado, OID_CERTIFICADO_MIEMBRO_EMPRESA)) {
			return new CertificadoMiembroEmpresaUanataca(certificado);
		} else if (certificateHasPolicy(certificado, OID_CERTIFICADO_REPRESENTANTE_EMPRESA)) {
			return new CertificadoRepresentanteLegalUanataca(certificado);
		} else {
			throw new RuntimeException("Certificado del Unataca S.A. de tipo desconocido!");
		}
	}

}
