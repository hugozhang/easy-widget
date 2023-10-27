package me.about.widget.config.model;


import me.about.widget.config.enums.PropertyChangeType;

/**
 * 配置变更信息
 *
 * @author: hugo.zxh
 * @date: 2023/10/20 13:59
 */
public class ConfigChange {

    private final String propertyName;
    private String oldValue;
    private String newValue;
    private PropertyChangeType changeType;

    public ConfigChange(String propertyName, String oldValue, String newValue,
                        PropertyChangeType changeType) {
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changeType = changeType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public PropertyChangeType getChangeType() {
        return changeType;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setChangeType(PropertyChangeType changeType) {
        this.changeType = changeType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConfigChange{");
        sb.append("propertyName='").append(propertyName).append('\'');
        sb.append(", oldValue='").append(oldValue).append('\'');
        sb.append(", newValue='").append(newValue).append('\'');
        sb.append(", changeType=").append(changeType);
        sb.append('}');
        return sb.toString();
    }
}
