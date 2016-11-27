package harshwinds.fileRepository.error;

public class RepositoryException extends RuntimeException {

	private static final long serialVersionUID = 7883778394801131997L;

	public RepositoryException(String message) {
		super(message);
	}
	
	public RepositoryException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
