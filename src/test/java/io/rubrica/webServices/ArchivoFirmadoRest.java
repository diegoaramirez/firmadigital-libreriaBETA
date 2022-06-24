/**
 *
 */
package io.rubrica.webServices;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//http://localhost:8080/serviciosistema/rest/documentoFirmado/mensaje
@Path("/documentoFirmado")
public class ArchivoFirmadoRest {

    private final Logger log = LoggerFactory.getLogger(ArchivoFirmadoRest.class);

    private static final String API_KEY_HEADER = "X-API-KEY";

    @POST
    @Path("/grabar_archivos_firmados")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces(MediaType.TEXT_PLAIN)
    public Response guardarDocumentosFirmados(@HeaderParam(API_KEY_HEADER) String apiKey, String jsonParameter) {

        if (apiKey == null) {
            return Response.status(Status.BAD_REQUEST).entity("Sin API Key").build();
        }

        log.info("TO firmado:" + jsonParameter + " apikey:" + apiKey);

        if (jsonParameter == null || jsonParameter.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("No hay texto tipo JSON").build();
        }

        JsonReader jsonReader = Json.createReader(new StringReader(jsonParameter));
        JsonObject json;
        try {
            json = (JsonObject) jsonReader.read();
        } catch (JsonParsingException e) {
            return Response.status(Status.BAD_REQUEST).entity("Error al decodificar JSON: \"" + e.getMessage() + "\"")
                    .build();
        }

        if (!apiKey.equals("prueba")) {
            log.error("Error al validar API_KEY {0}", apiKey);
            return Response.status(Status.FORBIDDEN).entity("Error al validar API_KEY").build();
        }
        String usuario;
        try {
            usuario = json.getString("usuario");
        } catch (NullPointerException e) {
            return Response.status(Status.BAD_REQUEST).entity("Error al decodificar JSON: Se debe incluir \"cedula\"")
                    .build();
        }
        String archivo;
        try {
            archivo = json.getString("archivo");
            if (archivo == null) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Error al decodificar JSON: Se debe incluir \"archivo\"").build();
            }
        } catch (NullPointerException e) {
            return Response.status(Status.BAD_REQUEST).entity("Error al decodificar JSON: Se debe incluir \"archivo\"")
                    .build();
        }
        log.info("Validando archivo {0}", archivo);
        try {
            //preparando para almacenar documento
            log.info("Construyendo objeto para su almacenamiento {0}", "info documento");
            //almacenar documento
            log.info("Finalizado el proceso............");
        } catch (Exception e) {
            return Response.status(Status.FORBIDDEN).entity("Error al guardar el archivo").build();
        }

        return enviarRespuestaExitoHTTP("200");
    }

    @GET
    @Path("/mensaje")
    public Response imprimirMessage() {
        String salida = "Hola desde un Restful Web Service ejemplo ";
        return Response.status(200).entity(salida).build();
    }

    /**
     * Envia respuesta de exito segun contenido
     *
     * @param contenido objeto pojo o lista
     * @return respuesta HTTP (200 o 204)
     */
    public static Response enviarRespuestaExitoHTTP(Object contenido) {
        if (esObjetoVacio(contenido)) {
            return Response.noContent().build();
        } else {
            GenericEntity<Object> entidadRespuesta = new GenericEntity<Object>(contenido) {
            };
            return Response.ok(entidadRespuesta, MediaType.APPLICATION_JSON).build();
        }
    }

    public static Boolean esObjetoVacio(Object contenido) {
        if (null == contenido) {
            return Boolean.TRUE;
        } else if (contenido instanceof String) {
            return contenido.toString().isEmpty();
        } else if (contenido instanceof Collection<?>) {
            List<Object> listaContenido = (List<Object>) contenido;
            if (listaContenido.isEmpty()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

}
