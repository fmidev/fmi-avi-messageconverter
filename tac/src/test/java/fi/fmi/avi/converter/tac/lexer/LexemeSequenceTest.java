package fi.fmi.avi.converter.tac.lexer;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import fi.fmi.avi.converter.tac.conf.TACConverter;

/**
 * Created by rinne on 30/05/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TACConverter.class, loader = AnnotationConfigContextLoader.class)
public class LexemeSequenceTest {

    @Autowired
    private AviMessageLexer lexer;

    @Test
    public void testSplit() {
        LexemeSequence seq = lexer.lexMessage("TAF EFHK 011733Z 0118/0218 VRB02KT 4000 -SN BKN003 " +
                "TEMPO 0118/0120 1500 SN " +
                "BECMG 0120/0122 1500 BR " +
                "PROB40 TEMPO 0122/0203 0700 FG " +
                "BECMG 0204/0206 21010KT 5000 BKN005 " +
                "BECMG 0210/0212 9999 BKN010=");

        List<LexemeSequence> splitUp = seq.splitBy(Lexeme.Identity.FORECAST_CHANGE_INDICATOR);
        assertTrue("Incorrect number of split sequences", splitUp.size() == 6);
        assertEquals("First split-up sequence does not match", splitUp.get(0).getTAC(), "TAF EFHK 011733Z 0118/0218 VRB02KT 4000 -SN BKN003");
        assertEquals("Second split-up sequence does not match", splitUp.get(1).getTAC(), "TEMPO 0118/0120 1500 SN");
        assertEquals("Third split-up sequence does not match", splitUp.get(2).getTAC(), "BECMG 0120/0122 1500 BR");
        assertEquals("Fourth split-up sequence does not match", splitUp.get(3).getTAC(), "PROB40 TEMPO 0122/0203 0700 FG");
        assertEquals("Fifth split-up sequence does not match", splitUp.get(4).getTAC(), "BECMG 0204/0206 21010KT 5000 BKN005");
        assertEquals("Sixth split-up sequence does not match", splitUp.get(5).getTAC(), "BECMG 0210/0212 9999 BKN010=");

    }
}
