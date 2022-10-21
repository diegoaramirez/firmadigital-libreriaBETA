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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Logger;

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
            String parametro = "base64=" + base64;
            URL url = new URL(fecha_hora_url);
            HttpURLConnection conn = null;
            DataOutputStream dataOutputStream = null;
            InputStream inputStream = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(TIME_OUT);
                conn.setReadTimeout(TIME_OUT);
                conn.setRequestMethod("POST");
                conn.setInstanceFollowRedirects(false);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(parametro.getBytes().length));

                dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes(parametro);
                dataOutputStream.flush();

                int responseCode = conn.getResponseCode();
                LOGGER.fine("POST Response Code: " + responseCode);
                System.out.println("POST Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = conn.getInputStream();
                    Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                    String response = s.hasNext() ? s.next() : null;
                    System.out.println("response: " + response);
                    return response;
                } else {
                    throw new HoraServidorException(PropertiesUtils.getMessages().getProperty("mensaje.error.problema_red"));
                }
            } finally {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }
}
