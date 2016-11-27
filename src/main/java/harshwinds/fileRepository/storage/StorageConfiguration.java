package harshwinds.fileRepository.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageConfiguration {

	/**
	 * Directory used to store uploaded files
	 */
	private String location = "repository";

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
