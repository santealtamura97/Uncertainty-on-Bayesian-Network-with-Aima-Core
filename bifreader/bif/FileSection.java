package bifreader.bif;


/**
 * The section of the BIF file that we are currently in.
 */
public enum FileSection {
    /**
     * The BIF header.
     */
    BIF,
    /**
     * The network specs.
     */
    NETWORK,
    /**
     * A variable.
     */
    VARIABLE,
    /**
     * A definition.
     */
    DEFINITION
}
