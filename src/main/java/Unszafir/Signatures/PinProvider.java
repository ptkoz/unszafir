package Unszafir.Signatures;

import Unszafir.Cli.Ui;
import xades4j.providers.impl.KeyStoreKeyingDataProvider;

import javax.inject.Inject;
import java.security.cert.X509Certificate;

public class PinProvider implements KeyStoreKeyingDataProvider.KeyStorePasswordProvider, KeyStoreKeyingDataProvider.KeyEntryPasswordProvider {
    private final Ui ui;
    private String pin = null;

    @Inject
    PinProvider(Ui ui) {
        this.ui = ui;
    }

    private char[] getPin() {
        if (pin == null) {
            pin = ui.requestPIN();
        }

        return pin.toCharArray();
    }

    @Override
    public char[] getPassword(String s, X509Certificate x509Certificate) {
        return getPin();
    }

    @Override
    public char[] getPassword() {
        return getPin();
    }
}
