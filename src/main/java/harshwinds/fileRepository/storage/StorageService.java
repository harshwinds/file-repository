package harshwinds.fileRepository.storage;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	void initialize();
	
	void store(MultipartFile file, String id);
	
	Path load(String id);
	
	Resource loadAsResource(String id);
	
	void deleteAll();
}
