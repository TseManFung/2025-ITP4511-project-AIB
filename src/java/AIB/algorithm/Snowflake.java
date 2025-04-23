package AIB.algorithm;

// use long id = SnowflakeSingleton.getInstance().nextId();
public class Snowflake {

    private final long epoch = 1735689600000L; // 2025-01-01 00:00:00 UTC

    private final long sequenceBits = 12L;
    private final long machineIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxMachineId = ~(-1L << machineIdBits);
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);

    private final long machineIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + machineIdBits;
    private final long timestampShift = sequenceBits + machineIdBits + datacenterIdBits;

    private final long sequenceMask = ~(-1L << sequenceBits);

    private final long datacenterId;
    private final long machineId;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public Snowflake(long datacenterId, long machineId) {
        if (machineId > maxMachineId || machineId < 0) {
            throw new IllegalArgumentException("Machine ID invalid");
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID invalid");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis() - epoch;

        if (timestamp < 0) {
            throw new RuntimeException("Invalid system time before epoch");
        }
        if (timestamp > ((1L << 41) - 1)) {
            throw new RuntimeException("Timestamp overflow (69 years expired)");
        }

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return (timestamp << timestampShift)
                | (datacenterId << datacenterIdShift)
                | (machineId << machineIdShift)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
