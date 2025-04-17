# Unszafir

```bash
Usage: unszafir [-hV] -m=<pkcs11Module> [-s=<pkcs11SlotIndex>] [COMMAND]
CLI tool to manage PKCS#11 signatures
  -h, --help      Show this help message and exit.
  -m, --module=<pkcs11Module>
                  Path to PKCS#11 library.
  -s, --slot=<pkcs11SlotIndex>
                  The index of the PKCS#11 slot to use, default is 0.
  -V, --version   Print version information and exit.
Commands:
  help  Display help information about the specified command.
  list  List available certificates
  sign  Create signature for given file.
```

## Listing available certificates

```bash
unszafir -m libpkcs11.so -s 0 list 
```

## Creating Xades signature

```bash
unszafir -m libpkcs11.so -s 0 sign inputfile.xml
```