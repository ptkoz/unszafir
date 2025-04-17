package Unszafir.Signatures;

import javax.inject.Inject;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.impl.KeyStoreKeyingDataProvider;
import xades4j.providers.impl.PKCS11KeyStoreKeyingDataProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProviderException;
import java.security.cert.X509Certificate;

public class CertificateProviderFactory {
    private final PinProvider pinProvider;

    @Inject
    public CertificateProviderFactory(PinProvider pinProvider) {
        this.pinProvider = pinProvider;
    }

    public KeyingDataProvider createKeyingDataProvider(String pkcs11Module, int slotIndex) throws InvalidProviderException {
        Path pkcs11ModulePath = Paths.get(pkcs11Module).toAbsolutePath().normalize();;
        try {
            pkcs11ModulePath = pkcs11ModulePath.toRealPath();
        } catch (IOException e) {
            throw new InvalidProviderException("PKCS#11 module not found: " + pkcs11ModulePath);
        }

        return PKCS11KeyStoreKeyingDataProvider
            .builder(pkcs11ModulePath.toString(), KeyStoreKeyingDataProvider.SigningCertificateSelector.single())
            .storePassword(pinProvider)
            .entryPassword(pinProvider)
            .slot(slotIndex)
            .fullChain(true)
            .build();
    }

    public X509Certificate extractCertificate(KeyingDataProvider keyingDataProvider) throws InvalidProviderException {
        try {
            return keyingDataProvider.getSigningCertificateChain().getFirst();
        } catch (ProviderException e) {
            if (e.getCause() != null) {
                if (e.getCause().getMessage().equals("CKR_SLOT_ID_INVALID")) {
                    throw new InvalidProviderException("No certificate found for this PKCS#11 slot");
                }

                if (e.getCause() instanceof java.io.IOException) {
                    throw new InvalidProviderException("No certificate found - unable to use this PKCS#11 library");
                }
            }

            throw new InvalidProviderException("Unexpected error when reading certificate chain: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidProviderException("Unexpected error when reading certificate chain: " + e.getMessage());
        }
    }
}
