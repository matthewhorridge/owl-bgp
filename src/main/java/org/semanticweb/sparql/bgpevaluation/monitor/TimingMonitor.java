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


public class TimingMonitor extends MonitorAdapter {
    protected long tBgpEval;
    protected long tParsing;
    protected long tComponentComputation;
    protected int numComponents;
    protected long[] tComponentEvaluation;
    protected int componentIndex;
    protected int[] numResultsPerComponent;
    protected long tCostEstimation;
    protected long[][] tQueryObjectEvaluationPerComponent;
    protected int[][] numResultsPerQueryObjectAndComponent;
    protected int queryObjectIndex;

    public void bgpEvaluationStarted() {
        tBgpEval = System.currentTimeMillis();
    }

    public void bgpEvaluationFinished(int resultSize) {
        tBgpEval = System.currentTimeMillis() - tBgpEval;
    }

    public long getLastBGPEvaluationTime() {
        return tBgpEval;
    }

    public void bgpParsingStarted() {
        tParsing = System.currentTimeMillis();
    }

    public void bgpParsingFinished(String parsedAxioms) {
        tParsing = System.currentTimeMillis() - tParsing;
    }

    public long getLastBGPParsingTime() {
        return tParsing;
    }

    public void connectedComponentsComputationStarted() {
        tComponentComputation = System.currentTimeMillis();
    }

    public void connectedComponentsComputationFinished(int numComponents) {
        tComponentComputation = System.currentTimeMillis() - tComponentComputation;
        this.numComponents = numComponents;
        tComponentEvaluation = new long[numComponents];
        numResultsPerComponent = new int[numComponents];
        tQueryObjectEvaluationPerComponent = new long[numComponents][];
        numResultsPerQueryObjectAndComponent = new int[numComponents][];
        componentIndex = 0;
    }

    public long getLastConnectedComponentComputationTime() {
        return tComponentComputation;
    }

    public int getNumberOfComponents() {
        return numComponents;
    }

    public void componentsEvaluationStarted(List<QueryObject<? extends Axiom>> queryObjectsInComponent) {
        tComponentEvaluation[componentIndex] = System.currentTimeMillis();
        tQueryObjectEvaluationPerComponent[componentIndex] = new long[queryObjectsInComponent.size()];
        numResultsPerQueryObjectAndComponent[componentIndex] = new int[queryObjectsInComponent.size()];
        queryObjectIndex = 0;
    }

    public void componentsEvaluationFinished(int results) {
        tComponentEvaluation[componentIndex] = System.currentTimeMillis() - tComponentEvaluation[componentIndex];
        numResultsPerComponent[componentIndex] = results;
        componentIndex++;
    }

    public long getLastComponentEvaluationTime() {
        if (componentIndex > 0)
            return tComponentEvaluation[componentIndex - 1];
        else
            return 0;
    }

    public int getLastComponentResultSize() {
        if (componentIndex > 0)
            return numResultsPerComponent[componentIndex - 1];
        else
            return 0;
    }

    public long[] getComponentEvaluationTimes() {
        return tComponentEvaluation;
    }

    public int[] getResultSizePerComponent() {
        return numResultsPerComponent;
    }

    public void costEvaluationStarted() {
        tCostEstimation = System.currentTimeMillis();
    }

    public void costEvaluationStarted(QueryObject<? extends Axiom> qo) {
    }

    public void costEvaluationFinished(double costEstimate, double resultSizeEstimate) {
    }

    public void costEvaluationFinished(QueryObject<? extends Axiom> cheapest) {
        tCostEstimation = System.currentTimeMillis() - tCostEstimation;
    }

    public long getLastCostEstimationTime() {
        return tCostEstimation;
    }

    public void queryObjectEvaluationStarted(QueryObject<? extends Axiom> queryObject) {
        tQueryObjectEvaluationPerComponent[componentIndex][queryObjectIndex] = System.currentTimeMillis();
    }

    public void queryObjectEvaluationFinished(int resultSize) {
        tQueryObjectEvaluationPerComponent[componentIndex][queryObjectIndex] = System.currentTimeMillis() - tQueryObjectEvaluationPerComponent[componentIndex][queryObjectIndex];
        numResultsPerQueryObjectAndComponent[componentIndex][queryObjectIndex] = resultSize;
        queryObjectIndex++;
    }

    public int getLastQueryObjectResultSize() {
        if (queryObjectIndex > 0)
            return numResultsPerQueryObjectAndComponent[componentIndex][queryObjectIndex - 1];
        else if (componentIndex > 0)
            return numResultsPerQueryObjectAndComponent[componentIndex - 1][tQueryObjectEvaluationPerComponent[componentIndex - 1].length - 1];
        else
            return 0;
    }

    public long getLastQueryObjectEvaluationTime() {
        if (queryObjectIndex > 0)
            return tQueryObjectEvaluationPerComponent[componentIndex][queryObjectIndex - 1];
        else if (componentIndex > 0)
            return tQueryObjectEvaluationPerComponent[componentIndex - 1][tQueryObjectEvaluationPerComponent[componentIndex - 1].length - 1];
        else
            return 0;
    }
}
