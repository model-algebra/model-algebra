package org.kulentsov;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class TypedOptionalTest
{
	static class Clazz1 {}
	static class Clazz2 {}
	static class Clazz3 extends Clazz2 {}
	
	@Test
	void testTypedOptional()
	{
		var obj1 = new Clazz1();
		var obj2 = new Clazz2();
		var obj3 = new Clazz3();
		
		Optional<Clazz1> to1 = TypedOptional.of(Clazz1.class, obj1);
		assertTrue(to1.isPresent());
		Optional<Clazz1> to1empty = TypedOptional.of(Clazz1.class, obj2);
		assertTrue(to1empty.isEmpty());
		Optional<Clazz2> to2 = TypedOptional.of(Clazz2.class, obj2);
		assertTrue(to2.isPresent());
		Optional<Clazz2> to2from3 = TypedOptional.of(Clazz2.class, obj3);
		assertTrue(to2from3.isPresent());
	}
	
}
