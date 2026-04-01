package jopenapi.test.integers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record SimpleInteger(@JsonValue() int value) {

	@JsonCreator()
	public static SimpleInteger of(int value) {
		return new SimpleInteger(value);
	}
}
