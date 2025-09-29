package io.github.adainish.wynautrankup.season;

public class Condition
{
    private String type; // e.g., "elo", "streak", "typeWin"
    private int minValue;
    private int maxValue;
    private String value; // For string-based conditions

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
