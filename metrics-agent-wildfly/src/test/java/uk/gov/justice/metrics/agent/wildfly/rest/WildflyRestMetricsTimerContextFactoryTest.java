package uk.gov.justice.metrics.agent.wildfly.rest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.metrics.agent.wildfly.rest.WildflyRestMetricsTimerContextFactory.timerContextNameFrom;
import static uk.gov.justice.metrics.agent.wildfly.rest.WildflyRestMetricsTimerContextFactory.timerContextOf;

import uk.gov.justice.metrics.agent.artemis.agent.common.TimerContext;
import uk.gov.justice.metrics.agent.artemis.agent.common.TimerRegistry;

import java.util.Optional;

import org.junit.Test;

public class WildflyRestMetricsTimerContextFactoryTest {

    @Test
    public void shouldConstructTimerName() throws Exception {
        assertThat(timerContextNameFrom("/example-query-api/query/api/rest/cakeshop/recipes"),
                is("wildfly.rest.example-query-api/cakeshop/recipes"));

        assertThat(timerContextNameFrom("/example-query-controller/query/controller/rest/cakeshop/recipes/"),
                is("wildfly.rest.example-query-controller/cakeshop/recipes"));

        assertThat(timerContextNameFrom("/example-query-view/query/view/rest/cakeshop/recipes"),
                is("wildfly.rest.example-query-view/cakeshop/recipes"));

        assertThat(timerContextNameFrom("/example-command-api/command/api/rest/cakeshop/recipes"),
                is("wildfly.rest.example-command-api/cakeshop/recipes"));


    }

    @Test
    public void shouldReplaceIdsWithPlaceHolders() throws Exception {

        assertThat(timerContextNameFrom("/example-query-api/query/api/rest/cakeshop/recipes/263af847-effb-46a9-96bc-32a0f7526e44"),
                is("wildfly.rest.example-query-api/cakeshop/recipes/{id}"));

        assertThat(timerContextNameFrom("/example-query-api/query/api/rest/cakeshop/recipes/123aa"),
                is("wildfly.rest.example-query-api/cakeshop/recipes/{id}"));

        assertThat(timerContextNameFrom("/example-query-api/query/api/rest/cakeshop/recipes/263af847-effb-46a9-96bc-32a0f7526e44/cakes"),
                is("wildfly.rest.example-query-api/cakeshop/recipes/{id}/cakes"));

        assertThat(timerContextNameFrom("/example-query-api/query/api/rest/cakeshop/recipes/123aa/cakes"),
                is("wildfly.rest.example-query-api/cakeshop/recipes/{id}/cakes"));

    }

    @Test
    public void shouldCreateTimerContextForRestRequest() {

        final TimerContext timerContext = timerContextOf(Optional.of("/example-query-view/query/view/rest/cakeshop/cakes"));
        timerContext.startTimer("1");
        timerContext.stopTimer("1");
        timerContext.startTimer("2");
        timerContext.stopTimer("2");
        assertThat(TimerRegistry.timerOf("wildfly.rest.example-query-view/cakeshop/cakes").getCount(), is(2L));

    }

}
