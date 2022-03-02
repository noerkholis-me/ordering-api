package assets;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

//import models.Role;

public abstract class JsonMask {
//	  @JsonIgnore
//	  public Long id;
	  @JsonIgnore
	  public Date createdAt;
	  @JsonIgnore
	  public Date updatedAt;
	  @JsonIgnore
	  public boolean isDeleted;
//	  @JsonIgnore
//	  public Role role;
}
