package assets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Tool {
	public static String prettyPrint(JsonNode json){
		ObjectMapper om = new ObjectMapper();
		String pretty = "";
		try {
			pretty = om.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return pretty;
	}
}
