package uk.gov.justice.metrics.agent.wildfly;

import java.lang.instrument.Instrumentation;

import org.jboss.byteman.agent.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyAgent {

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        Main.premain("resourcescript:ic.btm", inst);
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        premain(args, inst);
    }

}
