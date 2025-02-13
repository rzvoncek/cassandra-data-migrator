package com.datastax.cdm.feature;

import com.datastax.cdm.properties.IPropertyHelper;
import com.datastax.cdm.properties.KnownProperties;
import com.datastax.cdm.schema.CqlTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OriginFilterCondition extends AbstractFeature  {
    public final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private String filterCondition;

    @Override
    public boolean loadProperties(IPropertyHelper propertyHelper) {
        this.filterCondition = propertyHelper.getString(KnownProperties.FILTER_CQL_WHERE_CONDITION);
        isValid = validateProperties();
        isLoaded =true;
        isEnabled=(isValid && null != filterCondition && !filterCondition.isEmpty());
        return isValid;
    }

    @Override
    protected boolean validateProperties() {
        isValid = true;
        if (null == filterCondition || filterCondition.isEmpty()) return isValid;

        String trimmedFilter = filterCondition.trim();
        if (trimmedFilter.isEmpty()) {
            logger.error("Provided filter contains only whitespace characters");
            isValid = false;
        }
        return isValid;
    }

    @Override
    public boolean initializeAndValidate(CqlTable originTable, CqlTable targetTable) {
        isValid = true;
        if (!validateProperties()) {
            isEnabled = false;
            return false;
        }
        if (null == filterCondition || filterCondition.isEmpty()) {
            isEnabled = false;
            return isValid;
        }

        if (!filterCondition.toUpperCase().startsWith("AND")) {
            filterCondition = " AND " + filterCondition;
        }

        // TODO: in future, we may want to validate the condition against the origin table via initializeAndValidate
        logger.info("Feature {} is {}", this.getClass().getSimpleName(), isEnabled?"enabled":"disabled");
        return isValid;
    }

    public String getFilterCondition() { return null == filterCondition ? "" : filterCondition; }
}
