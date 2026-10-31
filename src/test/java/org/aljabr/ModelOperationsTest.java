package org.aljabr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Model operations on the models of model-examples: user_core (id, userName, firstName, secondName), user_extended
 * (fields a user_core doesn't have), user_db (database keys of id and userName) and person_search (with named
 * sortings and filters).
 */
public class ModelOperationsTest
{
	private final IModel core;
	private final IModel extended;
	private final IModel db;
	private final IModel person;

	public ModelOperationsTest() throws IOException, InvalidArgumentException
	{
		core = Model.from(ResourceLoader.loadResourceAsString(ResourceUrl.user_core_model));
		extended = Model.from(ResourceLoader.loadResourceAsString(ResourceUrl.user_extended_model));
		db = Model.from(ResourceLoader.loadResourceAsString(ResourceUrl.user_db_model));
		person = Model.from(ResourceLoader.loadResourceAsString(ResourceUrl.person_search_model));
	}

	private static JsonNode json(IModel model) throws InvalidArgumentException
	{
		return JsonUtils.string2node(model.toJson());
	}

	private static List<String> fieldNames(IModel model)
	{
		return model.fieldsAsStream().map(Field::getName).toList();
	}

	private static JsonNode field(IModel model, String name) throws InvalidArgumentException
	{
		for (JsonNode field : json(model).get("fields"))
			if (field.get("name").asText().equals(name)) return field;
		throw new AssertionError("No field " + name);
	}

	private static final List<String> CORE_FIELDS = List.of("id", "userName", "firstName", "secondName");

	// Union

	@Test
	void unionAddsTheFieldsOfTheOtherModel() throws InvalidArgumentException, IOException
	{
		assertEquals(
			JsonUtils.string2node(ResourceLoader.loadResourceAsString(ResourceUrl.user_core_add_extended)),
			json(core.add(extended)));
	}

	@Test
	void unionCombinesFieldsBothModelsHaveKeyByKey() throws InvalidArgumentException
	{
		IModel user = core.add(db);
		assertEquals(CORE_FIELDS, fieldNames(user));
		assertEquals(JsonUtils.string2node(
			"{\"name\":\"id\",\"type\":\"integer\",\"description\":\"Main index\","
				+ "\"constraints\":{\"primaryKey\":true,\"nullable\":false},\"primaryKey\":true}"),
			field(user, "id"));
		assertEquals(JsonUtils.string2node("{\"nullable\":false,\"unique\":true}"), field(user, "userName").get("constraints"));
		assertEquals("varchar(25)", field(user, "userName").get("dbType").asText());
	}

	@Test
	void unionKeepsTheMetadataOfTheFirstModel() throws InvalidArgumentException
	{
		assertEquals(json(core).get("metadata"), json(core.add(db)).get("metadata"));
		assertEquals(json(db).get("metadata"), json(db.add(core)).get("metadata"));
	}

	@Test
	void unionKeepsKeysOnlyOneModelHas() throws InvalidArgumentException
	{
		JsonNode result = json(person.add(core));
		assertEquals(json(person).get("sortings"), result.get("sortings"));
		assertEquals(json(person).get("filters"), result.get("filters"));
		assertTrue(fieldNames(person.add(core)).containsAll(CORE_FIELDS));
	}

	// Subtraction

	@Test
	void subtractionTakesAwayTheFieldsOfTheOtherModel() throws InvalidArgumentException
	{
		assertEquals(json(core), json(core.add(extended).sub(extended)));
		assertEquals(List.of("firstName", "secondName"), fieldNames(core.sub(db)));
	}

	@Test
	void subtractionKeepsTheMetadata() throws InvalidArgumentException
	{
		JsonNode result = json(core.sub(core));
		assertEquals(json(core).get("metadata"), result.get("metadata"));
		assertTrue(result.get("fields").isEmpty());
	}

	@Test
	void subtractionTakesAwayNamedSortingsAndFilters() throws InvalidArgumentException
	{
		IModel withoutOwn = person.sub(person);
		assertTrue(json(withoutOwn).get("sortings").isEmpty());
		assertTrue(json(withoutOwn).get("filters").isEmpty());
		assertEquals(json(person).get("sortings"), json(person.sub(core)).get("sortings"));
	}

	// Intersection

	@Test
	void intersectionKeepsTheFieldsBothModelsHave() throws InvalidArgumentException
	{
		assertEquals(json(core), json(core.add(extended).intersect(core)));
		assertEquals(List.of("id", "userName"), fieldNames(core.intersect(db)));
	}

	@Test
	void fieldsOfModelsThatOnlyAddKeysHaveNoType()
	{
		assertEquals(List.of("id", "userName"), fieldNames(db));
		assertEquals(null, db.fieldsAsMap().get("id").getType());
	}

	@Test
	void intersectionKeepsTheKeysBothFieldsHave() throws InvalidArgumentException
	{
		IModel common = core.intersect(db);
		assertEquals(JsonUtils.string2node("{\"name\":\"id\"}"), field(common, "id"));
		assertEquals(JsonUtils.string2node("{\"name\":\"userName\",\"constraints\":{}}"), field(common, "userName"));
	}

	@Test
	void intersectionDropsKeysOnlyOneModelHas() throws InvalidArgumentException
	{
		JsonNode result = json(person.intersect(core));
		assertFalse(result.has("sortings"));
		assertFalse(result.has("filters"));
		assertEquals(List.of("id", "firstName"), fieldNames(person.intersect(core)));
	}

	// Exclusive or

	@Test
	void xorKeepsTheFieldsOnlyOneModelHas() throws InvalidArgumentException
	{
		assertEquals(fieldNames(extended), fieldNames(core.xor(core.add(extended))));
		assertEquals(List.of("firstName", "secondName"), fieldNames(core.xor(db)));
		assertEquals(json(core).get("metadata"), json(core.xor(db)).get("metadata"));
	}

	@Test
	void xorOfAModelWithItselfHasNoFields() throws InvalidArgumentException
	{
		assertTrue(json(core.xor(core)).get("fields").isEmpty());
	}

	// Relations between the operations

	@Test
	void unionIsSubtractionAndIntersectionTogether() throws InvalidArgumentException
	{
		IModel all = core.add(extended);
		List<String> parts = new java.util.ArrayList<>(fieldNames(all.sub(core)));
		parts.addAll(fieldNames(all.intersect(core)));
		assertEquals(fieldNames(all).stream().sorted().toList(), parts.stream().sorted().toList());
	}

	@Test
	void xorIsUnionWithoutIntersection() throws InvalidArgumentException
	{
		assertEquals(fieldNames(core.add(db).sub(core.intersect(db))), fieldNames(core.xor(db)));
	}
}
