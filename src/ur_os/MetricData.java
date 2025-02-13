package ur_os;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class MetricData {
    private final StringProperty metricName;
    private final StringProperty metricValue;

    public MetricData(String metricName, String metricValue) {
        this.metricName = new SimpleStringProperty(metricName);
        this.metricValue = new SimpleStringProperty(metricValue);
    }

    public StringProperty metricNameProperty() {
        return metricName;
    }

    public StringProperty metricValueProperty() {
        return metricValue;
    }

    public String getMetricName() {
        return metricName.get();
    }

    public void setMetricName(String metricName) {
        this.metricName.set(metricName);
    }

    public String getMetricValue() {
        return metricValue.get();
    }

    public void setMetricValue(String metricValue) {
        this.metricValue.set(metricValue);
    }
}