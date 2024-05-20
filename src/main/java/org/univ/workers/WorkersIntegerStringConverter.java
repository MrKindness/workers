package org.univ.workers;

import javafx.util.StringConverter;

public class WorkersIntegerStringConverter extends StringConverter<Integer> {
    public WorkersIntegerStringConverter() {
    }

    public Integer fromString(String var1) {
        try {
            if (var1 == null) {
                return -1;
            } else {
                var1 = var1.trim();
                return var1.isEmpty() ? -1 : Integer.parseInt(var1);
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public String toString(Integer var1) {
        return var1 == null ? "" : Integer.toString(var1);
    }
}
