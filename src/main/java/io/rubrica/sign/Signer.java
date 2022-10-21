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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Properties;

import io.rubrica.exceptions.InvalidFormatException;
import io.rubrica.exceptions.RubricaException;
import java.io.InputStream;

/**
 * Permite la firma digital de documentos.
 *
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 */
public interface Signer {

    /**
     * Firma digitalmente un archivo.
     *
     * @param data Archivo a firmar
     * @param algorithm Algoritmo a usar para la firma
     * @param key Clave privada a usar para firmar
     * @param certChain Cadena de certificados del firmante
     * @param extraParams Parámetros adicionales para la firma
     * @param base64
     * @return Contenido firmado
     * @throws RubricaException Cuando ocurre cualquier problema durante el
     * proceso
     * @throws IOException Cuando ocurren problemas relacionados con la lectura
     * de los datos
     */
    byte[] sign(byte[] data, String algorithm, PrivateKey key, Certificate[] certChain, Properties extraParams, String base64)
            throws RubricaException, IOException;

    List<SignInfo> getSigners(byte[] sign) throws InvalidFormatException, IOException;

    /**
     * Returns the hash algorithm.
     *
     * @return The hash algorithm (e.g. "SHA-1", "SHA-256,...").
     */
    // String getHashAlgorithm();
    /**
     * Returns the encryption algorithm used for signing.
     *
     * @return The encryption algorithm ("RSA" or "DSA").
     */
    // String getEncryptionAlgorithm();
    /**
     * Signs the given message using the encryption algorithm in combination
     * with the hash algorithm.
     *
     * @param message The message you want to be hashed and signed.
     * @return A signed message digest.
     * @throws GeneralSecurityException when requested cryptographic algorithm
     * or security provider is not available
     */
    //byte[] sign(byte[] message) throws GeneralSecurityException;
}
