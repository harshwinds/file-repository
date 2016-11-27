package harshwinds.fileRepository.repository;

import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import harshwinds.fileRepository.model.FileDescriptor;

public interface RepositoryService {

	void initialize();
	
	FileDescriptor store(MultipartFile file, FileDescriptor fileDescriptor);
	
	Stream<FileDescriptor> loadAll();
	
	FileDescriptor load(String id);
	
	Resource loadAsResource(String id);
	
	void deleteAll();
}
