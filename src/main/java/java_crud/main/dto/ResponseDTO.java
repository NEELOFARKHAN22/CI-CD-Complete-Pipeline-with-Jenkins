package java_crud.main.dto;

public class ResponseDTO<T> {

    boolean status = true;
    String message = null;
    T data = null;

    public void setSuccessResponse(T data, String msg) {
        this.status = true;
        this.message = msg != null ? msg : " ";
        this.data = data;
    }

    public void setFailureResponse(String msg, T data) {
        this.status = false;
        this.message = msg;
        this.data = data;
    }

    public void setFailureResponse(String msg) {
        this.status = false;
        this.message = msg;
        this.data = null;
    }

	public void setSuccessResponse(String string) {
		 this.status = true;
	        this.message = string;
	        this.data = null;
		
	}
    

}
