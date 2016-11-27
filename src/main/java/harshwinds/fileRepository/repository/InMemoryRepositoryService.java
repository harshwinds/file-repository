package harshwinds.fileRepository.repository;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import harshwinds.fileRepository.error.RepositoryFileNotFoundException;
import harshwinds.fileRepository.model.FileDescriptor;
import harshwinds.fileRepository.storage.StorageService;

@Service
public class InMemoryRepositoryService implements RepositoryService {
    private final StorageService storageService;
    private ConcurrentMap<String, FileDescriptor> repository;

    @Autowired
    public InMemoryRepositoryService(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public FileDescriptor store(MultipartFile file, FileDescriptor fileDescriptor) {
    	if (fileDescriptor.getId() == null) {
    		UUID id = UUID.randomUUID();
    		fileDescriptor.setId(id.toString());
    	}
    	
        storageService.store(file, fileDescriptor.getId());
        fileDescriptor.setFilename(file.getOriginalFilename());
        repository.put(fileDescriptor.getId(), fileDescriptor);
        
        return fileDescriptor;
    }

    @Override
    public Stream<FileDescriptor> loadAll() {
        return repository.values().stream();
    }

    @Override
    public FileDescriptor load(String id) {
    	FileDescriptor fileDescriptor = repository.get(id);
    	
    	if (fileDescriptor == null) {
    		throw new RepositoryFileNotFoundException("Cound not file file descriptor for " + id);
    	}
    	
    	return fileDescriptor;
    }

    @Override
    public Resource loadAsResource(String id) {
        return storageService.loadAsResource(id);
    }

    @Override
    public void deleteAll() {
    	storageService.deleteAll();
    	repository = null;
    }

    @Override
    public void initialize() {
    	repository = new ConcurrentHashMap<String, FileDescriptor>();
        storageService.initialize();
    }
}
