behavior-verifier
=================

instrument java bytecode to product simple sequence diagram

natively integrated with Plantuml

usage guide:

1. run mvn clean install
2. export JAVA_TOOL_OPTIONS=-javaagent:path/to/behavior-verifier.java[listeners:${comaSeparatedListeners},reporters:${commaSeparatedReporters},reportDir:${umlReportPath}]

provided listener: com.smash.revolance.appreview.agent.PlantUmlEventListener
