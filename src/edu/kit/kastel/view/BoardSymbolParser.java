package edu.kit.kastel.view;
import edu.kit.kastel.model.InvalidSymbolSetFormatException;
import edu.kit.kastel.model.SymbolSet;
import java.util.List;

/**
 * Parses a symbol set from a list of strings, validating the format and content.
 * @author uqibg
 */
public class BoardSymbolParser {
    /**
     * Parses the symbol set from the provided lines, ensuring it meets the required format and length.
     * @param lines the list of strings representing the symbol set, where the first line should contain exactly 29 characters
     * @return a SymbolSet object created from the first line of the input
     * @throws InvalidSymbolSetException if the input is null, empty, does not contain exactly 29 characters
     */
    public SymbolSet parse(List<String> lines) throws InvalidSymbolSetException {
        if (lines == null || lines.isEmpty()) {
            throw new InvalidSymbolSetException("ERROR: Symbol set file is empty");
        }

        String firstLine = lines.get(0);

        if (firstLine.length() != 29) {
            throw new InvalidSymbolSetException("ERROR: Symbol set must be exactly 29 characters.");
        }

        try {
            return new SymbolSet(firstLine);
        } catch (InvalidSymbolSetFormatException e) {
            throw new InvalidSymbolSetException(e.getMessage(), e);
        }
    }
}
