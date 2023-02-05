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
package io.rubrica.keystore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AuthProvider;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

/**
 * Implementacion de <code>KeyStoreProvider</code> para utilizar con
 * dispositivos fisicos tipo PKCS#11 (Token USB, Smart Card, etc).
 * https://docs.oracle.com/javase/9/security/pkcs11-reference-guide1.htm#JSSEC-GUID-85EA1017-E59C-49B9-9207-65B7B2BF171E
 *
 * Utiliza internamente la clase <code>sun.security.pkcs11.SunPKCS11</code> para
 * acceder al API de PKCS#11 provisto en Java, por tanto funciona solo con el
 * JVM de Sun Microsystems.
 *
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 */
public abstract class PKCS11KeyStoreProvider implements KeyStoreProvider {

    private static final Logger log = Logger.getLogger(PKCS11KeyStoreProvider.class.getName());

    /**
     * Obtiene la configuracion para el Provider, segun el sistema operativo que
     * se utilice.
     *
     * @return
     */
    public abstract String getConfig();
    public abstract boolean existeDriver();
    public abstract String getCfg();

    @Override
    public KeyStore getKeystore() throws KeyStoreException {
        return getKeystore(null);
    }

    @Override
    public KeyStore getKeystore(char[] password) throws KeyStoreException {
        InputStream configStream = null;

        try {
            // Crear una instancia de sun.security.pkcs11.SunPKCS11
            configStream = new ByteArrayInputStream(getConfig().getBytes());
            Provider sunPKCS11Provider = this.createSunPKCS11Provider();
            Security.addProvider(sunPKCS11Provider);

            KeyStore keyStore = KeyStore.getInstance("PKCS11");
            keyStore.load(null, password);
            return keyStore;
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new KeyStoreException(e);
        } finally {
            if (configStream != null) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    log.warning(e.getMessage());
                }
            }
        }
    }

    /**
     * Instancia la clase <code>sun.security.pkcs11.SunPKCS11</code>
     * dinamicamente, usando Java Reflection.
     *
     * @return una instancia de <code>sun.security.pkcs11.SunPKCS11</code>
     */
    private static final String SUN_PKCS11_CLASSNAME = "sun.security.pkcs11.SunPKCS11";
    private static final String SUN_PKCS11_PROVIDER_NAME = "SunPKCS11";

    @SuppressWarnings("unchecked")
    private Provider createSunPKCS11Provider() throws KeyStoreException {
        try {
            Provider prototype = Security.getProvider(SUN_PKCS11_PROVIDER_NAME);
            Class<?> sunPkcs11ProviderClass = Class.forName(SUN_PKCS11_CLASSNAME);
            Method configureMethod = sunPkcs11ProviderClass.getMethod("configure", String.class);
            return (Provider) configureMethod.invoke(prototype, getCfg());
        } catch (ClassNotFoundException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new KeyStoreException(e);
        }
    }

    public void logout() throws KeyStoreException {
        InputStream configStream = null;

        try {
            // Crear una instancia de sun.security.pkcs11.SunPKCS11
            configStream = new ByteArrayInputStream(getConfig().getBytes());
            Provider sunPKCS11Provider = this.createSunPKCS11Provider();
            AuthProvider auth = (AuthProvider) sunPKCS11Provider;

            try {
                auth.logout();
            } catch (LoginException e) {
                throw new KeyStoreException(e);
            }
        } finally {
            if (configStream != null) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    log.warning(e.getMessage());
                }
            }
        }
    }

    protected static boolean is64bit() {
        return System.getProperty("sun.arch.data.model").contains("64");
    }
}
