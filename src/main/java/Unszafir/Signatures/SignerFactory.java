package Unszafir.Signatures;

import xades4j.production.XadesBesSigningProfile;
import xades4j.production.XadesSigner;
import xades4j.production.XadesSigningProfile;
import xades4j.providers.KeyingDataProvider;
import xades4j.utils.XadesProfileResolutionException;

public class SignerFactory {
    public XadesSigner createXadesSigner(KeyingDataProvider keyingDataProvider) {
        XadesSigningProfile profile = new XadesBesSigningProfile(keyingDataProvider);

        try {
            return profile.newSigner();
        } catch (XadesProfileResolutionException e) {
            throw new RuntimeException(e);
        }
    }
}
