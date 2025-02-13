package ur_os;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.util.ArrayList;
import java.util.List;

public class ProcessData {
    private final IntegerProperty pid;
    private final IntegerProperty ioTime;
    private final IntegerProperty timeOfArrival;  // Nueva propiedad para Time of Arrival
    private final IntegerProperty[] cpuTimes;

    public ProcessData(int pid, int cpuTimeCount) {
        this.pid = new SimpleIntegerProperty(pid);
        this.ioTime = new SimpleIntegerProperty(0);
        this.timeOfArrival = new SimpleIntegerProperty(0); // Inicializamos con 0 por defecto
        this.cpuTimes = new IntegerProperty[cpuTimeCount];
        for (int i = 0; i < cpuTimeCount; i++) {
            cpuTimes[i] = new SimpleIntegerProperty(0);
        }
    }

    public int getPid() { return pid.get(); }
    public IntegerProperty pidProperty() { return pid; }

    public int getIoTime() { return ioTime.get(); }
    public void setIoTime(int ioTime) { this.ioTime.set(ioTime); }
    public IntegerProperty ioTimeProperty() { return ioTime; }

    public int getTimeOfArrival() { return timeOfArrival.get(); }
    public void setTimeOfArrival(int timeOfArrival) { this.timeOfArrival.set(timeOfArrival); }
    public IntegerProperty timeOfArrivalProperty() { return timeOfArrival; }

    public IntegerProperty getCpuTimeProperty(int index) {
        return cpuTimes[index - 1];
    }

    public void setCpuTime(int index, int value) {
        cpuTimes[index - 1].set(value);
    }

    public List<Integer> getCpuTimes() {
        List<Integer> cpuTimeList = new ArrayList<>();
        for (IntegerProperty cpuTime : cpuTimes) {
            cpuTimeList.add(cpuTime.get());
        }
        return cpuTimeList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PID: " + getPid() + ", Arrival: " + getTimeOfArrival() + ", I/O: " + getIoTime() + ", CPU Times: ");
        for (IntegerProperty cpuTime : cpuTimes) {
            sb.append(cpuTime.get()).append(" ");
        }
        return sb.toString().trim();
    }

    
}