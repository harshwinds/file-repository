package harshwinds.fileRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import harshwinds.fileRepository.repository.RepositoryService;
import harshwinds.fileRepository.storage.StorageConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(StorageConfiguration.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	CommandLineRunner init(RepositoryService repositoryManager) {
		return (args) -> {
			repositoryManager.deleteAll();
			repositoryManager.initialize();
		};
	}
}
