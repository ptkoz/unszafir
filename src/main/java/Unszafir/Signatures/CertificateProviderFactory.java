package Unszafir.Signatures;

import com.google.inject.Inject;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.impl.KeyStoreKeyingDataProvider;
import xades4j.providers.impl.PKCS11KeyStoreKeyingDataProvider;

public class CertificateProviderFactory {
    private final PinProvider pinProvider;

    @Inject
    public CertificateProviderFactory(PinProvider pinProvider) {
        this.pinProvider = pinProvider;
    }

    public KeyingDataProvider createKeyingDataProvider(String pkcs11Module, int slotIndex) {
        return PKCS11KeyStoreKeyingDataProvider
            .builder(pkcs11Module, KeyStoreKeyingDataProvider.SigningCertificateSelector.single())
            .storePassword(pinProvider)
            .entryPassword(pinProvider)
            .slot(slotIndex)
            .fullChain(true)
            .build();
    }
}
