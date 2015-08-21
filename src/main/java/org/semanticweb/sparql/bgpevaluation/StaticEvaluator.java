package org.semanticweb.sparql.bgpevaluation;

import java.util.List;
import java.util.Map;

import org.semanticweb.HermiT.OWLBGPHermiT;
import org.semanticweb.HermiT.StaticHermiTCostEstimationVisitor;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.monitor.Monitor;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QueryObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;

public class StaticEvaluator extends QueryEvaluator {

    protected final StaticCostEstimationVisitor m_costEstimator;

    public StaticEvaluator(OWLOntologyGraph graph, Monitor monitor) {
        super(graph, monitor);
        if (m_graph.getReasoner() instanceof OWLBGPHermiT) {
            m_costEstimator = new StaticHermiTCostEstimationVisitor(m_graph);
            //OWLBGPHermiT hermit=(OWLBGPHermiT)m_graph.getReasoner();
            //for (OWLClass cls:hermit.getRootOntology().getClassesInSignature()) {
            //	int[] estimate=hermit.getInstanceStatistics().getNumberOfInstances(cls);
            //	System.out.println("Class "+cls+"  known: "+estimate[0] + "  possible: " +estimate[1]);
            //}
            //for (OWLObjectProperty prop: hermit.getRootOntology().getObjectPropertiesInSignature()) {
            //	int[] estimate=new int[2];
            //	estimate[0]=hermit.getInstanceStatistics().getRoleInstanceStatistics(prop).getNumberOfKnownInstances();
            //	estimate[1]=hermit.getInstanceStatistics().getRoleInstanceStatistics(prop).getNumberOfPossibleInstances();
            //	System.out.println("Property "+prop+"  known: "+estimate[0] + "  possible: " +estimate[1]);
            //}
        }
        else
            m_costEstimator = new StaticCostEstimationVisitor(m_graph);
    }

    @Override
    public List<Atomic[]> execute(List<QueryObject<? extends Axiom>> connectedComponent, Map<Variable, Integer> positionInTuple, List<Atomic[]> bindings) {
        //List<QueryObject<? extends Axiom>> staticAxiomOrder= new ArrayList<QueryObject<? extends Axiom>>();
        //staticAxiomOrder.addAll(connectedComponent);
        List<QueryObject<? extends Axiom>> staticAxiomOrder = StaticQueryReordering.getCheapestOrdering(m_costEstimator, connectedComponent, m_monitor);
        for (QueryObject<? extends Axiom> cheapest : staticAxiomOrder) {
            //System.out.println(cheapest);
            if (!bindings.isEmpty()) {
//                for (Variable v : positionInTuple.keySet()) {
//                    System.out.println(v + " " + positionInTuple.get(v));
//                }
                bindings = cheapest.computeBindings(bindings, positionInTuple);
                //System.out.println("The result size is: "+bindings.size());
                /*for (Atomic[] bind:bindings) {
                    for (int p=0; p<bind.length; p++)
                		System.out.print(bind[p]+"  ");
                	System.out.println();
                }
*/
                //System.out.println("The result size is: "+bindings.size());
//                Prefixes pre=new Prefixes();
//                pre.addPrefixes(Prefixes.STANDARD_PREFIXES);
//                pre.declarePrefix("test", "http://www.whatif-project.org/ontology/authoringOntology/CarDriverTest.owl#");
//                for (Atomic[] bind:bindings) {
//                	for (int p=0; p<bind.length; p++)
////                	    if (bind[p] != null)
////                		    System.out.print(bind[p].toString(pre)+"  ");
////                	    else 
//                	        System.out.print(bind[p]+"  ");
//                	System.out.println();
//                }

            }
        }
        return bindings;
    }

}
