package uk.gov.justice.metrics.agent.wildfly.rest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import uk.gov.justice.metrics.agent.artemis.agent.common.TimerRegistry;
import uk.gov.justice.metrics.agent.wildfly.util.TestAppender;

import java.util.List;

import io.undertow.server.HttpServerExchange;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

public class WildflyRestAgentHelperTest {

    private WildflyRestAgentHelper agentHelper = new WildflyRestAgentHelper();

    @Before
    public void setUp() throws Exception {
        TimerRegistry.reset();
    }

    @Test
    public void shouldCollectMetricsForRestRequests() throws Exception {
        HttpServerExchange exchange1 = new HttpServerExchange("/example-query-view/query/view/rest/cakeshop/recipes");
        HttpServerExchange exchange2 = new HttpServerExchange("/example-query-view/query/view/rest/cakeshop/recipes/163af847-effb-46a9-96bc-32a0f7526e14");
        HttpServerExchange exchange3 = new HttpServerExchange("/example-query-view/query/view/rest/cakeshop/recipes/163af847-effb-46a9-96bc-32a0f7526e15");
        HttpServerExchange exchange4 = new HttpServerExchange("/example-query-controller/query/controller/rest/cakeshop/recipes");

        agentHelper.onEntry(exchange1);
        agentHelper.onEntry(exchange2);
        agentHelper.onEntry(exchange3);
        agentHelper.onEntry(exchange4);

        agentHelper.onExit(exchange2);
        agentHelper.onExit(exchange1);
        agentHelper.onExit(exchange4);
        agentHelper.onExit(exchange3);

        assertThat(TimerRegistry.timerOf("wildfly.rest.example-query-view/cakeshop/recipes").getCount(), is(1L));
        assertThat(TimerRegistry.timerOf("wildfly.rest.example-query-view/cakeshop/recipes/{id}").getCount(), is(2L));
        assertThat(TimerRegistry.timerOf("wildfly.rest.example-query-controller/cakeshop/recipes").getCount(), is(1L));

    }

    @Test
    public void shouldDoNothingWhenRequestUrlNull() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        agentHelper.onEntry(exchange);
        agentHelper.onExit(exchange);

    }
}