package edu.kit.kastel.view;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility class for loading files and echoing their contents to the console.
 * @author uqibg
 */
public class FileLoader {
    /**
     * Loads the contents of a file and prints each line to the console.
     * @param filePath The path to the file to be loaded
     * @return A list of strings representing the lines of the file
     * @throws IOException If an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read
     */
    public List<String> loadAndEcho(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);

        for (String line : lines) {
            System.out.println(line);
        }

        return lines;
    }
}

