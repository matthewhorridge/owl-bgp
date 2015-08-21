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


package org.semanticweb.sparql.owlbgp.wgtests.profilechecker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.literals.LiteralVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;
import org.semanticweb.sparql.owlbgp.parser.OWLBGPParser;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterBlockTriples;
import com.hp.hpl.jena.sparql.engine.main.StageGenerator;

public class ProfileCheckerStageGenerator implements StageGenerator {
    public static final String LB = System.getProperty("line.separator"); 
    
    protected final StageGenerator m_above;
    
    public ProfileCheckerStageGenerator(StageGenerator original){
        m_above=original;
    }
    @Override
    public QueryIterator execute(BasicPattern pattern, QueryIterator input, ExecutionContext execCxt) {
        Graph activeGraph=execCxt.getActiveGraph();
        // Test to see if this is a graph we support.  
        if (!(activeGraph instanceof OWLOntologyGraph)) {
            // Not us - bounce up the StageGenerator chain
            return m_above.execute(pattern, input, execCxt);
        }
        OWLOntologyGraph ontologyGraph=(OWLOntologyGraph)activeGraph;
        
        // parsing into extended OWL objects
        Set<Axiom> queryAxiomTemplates=null;
        String bgp=arqPatternToBGP(pattern);
        OWLBGPParser parser=new OWLBGPParser(new StringReader(bgp));
        try {
            parser.loadDeclarations(ontologyGraph.getClassesInSignature(),ontologyGraph.getDatatypesInSignature(), ontologyGraph.getObjectPropertiesInSignature(), ontologyGraph.getDataPropertiesInSignature(), ontologyGraph.getAnnotationPropertiesInSignature(), ontologyGraph.getIndividualsInSignature());
            parser.parse();
            queryAxiomTemplates=parser.getParsedAxioms();
            OWLOntologyManager m=OWLManager.createOWLOntologyManager();
            OWLDataFactory df=m.getOWLDataFactory();
            ToOWLAPIConverter toOWLAPIConverter=new ToOWLAPIConverter(df);
            Map<Variable,Atomic> mapping=new HashMap<Variable,Atomic>();
            Set<OWLAxiom> boundAxioms=new HashSet<OWLAxiom>();
            for (Axiom ax : queryAxiomTemplates) {
                for (Variable var : ax.getVariablesInSignature()) {
                    Iterator<? extends Atomic> it=null;
                    if (var instanceof ClassVariable)
                        it=ontologyGraph.getClassesInSignature().iterator();
                    else if (var instanceof DataPropertyVariable)
                        it=ontologyGraph.getDataPropertiesInSignature().iterator();
                    else if (var instanceof DatatypeVariable)
                        it=ontologyGraph.getDatatypesInSignature().iterator();
                    else if (var instanceof IndividualVariable)
                        it=ontologyGraph.getIndividualsInSignature().iterator();
                    else if (var instanceof LiteralVariable)
                        it=ontologyGraph.getLiteralsInSignature().iterator();
                    else if (var instanceof ObjectPropertyVariable)
                        it=ontologyGraph.getObjectPropertiesInSignature().iterator();
                    else 
                        System.err.println("untyped variable");
                    
                    if (it!=null&&it.hasNext()) {
                        Atomic at=it.next();
                        while (at instanceof AnonymousIndividual && it.hasNext())
                            at=it.next();
                        mapping.put(var, at);
                    } else 
                        System.err.println("No mapping possible");
                }
                Axiom bound=(Axiom)ax.getBoundVersion(mapping);
                OWLAxiom owlBound=(OWLAxiom)bound.asOWLAPIObject(toOWLAPIConverter);
                boundAxioms.add(owlBound);
            }
            OWLOntology owlOnt=ontologyGraph.getOntology();
            m.addAxioms(owlOnt, boundAxioms);
            
            FileWriter fstream=new FileWriter("profileCheckerResults.txt", true);
            BufferedWriter out=new BufferedWriter(fstream);
            List<OWLProfile> okProfiles=new ArrayList<OWLProfile>();
            for (OWLProfile profile : ProfileChecker.owlProfiles) {
                OWLProfileReport profileReport=profile.checkOntology(owlOnt);
                if (profileReport.isInProfile())
                    okProfiles.add(profile);
                else {
                    out.write("Not ok "+profile.getName()+"\n");
                    for (OWLProfileViolation violation : profileReport.getViolations())
                        out.write(violation.toString()+"\n");
                }
                    
            }
            out.write("Ok Profiles: ");
            for (OWLProfile okProfile : okProfiles)
                out.write(okProfile.getName()+"  ");
            out.write("\n");
            out.close();
        } catch (Exception e) { //Parse
            System.err.println("ParseException: Probably types could not be disambuguated with this active graph. ");
        } 
        
        // splitting into weakly connected components
        return QueryIterBlockTriples.create(input,pattern,execCxt);
    }
    private String arqPatternToBGP(BasicPattern pattern) {
        StringBuffer buffer=new StringBuffer();
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
            return "<"+node+">";
        else if (node.isVariable()) {
            String name=node.getName();
            if (name.startsWith("?")) {
                name=name.substring(1);
                return "_:"+name;
            }
            return "?"+name;
        } else if (node.isLiteral()) {
                String datatypeURI=node.getLiteralDatatypeURI();
                String language=node.getLiteralLanguage();
                String lexicalForm=node.getLiteralLexicalForm();
                if (datatypeURI!=null&&datatypeURI!=""&&(language==null||language=="")) 
                    return "\""+lexicalForm+"\"^^<"+datatypeURI+">";
                else 
                    return node.toString();
            
        } else 
            return node.toString();
    }
}
