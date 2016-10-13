# Metrics Agent

[![Build Status](https://travis-ci.org/CJSCommonPlatform/metrics-agent.svg?branch=master)](https://travis-ci.org/CJSCommonPlatform/metrics-agent) [![Coverage Status](https://coveralls.io/repos/github/CJSCommonPlatform/metrics-agent/badge.svg?branch=master)](https://coveralls.io/github/CJSCommonPlatform/metrics-agent?branch=master)

Introspection agent collecting metrics at runtime

Usage:

* metrics-agent-artemis

Run artemis with option: 
_-javaagent:metrics-agent-artemis.jar_

* metrics-agent-wildfly

Run wildfly with options: 
_-Djboss.modules.system.pkgs=org.jboss.byteman,uk.gov.justice.metrics -javaagent:metrics-agent-wildfly.jar_

Metrics are exposed via JMX in the domain: _uk.gov.justice.metrics_