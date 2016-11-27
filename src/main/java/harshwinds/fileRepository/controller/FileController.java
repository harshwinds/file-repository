package harshwinds.fileRepository.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import harshwinds.fileRepository.error.RepositoryFileNotFoundException;
import harshwinds.fileRepository.model.FileDescriptor;
import harshwinds.fileRepository.repository.RepositoryService;

@RestController
@RequestMapping("/files")
public class FileController {

    private final RepositoryService repositoryManager;

    @Autowired
    public FileController(RepositoryService repositoryManager) {
        this.repositoryManager = repositoryManager;
    }
    
    @GetMapping("/")
    public List<FileDescriptor> getFileDescriptors(Model model) throws IOException {
        List<FileDescriptor> fileDescriptors = repositoryManager
                .loadAll()
                .collect(Collectors.toList());

        return fileDescriptors;
    }
    
    @GetMapping("/{id:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable() String id) {
    	FileDescriptor fileDescriptor = repositoryManager.load(id);
    	Resource file = repositoryManager.loadAsResource(id);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDescriptor.getFilename() + "\"")
                .body(file);
    }
    
    @GetMapping("/{id:.+}/descriptor")
    public FileDescriptor getFileDescriptor(@PathVariable() String id) throws IOException {
    	FileDescriptor fileDescriptor = repositoryManager.load(id);
        return fileDescriptor;
    }
    
    @PostMapping("/")
    public ResponseEntity<FileDescriptor> uploadFile(@RequestPart("file") MultipartFile file,
    									@RequestPart("fileDescriptor") FileDescriptor fileDescriptor) {
    	
    	if (fileDescriptor.getId() != null) {
    		return ResponseEntity
    				.badRequest()
    				.body(null);
    	}
    	
    	FileDescriptor uploadedFileDescriptor = repositoryManager.store(file, fileDescriptor);
    	return ResponseEntity
    			.ok()
    			.body(uploadedFileDescriptor);
    }
    
    @PutMapping("/")
    public ResponseEntity<FileDescriptor> reuploadFile(@RequestParam("file") MultipartFile file,
    							@RequestPart("fileDescriptor") FileDescriptor fileDescriptor) {
    	
    	if (fileDescriptor.getId() == null) {
    		return ResponseEntity
    				.badRequest()
    				.body(null);
    	}
    	
    	FileDescriptor uploadedFileDescriptor = repositoryManager.store(file, fileDescriptor);
    	return ResponseEntity
    			.ok()
    			.body(uploadedFileDescriptor);
    }
    
    @ExceptionHandler(RepositoryFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(RepositoryFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
