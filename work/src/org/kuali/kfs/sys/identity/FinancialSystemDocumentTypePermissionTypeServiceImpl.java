/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.identity;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class FinancialSystemDocumentTypePermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

    private RoleService roleService;
    
    public boolean performPermissionMatch(AttributeSet requestedDetails, KimPermissionInfo permission) {
        if(KFSConstants.SysKimConstants.CLAIM_ELECTRONIC_PAYMENT_PERMISSION_TEMPLATE_NAME.equals(permission.getTemplate().getName())){
            String documentTypeName = requestedDetails.get(KfsKimAttributes.DOCUMENT_TYPE_NAME);
            String qualifierDocumentTypeName = permission.getDetails().get(KfsKimAttributes.DOCUMENT_TYPE_NAME);
            if(documentTypeName==null && qualifierDocumentTypeName==null || 
                    (StringUtils.isNotEmpty(documentTypeName) && StringUtils.isNotEmpty(qualifierDocumentTypeName) 
                            && documentTypeName.equals(qualifierDocumentTypeName))){
                return true;
            }
        } 
        return super.performPermissionMatch(requestedDetails, permission);
    }
	
    /**
     * @return the RoleService
     */
     protected RoleService getRoleService(){
        if(roleService == null){
            roleService = KIMServiceLocator.getRoleService();
        }
        return roleService;
    }

}
