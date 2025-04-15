package Unszafir.Helpers;

import javax.annotation.Nullable;
import java.security.ProviderException;

public class CertificateExceptionHandler {
    static public @Nullable String getMessageForProviderException(ProviderException e, int pkcs11SlotIndex) {
        if (e.getCause() != null) {
            if (e.getCause().getMessage().equals("CKR_SLOT_ID_INVALID")) {
                return "No certificate found for PKCS#11 slot " + pkcs11SlotIndex;
            }

            if (e.getCause() instanceof java.io.IOException) {
                return "No certificate found - unable to use this PKCS#11 library";
            }
        }

        return null;
    }
}
