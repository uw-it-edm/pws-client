package edu.uw.edm.pws.autoconfigure.security;

import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class KeyManagerCabinet {

    private final KeyStore keystore;
    private final KeyManager[] keyManagers;
    private final TrustManager[] trustManagers;

    /**
     * Initializes instance variables for KeyStore, KeyManager array, and TrustManager array and
     * constructs KeyManagerCabinet object.
     */
    private KeyManagerCabinet(KeyStore keystore, KeyManager[] keyManagers, TrustManager[] trustManagers) {
        this.keystore = keystore;
        this.keyManagers = keyManagers;
        this.trustManagers = trustManagers;
    }

    /**
     * Returns KeyStore that was provided to the Builder constructor
     *
     * @return KeyStore
     */
    public KeyStore getKeystore() {
        return keystore;
    }

    /**
     * Returns KeyManager array that was created by the KeyManagerFactory in the Builder.build()
     * method.
     *
     * @return KeyManager array
     */
    public KeyManager[] getKeyManagers() {
        return keyManagers;
    }

    /**
     * Returns TrustManager array that was created by the TrustManagerFactory in the Builder.build()
     * method.
     */
    public TrustManager[] getTrustManagers() {
        return trustManagers;
    }

    /**
     * Fluent builder class, as per Joshua Bloch's Effective Java
     */
    public final static class Builder {

        private final String keystoreLocation;
        private final String keystorePassword;
        private String keystoreType;


        public Builder(String keystoreLocation, String keystorePassword) {
            this.keystoreLocation = keystoreLocation;
            this.keystorePassword = keystorePassword;
            this.keystoreType = "JKS";
        }

        /**
         * Sets KeyStore type and returns this Builder object.
         *
         * @return This Builder object
         */
        public Builder keystoreType(String keystoreType) {
            this.keystoreType = keystoreType;
            return this;
        }

        /**
         * If KeyStore file provided by {@link this.keystoreLocation} is empty, returns new
         * KeyManagerCabinet which has no KeyStore, KeyManagers, or TrustManagers.  If not, opens
         * KeyStore (default KeyStore type is "JKS"), creates and initializes "SunX509"
         * KeyManagerFactory, creates and initializes "SunX509" TrustManagerFactory, and uses them
         * to create a KeyManagerCabinet.  Returns this KeyManager cabinet.
         *
         * @return KeyManagerCabinet
         */
        public KeyManagerCabinet build() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
            if (keystoreType == null)
                keystoreType = "JKS";

            if (StringUtils.isEmpty(this.keystoreLocation))
                return new KeyManagerCabinet(null, null, null);

            KeyStore ks = KeyStore.getInstance(keystoreType);

            FileInputStream fis = new FileInputStream(this.keystoreLocation);
            char[] password = this.keystorePassword != null ? this.keystorePassword.toCharArray() : "changeit".toCharArray();

            try {
                ks.load(fis, password);
            } finally {
                if (fis != null)
                    fis.close();
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);


            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init((KeyStore) null);

            return new KeyManagerCabinet(ks, kmf.getKeyManagers(), tmf.getTrustManagers());
        }
    }

}
