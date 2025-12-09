package org.aljabr;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public class Field
{
	final String name;
	final String type;
	final Map<String, JsonNode> attributes;
	
	public Field(String name, String type, Map<String, JsonNode> attributes)
	{
		this.name = name;
		this.type = type;
		this.attributes = attributes;
	}
	
	public Field(String name, JsonNode value)
	{
		this.name = name;
		this.type = value.get("type").asText();
		this.attributes = convertAttributes(value);
		this.attributes.remove("type");
	}
	
	private Map<String, JsonNode> convertAttributes(JsonNode value)
	{
		try
		{
			return JsonUtils.node2map(value);
		} catch (InvalidArgumentException e)
		{
			e.printStackTrace();
			return new LinkedHashMap<>();
		}
	}

	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}

	public Map<String, JsonNode> getAttributes()
	{
		return attributes;
	}

}
