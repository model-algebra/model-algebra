package org.aljabr;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kulentsov.Boxed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public abstract class JsonUtils
{
	protected static ObjectMapper mapperTo = new ObjectMapper();
	protected static ObjectMapper mapperFrom = JsonMapper.builder().build();
	
	private JsonUtils()
	{
		// prevent instantiation
	}

	public static JsonNode string2node(String json) throws InvalidArgumentException
	{
			try
			{
				return mapperFrom.readTree(json);
			} catch (JsonMappingException e)
			{
				throw new InvalidArgumentException("Invalid JSON mapping", e);
			} catch (JsonProcessingException e)
			{
				throw new InvalidArgumentException("Invalid JSON processing", e);
			}
	}

	public static String node2string(JsonNode node) throws InvalidArgumentException
	{
		try
		{
			return mapperTo.writeValueAsString(node);
		} catch (JsonProcessingException e)
		{
			throw new InvalidArgumentException("Invalid JSON node", e);
		}
	}
	
	public static Map<String, JsonNode> node2map(JsonNode node) throws InvalidArgumentException
	{
		try 
		{
			return mapperFrom.convertValue(node, new TypeReference<Map<String, JsonNode>>() {});
		} catch (IllegalArgumentException e)
		{
			throw new InvalidArgumentException("Invalid JSON processing", e);
		}
	}

	public static List<JsonNode> node2list(JsonNode jsonNode) throws InvalidArgumentException
	{
		try {
			return mapperFrom.convertValue(jsonNode, new TypeReference<List<JsonNode>>()
			{
			});
		} catch (IllegalArgumentException e)
		{
			throw new InvalidArgumentException("Invalid JSON processing", e);
		}
	}

	public static Map<String, Integer> getIndexMap(JsonNode target)
	{
		Map<String, Integer> indexMap = new LinkedHashMap<>();
		Boxed<Integer> index = Boxed.of(0);
		target.elements().forEachRemaining(node -> indexMap.put(node.get("name").asText(), index.value++));
		return indexMap;
	}
}
