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
package io.rubrica.webServices;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyStoreException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

/**
 * Metodo de pruebas funcionales
 *
 * @author mfernandez
 */
public class Main {

    private static final String URLAPI = "https://api.firmadigital.gob.ec/api";
//    private static final String URLAPI = "https://impapi.firmadigital.gob.ec/api";
//    private static final String URLAPI = "http://impapi.firmadigital.gob.ec:8181/api";
//    private static final String URLAPI = "http://localhost:8080/api";
    private static final String URLWS = "https://impws.firmadigital.gob.ec/servicio";
//    private static final String URLWS = "http://impws.firmadigital.gob.ec:8080/servicio";
//    private static final String URLWS = "http://impws.firmadigital.gob.ec:8080/servicio";
//    private static final String URLWS = "http://localhost:8080/servicio";
    private static final String PKCS12 = "/home/mfernandez/appFirmaEC/prueba.p12";
    private static final String PASSWORD = "123456";
    private static final String FILE = "/home/mfernandez/appFirmaEC/Casos QA/ACTA DE 65 FIRMAS-signed-signed-signed-signed.pdf";
//    private static final String FILE = "/home/mfernandez/Test/documento_blanco.pdf";
    private static String cedula = "1234567890";

    public static void main(String args[]) throws Exception {
        appFirmarDocumento();
//        appVerificarDocumento();
//        appValidarCertificado();
//        appFirmarDocumentoTransversal();
    }

    private static void appFirmarDocumento() throws IOException, KeyStoreException, Exception {
        String tipoEstampado = "QR";//QR, information1, information2
        int pagina = 1;//pagina en donde se estampa la firma (sin el parametro, se estampa en la ultima hoja)
        //SUPERIOR IZQUIERDA
        String llx = "10";
        String lly = "830";
        //FIRMAR
        String urlws = URLAPI + "/appfirmardocumento";
        File pkcs12 = new File(PKCS12);
        String pkcs12Base64 = java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(pkcs12.toPath()));
        File file = new File(FILE);
        String fileBase64 = java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(urlws);
        Invocation.Builder builder = target.request();

        //creacion del JSON
        com.google.gson.JsonObject gsonObject = new com.google.gson.JsonObject();
        gsonObject = new com.google.gson.JsonObject();
        gsonObject.addProperty("versionFirmaEC", "RUBRICA");
        gsonObject.addProperty("formatoDocumento", "PDF");
        gsonObject.addProperty("llx", llx);
        gsonObject.addProperty("lly", lly);
        gsonObject.addProperty("pagina", pagina);
        gsonObject.addProperty("tipoEstampado", tipoEstampado);
        System.out.println("gsonObject: " + gsonObject.toString());

        Form form = new Form();
        form.param("pkcs12", pkcs12Base64);
        form.param("password", PASSWORD);
        form.param("documento", fileBase64);
        form.param("json", gsonObject.toString());

        Invocation invocation = builder.buildPost(Entity.form(form));
        Response response = invocation.invoke();
        System.out.println("Status: " + response.getStatus() + "\nResult: " + response.readEntity(String.class));
    }

    private static void appVerificarDocumento() throws IOException, KeyStoreException, Exception {
        String urlws = URLAPI + "/appverificardocumento";
        File file = new File(FILE);
        String fileBase64 = java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(urlws);
        Invocation.Builder builder = target.request();

        Form form = new Form();
        form.param("documento", fileBase64);

        Invocation invocation = builder.buildPost(Entity.form(form));
        Response response = invocation.invoke();
        int status = response.getStatus();
        String result = response.readEntity(String.class
        );
        System.out.println("Status: " + status + "\nResult: " + result);
    }

    private static void appValidarCertificado() throws IOException, KeyStoreException, Exception {
        String urlws = URLAPI + "/appvalidarcertificadodigital";
        File pkcs12 = new File(PKCS12);
        String pkcs12Base64 = java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(pkcs12.toPath()));

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(urlws);
        Invocation.Builder builder = target.request();

        Form form = new Form();
        form.param("pkcs12", pkcs12Base64);
        form.param("password", PASSWORD);

        Invocation invocation = builder.buildPost(Entity.form(form));
        Response response = invocation.invoke();
        int status = response.getStatus();
        String result = response.readEntity(String.class
        );
        System.out.println("Status: " + status + "\nResult: " + result);
    }

    private static String generarJWT(String sistema, String apiKey, String tipoEstampado, int pagina, String llx, String lly, int certificado, String cedula, int numeroCopias, File documento) throws KeyStoreException, Exception {
        String result = null;

        //configuracion de cabecera
        HttpPost post = new HttpPost(URLWS + "/documentos");
        post.addHeader("content-type", "application/json");
        post.addHeader("X-API-KEY", apiKey);

        StringBuilder entity = new StringBuilder();

        //creacion del JSON
        com.google.gson.JsonArray gsonArray = new com.google.gson.JsonArray();
        com.google.gson.JsonObject gsonObject = null;
        gsonObject = new com.google.gson.JsonObject();
        gsonObject.addProperty("cedula", cedula);
        gsonObject.addProperty("sistema", sistema);

        //Arreglo de documento(s)
        com.google.gson.JsonArray gsonDocumentoArray = new com.google.gson.JsonArray();
        com.google.gson.JsonObject gsonDocumentoObject = null;
        //dependiendo numero de copias
        for (int i = 0; i < numeroCopias; i++) {
            gsonDocumentoObject = new com.google.gson.JsonObject();
            //Generando documento en base64 y agregando en JSON
            gsonDocumentoObject.addProperty("nombre", documento.getName() + i + ".pdf");
            gsonDocumentoObject.addProperty("documento", java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(documento.toPath())));
            gsonDocumentoArray.add(gsonDocumentoObject);
        }
        gsonObject.add("documentos", new com.google.gson.JsonParser()
                .parse(new com.google.gson.Gson().toJson(gsonDocumentoArray)).getAsJsonArray());
        gsonArray.add(gsonObject);

        //presentar por consola el JSON
        System.out.println("JSON: " + gsonObject.toString());
        entity.append(gsonObject.toString());

        // send a JSON data
        post.setEntity(new StringEntity(entity.toString()));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(post)) {
            result = EntityUtils.toString(response.getEntity());
            //presentar por consola la respuesta
            System.out.println(response.getStatusLine().getStatusCode() + " - " + response.getStatusLine().getReasonPhrase());
            httpClient.close();
        }

        //presentar por consola el JWT (Json Web Token)
        System.out.println("JWT: " + result);

        //presentar por consola la url para ejecutar desde navegador
        System.out.println("firmaec://" + sistema + "/firmar?token=" + result + "&tipo_certificado=" + certificado + "&llx=" + llx + "&lly=" + lly + "&estampado=" + tipoEstampado + "&pagina=" + pagina + "&pre=true&url=" + URLAPI);

        //presentar por consola la url para ejecutar desde navegador (URLEncoder)
        System.out.println("firmaec://" + sistema + "/firmar?token=" + result + "&tipo_certificado=" + certificado + "&llx=" + llx + "&lly=" + lly + "&estampado=" + tipoEstampado + "&pagina=" + pagina + "&pre=true&url=" + URLEncoder.encode(URLAPI, StandardCharsets.UTF_8));

        return result;
    }

    private static void appFirmarDocumentoTransversal() throws KeyStoreException, Exception {
        String sistema = "pruebas";
        String apiKey = "pruebas";
        String tipoEstampado = "QR";//QR, information1, information2
        int pagina = 1;//pagina en donde se estampa la firma (sin el parametro, se estampa en la ultima hoja)
        //ubicación de la estampa 
        //SUPERIOR IZQUIERDA
        String llx = "10";
        String lly = "830";
        //INFERIOR IZQUIERDA
        //String llx = "100";
        //String lly = "91";
        //INFERIOR DERECHA
        //String llx = "419";
        //String lly = "91";
        //INFERIOR CENTRADO
        //String llx = "260";
        //String lly = "91";

        //Variantes
        int certificado = 2;//1 token 2 archivo
        
        int numeroCopias = 3;
        File documento = new File(FILE);

        //creacion del JSON
        com.google.gson.JsonObject gsonObject = null;
        gsonObject = new com.google.gson.JsonObject();
        
        String jwt = null;
        //PRUEBAS GENERANDO JWT
        if (documento.exists() == true) {
            jwt = generarJWT(sistema, apiKey, tipoEstampado, pagina, llx, lly, certificado, cedula, numeroCopias, documento);
        } else {
            System.out.println("No se encontró el documento: " + documento.getPath());
        }
        //PRUEBAS GENERANDO JWT
        
        //PRUEBAS CON JWT GENERADO
//        sistema = "quipuxPruebas";
//        jwt = "";
        //PRUEBAS CON JWT GENERADO
        
        if (jwt != null) {
            //FIRMAR TRANSVERSAL
            String urlws = URLAPI + "/appfirmardocumentotransversal";
            File pkcs12 = new File(PKCS12);
            String pkcs12Base64 = java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(pkcs12.toPath()));
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(urlws);
            Invocation.Builder builder = target.request();

            //creacion del JSON
            gsonObject = new com.google.gson.JsonObject();
            gsonObject.addProperty("sistema", sistema);
            gsonObject.addProperty("operacion", "firmar");
            gsonObject.addProperty("versionFirmaEC", "RUBRICA");
            gsonObject.addProperty("formatoDocumento", "PDF");
            gsonObject.addProperty("tokenJwt", jwt);
            gsonObject.addProperty("llx", llx);
            gsonObject.addProperty("lly", lly);
            gsonObject.addProperty("tipoEstampado", tipoEstampado);
            gsonObject.addProperty("pagina", pagina);
            gsonObject.addProperty("pre", true);
//            gsonObject.addProperty("des", true);
//            gsonObject.addProperty("url", URLAPI);
            System.out.println("gsonObject: " + gsonObject.toString());

            Form form = new Form();
            form.param("pkcs12", pkcs12Base64);
            form.param("password", PASSWORD);
            form.param("json", gsonObject.toString());

            Invocation invocation = builder.buildPost(Entity.form(form));
            Response response = invocation.invoke();
            System.out.println("Status: " + response.getStatus() + "\nResult: " + response.readEntity(String.class
            ));

        }
    }
}
