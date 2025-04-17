package Unszafir.Cli;

import Unszafir.Signatures.CertificateProviderFactory;
import Unszafir.Signatures.InvalidProviderException;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "list",
    description = "List available certificates"
)
public class ListCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    private MainCommand mainCommand;

    private final CertificateProviderFactory certificateProviderFactory;
    private final Ui ui;

    @Inject
    public ListCommand(CertificateProviderFactory certificateProviderFactory, Ui ui) {
        this.certificateProviderFactory = certificateProviderFactory;
        this.ui = ui;
    }

    @Override
    public Integer call() {
        try {
            ui.displayCertificate(
                certificateProviderFactory.extractCertificate(
                    certificateProviderFactory
                        .createKeyingDataProvider(mainCommand.pkcs11Module, mainCommand.pkcs11SlotIndex)
                )
            );
        } catch (InvalidProviderException e) {
            ui.displayError(e.getMessage());
            return 1;
        }


        return 0;
    }
}
