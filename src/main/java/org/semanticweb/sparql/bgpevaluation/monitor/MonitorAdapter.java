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

package org.semanticweb.sparql.bgpevaluation.monitor;

import java.util.List;

import org.semanticweb.sparql.bgpevaluation.queryobjects.QueryObject;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;

public class MonitorAdapter implements Monitor {
    public MonitorAdapter() {
    }

    public void bgpEvaluationStarted() {
    }

    public void bgpEvaluationFinished(int resultSize) {
    }

    public void bgpParsingStarted() {
    }

    public void bgpParsingFinished(String parsedAxioms) {
    }

    public void connectedComponentsComputationStarted() {
    }

    public void connectedComponentsComputationFinished(int numComponents) {
    }

    public void componentsEvaluationStarted(List<QueryObject<? extends Axiom>> queryObjectsInComponent) {
    }

    public void componentsEvaluationFinished(int results) {
    }

    public void costEvaluationStarted() {
    }

    public void costEvaluationStarted(QueryObject<? extends Axiom> qo) {
    }

    public void costEvaluationFinished(double costEstimate, double resultSizeEstimate) {
    }

    public void costEvaluationFinished(QueryObject<? extends Axiom> cheapest) {
    }

    public void queryObjectEvaluationStarted(QueryObject<? extends Axiom> queryObject) {
    }

    public void queryObjectEvaluationFinished(int resultSize) {
    }

    public void parsingFinished(String parsedQuery) {
    }
}
