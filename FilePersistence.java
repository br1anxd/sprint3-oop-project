package com.umt.sprint2.data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilePersistence {
    private final Path filePath;

    public FilePersistence(Path filePath) {
        this.filePath = filePath;
    }

    public DataStore loadOrCreate() {
        try {
            if (!Files.exists(filePath)) {
                return new DataStore();
            }
            try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(filePath)))) {
                Object obj = ois.readObject();
                return (DataStore) obj;
            }
        } catch (Exception e) {
            // if file is corrupted, start fresh rather than crashing the app
            return new DataStore();
        }
    }

    public void save(DataStore store) throws IOException {
        Files.createDirectories(filePath.getParent());
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(filePath)))) {
            oos.writeObject(store);
        }
    }
}
