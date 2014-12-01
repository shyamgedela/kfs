/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.kuali.kfs.integration.cg.ContractsAndGrantsUnit;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.service.KfsService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.webService.InstitutionalUnitSoapService;
import org.kuali.kra.external.unit.service.InstitutionalUnitService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 05:29:28 HST 2010
 * Generated source version: 2.2.10
 *
 */

public class UnitServiceImpl implements ExternalizableBusinessObjectService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UnitServiceImpl.class);

    protected InstitutionalUnitService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        InstitutionalUnitService institutionalUnitService = (InstitutionalUnitService) GlobalResourceLoader.getService(KcConstants.Unit.SERVICE);

        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (institutionalUnitService == null) {
            LOG.warn("Couldn't get InstitutionalUnitService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");
            InstitutionalUnitSoapService ss =  null;
            try {
                ss = new InstitutionalUnitSoapService();
            }
            catch (MalformedURLException ex) {
                LOG.error("Could not intialize InstitutionalUnitSoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize InstitutionalUnitSoapService: " + ex.getMessage());
            }
            institutionalUnitService = ss.getInstitutionalUnitServicePort();
        }

        return institutionalUnitService;
    }

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        ContractsAndGrantsUnit unitDTO  = this.getWebService().getUnit((String)primaryKeys.get("unitNumber"));
        return unitDTO;
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        java.util.List <HashMapElement> hashMapList = new ArrayList<HashMapElement>();
        List lookupUnitsReturn = null;

        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            String val = (String) e.getValue();

            if ( KcConstants.Unit.KC_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
                HashMapElement hashMapElement = new HashMapElement();
                hashMapElement.setKey(key);
                hashMapElement.setValue(val);
                hashMapList.add(hashMapElement);
            }
        }
        try {
          lookupUnitsReturn  = this.getWebService().lookupUnits( hashMapList);
        } catch (WebServiceException ex) {
            GlobalVariablesExtractHelper.insertError(KcConstants.WEBSERVICE_UNREACHABLE, KfsService.getWebServiceServerName());
        }

        if (lookupUnitsReturn == null) {
            lookupUnitsReturn = new ArrayList();
        }
        return lookupUnitsReturn;
    }

 }
