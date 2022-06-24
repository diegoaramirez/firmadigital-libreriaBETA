/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.rubrica.sw;

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
import io.rubrica.utils.TiempoUtils;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author mfernandez
 */
public class Main {

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

    public static void main(String args[]) throws Exception {
        //fechaHora(240);//espera en segundos
        consumoServicioWeb();
    }

    private static void consumoServicioWeb() throws KeyStoreException, Exception {
        //constantes
        String urlapi = "https://impapi.firmadigital.gob.ec/api";
        String urlws = "https://impws.firmadigital.gob.ec/servicio/documentos";
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
        String cedula = "0704604032";
        int numeroCopias = 2;
        File documento = new File("/home/mfernandez/documento_blanco.pdf");

        //configuracion de cabecera
        HttpPost post = new HttpPost(urlws);
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

            /*String url = "firmaec://" + sistema + "/firmar?token=" + result + "&tipo_certificado=" + certificado + "&llx=" + llx + "&lly=" + lly + "&estampado=" + tipoEstampado + "&pagina=" + pagina + "&url=" + urlapi;
            //presentar por consola la url para ejecutar desde navegador
            System.out.println(url);
            
            //presentar por consola la url para ejecutar desde navegador ()
            System.out.println(URLEncoder.encode(url, StandardCharsets.UTF_8));*/
            
            //presentar por consola la url para ejecutar desde navegador
            System.out.println("firmaec://" + sistema + "/firmar?token=" + result + "&tipo_certificado=" + certificado + "&llx=" + llx + "&lly=" + lly + "&estampado=" + tipoEstampado + "&pagina=" + pagina + "&pre=true&url=" + urlapi);
            
            //presentar por consola la url para ejecutar desde navegador (URLEncoder)
            System.out.println("firmaec://" + sistema + "/firmar?token=" + result + "&tipo_certificado=" + certificado + "&llx=" + llx + "&lly=" + lly + "&estampado=" + tipoEstampado + "&pagina=" + pagina + "&pre=true&url=" + URLEncoder.encode(urlapi, StandardCharsets.UTF_8));
        } else {
            System.out.println("No se encontró el documento: " + documento.getPath());
        }
    }
}
