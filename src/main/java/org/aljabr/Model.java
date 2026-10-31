package org.aljabr;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Model implements IModel
{
	protected final JsonNode root;
	
	private final Map<String, JsonNode> metadata;
	private final List<JsonNode> fields;

	public Model(JsonNode node) throws InvalidArgumentException
	{
		this.root = node;
		this.fields = JsonUtils.node2list(root.get("fields"));
		this.metadata = JsonUtils.node2map(root.get("metadata"));
	}

	public Model(String json) throws InvalidArgumentException
	{
		this(JsonUtils.string2node(json));
	}

	public static Model from(String json) throws InvalidArgumentException
	{
		return new Model(json);
	}

	public static Model from(JsonNode json) throws InvalidArgumentException
	{
		return new Model(json);
	}
	
	public static Model from(IModel source) throws InvalidArgumentException
	{
		return new Model(source.toJson());
	}

	@Override
	public String toJson() throws InvalidArgumentException
	{
		return JsonUtils.node2string(root);
	}

	@Override
	public Stream<Field> fieldsAsStream()
    {
		return this.fields.stream().map(entry -> new Field(entry.get("name").asText(), entry));
	}

	@Override
	public Map<String, Field> fieldsAsMap()
    {
		return fieldsAsStream().collect(java.util.stream.Collectors.toMap(Field::getName, field -> field));
    }

	@Override
	public Map<String, JsonNode> getMetadata()
    {
        return this.metadata;
    }
	
	/**
	 * Combines this model with another one. Below the top level as JsonProcessor.processLevel() does; at the top level,
	 * parts both models have (fields, constraints, keys of bridges...) are combined one level down whatever the
	 * operation, so that subtraction and xor work on the fields instead of taking away the "fields" key itself. The
	 * metadata is this model's.
	 */
	private IModel applyOperation(IModel m, JsonProcessor.Operation operation)
	{
		try
		{
			JsonNode otherRoot = JsonUtils.string2node(m.toJson());
			JsonProcessor processor = new JsonProcessor();
			ObjectNode result = JsonNodeFactory.instance.objectNode();

			Set<String> keys = new LinkedHashSet<>();
			root.fieldNames().forEachRemaining(keys::add);
			otherRoot.fieldNames().forEachRemaining(keys::add);

			for (String key : keys)
			{
				JsonNode left = root.get(key);
				JsonNode right = otherRoot.get(key);
				if (key.equals("metadata"))
				{
					if (left != null) result.set(key, left);
				} else if (left != null && right != null && isContainer(left) && left.getNodeType() == right.getNodeType())
				{
					result.set(key, processor.processLevel(left, right, operation));
				} else
				{
					Set<String> present = operation.combine(
						left != null ? Set.of(key) : Set.of(),
						right != null ? Set.of(key) : Set.of());
					if (!present.isEmpty()) result.set(key, left != null ? left : right);
				}
			}
			return new Model(result);
		} catch (InvalidArgumentException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static boolean isContainer(JsonNode node)
	{
		return node.isObject() || node.isArray();
	}

	@Override
	public IModel add(IModel m)
	{
		return applyOperation(m, JsonProcessor.Operation.UNION);
	}

	@Override
	public IModel sub(IModel m)
	{
		return applyOperation(m, JsonProcessor.Operation.SUBTRACTION);
	}

	@Override
	public IModel intersect(IModel m)
	{
		return applyOperation(m, JsonProcessor.Operation.INTERSECTION);
	}

	@Override
	public IModel xor(IModel m)
	{
		return applyOperation(m, JsonProcessor.Operation.XOR);
	}

}
