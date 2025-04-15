package Unszafir.Cli;

import Unszafir.Helpers.CertificateExceptionHandler;
import Unszafir.Signatures.CertificateProviderFactory;
import com.google.inject.Inject;
import picocli.CommandLine;

import java.security.ProviderException;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "list",
    description = "List available certificates"
)
public class ListCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-m", "--module"}, description = "Path to PKCS#11 library.", required = true)
    String pkcs11Module;

    @CommandLine.Option(names = {"-s", "--slot"}, description = "The index of the PKCS#11 slot to use, default is 0.", defaultValue = "0")
    int pkcs11SlotIndex;

    private final CertificateProviderFactory certificateProviderFactory;
    private final Ui ui;

    @Inject
    public ListCommand(CertificateProviderFactory certificateProviderFactory, Ui ui) {
        this.certificateProviderFactory = certificateProviderFactory;
        this.ui = ui;
    }

    @Override
    public Integer call() throws Exception {
        try {
            ui.displayCertificate(
                certificateProviderFactory
                    .createKeyingDataProvider(pkcs11Module, pkcs11SlotIndex)
                    .getSigningCertificateChain()
                    .getFirst()
            );
        } catch (ProviderException e) {
            String message = CertificateExceptionHandler.getMessageForProviderException(e, pkcs11SlotIndex);
            if (message != null) {
                ui.displayError(message);
                return 1;
            }

            throw e;
        }


        return 0;
    }
}
