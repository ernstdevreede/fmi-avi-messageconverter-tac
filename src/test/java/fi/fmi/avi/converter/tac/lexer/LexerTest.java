package fi.fmi.avi.converter.tac.lexer;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import fi.fmi.avi.converter.tac.TACTestConfiguration;

/**
 * Generic tests for the TAC Lexer implementation
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TACTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class LexerTest {

    @Autowired
    private AviMessageLexer lexer;

    @Test
    public void testSplit() {
        LexemeSequence seq = lexer.lexMessage("TAF EFHK 011733Z 0118/0218 VRB02KT 4000 -SN BKN003\n" +
                "TEMPO 0118/0120 1500 SN \n" +
                "BECMG 0120/0122 1500 BR \t\n" +
                "PROB40 TEMPO 0122/0203 0700 FG\n" +
                "BECMG 0204/0206 21010KT 5000 BKN005\n" +
                "BECMG 0210/0212 9999 BKN010=");

        List<LexemeSequence> splitUp = seq.splitBy(Lexeme.Identity.TAF_FORECAST_CHANGE_INDICATOR);
        assertTrue("Incorrect number of split sequences", splitUp.size() == 6);
        assertEquals("First split-up sequence does not match", splitUp.get(0).getTAC(), "TAF EFHK 011733Z 0118/0218 VRB02KT 4000 -SN BKN003\n");
        assertEquals("Second split-up sequence does not match", splitUp.get(1).getTAC(), "TEMPO 0118/0120 1500 SN \n");
        assertEquals("Third split-up sequence does not match", splitUp.get(2).getTAC(), "BECMG 0120/0122 1500 BR \t\n");
        assertEquals("Fourth split-up sequence does not match", splitUp.get(3).getTAC(), "PROB40 TEMPO 0122/0203 0700 FG\n");
        assertEquals("Fifth split-up sequence does not match", splitUp.get(4).getTAC(), "BECMG 0204/0206 21010KT 5000 BKN005\n");
        assertEquals("Sixth split-up sequence does not match", splitUp.get(5).getTAC(), "BECMG 0210/0212 9999 BKN010=");

    }

    @Test
    public void testBasicLexing() {
        String original = "TAF EFHK 011733Z 0118/0218 VRB02KT 4000 -SN BKN003\n" +
                "TEMPO 0118/0120 1500 SN\n" +
                "BECMG 0120/0122 1500 BR\n" +
                "PROB40 TEMPO 0122/0203 0700 FG\n" +
                "BECMG 0204/0206 21010KT 5000 BKN005\n" +
                "BECMG 0210/0212 9999 BKN010=";
        LexemeSequence seq = lexer.lexMessage(original);
        assertEquals(58, seq.getLexemes().size());
        String back2tac = seq.getTAC();
        assertEquals(original, back2tac);
    }

    @Test
    public void testIteratorWithWhiteSpaceEnding() {
        LexemeSequence seq = lexer.lexMessage(
                "TAF EFHK 011733Z 0118/0218 VRB02KT 4000 -SN BKN003\n" + "TEMPO 0118/0120 1500 SN \n" + "BECMG 0120/0122 1500 BR \t\n"
                        + "PROB40 TEMPO 0122/0203 0700 FG\n" + "BECMG 0204/0206 21010KT 5000 BKN005\n" + "BECMG 0210/0212 9999 BKN010=");

        List<LexemeSequence> splitUp = seq.splitBy(Lexeme.Identity.TAF_FORECAST_CHANGE_INDICATOR);
        Lexeme l = splitUp.get(0).getFirstLexeme();
        while (!l.getTACToken().equals("BKN003")) {
            l = l.getNext();
        }
        assertFalse(l.hasNext());
        assertTrue(l.getNext() == null);

        assertTrue(l.hasNext(true));
        assertTrue(l.getNext(true) != null);

    }
        
}
