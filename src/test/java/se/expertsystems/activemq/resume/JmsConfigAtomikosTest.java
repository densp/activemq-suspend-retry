package se.expertsystems.activemq.resume;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test case to test ActiveMQ suspend/resume using Atomikos JTA
 * @author Mattias Jiderhamn
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/spring/atomikos.xml",
    "classpath*:/META-INF/spring/activemq-jta.xml",
    "classpath*:/META-INF/spring/jms.xml"})
public class JmsConfigAtomikosTest extends JmsConfigTestBase {
  
}