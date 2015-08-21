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
package org.semanticweb.HermiT;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.hierarchy.InstanceStatistics;
import org.semanticweb.HermiT.model.DescriptionGraph;
import org.semanticweb.HermiT.monitor.CountingMonitor;
import org.semanticweb.HermiT.monitor.TableauMonitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;


    public class OWLBGPHermiT extends Reasoner {
    	protected final InstanceStatistics m_instanceStatistics;

    /**
     * Creates a new reasoner object with standard parameters for blocking, expansion strategy etc. Then the given manager is used to find all required imports for the given ontology and the ontology with the imports is loaded into the reasoner and the data factory of the manager is used to create fresh concepts during the preprocessing phase if necessary.
     *
     * @param rootOntology
     *            - the ontology that should be loaded by the reasoner
     */
    public OWLBGPHermiT(OWLOntology rootOntology) {
        super(rootOntology);
        m_instanceStatistics=createInstanceStatistics();
        m_instanceStatistics.getObjectPropertyHierarchyDepth();
        m_instanceStatistics.getClassHierarchyDepth();
        //m_instanceStatistics=null;
    }

    /**
     * Creates a new reasoner object with the parameters for blocking, expansion strategy etc as specified in the given configuration object. A default configuration can be obtained by just passing new Configuration(). Then the given manager is used to find all required imports for the given ontology and the ontology with the imports is loaded into the reasoner and the data factory of the manager is used to create fresh concepts during the preprocessing phase if necessary.
     *
     * @param configuration
     *            - a configuration in which parameters can be defined such as the blocking strategy to be used etc
     * @param rootOntology
     *            - the ontology that should be loaded by the reasoner
     */
    public OWLBGPHermiT(Configuration configuration,OWLOntology rootOntology) {
        super(configuration,rootOntology);
        m_instanceStatistics=createInstanceStatistics();  
        //m_instanceStatistics=null;
        m_instanceStatistics.getObjectPropertyHierarchyDepth();
        m_instanceStatistics.getClassHierarchyDepth();
    }

    /**
     * Creates a new reasoner object loaded with the given ontology and the given description graphs. When creating the reasoner, the given configuration determines the parameters for blocking, expansion strategy etc. A default configuration can be obtained by just passing new Configuration(). Then the given manager is used to find all required imports for the given ontology and the ontology with the imports and the description graphs are loaded into the reasoner. The data factory of the manager is used to create fresh concepts during the preprocessing phase if necessary.
     *
     * @param configuration
     *            - a configuration in which parameters can be defined such as the blocking strategy to be used etc
     * @param rootOntology
     *            - the ontology that should be loaded by the reasoner
     * @param descriptionGraphs
     *            - a set of description graphs
     */
    public OWLBGPHermiT(Configuration configuration,OWLOntology rootOntology,Collection<DescriptionGraph> descriptionGraphs) {
    	super(configuration, rootOntology, descriptionGraphs);
    	m_instanceStatistics=getInstanceStatistics();
    	m_instanceStatistics.getObjectPropertyHierarchyDepth();
    	m_instanceStatistics.getClassHierarchyDepth();
    }

    public InstanceStatistics getInstanceStatistics() {
    	return m_instanceStatistics;
    }
    
    public boolean hasType(OWLNamedIndividual namedIndividual,OWLClassExpression type,boolean direct) {
    	int testsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
    	long t=System.currentTimeMillis();
        boolean result=super.hasType(namedIndividual, type, direct);
        int currentCost=(int)(System.currentTimeMillis()-t);
        if (((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests()!=testsno) {
        	////EntailmentLookUpCostEstimation.entailmentCost=(int)((CountingMonitor)m_configuration.monitor).getAverageTime();
        	//EntailmentLookUpCostEstimation.entailmentConceptCostTotal=EntailmentLookUpCostEstimation.entailmentConceptCostTotal+currentCost;
        	//EntailmentLookUpCostEstimation.entailmentConceptTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	//EntailmentLookUpCostEstimation.entailmentConceptCost=(int)(EntailmentLookUpCostEstimation.entailmentConceptCostTotal/EntailmentLookUpCostEstimation.entailmentConceptTestsno);
            EntailmentLookUpCostEstimation.entailmentCostTotal=EntailmentLookUpCostEstimation.entailmentCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.entailmentTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	EntailmentLookUpCostEstimation.entailmentCost=(int)(EntailmentLookUpCostEstimation.entailmentCostTotal/EntailmentLookUpCostEstimation.entailmentTestsno);
        }
/*        else {
        	EntailmentLookUpCostEstimation.lookUpCostTotal=EntailmentLookUpCostEstimation.lookUpCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.lookUpTestsno++;
        	EntailmentLookUpCostEstimation.lookUpCost=(int)(EntailmentLookUpCostEstimation.lookUpCostTotal/EntailmentLookUpCostEstimation.lookUpTestsno);
        
        }*/
        return result;
    }
    
    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression classExpression,boolean direct) {
    	int testsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
    	long t=System.currentTimeMillis();
    	NodeSet<OWLNamedIndividual> indSet=super.getInstances(classExpression, direct);
    	int currentCost=(int)(System.currentTimeMillis()-t);
    	if (((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests()!=testsno) {
        	////EntailmentLookUpCostEstimation.entailmentCost=(int)((CountingMonitor)m_configuration.monitor).getAverageTime();
        	//EntailmentLookUpCostEstimation.entailmentConceptCostTotal=EntailmentLookUpCostEstimation.entailmentConceptCostTotal+currentCost;
        	//EntailmentLookUpCostEstimation.entailmentConceptTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	//EntailmentLookUpCostEstimation.entailmentConceptCost=(int)(EntailmentLookUpCostEstimation.entailmentConceptCostTotal/EntailmentLookUpCostEstimation.entailmentConceptTestsno);
    	    EntailmentLookUpCostEstimation.entailmentCostTotal=EntailmentLookUpCostEstimation.entailmentCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.entailmentTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	EntailmentLookUpCostEstimation.entailmentCost=(int)(EntailmentLookUpCostEstimation.entailmentCostTotal/EntailmentLookUpCostEstimation.entailmentTestsno);
    	}
/*        else {
        	EntailmentLookUpCostEstimation.lookUpCostTotal=EntailmentLookUpCostEstimation.lookUpCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.lookUpTestsno++;
        	EntailmentLookUpCostEstimation.lookUpCost=(int)(EntailmentLookUpCostEstimation.lookUpCostTotal/EntailmentLookUpCostEstimation.lookUpTestsno);
        }*/
    	return indSet;
    }

    public NodeSet<OWLClass> getTypes(OWLNamedIndividual namedIndividual,boolean direct) {
    	int testsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
    	long t=System.currentTimeMillis();
    	NodeSet<OWLClass> clSet=super.getTypes(namedIndividual, direct);
    	int currentCost=(int)(System.currentTimeMillis()-t);
    	if (((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests()!=testsno) {
        	////EntailmentLookUpCostEstimation.entailmentCost=(int)((CountingMonitor)m_configuration.monitor).getAverageTime();
        	//EntailmentLookUpCostEstimation.entailmentConceptCostTotal=EntailmentLookUpCostEstimation.entailmentConceptCostTotal+currentCost;
        	//EntailmentLookUpCostEstimation.entailmentConceptTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	//EntailmentLookUpCostEstimation.entailmentConceptCost=(int)(EntailmentLookUpCostEstimation.entailmentConceptCostTotal/EntailmentLookUpCostEstimation.entailmentConceptTestsno);
    	    EntailmentLookUpCostEstimation.entailmentCostTotal=EntailmentLookUpCostEstimation.entailmentCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.entailmentTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	EntailmentLookUpCostEstimation.entailmentCost=(int)(EntailmentLookUpCostEstimation.entailmentCostTotal/EntailmentLookUpCostEstimation.entailmentTestsno);
    	}
/*        else {
        	EntailmentLookUpCostEstimation.lookUpCostTotal=EntailmentLookUpCostEstimation.lookUpCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.lookUpTestsno++;
        	EntailmentLookUpCostEstimation.lookUpCost=(int)(EntailmentLookUpCostEstimation.lookUpCostTotal/EntailmentLookUpCostEstimation.lookUpTestsno);
        }*/
    	return clSet;
    }
    
    public Map<OWLNamedIndividual,Set<OWLNamedIndividual>> getObjectPropertyInstances(OWLObjectProperty property) {
    	int testsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
    	long t=System.currentTimeMillis();
    	Map<OWLNamedIndividual, Set<OWLNamedIndividual>> indSet=super.getObjectPropertyInstances(property);
    	int currentCost=(int)(System.currentTimeMillis()-t);
    	if (((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests()!=testsno) {
        	////EntailmentLookUpCostEstimation.entailmentCost=(int)((CountingMonitor)m_configuration.monitor).getAverageTime();
    		//EntailmentLookUpCostEstimation.entailmentRoleCostTotal=EntailmentLookUpCostEstimation.entailmentRoleCostTotal+currentCost;
        	//EntailmentLookUpCostEstimation.entailmentRoleTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	//EntailmentLookUpCostEstimation.entailmentRoleCost=(int)(EntailmentLookUpCostEstimation.entailmentRoleCostTotal/EntailmentLookUpCostEstimation.entailmentRoleTestsno);
    	    EntailmentLookUpCostEstimation.entailmentCostTotal=EntailmentLookUpCostEstimation.entailmentCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.entailmentTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	EntailmentLookUpCostEstimation.entailmentCost=(int)(EntailmentLookUpCostEstimation.entailmentCostTotal/EntailmentLookUpCostEstimation.entailmentTestsno);
    	}
/*        else {
        	EntailmentLookUpCostEstimation.lookUpCostTotal=EntailmentLookUpCostEstimation.lookUpCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.lookUpTestsno++;
        	EntailmentLookUpCostEstimation.lookUpCost=(int)(EntailmentLookUpCostEstimation.lookUpCostTotal/EntailmentLookUpCostEstimation.lookUpTestsno);
        }*/
    	return indSet;
    }
    
    public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual namedIndividual,OWLObjectPropertyExpression propertyExpression) {
    	int testsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
    	long t=System.currentTimeMillis();
    	NodeSet<OWLNamedIndividual> indSet=super.getObjectPropertyValues(namedIndividual, propertyExpression);
    	long l=System.currentTimeMillis();
    	//int currentCost=(int)(System.currentTimeMillis()-t);
    	long currentCost1=l-t;
        int currentCost=(int)currentCost1;
    	if (((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests()!=testsno) {
        	////EntailmentLookUpCostEstimation.entailmentCost=(int)((CountingMonitor)m_configuration.monitor).getAverageTime();
    		//EntailmentLookUpCostEstimation.entailmentRoleCostTotal=EntailmentLookUpCostEstimation.entailmentRoleCostTotal+currentCost;
        	//EntailmentLookUpCostEstimation.entailmentRoleTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	//EntailmentLookUpCostEstimation.entailmentRoleCost=(int)(EntailmentLookUpCostEstimation.entailmentRoleCostTotal/EntailmentLookUpCostEstimation.entailmentRoleTestsno);
    	    EntailmentLookUpCostEstimation.entailmentCostTotal=EntailmentLookUpCostEstimation.entailmentCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.entailmentTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	EntailmentLookUpCostEstimation.entailmentCost=(int)(EntailmentLookUpCostEstimation.entailmentCostTotal/EntailmentLookUpCostEstimation.entailmentTestsno);
    	}
/*        else {
        	EntailmentLookUpCostEstimation.lookUpCostTotal=EntailmentLookUpCostEstimation.lookUpCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.lookUpTestsno++;
        	EntailmentLookUpCostEstimation.lookUpCost=(int)(EntailmentLookUpCostEstimation.lookUpCostTotal/EntailmentLookUpCostEstimation.lookUpTestsno);
        }*/
    	return indSet;
    }
    
    public boolean hasObjectPropertyRelationship(OWLNamedIndividual subject,OWLObjectPropertyExpression propertyExpression,OWLNamedIndividual object) {
    	int testsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
    	long t=System.currentTimeMillis();
        boolean result=super.hasObjectPropertyRelationship(subject, propertyExpression, object);
        int currentCost=(int)(System.currentTimeMillis()-t);
        if (((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests()!=testsno) {
        	////EntailmentLookUpCostEstimation.entailmentCost=(int)((CountingMonitor)m_configuration.monitor).getAverageTime();
        	//EntailmentLookUpCostEstimation.entailmentRoleCostTotal=EntailmentLookUpCostEstimation.entailmentRoleCostTotal+currentCost;
        	//EntailmentLookUpCostEstimation.entailmentRoleTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	//EntailmentLookUpCostEstimation.entailmentRoleCost=(int)(EntailmentLookUpCostEstimation.entailmentRoleCostTotal/EntailmentLookUpCostEstimation.entailmentRoleTestsno);
            EntailmentLookUpCostEstimation.entailmentCostTotal=EntailmentLookUpCostEstimation.entailmentCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.entailmentTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	EntailmentLookUpCostEstimation.entailmentCost=(int)(EntailmentLookUpCostEstimation.entailmentCostTotal/EntailmentLookUpCostEstimation.entailmentTestsno);
        }
/*        else {
        	EntailmentLookUpCostEstimation.lookUpCostTotal=EntailmentLookUpCostEstimation.lookUpCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.lookUpTestsno++;
        	EntailmentLookUpCostEstimation.lookUpCost=(int)(EntailmentLookUpCostEstimation.lookUpCostTotal/EntailmentLookUpCostEstimation.lookUpTestsno);
        }*/
        return result;
    }
    
    public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual namedIndividual,OWLDataProperty property) {
    	int testsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
    	long t=System.currentTimeMillis();
    	Set<OWLLiteral> litSet=super.getDataPropertyValues(namedIndividual, property);
    	int currentCost=(int)(System.currentTimeMillis()-t);
    	if (((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests()!=testsno) {
        	////EntailmentLookUpCostEstimation.entailmentCost=(int)((CountingMonitor)m_configuration.monitor).getAverageTime();
        	//EntailmentLookUpCostEstimation.entailmentRoleCostTotal=EntailmentLookUpCostEstimation.entailmentRoleCostTotal+currentCost;
        	//EntailmentLookUpCostEstimation.entailmentRoleTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	//EntailmentLookUpCostEstimation.entailmentRoleCost=(int)(EntailmentLookUpCostEstimation.entailmentRoleCostTotal/EntailmentLookUpCostEstimation.entailmentRoleTestsno);
    	    EntailmentLookUpCostEstimation.entailmentCostTotal=EntailmentLookUpCostEstimation.entailmentCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.entailmentTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
        	EntailmentLookUpCostEstimation.entailmentCost=(int)(EntailmentLookUpCostEstimation.entailmentCostTotal/EntailmentLookUpCostEstimation.entailmentTestsno);
    	}
/*        else {
        	EntailmentLookUpCostEstimation.lookUpCostTotal=EntailmentLookUpCostEstimation.lookUpCostTotal+currentCost;
        	EntailmentLookUpCostEstimation.lookUpTestsno++;
        	EntailmentLookUpCostEstimation.lookUpCost=(int)(EntailmentLookUpCostEstimation.lookUpCostTotal/EntailmentLookUpCostEstimation.lookUpTestsno);
        }*/
    	return litSet;
    }
    
    // statistics for cost-based query axiom ordering

    public InstanceStatistics createInstanceStatistics() {
    	if (m_instanceManager==null) {
    		long t=System.currentTimeMillis();
    		initialiseClassInstanceManager(); 
    		initialisePropertiesInstanceManager();
    		int currentCost=(int)(System.currentTimeMillis()-t);
    		//EntailmentLookUpCostEstimation.entailmentConceptCostTotal=EntailmentLookUpCostEstimation.entailmentConceptCostTotal+currentCost;
    		EntailmentLookUpCostEstimation.entailmentCostTotal=EntailmentLookUpCostEstimation.entailmentCostTotal+currentCost;
    		TableauMonitor monitor=m_configuration.monitor;
    		if (monitor != null && monitor instanceof CountingMonitor) {
        	   //EntailmentLookUpCostEstimation.entailmentConceptTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
    		   EntailmentLookUpCostEstimation.entailmentTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
    		}
    		//if (EntailmentLookUpCostEstimation.entailmentConceptTestsno > 0)
        	//   EntailmentLookUpCostEstimation.entailmentConceptCost=(int)(EntailmentLookUpCostEstimation.entailmentConceptCostTotal/EntailmentLookUpCostEstimation.entailmentConceptTestsno);
    		if (EntailmentLookUpCostEstimation.entailmentTestsno > 0)
        	   EntailmentLookUpCostEstimation.entailmentCost=(int)(EntailmentLookUpCostEstimation.entailmentCostTotal/EntailmentLookUpCostEstimation.entailmentTestsno);
    		else 
    		   //EntailmentLookUpCostEstimation.entailmentConceptCost=1; // random value, but better then division by zero error
    		   EntailmentLookUpCostEstimation.entailmentCost=1; // random value, but better then division by zero error
    		/*t=System.currentTimeMillis();
    		initialisePropertiesInstanceManager();
    		currentCost=(int)(System.currentTimeMillis()-t);
    		EntailmentLookUpCostEstimation.entailmentRoleCostTotal=EntailmentLookUpCostEstimation.entailmentRoleCostTotal+currentCost;
    		if (monitor != null && monitor instanceof CountingMonitor) {
        	   EntailmentLookUpCostEstimation.entailmentRoleTestsno=((CountingMonitor)m_configuration.monitor).getOverallNumberOfTests();
    		}
    		if (EntailmentLookUpCostEstimation.entailmentRoleTestsno > 0)
        	   EntailmentLookUpCostEstimation.entailmentRoleCost=(int)(EntailmentLookUpCostEstimation.entailmentRoleCostTotal/EntailmentLookUpCostEstimation.entailmentRoleTestsno);
    		else 
    		   EntailmentLookUpCostEstimation.entailmentRoleCost=1; // random value, but better then division by zero error
            */
    	} else {
    		
    	}
        assert m_instanceManager!=null;
        assert m_instanceManager.areClassesInitialised();
        assert m_instanceManager.arePropertiesInitialised();
        return new InstanceStatistics(m_instanceManager, this);
    }
    
    public boolean isInstanceManagerInitialised() {
    	return m_instanceManager==null;
    }
}
