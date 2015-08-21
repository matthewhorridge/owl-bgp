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

package org.semanticweb.sparql.arq;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.atlas.io.IndentedWriter;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingFactory;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIter;
import com.hp.hpl.jena.sparql.serializer.SerializationContext;
import com.hp.hpl.jena.sparql.util.FmtUtils;
import com.hp.hpl.jena.sparql.util.Utils;

public class OWLBGPQueryIterator extends QueryIter {//QueryIter1, QueryIteratorBase
    protected final BasicPattern pattern;
    protected final List<Map<Variable, Integer>> bindingPositionsPerComponent;
    protected final Set<String> m_skolemConstants;
    protected QueryIterator input;
    protected List<List<Atomic[]>> resultsPerComponent;
    protected final Set<IndividualVariable> bnodes;
    protected int currentRow;
    protected int[] m_currentBindingIndexes;
    protected final int numRows;

    public OWLBGPQueryIterator(BasicPattern pattern, QueryIterator input, ExecutionContext execCxt, List<List<Atomic[]>> results, List<Map<Variable, Integer>> bindingPositionsPerComponent, Set<IndividualVariable> bnodes) {
        super(execCxt);
        this.pattern = pattern;
        m_skolemConstants = ((OWLOntologyGraph) execCxt.getActiveGraph()).getSkolemConstants();
        this.bindingPositionsPerComponent = bindingPositionsPerComponent;
        this.input = input;
        this.resultsPerComponent = results;
        this.bnodes = bnodes;
        // project out bnodes
        for (int i = 0; i < resultsPerComponent.size(); i++) {
            Map<Variable, Integer> positionInTuple = bindingPositionsPerComponent.get(i);
            for (Variable bnode : bnodes)
                positionInTuple.remove(bnode);
        }
        this.currentRow = 0;
        int size = 0;
        boolean first = true;
        for (List<Atomic[]> result : resultsPerComponent) {
            if (first) {
                first = false;
                size += result.size();
            }
            else {
                size *= result.size();
            }
        }
        numRows = size;
        m_currentBindingIndexes = new int[resultsPerComponent.size()];
    }

    protected boolean hasNextBinding() {
        return currentRow < numRows;
    }

    protected Binding moveToNextBinding() {
        boolean isNew = true;
        if (hasNextBinding()) {
            BindingMap bindingMap = null;
            if (input != null && input.hasNext()) {
                bindingMap = BindingFactory.create(input.next());
                isNew = false;
            }
            else
                bindingMap = BindingFactory.create();
            boolean flip = false;
            for (int index = resultsPerComponent.size() - 1; index >= 0; index--) {
                if (index == resultsPerComponent.size() - 1) {
                    // last bit, always flip
                    if (m_currentBindingIndexes[index] < resultsPerComponent.get(index).size() - 1)
                        m_currentBindingIndexes[index] += 1;
                    else {
                        m_currentBindingIndexes[index] = 0;
                        flip = true;
                    }
                }
                else if (flip) {
                    if (m_currentBindingIndexes[index] < resultsPerComponent.get(index).size() - 1) {
                        m_currentBindingIndexes[index] = m_currentBindingIndexes[index] + 1;
                        flip = false;
                    }
                    else
                        m_currentBindingIndexes[index] = 0;
                }
            }
            for (int i = 0; i < resultsPerComponent.size(); i++) {
                Map<Variable, Integer> positionInTuple = bindingPositionsPerComponent.get(i);
                Atomic[] result = resultsPerComponent.get(i).get(m_currentBindingIndexes[i]);
                for (Variable variable : positionInTuple.keySet()) {
                    Var var = Var.alloc(variable.getVariable());
                    Node node = createNode(result[positionInTuple.get(variable)]);
                    if (bindingMap.contains(var)) {
                        System.err.println("*************************** binding contains var " + var + "already (is newly created binding? " + isNew + ")");
                        BindingHashMap bhm = (BindingHashMap) bindingMap;
                        Binding parent = bhm.getParent();
                        if (parent != null && parent.contains(var)) {
                            System.err.println("*************************** parent contains var " + var + "already");
                        }
                    }
                    else
                        bindingMap.add(var, node);
                }
            }
            currentRow++;
            return bindingMap;
        }
        else
            return null;
    }

    protected void closeSubIterator() {
        resultsPerComponent = null;
    }

    //    protected void closeIterator() {
//        input.close();
//        input=null;
//        resultsPerComponent=null;
//    }
    public void output(IndentedWriter out, SerializationContext sCxt) {
        out.print(Utils.className(this));
        out.println();
        out.incIndent();
        FmtUtils.formatPattern(out, pattern, sCxt);
        out.decIndent();
    }

    protected Node createNode(Atomic atomic) {
        if (atomic instanceof Literal) {
            Literal lit = (Literal) atomic;
            String iri = lit.getDatatype().getIRI().toString(Prefixes.NO_PREFIXES);
            if (iri.equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral>"))
                return NodeFactory.createLiteral(lit.getLexicalForm(), lit.getLangTag(), null);
            else
                return NodeFactory.createLiteral(lit.getLexicalForm(), lit.getLangTag(), NodeFactory.getType(iri.substring(1, iri.length() - 1)));
        }
        else {
            // we could do something with the Skolem constants here
            String iri = atomic.toString(Prefixes.NO_PREFIXES);
            iri = iri.substring(1, iri.length() - 1);
            if (atomic instanceof NamedIndividual && m_skolemConstants.contains(iri) && iri.startsWith("_:genid-nodeid-")) {
                String label = iri.substring("_:genid-nodeid-".length());
                AnonId id = AnonId.create(label);
                Node node = NodeFactory.createAnon(id);
                return node;
            }
            else
                return NodeFactory.createURI(iri);
        }
    }

    @Override
    protected void closeIterator() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void requestCancel() {
        // TODO Auto-generated method stub

    }
}