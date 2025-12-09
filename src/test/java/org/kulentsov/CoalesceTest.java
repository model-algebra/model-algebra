package org.kulentsov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class CoalesceTest
{
	@Test
	void testCoalesceWithValues()
	{
		Optional<Integer> result = Coalesce.of(null, 42, 100);
		assertTrue(result.isPresent());
		assertEquals(42, result.get());
	}
	
	@Test
	void testEmptyCoalesce()
	{
		Optional<Integer> result = Coalesce.of(null, null, null);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void testCoalesceWithOptionals()
	{
		Optional<Integer> result = Coalesce.ofOptionals(Optional.empty(), Optional.of(7), Optional.of(15));
		assertTrue(result.isPresent());
		assertEquals(7, result.get());
	}
	
	@Test
	void testCoalesceWithSuppliers()
	{
		Optional<Integer> result = Coalesce.of(Optional::empty, () -> Optional.of(34), () -> Optional.of(115));
		assertTrue(result.isPresent());
		assertEquals(34, result.get());
	}

}
