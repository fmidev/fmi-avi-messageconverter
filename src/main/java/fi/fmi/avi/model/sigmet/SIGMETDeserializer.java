package fi.fmi.avi.model.sigmet;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import fi.fmi.avi.model.sigmet.immutable.WVSIGMETImpl;
import fi.fmi.avi.model.sigmet.immutable.VASIGMETImpl;

public class SIGMETDeserializer extends StdDeserializer<SIGMET> {
    public SIGMETDeserializer() {
        this(null);
    }

    public SIGMETDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SIGMET deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {

        TreeNode sigmetTree = jp.readValueAsTree();
        if (sigmetTree.get("volcano") != null) {
            System.err.println("Deserializing a WV SIGMET");
            VASIGMETImpl vaSigmet = jp.getCodec().treeToValue(sigmetTree, VASIGMETImpl.class);
            return vaSigmet;
        }

        System.err.println("Deserializing a WS SIGMET");
        WVSIGMETImpl sigmet = jp.getCodec().treeToValue(sigmetTree, WVSIGMETImpl.class);
        return sigmet;
    }
}
