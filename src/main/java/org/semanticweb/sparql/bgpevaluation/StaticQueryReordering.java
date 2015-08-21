/* Copyright 2010-2012 by the developers of the OWL-BGP project. 

   This file is part of the OWL-BGP project.

   OWL-BGP is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OWL-BGP is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with OWL-BGP.  If not, see <http://www.gnu.org/licenses/>.
*/

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
        List<QueryObject<? extends Axiom>> cheapestOrder = new ArrayList<QueryObject<? extends Axiom>>();
        Set<Variable> bound = new HashSet<Variable>();
        double cheapestCost = 0;
        boolean first = true;
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
        List<QueryObject<? extends Axiom>> simpleAtoms = new ArrayList<QueryObject<? extends Axiom>>();
        List<QueryObject<? extends Axiom>> complexAtoms = new ArrayList<QueryObject<? extends Axiom>>();
        for (QueryObject<? extends Axiom> ax : atoms) {
            if (ax.isComplex())
                complexAtoms.add(ax);
            else simpleAtoms.add(ax);
        }
        if (complexAtoms.isEmpty()) {
            int atomsize = atoms.size();
            while (!atoms.isEmpty()) {
                cheapestCost = 0;
                first = true;
                Set<Variable> vars = new HashSet<Variable>();
                Set<Variable> varscheck = new HashSet<Variable>();
                for (QueryObject<? extends Axiom> at : atoms)
                    varscheck.addAll(at.getAxiomTemplate().getVariablesInSignature());
                QueryObject<? extends Axiom> cheapestAtom = null;
                for (QueryObject<? extends Axiom> qo : atoms) {
                    int flag = 0;
                    vars = qo.getAxiomTemplate().getVariablesInSignature();
                    if (vars.isEmpty()) {
                        flag = 1;
                    }
                    if (atoms.size() == atomsize)
                        vars.retainAll(varscheck);
                    else vars.retainAll(bound);
                    if (!vars.isEmpty() || flag == 1) {
                        monitor.costEvaluationStarted(qo);
                        double[] costs = qo.accept(estimator, bound);
                        monitor.costEvaluationFinished(costs[0], costs[1]);
                        double totalCost = costs[0] + costs[1];
                        if (first || totalCost < cheapestCost) {
                            first = false;
                            cheapestCost = totalCost;
                            cheapestAtom = qo;
                        }
                    }
                }
                cheapestOrder.add(cheapestAtom);
                bound.addAll(cheapestAtom.getAxiomTemplate().getVariablesInSignature());
                atoms.remove(cheapestAtom);
            }
        }
        else {
            while (!atoms.isEmpty()) {
                cheapestCost = 0;
                first = true;
                //Set<Variable> vars=new HashSet<Variable>();
                //Set<Variable> varscheck=new HashSet<Variable>();
                //for (QueryObject<? extends Axiom> at:simpleAtoms)
                // varscheck.addAll(at.getAxiomTemplate().getVariablesInSignature());
                QueryObject<? extends Axiom> cheapestAtom = null;
                for (QueryObject<? extends Axiom> qo : atoms) {
                    monitor.costEvaluationStarted(qo);
                    double[] costs = qo.accept(estimator, bound);
                    monitor.costEvaluationFinished(costs[0], costs[1]);
                    double totalCost = costs[0] + costs[1];
                    if (first || totalCost < cheapestCost) {
                        first = false;
                        cheapestCost = totalCost;
                        cheapestAtom = qo;
                    }
                }
                cheapestOrder.add(cheapestAtom);
                bound.addAll(cheapestAtom.getAxiomTemplate().getVariablesInSignature());
                atoms.remove(cheapestAtom);
            }
        }
        return cheapestOrder;
    }
}
