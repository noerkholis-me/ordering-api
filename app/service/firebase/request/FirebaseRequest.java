package service.firebase.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FirebaseRequest {
	private String to;
	private FirebaseNotificationRequest notification; //for background handler
	private FirebaseDataRequest data; //for foreground handler
	
}
