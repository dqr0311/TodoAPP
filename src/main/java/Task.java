import java.util.Objects;

public class Task {
    public enum Status { TODO, DONE }

    private final int id;
    private String description;
    private Status status;

    public Task(int id, String description, Status status) {
        this.id = id;
        this.description = description;
        this.status = status;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public Status getStatus() { return status; }

    public void setDescription(String description) { this.description = description; }
    public void setStatus(Status status) { this.status = status; }

    // 用于保存到文件：id|status|description
    public String encode() {
        return id + "|" + status + "|" + description;
    }

    public static Task decode(String line) {
        // 跳过坏行：不抛异常，返回 null（调用处负责忽略）
        try {
            String[] parts = line.split("\\|", 3);
            if (parts.length < 3) return null;
            int id = Integer.parseInt(parts[0].trim());
            Status st = Status.valueOf(parts[1].trim());
            String desc = parts[2];
            return new Task(id, desc, st);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%-4d [%s] %s", id, status, description);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task)) return false;
        return ((Task) o).id == this.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}


