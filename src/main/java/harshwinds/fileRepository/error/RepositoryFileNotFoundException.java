package harshwinds.fileRepository.error;

public class RepositoryFileNotFoundException extends RepositoryException {

	private static final long serialVersionUID = 7938528555247838262L;

	public RepositoryFileNotFoundException(String message) {
		super(message);
	}
	
	public RepositoryFileNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
