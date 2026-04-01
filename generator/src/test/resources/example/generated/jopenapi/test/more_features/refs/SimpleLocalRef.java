package jopenapi.test.more_features.refs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record SimpleLocalRef(@JsonValue() jopenapi.test.more_features.refs.LocalCommonObject value) {

	@JsonCreator()
	public static SimpleLocalRef of(jopenapi.test.more_features.refs.LocalCommonObject value) {
		return new SimpleLocalRef(value);
	}
}
