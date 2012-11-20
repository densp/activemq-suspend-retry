package se.expertsystems.activemq.resume;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test case to test ActiveMQ suspend/resume using Bitronix JTA
 * @author Mattias Jiderhamn
 */
// @Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/spring/bitronix.xml",
    "classpath*:/META-INF/spring/activemq-bitronix.xml",
    "classpath*:/META-INF/spring/jms.xml"})
public class JmsConfigBitronixTest extends JmsConfigTestBase {
  
}