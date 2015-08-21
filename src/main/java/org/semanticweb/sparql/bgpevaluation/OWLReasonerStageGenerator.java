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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.sparql.arq.OWLBGPQueryIterator;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.monitor.Monitor;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QueryObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.UntypedVariable;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.LiteralVariable;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;
import org.semanticweb.sparql.owlbgp.parser.OWLBGPParser;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.main.StageGenerator;

public class OWLReasonerStageGenerator implements StageGenerator {
    public static final String LB = System.getProperty("line.separator");

    protected final StageGenerator m_above;
    protected final Monitor m_monitor;

    public OWLReasonerStageGenerator(StageGenerator original, Monitor monitor) {
        m_above = original;
        m_monitor = monitor;
    }

    @Override
    public QueryIterator execute(BasicPattern pattern, QueryIterator input, ExecutionContext execCxt) {
        m_monitor.bgpEvaluationStarted();

        Graph activeGraph = execCxt.getActiveGraph();
        // Test to see if this is a graph we support.  
        if (!(activeGraph instanceof OWLOntologyGraph)) {
            // Not us - bounce up the StageGenerator chain
            return m_above.execute(pattern, input, execCxt);
        }
        OWLOntologyGraph ontologyGraph = (OWLOntologyGraph) activeGraph;

        // parsing into extended OWL objects
        Set<Axiom> queryAxiomTemplates = null;
        Set<IndividualVariable> bnodes = null;
        m_monitor.bgpParsingStarted();
        String bgp = arqPatternToBGP(pattern);
        OWLBGPParser parser = new OWLBGPParser(new StringReader(bgp));
        try {
            parser.loadDeclarations(ontologyGraph.getClassesInSignature(), ontologyGraph.getDatatypesInSignature(), ontologyGraph.getObjectPropertiesInSignature(), ontologyGraph.getDataPropertiesInSignature(), ontologyGraph.getAnnotationPropertiesInSignature(), ontologyGraph.getIndividualsInSignature());
            parser.parse();
            queryAxiomTemplates = parser.getParsedAxioms();
            bnodes = parser.getVariablesForAnonymousIndividual();
            m_monitor.bgpParsingFinished(queryAxiomTemplates.toString());
        } catch (Exception e) { //Parse
            System.err.println("ParseException: Probably types could not be disambuguated with this active graph. ");
            e.printStackTrace();
            m_monitor.bgpEvaluationFinished(0);
            return new OWLBGPQueryIterator(pattern, input, execCxt, new ArrayList<List<Atomic[]>>(), new ArrayList<Map<Variable, Integer>>(), new HashSet<IndividualVariable>());
        }

        // splitting into weakly connected components
        m_monitor.connectedComponentsComputationStarted();
        RewriterAndSplitter rewriteAndSplitter = new RewriterAndSplitter(ontologyGraph, queryAxiomTemplates);
        Set<List<QueryObject<? extends Axiom>>> connectedComponents = rewriteAndSplitter.rewriteAndSplit();
        m_monitor.connectedComponentsComputationFinished(connectedComponents.size());

        // evaluation of each component
        String orderingMode = System.getProperty("ordering");
        QueryEvaluator evaluator = null;
        if (orderingMode != null) {
            if (orderingMode.equals("Static")) {
                evaluator = new StaticEvaluator(ontologyGraph, m_monitor);
            }
            else if (orderingMode.equals("PlanChecking")) {
                evaluator = new PlanChecker(ontologyGraph, m_monitor);
            }
            else if (orderingMode.equals("Dynamic")) {
                evaluator = new DynamicEvaluator(ontologyGraph, m_monitor);
            }
            else if (orderingMode.equals("Intersection")) {
                evaluator = new IntersectionEvaluator(ontologyGraph, m_monitor);
            }
        }
        if (evaluator == null)
            evaluator = new StaticEvaluator(ontologyGraph, m_monitor);

        int resultSize = 0;
        List<List<Atomic[]>> bindingsPerComponent = new ArrayList<List<Atomic[]>>();
        List<Map<Variable, Integer>> bindingPositionsPerComponent = new ArrayList<Map<Variable, Integer>>();
        for (List<QueryObject<? extends Axiom>> connectedComponent : connectedComponents) {
            m_monitor.componentsEvaluationStarted(connectedComponent);
            Map<Variable, Integer> positionInTuple = new HashMap<Variable, Integer>();
            int position = 0;

            for (QueryObject<? extends Axiom> ax : connectedComponent) {
                for (Variable var : ax.getAxiomTemplate().getVariablesInSignature()) {
                    if (!positionInTuple.containsKey(var)) {
                        positionInTuple.put(var, position);
                        position++;
                    }
                }
            }

            bindingPositionsPerComponent.add(positionInTuple);
            List<Atomic[]> bindings = initiliseBindings(input, positionInTuple, connectedComponent);
            bindings = evaluator.execute(connectedComponent, positionInTuple, bindings);
            bindingsPerComponent.add(bindings);
            if (resultSize == 0)
                resultSize = bindings.size();
            else resultSize *= bindings.size();
            m_monitor.componentsEvaluationFinished(bindings.size());
        }
        m_monitor.bgpEvaluationFinished(resultSize);
        return new OWLBGPQueryIterator(pattern, input, execCxt, bindingsPerComponent, bindingPositionsPerComponent, bnodes);
    }

    protected List<Atomic[]> initiliseBindings(QueryIterator input, Map<Variable, Integer> positionInTuple, List<QueryObject<? extends Axiom>> connectedComponent) {
        List<Atomic[]> bindings = new ArrayList<Atomic[]>();
        while (input.hasNext()) {
            Atomic[] initialBinding = new Atomic[positionInTuple.keySet().size()];
            Binding binding = input.nextBinding();
            Var jenaVar;
            for (Iterator<Var> bindingsIterator = binding.vars(); bindingsIterator.hasNext(); ) {
                jenaVar = bindingsIterator.next();
                for (QueryObject<? extends Axiom> ax : connectedComponent) {
                    for (Variable var : ax.getAxiomTemplate().getVariablesInSignature()) {
                        if (var.toString().equals(jenaVar.toString())) {
                            Atomic existingBinding = null;
                            String existingBindingString = binding.get(jenaVar).toString();
                            if (var instanceof ClassVariable)
                                existingBinding = Clazz.create(existingBindingString);
                            else if (var instanceof DatatypeVariable)
                                existingBinding = Datatype.create(existingBindingString);
                            else if (var instanceof ObjectPropertyVariable)
                                existingBinding = ObjectProperty.create(existingBindingString);
                            else if (var instanceof DataPropertyVariable)
                                existingBinding = DataProperty.create(existingBindingString);
                            else if (var instanceof IndividualVariable)
                                if (binding.get(jenaVar).isURI())
                                    existingBinding = NamedIndividual.create(existingBindingString);
                                else
                                    existingBinding = AnonymousIndividual.create(binding.get(jenaVar).getBlankNodeLabel());
                            else if (var instanceof LiteralVariable) {
                                String datatypeURI = binding.get(jenaVar).getLiteralDatatypeURI();
                                if (datatypeURI == null)
                                    datatypeURI = Prefixes.s_semanticWebPrefixes.get("rdf") + "PlainLiteral";
                                String langTag = binding.get(jenaVar).getLiteralLanguage();
                                String lexical = binding.get(jenaVar).getLiteralLexicalForm();
                                existingBinding = TypedLiteral.create(lexical, langTag, Datatype.create(datatypeURI));
                            }
                            else if (var instanceof AnnotationPropertyVariable)
                                existingBinding = AnnotationProperty.create(existingBindingString);
                            else
                                existingBinding = UntypedVariable.create(existingBindingString);
                            initialBinding[positionInTuple.get(var)] = existingBinding;
                        }
                    }
                }
            }
            bindings.add(initialBinding);
        }
        if (bindings.isEmpty())
            bindings.add(new Atomic[positionInTuple.keySet().size()]);
        return bindings;
    }

    protected String arqPatternToBGP(BasicPattern pattern) {
        StringBuffer buffer = new StringBuffer();
        for (Triple triple : pattern) {
            buffer.append(printNode(triple.getSubject()));
            buffer.append(" ");
            buffer.append(printNode(triple.getPredicate()));
            buffer.append(" ");
            buffer.append(printNode(triple.getObject()));
            buffer.append(" . ");
            buffer.append(LB);
        }
        return buffer.toString();
    }

    protected String printNode(Node node) {
        if (node.isURI())
            return "<" + node + ">";
        else if (node.isVariable()) {
            String name = node.getName();
            if (name.startsWith("?")) {
                name = name.substring(1);
                return "_:" + name;
                //m_bnodes.add(IndividualVariable.create(name));
                //TODO: check how we get the bnodes (without also taking the auxiliary ones)
            }
            return "?" + name;
        }
        else if (node.isLiteral()) {
            String datatypeURI = node.getLiteralDatatypeURI();
            String language = node.getLiteralLanguage();
            String lexicalForm = node.getLiteralLexicalForm();
            if (datatypeURI != null && datatypeURI != "" && (language == null || language == ""))
                return "\"" + lexicalForm + "\"^^<" + datatypeURI + ">";
            else
                return node.toString();

        }
        else
            return node.toString();
    }
}
