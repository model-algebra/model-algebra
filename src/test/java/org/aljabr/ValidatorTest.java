package org.aljabr;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.networknt.schema.Error;

class ValidatorTest
{

	@Test
	void testValidSchema() throws IOException, InvalidArgumentException
	{
		// Validate resource user_core_model.json
		var validator = new Validator();
		var model = new Model(ResourceLoader.loadResourceAsString("user_core_model.json"));
		List<Error> result = validator.validate(model.root);
		assertTrue(result.isEmpty(), "Expected no validation errors, but found: " + result);
	}
	
	@Test
	void testAllTypesModel() throws IOException, InvalidArgumentException
	{
		var validator = new Validator();
		var model = new Model(ResourceLoader.loadResourceAsString(ResourceUrl.all_types_model));
		List<Error> result = validator.validate(model.root);
		assertTrue(result.isEmpty(), "Expected no validation errors, but found: " + result);
	}

	@Test
	void testInvalidSchema() throws IOException, InvalidArgumentException
	{
		// Validate resource invalid_model.json
		var validator = new Validator();
		var model = new Model(ResourceLoader.loadResourceAsString("invalid_model.json"));
		List<Error> result = validator.validate(model.root);
		assertTrue(!result.isEmpty(), "Expected validation errors, but found none.");
	}
}
