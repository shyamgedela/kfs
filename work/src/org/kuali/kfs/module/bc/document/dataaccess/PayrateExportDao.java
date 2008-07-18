/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.document.dataaccess;

import java.util.List;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPayRateHolding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;

public interface PayrateExportDao {
    
    /**
     * Returns the list of PendingBudgetConstructionAppointmentFunding for this BudgetConstructionPayRateHolding
     * 
     * @param holdingRecord
     * @param budgetYear
     * @return
     */
    List<PendingBudgetConstructionAppointmentFunding> getFundingRecords(Integer budgetYear, String positionUnionCode);
    
    /**
     * Checks if this position union code exsits in the database
     * 
     * @param positionUnionCode
     * @return
     */
    public boolean isValidPositionUnionCode(String positionUnionCode);
}
