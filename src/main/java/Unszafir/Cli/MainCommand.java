package Unszafir.Cli;
import picocli.CommandLine;
import javax.inject.Inject;


@CommandLine.Command(
    mixinStandardHelpOptions = true,
    description = "CLI tool to manage PKCS#11 signatures",
    subcommands = {CommandLine.HelpCommand.class}
)
public class MainCommand {
    @CommandLine.Option(names = {"-m", "--module"}, description = "Path to PKCS#11 library.", required = true)
    String pkcs11Module;

    @CommandLine.Option(names = {"-s", "--slot"}, description = "The index of the PKCS#11 slot to use, default is 0.", defaultValue = "0")
    int pkcs11SlotIndex;

    @Inject
    public MainCommand() {
    }
}