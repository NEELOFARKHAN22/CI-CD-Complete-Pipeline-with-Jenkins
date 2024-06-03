package java_crud.main.message;

public class BaseMessage {
	private String message;

	public BaseMessage(String message) {
		this.message = message;
	}

	BaseMessage() {

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
