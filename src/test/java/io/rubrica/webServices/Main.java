/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author mfernandez
 */
public class Main {

//    private static final String URLAPI = "https://impapi.firmadigital.gob.ec/api";
    private static final String URLAPI = "http://impapi.firmadigital.gob.ec:8080/api";
//    private static final String URLAPI = "http://localhost:8080/api";
//    private static final String URLWS = "https://impws.firmadigital.gob.ec/servicio";
    private static final String URLWS = "http://impws.firmadigital.gob.ec:8080/servicio";
//    private static final String URLWS = "http://localhost:8080/servicio";

    private static final String PKCS12 = "/home/mfernandez/appFirmaEC/prueba.p12";
    private static final String PASSWORD = "123456";
    private static final String FILE = "/home/mfernandez/Test/documento_blanco.pdf";

    public static void main(String args[]) throws Exception {
//        appValidarCertificado();
        appFirmaTransversal();
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
        System.out.println(invocation.invoke(String.class));
    }

    private static void appFirmaTransversal() throws KeyStoreException, Exception {
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
        String cedula = "1234567890";
        int numeroCopias = 1;
        File documento = new File(FILE);

        //configuracion de cabecera
        HttpPost post = new HttpPost(URLWS + "/documentos");
        post.addHeader("content-type", "application/json");
        post.addHeader("X-API-KEY", apiKey);

        StringBuilder entity = new StringBuilder();
        if (documento.exists() == true) {
            //creacion del JSON
            com.google.gson.JsonArray gsonArray = new com.google.gson.JsonArray();
            com.google.gson.JsonObject gsonObject = null;
            gsonObject = new com.google.gson.JsonObject();
            gsonObject.addProperty("cedula", cedula);
            gsonObject.addProperty("sistema", sistema);

            //Arreglo de documento(s)
            com.google.gson.JsonArray gsonDocumentoArray = new com.google.gson.JsonArray();
            com.google.gson.JsonObject gsonDocumentoObject = null;
            gsonDocumentoObject = new com.google.gson.JsonObject();
            //dependiendo numero de copias
            for (int i = 0; i < numeroCopias; i++) {
                //Generando documento en base64 y agregando en JSON
                gsonDocumentoObject.addProperty("nombre", documento.getName() + i);
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
            String result;
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
            gsonObject.addProperty("tokenJwt", result);
            gsonObject.addProperty("llx", llx);
            gsonObject.addProperty("lly", lly);
            gsonObject.addProperty("tipoEstampado", tipoEstampado);
            gsonObject.addProperty("pagina", pagina);
//            gsonObject.addProperty("pre", true);
            gsonObject.addProperty("des", true);
            gsonObject.addProperty("url", URLAPI);
            System.out.println("gsonObject: " + gsonObject.toString());
            
            Form form = new Form();
            form.param("pkcs12", pkcs12Base64);
            form.param("password", PASSWORD);
            form.param("json", gsonObject.toString());

            Invocation invocation = builder.buildPost(Entity.form(form));
            System.out.println(invocation.invoke(String.class));
        } else {
            System.out.println("No se encontró el documento: " + documento.getPath());
        }
    }
}
