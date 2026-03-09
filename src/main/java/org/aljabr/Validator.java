package org.aljabr;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import com.networknt.schema.Error;

public class Validator
{
	final Schema schema;
	
	public Validator() throws IOException
	{
		schema = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12).getSchema(ResourceLoader.loadResourceAsString("schemas/model-schema.json"));;
	}
	
	public List<Error> validate(JsonNode root)
	{
		return schema.validate(root);
	}
	
	public List<Error> validate(String json) throws InvalidArgumentException
	{
		return validate(JsonUtils.string2node(json));
	}
		
}
