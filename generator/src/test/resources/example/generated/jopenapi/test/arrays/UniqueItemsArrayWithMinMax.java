package jopenapi.test.arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;

public record UniqueItemsArrayWithMinMax(@JsonValue() java.util.Set<Integer> value) {

	public UniqueItemsArrayWithMinMax {
		value = value == null ? java.util.Set.of() : java.util.Collections.unmodifiableSet(value);
	}

	@JsonCreator()
	public static UniqueItemsArrayWithMinMax of(java.util.Set<Integer> value) {
		return new UniqueItemsArrayWithMinMax(value);
	}
}
