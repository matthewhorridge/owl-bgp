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

import java.util.List;
import java.util.Set;

import org.semanticweb.sparql.bgpevaluation.monitor.Monitor;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QueryObject;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;

public class DynamicQueryReordering {
    public static QueryObject<? extends Axiom> getCheapest(DynamicCostEstimationVisitor estimator, List<QueryObject<? extends Axiom>> atoms, Set<Variable> boundVar, Monitor monitor) {
        QueryObject<? extends Axiom> cheapest = null;
        double cheapestCost = 0;
        boolean first = true;
        for (QueryObject<? extends Axiom> qo : atoms) {
            //long t=System.currentTimeMillis();
            Set<Variable> vars = qo.getAxiomTemplate().getVariablesInSignature();
            if (!boundVar.isEmpty()) {
                vars.retainAll(boundVar);
            }
            if (!vars.isEmpty()) {
                monitor.costEvaluationStarted(qo);
                double[] costs = qo.accept(estimator);
                monitor.costEvaluationFinished(costs[0], costs[1]);
                double totalCost = costs[0] + costs[1];
                if (first || totalCost < cheapestCost) {
                    first = false;
                    cheapestCost = totalCost;
                    cheapest = qo;
                }
            }
            //System.out.println("The reordering time for the atom "+qo.toString()+" is "+(System.currentTimeMillis()-t)+" ms.");
        }
//        System.out.println(cheapest);
        return cheapest;
    }
}
 