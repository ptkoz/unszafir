package Unszafir.Cli;

import Unszafir.Signatures.UnableToSaveException;
import com.google.inject.Inject;

import javax.annotation.Nullable;
import java.io.Console;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Scanner;

public class Ui {
    private final Console console;

    @Inject
    Ui(@Nullable Console console) {
        this.console = console;
    }

    public void confirmCancellation() {
        System.out.println("OK, exiting...");
    }

    public void confirmSigning() {
        System.out.println("Signing document...");
    }

    public void confirmSignatureCreation(String outputFilePath) {
        System.out.println("Signature created: " + outputFilePath);
    }

    public void displayCertificate(X509Certificate certificate) {
        System.out.println("  Subject:    " + extractCertificateCommonName(certificate.getSubjectX500Principal().getName()));
        System.out.println("  Type:       " + extractCertificateCommonName(certificate.getIssuerX500Principal().getName()));
        System.out.println("  Issuer:     " + extractCertificateOrganization(certificate.getIssuerX500Principal().getName()));
        System.out.println("  Valid from: " + certificate.getNotBefore());
        System.out.println("  Valid to:   " + certificate.getNotAfter());
    }

    private static String extractCertificateCommonName(String certValue) {
        for (String part : certValue.split(",")) {
            if (part.trim().startsWith("CN=")) {
                return part.trim().substring(3);
            }
        }
        return "(none)";
    }

    private static String extractCertificateOrganization(String certValue) {
        for (String part : certValue.split(",")) {
            if (part.trim().startsWith("O=")) {
                return part.trim().substring(2);
            }
        }
        return "(none)";
    }

    public void displayError(String error) {
        System.err.println(error);
    }

    public String requestPIN() {
        if (console != null) {
            return new String(console.readPassword("PIN: "));
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("PIN: ");
            return scanner.nextLine();
        }
    }

    public boolean promptSignatureCreation(X509Certificate certificate, String inputFile) {
        System.out.println("Signing file '" + inputFile + "' with: ");
        displayCertificate(certificate);
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        Map <String, Boolean> answerMap = Map.of(
            "y", true,
            "yes", true,
            "n", false,
            "no", false
        );

        String answer;
        do {
            System.out.print("Confirm signature creation? (y/n): ");
            answer = scanner.nextLine().trim().toLowerCase();
        } while(!answerMap.containsKey(answer));

        return answerMap.get(answer);
    }

    public String requestOutputFilePath(String suggestion) {
        System.out.print("Signature file (" + suggestion + "): ");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().trim();

        return answer.isEmpty() ? suggestion : answer;
    }
}
