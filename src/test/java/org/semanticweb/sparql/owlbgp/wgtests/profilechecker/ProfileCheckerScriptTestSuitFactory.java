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

package org.semanticweb.sparql.owlbgp.wgtests.profilechecker;

import org.semanticweb.sparql.owlbgp.wgtests.OWLBGPScriptTestSuitFactory;

import junit.framework.Test;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.junit.OWLBGPQueryTest;

public class ProfileCheckerScriptTestSuitFactory extends OWLBGPScriptTestSuitFactory {
    
    @Override
    public Test makeTest(Resource manifest,Resource entry,String testName,Resource action,Resource result) {
        Test t=super.makeTest(manifest, entry, testName, action, result);
        if (t!=null && t instanceof OWLBGPQueryTest) 
            return new ProfileCheckerQueryTest(testName,results,fileManager,((OWLBGPQueryTest)t).getTestItem());
        return null;
    }

}
