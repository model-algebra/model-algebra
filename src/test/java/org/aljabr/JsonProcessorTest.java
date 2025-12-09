package org.aljabr;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonProcessorTest
{
	public static String source1_string;
	public static String source2_string;
	public static String result_union_string;
	public static String result_subtraction_string;
	public static String result_intersection_string;
	public static String result_xor_string;

	public static JsonNode source1;
	public static JsonNode source2;
	
	public JsonProcessorTest() throws IOException, InvalidArgumentException
	{
		source1_string = ResourceLoader.loadResourceAsString(ResourceUrl.json_processor_source1);
		source2_string = ResourceLoader.loadResourceAsString(ResourceUrl.json_processor_source2);
		source1 = JsonUtils.string2node(source1_string);
		source2 = JsonUtils.string2node(source2_string);
		result_union_string = ResourceLoader.loadResourceAsString(ResourceUrl.json_processor_result_union);
	    result_subtraction_string = ResourceLoader.loadResourceAsString(ResourceUrl.json_processor_result_subtraction);
	    result_intersection_string = ResourceLoader.loadResourceAsString(ResourceUrl.json_processor_result_intersection);
	    result_xor_string = ResourceLoader.loadResourceAsString(ResourceUrl.json_processor_result_xor);
	}
	
	@Test
    void testProcessLevel() throws InvalidArgumentException 
	{
		JsonProcessor processor = new JsonProcessor();

		JsonNode unionResult = processor.processLevel(source1, source2, JsonProcessor.Operation.UNION);
		assertEquals(JsonUtils.string2node(result_union_string), unionResult);
		
		JsonNode subtractionResult = processor.processLevel(source1, source2, JsonProcessor.Operation.SUBTRACTION);
		assertEquals(JsonUtils.string2node(result_subtraction_string), subtractionResult);
		
		JsonNode intersectionResult = processor.processLevel(source1, source2, JsonProcessor.Operation.INTERSECTION);
		assertEquals(JsonUtils.string2node(result_intersection_string), intersectionResult);
		
		JsonNode xorResult = processor.processLevel(source1, source2, JsonProcessor.Operation.XOR);
		assertEquals(JsonUtils.string2node(result_xor_string), xorResult);
		
	}
	
}
