package org.semanticweb.sparql.bgpevaluation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.sparql.bgpevaluation.monitor.Monitor;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QueryObject;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;

public class StaticQueryReordering {
	public static List<QueryObject<? extends Axiom>> getCheapestOrdering(StaticCostEstimationVisitor estimator, List<QueryObject<? extends Axiom>> atoms, Monitor monitor) {
        List<QueryObject<? extends Axiom>> cheapestOrder=new ArrayList<QueryObject<? extends Axiom>>();
        Set<Variable> bound=new HashSet<Variable>();
        double cheapestCost=0;
        boolean first=true;
/*        for (int y=0;y<size;y++) {
          cheapestCost=0;
          first=true;
          QueryObject<? extends Axiom> cheapestAtom=null;
          for (QueryObject<? extends Axiom> qo : atoms) {
            monitor.costEvaluationStarted(qo);
            double[] costs=qo.accept(estimator,bound);
            monitor.costEvaluationFinished(costs[0], costs[1]);
            double totalCost=costs[0]+costs[1];
            if (first || totalCost<cheapestCost) {
                first=false;
            	cheapestCost=totalCost;
                cheapestAtom=qo;
            }
          }
          cheapestOrder.add(cheapestAtom);          
          bound.addAll(cheapestAtom.getAxiomTemplate().getVariablesInSignature());
          atoms.remove(cheapestAtom);
          
        }*/
        int atomsize=atoms.size();
        while (!atoms.isEmpty()) {         
          cheapestCost=0;
          first=true;
          Set<Variable> vars=new HashSet<Variable>();
          Set<Variable> varscheck=new HashSet<Variable>();
          for (QueryObject<? extends Axiom> at:atoms)
        	 varscheck.addAll(at.getAxiomTemplate().getVariablesInSignature());
          QueryObject<? extends Axiom> cheapestAtom=null;
          for (QueryObject<? extends Axiom> qo : atoms) {
            vars=qo.getAxiomTemplate().getVariablesInSignature();
            if (atoms.size()==atomsize)
              vars.retainAll(varscheck);
            else vars.retainAll(bound);
            if (!vars.isEmpty()) {
              monitor.costEvaluationStarted(qo);
              double[] costs=qo.accept(estimator,bound);
              monitor.costEvaluationFinished(costs[0], costs[1]);
              double totalCost=costs[0]+costs[1];
              if (first || totalCost<cheapestCost) {
                first=false;
          	    cheapestCost=totalCost;
                cheapestAtom=qo;
              }
            } 
          }
          cheapestOrder.add(cheapestAtom);          
          bound.addAll(cheapestAtom.getAxiomTemplate().getVariablesInSignature());
          atoms.remove(cheapestAtom);
        }
        return cheapestOrder;
    }
}