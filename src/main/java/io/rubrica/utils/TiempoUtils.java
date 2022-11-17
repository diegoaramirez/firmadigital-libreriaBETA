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
package io.rubrica.utils;

import io.rubrica.exceptions.HoraServidorException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientProperties;

/**
 * Utilidades para manejar tiempos
 *
 * @author mfernandez
 */
public class TiempoUtils {

    private static final Logger LOGGER = Logger.getLogger(TiempoUtils.class.getName());
    private static final int TIME_OUT = 5000; //set timeout to 5 seconds

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static Date getFechaHora(String apiUrl, String base64) throws HoraServidorException {
        String fechaHora;
        try {
            fechaHora = getFechaHoraServidor(apiUrl, base64);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("No se puede obtener la fecha del servidor: " + e.getMessage());
            throw new HoraServidorException(PropertiesUtils.getMessages().getProperty("mensaje.error.problema_red"));
        }
        try {
            TemporalAccessor accessor = DATE_TIME_FORMATTER.parse(fechaHora);
            return Date.from(Instant.from(accessor));
        } catch (DateTimeParseException e) {
            LOGGER.severe("La fecha indicada ('" + fechaHora + "') no sigue el patron ISO-8601: " + e);
            return new Date();
        }
    }

    public static String getFechaHoraServidor(String apiUrl, String base64) throws IOException, HoraServidorException {
        String fecha_hora_url = apiUrl == null ? PropertiesUtils.getConfig().getProperty("fecha_hora_url") : apiUrl;
        System.out.println("fecha_hora_url: " + fecha_hora_url);
        if (fecha_hora_url == null) {
            // La fecha actual en formato ISO-8601 (2017-08-27T17:54:43.562-05:00)
            //return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return null;
//            throw new RuntimeException(PropertiesUtils.getMessages().getProperty("mensaje.error.fecha_hora_url"));
        } else {
            Client client = ClientBuilder.newClient();
            client.property(ClientProperties.CONNECT_TIMEOUT, TIME_OUT);
            client.property(ClientProperties.READ_TIMEOUT, TIME_OUT);
            WebTarget target = client.target(fecha_hora_url);
            Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
            Form form = new Form();
            form.param("base64", base64);
            Invocation invocation = builder.buildPost(Entity.form(form));
            // Leer la respuesta
            Response response = invocation.invoke();
            int statusCode = response.getStatus();
            String respuesta = response.readEntity(String.class);
            if (statusCode == HttpURLConnection.HTTP_OK) {
                return respuesta;
            } else {
                throw new HoraServidorException(PropertiesUtils.getMessages().getProperty("mensaje.error.problema_red"));
            }
        }
    }
}
