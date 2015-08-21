package org.semanticweb.sparql.bgpevaluation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.DynamicHermiTCostEstimationVisitor;
import org.semanticweb.HermiT.OWLBGPHermiT;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.monitor.Monitor;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QueryObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;

public class DynamicEvaluator extends QueryEvaluator {

    protected final DynamicCostEstimationVisitor m_costEstimator;

    public DynamicEvaluator(OWLOntologyGraph graph, Monitor monitor) {
        super(graph, monitor);
        if (m_graph.getReasoner() instanceof OWLBGPHermiT)
            m_costEstimator = new DynamicHermiTCostEstimationVisitor(m_graph);
        else
            m_costEstimator = new DynamicCostEstimationVisitor(m_graph);
    }

    @Override
    public List<Atomic[]> execute(List<QueryObject<? extends Axiom>> connectedComponent, Map<Variable, Integer> positionInTuple, List<Atomic[]> bindings) {

        m_costEstimator.setCandidateBindings(bindings);
        m_costEstimator.setBindingPositions(positionInTuple);
        Set<Variable> boundVar = new HashSet<Variable>();
        while (!connectedComponent.isEmpty() && !bindings.isEmpty()) {
            m_monitor.costEvaluationStarted();
            QueryObject<? extends Axiom> cheapest;
            if (connectedComponent.size() == 1)
                cheapest = connectedComponent.iterator().next();
            else
                cheapest = DynamicQueryReordering.getCheapest(m_costEstimator, connectedComponent, boundVar, m_monitor);
            //System.out.println(cheapest);
            m_monitor.costEvaluationFinished(cheapest);
            connectedComponent.remove(cheapest);
            Axiom ax = cheapest.getAxiomTemplate();
            Set<Variable> varsss = ax.getVariablesInSignature();
            boundVar.addAll(varsss);
            m_monitor.queryObjectEvaluationStarted(cheapest);
            bindings = cheapest.computeBindings(bindings, positionInTuple);
            m_monitor.queryObjectEvaluationFinished(bindings.size());
            m_costEstimator.setCandidateBindings(bindings);
            m_costEstimator.setBindingPositions(positionInTuple);
        }
        return bindings;
    }

}
