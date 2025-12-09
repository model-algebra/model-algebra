package org.aljabr;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectContainer implements ContainerNode
{
	protected final ObjectNode node;
	
	public ObjectContainer(ObjectNode node)
	{
		this.node = node;
	}

	@Override
	public Set<String> getFieldNames()
	{
		Set<String> names = new LinkedHashSet<>();
		node.fieldNames().forEachRemaining(names::add);
		return names;
	}

	@Override
	public Optional<JsonNode> getField(String fieldName)
	{
		return Optional.ofNullable(node.get(fieldName));
	}

	@Override
	public ContainerNode createNew()
	{
		return new ObjectContainer(new ObjectNode(JsonNodeFactory.instance));
	}

	@Override
	public void set(String fieldName, JsonNode value)
	{
		node.set(fieldName,  value);
	}

	@Override
	public JsonNode getNode()
	{
		return node;
	}

}
