

package org.semanticweb.sparql.bgpevaluation;

import java.util.List;
import java.util.Map;

import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.monitor.Monitor;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QueryObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;

public abstract class QueryEvaluator {

    protected final OWLOntologyGraph m_graph;
    protected final Monitor m_monitor;

    public QueryEvaluator(OWLOntologyGraph graph, Monitor monitor) {
        m_graph = graph;
        m_monitor = monitor;
    }

    public abstract List<Atomic[]> execute(List<QueryObject<? extends Axiom>> connectedComponent, Map<Variable, Integer> positionInTuple, List<Atomic[]> initialBindings);
}
