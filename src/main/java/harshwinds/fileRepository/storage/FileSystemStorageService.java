package harshwinds.fileRepository.storage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import harshwinds.fileRepository.error.RepositoryException;
import harshwinds.fileRepository.error.RepositoryFileNotFoundException;

@Service
public class FileSystemStorageService implements StorageService {
    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageConfiguration properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file, String id) {
        try {
            if (file.isEmpty()) {
                throw new RepositoryException("Failed to store empty file " + id);
            }
            Path fileLocation = this.rootLocation.resolve(id);
            Files.deleteIfExists(fileLocation);
            Files.copy(file.getInputStream(), fileLocation);
        } catch (IOException e) {
            throw new RepositoryException("Failed to store file " + id, e);
        }
    }

    @Override
    public Path load(String id) {
        return rootLocation.resolve(id);
    }

    @Override
    public Resource loadAsResource(String id) {
        try {
            Path file = load(id);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new RepositoryFileNotFoundException("Could not read file: " + id);

            }
        } catch (MalformedURLException e) {
            throw new RepositoryFileNotFoundException("Could not read file: " + id, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void initialize() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new RepositoryException("Could not initialize storage", e);
        }
    }
}
