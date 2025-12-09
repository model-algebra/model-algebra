package org.aljabr;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.kulentsov.Coalesce;
import org.kulentsov.TypedOptional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonProcessor
{
	public enum Operation 
	{
		UNION
		{
			@Override
	        public Set<String> combine(Set<String> target, Set<String> source) 
			{
	            Set<String> result = new LinkedHashSet<>(target);
	            result.addAll(source);
	            
	            return result;
	        }
	    },
		
	    INTERSECTION 
	    {
	        @Override
	        public Set<String> combine(Set<String> target, Set<String> source) 
	        {
	            Set<String> result = new LinkedHashSet<>(target);
	            result.retainAll(source);
	            return result;
	        }
	    },
	    
	    SUBTRACTION 
	    {
	        @Override
	        public Set<String> combine(Set<String> target, Set<String> source) {
	            Set<String> result = new LinkedHashSet<>(target);
	            result.removeAll(source);
	            return result;
	        }
	    },
		XOR 
		{
            @Override
            public Set<String> combine(Set<String> target, Set<String> source) {
                Set<String> result = new LinkedHashSet<>(target);
                for (String item : source) {
                    if (!result.add(item)) {
                        result.remove(item);
                    }
                }
                return result;
            }
        };
        
		public abstract Set<String> combine(Set<String> target, Set<String> source);
	}

	public Optional<ContainerNode> getContainerForNode(JsonNode node)
    {
		return Coalesce.of(
				() -> TypedOptional.of(ArrayNode.class, node).map(ArrayContainer::new),
				() -> TypedOptional.of(ObjectNode.class, node).map(ObjectContainer::new)
				);
    }
	
	public JsonNode processLevel(JsonNode targetNode, JsonNode sourceNode, Operation operation) throws InvalidArgumentException
	{
		ContainerNode target = getContainerForNode(targetNode).orElseThrow(
				() -> new InvalidArgumentException("Unsupported target node type: " + targetNode.getNodeType()+' '+targetNode, null));
		ContainerNode source = getContainerForNode(sourceNode).orElseThrow(
				() -> new InvalidArgumentException("Unsupported source node type: " + sourceNode.getNodeType(), null));
		// Get fields list of target object
		Set<String> targetFields = target.getFieldNames();
		Set<String> sourceFields = source.getFieldNames();
		Set<String> resultFields = operation.combine(targetFields, sourceFields);
		
		ContainerNode result = target.createNew();
		for (String fieldName : resultFields)
		{
			JsonNode targetValue = target.getField(fieldName).orElse(null); 
			JsonNode sourceValue = source.getField(fieldName).orElse(null);

			if (targetValue != null && sourceValue != null)
			{
				if (targetValue.isObject() && sourceValue.isObject())
					result.set(fieldName, processLevel(targetValue, sourceValue, operation));
				else
					result.set(fieldName, targetValue);
			} else 
				result.set(fieldName, targetValue != null ? targetValue : sourceValue);
			
		}
		return result.getNode();
	}
}

