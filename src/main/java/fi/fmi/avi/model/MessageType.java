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

@JsonDeserialize(using = MessageType.Deserializer.class)
@JsonSerialize(using = MessageType.Serializer.class)
public class MessageType implements Serializable {

    public static final MessageType TAF = new MessageType("TAF");
    public static final MessageType METAR = new MessageType("METAR");
    public static final MessageType SPECI = new MessageType("SPECI");
    public static final MessageType SIGMET = new MessageType("SIGMET");
    public static final MessageType AIRMET = new MessageType("AIRMET");
    public static final MessageType TROPICAL_CYCLONE_ADVISORY = new MessageType("TROPICAL_CYCLONE_ADVISORY");
    public static final MessageType VOLCANIC_ASH_ADVISORY = new MessageType("VOLCANIC_ASH_ADVISORY");
    public static final MessageType SPACE_WEATHER_ADVISORY = new MessageType("SPACE_WEATHER_ADVISORY");
    public static final MessageType BULLETIN = new MessageType("BULLETIN");
    public static final MessageType SPECIAL_AIR_REPORT = new MessageType("SPECIAL_AIR_REPORT");
    public static final MessageType GENERIC = new MessageType("GENERIC");

    private static final long serialVersionUID = 8491210385030779165L;

    private final String name;

    public MessageType(final String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public boolean equals(final Object other) {
        if (other instanceof MessageType) {
            return name.equals(((MessageType) other).name());
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

    static class Deserializer extends StdDeserializer<MessageType> {
        private static final long serialVersionUID = 6812038258878332124L;

        public Deserializer() {
            this(null);
        }

        public Deserializer(final Class<?> vc) {
            super(vc);
        }

        @Override
        public MessageType deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
            final String value = ((JsonNode) jsonParser.getCodec().readTree(jsonParser)).asText();
            return new MessageType(value);
        }
    }

    static class Serializer extends StdSerializer<MessageType> {
        private static final long serialVersionUID = -5202057424512606741L;

        public Serializer() {
            this(null);
        }

        public Serializer(final Class<MessageType> vc) {
            super(vc);
        }

        @Override
        public void serialize(final MessageType messageType, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
                throws IOException {
            jsonGenerator.writeString(messageType.name);
        }
    }
}
