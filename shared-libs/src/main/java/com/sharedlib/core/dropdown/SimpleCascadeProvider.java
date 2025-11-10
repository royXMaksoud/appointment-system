// shared-lib: src/main/java/com/sharedlib/core/dropdown/SimpleCascadeProvider.java
package com.sharedlib.core.dropdown;

import com.sharedlib.core.web.dto.IdNameDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generic cascade dropdown provider.
 * - key(): unique key to register the provider.
 * - requiredParams(): the named parameters this provider expects (e.g., ["systemId"]).
 * - listChildren(params, lang): returns children for the given params (names must match requiredParams()).
 */
public interface SimpleCascadeProvider {

    /** Unique provider key (e.g., "access.system-sections-by-system"). */
    String key();

    /** Names of required parameters (e.g., ["systemId"] or ["systemSectionId"]). */
    Set<String> requiredParams();

    /**
     * Returns children for the given named params.
     * @param params map of required params (names must match requiredParams()).
     * @param lang optional language code (if the provider needs localization).
     */
    List<? extends IdNameDto<?>> listChildren(Map<String, Object> params, String lang);
}
