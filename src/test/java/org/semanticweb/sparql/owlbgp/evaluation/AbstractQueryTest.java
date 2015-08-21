/* Copyright 2010-2012 by the developers of the OWL-BGP project. 

   This file is part of OWL-BGP.

   OWL-BGP is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OWL-BGP is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with OWL-BGP. If not, see <http://www.gnu.org/licenses/>.
 */

package org.semanticweb.sparql.owlbgp.evaluation;

import java.util.HashMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.sparql.arq.OWLOntologyDataSet;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.AbstractTest;

public abstract class AbstractQueryTest extends AbstractTest {
    protected OWLOntologyManager m_ontologyManager;
    protected OWLOntology m_ontology;
    protected OWLOntologyDataSet m_dataSet;
    
    public AbstractQueryTest() {
        super();
    }
    public AbstractQueryTest(String name) {
        super(name);
    }
    protected void setUp() throws Exception {
        m_ontologyManager=OWLManager.createOWLOntologyManager();
    }
    protected void tearDown() throws Exception {
        super.tearDown();
        m_ontologyManager=null;
        m_ontology=null;
        m_dataSet=null;
    }
    /**
     * loads an OWL ontology that contains the given axioms
     */
    protected void loadOntologyWithAxioms(String axioms) throws Exception {
        StringBuffer buffer=new StringBuffer();
        buffer.append("Prefix(:=<"+NS+">)"+LB);
        buffer.append("Prefix(a:=<"+NS+">)"+LB);
        buffer.append("Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)"+LB);
        buffer.append("Prefix(owl2xml:=<http://www.w3.org/2006/12/owl2-xml#>)"+LB);
        buffer.append("Prefix(test:=<"+NS+">)"+LB);
        buffer.append("Prefix(owl:=<http://www.w3.org/2002/07/owl#>)"+LB);
        buffer.append("Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)"+LB);
        buffer.append("Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)"+LB);
        buffer.append("Prefix(foaf:=<http://xmlns.com/foaf/0.1/>)"+LB);
        buffer.append("Ontology("+ONTOLOGY_IRI+LB);
        buffer.append(axioms+LB);
        buffer.append(")");
        OWLOntologyDocumentSource input=new StringDocumentSource(buffer.toString());
        m_ontology=m_ontologyManager.loadOntologyFromOntologyDocument(input);
    }
    protected void loadDataSetWithAxioms(String axioms) throws Exception {
        loadOntologyWithAxioms(axioms);
        m_dataSet=new OWLOntologyDataSet(m_ontology, new HashMap<String, OWLOntology>());
        OWLOntologyGraph graph=m_dataSet.getDefaultGraph();
        graph.getReasoner().precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY);
    }
    protected void loadDataSetFromOntology(OWLOntology ontology) throws Exception {
        m_ontology=ontology;
        m_dataSet=new OWLOntologyDataSet(m_ontology, new HashMap<String, OWLOntology>());
        OWLOntologyGraph graph=m_dataSet.getDefaultGraph();
        graph.getReasoner().precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY);
    }
}