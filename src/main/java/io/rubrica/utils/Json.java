/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.rubrica.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;

import io.rubrica.certificate.to.Certificado;
import io.rubrica.certificate.to.DatosUsuario;
import io.rubrica.certificate.to.Documento;

/**
 *
 * @author mfernandez
 */
public class Json {

    private static final SimpleDateFormat simpleDateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static String generarJsonVersion(String sistemaOperativo, String aplicacion, String versionApp, String sha) {
        if (sistemaOperativo != null && versionApp != null && sha != null) {
            com.google.gson.JsonObject gsonObject = new com.google.gson.JsonObject();
            gsonObject.addProperty("sistemaOperativo", sistemaOperativo);
            gsonObject.addProperty("aplicacion", aplicacion);
            gsonObject.addProperty("versionApp", versionApp);
            gsonObject.addProperty("sha", sha);
            return gsonObject.toString();
        } else {
            return null;
        }
    }

    public static String generarJsonDocumento(Documento documento) {
        return generarJsonDocumentoFirmado(null, documento);
    }

    public static String generarJsonDocumentoFirmado(byte[] byteDocumentoSigned, Documento documento) {
        //creacion del JSON
        com.google.gson.JsonArray gsonArray = new com.google.gson.JsonArray();
        com.google.gson.JsonObject jsonObjectDocumento = null;
        jsonObjectDocumento = new com.google.gson.JsonObject();
        jsonObjectDocumento.addProperty("signValidate", documento.getSignValidate());
        jsonObjectDocumento.addProperty("docValidate", documento.getDocValidate());
        if (byteDocumentoSigned != null) {
            jsonObjectDocumento.addProperty("docSigned", java.util.Base64.getEncoder().encodeToString(byteDocumentoSigned));
        }
        jsonObjectDocumento.addProperty("error", documento.getError());

        //Arreglo de Certificado(s)
        com.google.gson.JsonArray jsonDocumentoArray = new com.google.gson.JsonArray();
        com.google.gson.JsonObject jsonObjectCertificado = null;
        for (Certificado certificado : documento.getCertificados()) {
            jsonObjectCertificado = new com.google.gson.JsonObject();
            jsonObjectCertificado.addProperty("issuedTo", certificado.getIssuedTo());
            jsonObjectCertificado.addProperty("issuedBy", certificado.getIssuedBy());
            jsonObjectCertificado.addProperty("validFrom", simpleDateFormatISO8601.format(certificado.getValidFrom().getTime()));
            jsonObjectCertificado.addProperty("validTo", simpleDateFormatISO8601.format(certificado.getValidTo().getTime()));
            jsonObjectCertificado.addProperty("generated", simpleDateFormatISO8601.format(certificado.getGenerated().getTime()));
            if (certificado.getRevocated() != null) {
                jsonObjectCertificado.addProperty("revocated", simpleDateFormatISO8601.format(certificado.getRevocated().getTime()));
            }
            jsonObjectCertificado.addProperty("validated", certificado.getValidated());
            jsonObjectCertificado.addProperty("keyUsages", certificado.getKeyUsages());
            if (certificado.getDocTimeStamp() != null) {
                jsonObjectCertificado.addProperty("docTimeStamp", simpleDateFormatISO8601.format(certificado.getDocTimeStamp()));
            }
            jsonObjectCertificado.addProperty("signVerify", certificado.getSignVerify());
            jsonObjectCertificado.addProperty("docReason", certificado.getDocReason());
            jsonObjectCertificado.addProperty("docLocation", certificado.getDocLocation());

            String json = generarJsonDatosUsuario(certificado.getDatosUsuario());
            JsonObject jsonObjectDatosUsuario = new Gson().fromJson(json, JsonObject.class);
            jsonObjectCertificado.add("datosUsuario", jsonObjectDatosUsuario);

            jsonDocumentoArray.add(jsonObjectCertificado);
        }
        jsonObjectDocumento.add("certificado", new com.google.gson.JsonParser()
                .parse(new com.google.gson.Gson().toJson(jsonDocumentoArray)).getAsJsonArray());
        gsonArray.add(jsonObjectDocumento);
        return gsonArray.toString();
    }

    public static String generarJsonCertificado(Certificado certificado) {
        //creacion del JSON
        com.google.gson.JsonArray gsonArray = new com.google.gson.JsonArray();
        com.google.gson.JsonObject jsonObjectCertificado = null;
        jsonObjectCertificado = new com.google.gson.JsonObject();
        jsonObjectCertificado.addProperty("issuedTo", certificado.getIssuedTo());
        jsonObjectCertificado.addProperty("issuedBy", certificado.getIssuedBy());
        jsonObjectCertificado.addProperty("validFrom", simpleDateFormatISO8601.format(certificado.getValidFrom().getTime()));
        jsonObjectCertificado.addProperty("validTo", simpleDateFormatISO8601.format(certificado.getValidTo().getTime()));
        if (certificado.getGenerated() != null) {
            jsonObjectCertificado.addProperty("generated", simpleDateFormatISO8601.format(certificado.getGenerated().getTime()));
        }
        if (certificado.getRevocated() != null) {
            jsonObjectCertificado.addProperty("revocated", simpleDateFormatISO8601.format(certificado.getRevocated().getTime()));
        }
        jsonObjectCertificado.addProperty("validated", certificado.getValidated());
        jsonObjectCertificado.addProperty("keyUsages", certificado.getKeyUsages());
        if (certificado.getDocTimeStamp() != null) {
            jsonObjectCertificado.addProperty("docTimeStamp", simpleDateFormatISO8601.format(certificado.getDocTimeStamp()));
        }
        jsonObjectCertificado.addProperty("signVerify", certificado.getSignVerify());
        jsonObjectCertificado.addProperty("docReason", certificado.getDocReason());
        jsonObjectCertificado.addProperty("docLocation", certificado.getDocLocation());

        String json = null;
        if (certificado.getDatosUsuario() != null) {
            json = generarJsonDatosUsuario(certificado.getDatosUsuario());
        }
        JsonObject jsonObjectDatosUsuario = new Gson().fromJson(json, JsonObject.class);
        jsonObjectCertificado.add("datosUsuario", jsonObjectDatosUsuario);

        gsonArray.add(jsonObjectCertificado);
        return gsonArray.toString();
    }

    public static String generarJsonDatosUsuario(DatosUsuario datosUsuario) {
        com.google.gson.JsonObject jsonObjectDatosUsuario = null;
        jsonObjectDatosUsuario = new com.google.gson.JsonObject();
        jsonObjectDatosUsuario.addProperty("cedula", datosUsuario.getCedula());
        jsonObjectDatosUsuario.addProperty("nombre", datosUsuario.getNombre());
        jsonObjectDatosUsuario.addProperty("apellido", datosUsuario.getApellido());
        jsonObjectDatosUsuario.addProperty("institucion", datosUsuario.getInstitucion());
        jsonObjectDatosUsuario.addProperty("cargo", datosUsuario.getCargo());
        jsonObjectDatosUsuario.addProperty("serial", datosUsuario.getSerial());
        if (datosUsuario.getFechaFirmaArchivo() != null) {
            jsonObjectDatosUsuario.addProperty("fechaFirmaArchivo", datosUsuario.getFechaFirmaArchivo());
        }
        jsonObjectDatosUsuario.addProperty("entidadCertificadora", datosUsuario.getEntidadCertificadora());
        jsonObjectDatosUsuario.addProperty("selladoTiempo", datosUsuario.getSelladoTiempo());
        jsonObjectDatosUsuario.addProperty("certificadoDigitalValido", datosUsuario.isCertificadoDigitalValido());
        return jsonObjectDatosUsuario.toString();
    }

}
