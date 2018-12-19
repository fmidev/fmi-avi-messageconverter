package fi.fmi.avi.model.sigmet;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import fi.fmi.avi.model.sigmet.immutable.WSSIGMETImpl;
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
      throws IOException {

        TreeNode sigmetTree = jp.readValueAsTree();
        if (sigmetTree.get("volcano") != null) {
            System.err.println("Deserializing a WV SIGMET");
            VASIGMETImpl vaSigmet = jp.getCodec().treeToValue(sigmetTree, VASIGMETImpl.class);
            return vaSigmet;
        }

        System.err.println("Deserializing a WS SIGMET");
        WSSIGMETImpl sigmet = jp.getCodec().treeToValue(sigmetTree, WSSIGMETImpl.class);
        return sigmet;
    }
}
