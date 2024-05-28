package java_crud.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data

public class Response<T> {
	@JsonProperty("Status")
	 private Boolean status;
	@JsonProperty("Message")
     private String message;
	@JsonProperty("Data")
    private T data;
    public Response() {
    }


	public Boolean getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		
	}

	public void setStatus(boolean status) {
		this.status = status;
		
	}


	public void setData(T data) {
	this.data = data;
		
	}



}
