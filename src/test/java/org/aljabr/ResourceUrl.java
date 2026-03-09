package org.aljabr;

public abstract class ResourceUrl
{
	public static final String user_core_model = "user_core_model.json";
	public static final String user_extended_model = "user_extended_model.json";
	public static final String user_db_model = "user_db_model.json";
	public static final String json_processor_source1 = "processor/source1.json";
	public static final String json_processor_source2 = "processor/source2.json";
	public static final String json_processor_result_union = "processor/result_union.json";
	public static final String json_processor_result_subtraction = "processor/result_subtraction.json";
	public static final String json_processor_result_intersection = "processor/result_intersection.json";
	public static final String json_processor_result_xor = "processor/result_xor.json";
	public static final String model_schema = "schemas/model-schema.json";
	
	private ResourceUrl()
	{
		// prevent instantiation
	}
}

