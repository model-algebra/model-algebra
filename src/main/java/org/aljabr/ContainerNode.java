package org.aljabr;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public interface ContainerNode
{
	Set<String> getFieldNames();
	Optional<JsonNode> getField(String fieldName);
	ContainerNode createNew();
	void set(String fieldName, JsonNode value);
	JsonNode getNode();
}
