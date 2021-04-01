package fi.fmi.avi.model;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@JsonDeserialize(using = MessageFormat.Deserializer.class)
@JsonSerialize(using = MessageFormat.Serializer.class)
public class MessageFormat implements Serializable {

    public static final MessageFormat TEXT = new MessageFormat("TEXT");
    public static final MessageFormat XML = new MessageFormat("XML");

    private static final long serialVersionUID = 3928419536224160490L;

    private final String name;

    public MessageFormat(final String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public boolean equals(final Object other) {
        if (other instanceof MessageFormat) {
            return name.equals(((MessageFormat) other).name());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name();
    }

    static class Deserializer extends StdDeserializer<MessageFormat> {
        private static final long serialVersionUID = 3553054094110024174L;

        public Deserializer() {
            this(null);
        }

        public Deserializer(final Class<?> vc) {
            super(vc);
        }

        @Override
        public MessageFormat deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
            final String value = ((JsonNode) jsonParser.getCodec().readTree(jsonParser)).asText();
            return new MessageFormat(value);
        }
    }

    static class Serializer extends StdSerializer<MessageFormat> {
        private static final long serialVersionUID = 1188513911781835496L;

        public Serializer() {
            this(null);
        }

        public Serializer(final Class<MessageFormat> vc) {
            super(vc);
        }

        @Override
        public void serialize(final MessageFormat messageFormat, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
                throws IOException {
            jsonGenerator.writeString(messageFormat.name);
        }
    }
}
