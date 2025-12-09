package org.aljabr;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class ArrayContainer implements ContainerNode
{
	protected final ArrayNode node;
	protected final Map<String, Integer> fieldIndexMap;
	
	public ArrayContainer(ArrayNode node)
	{
		this.node = node;
		fieldIndexMap = JsonUtils.getIndexMap(node);
	}
	
	@Override
	public Set<String> getFieldNames()
	{
		return fieldIndexMap.keySet();
	}

	@Override
	public Optional<JsonNode> getField(String fieldName)
	{
		return Optional.ofNullable(fieldIndexMap.get(fieldName)).map(node::get);
	}

	@Override
	public ArrayContainer createNew()
	{
		return new ArrayContainer(new ArrayNode(JsonNodeFactory.instance));
	}

	@Override
	public void set(String fieldName, JsonNode value)
	{
		Integer index = fieldIndexMap.get(fieldName);
		if (index != null)
		{
			node.set(index, value);
		} else
		{
			node.add(value);
			fieldIndexMap.put(fieldName, node.size() - 1);
		}
	}

	@Override
	public JsonNode getNode()
	{
		return node;
	}

}
