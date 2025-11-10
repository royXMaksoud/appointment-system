package com.sharedlib.core.dropdown;

import com.sharedlib.core.web.dto.IdNameDto;
import java.util.List;

public interface SimpleListProvider {
    String key();
    List<? extends IdNameDto<?>> listAll(String lang);  // <-- changed
}
