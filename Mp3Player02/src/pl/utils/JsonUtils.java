package pl.utils;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.LinkedList;

import pl.model.UserInfo;
import android.util.JsonReader;

public class JsonUtils {
	private String jsonData;

	public JsonUtils(String jsonData) {
		super();
		this.jsonData = jsonData;
	}
	public void parseJson(String jsonData){
		JsonReader reader = new JsonReader(new StringReader(jsonData));
	}
}
