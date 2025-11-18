import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TaskStore {
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;
    private final File dataFile;

    public TaskStore(String filePath) {
        this.dataFile = new File(filePath);
    }

    // 启动时加载（坏行跳过；确保 nextId 递增唯一）
    public void load() {
        if (!dataFile.exists()) return; // 文件不存在则空列表
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(dataFile), StandardCharsets.UTF_8))) {
            String line;
            int maxId = 0;
            while ((line = br.readLine()) != null) {
                Task t = Task.decode(line);
                if (t != null) {
                    tasks.add(t);
                    if (t.getId() > maxId) maxId = t.getId();
                }
            }
            nextId = maxId + 1;
        } catch (IOException e) {
            System.out.println("读取数据文件出错，但程序继续运行。");
        }
    }

    // 退出时保存（覆盖写入）
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(dataFile, false), StandardCharsets.UTF_8))) {
            for (Task t : tasks) {
                bw.write(t.encode());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("保存失败：" + e.getMessage());
        }
    }

    public Task add(String description) {
        Task t = new Task(nextId++, description, Task.Status.TODO);
        tasks.add(t);
        return t;
    }

    public List<Task> listAll() { return new ArrayList<>(tasks); }

    public List<Task> listByStatus(Task.Status status) {
        List<Task> out = new ArrayList<>();
        for (Task t : tasks) if (t.getStatus() == status) out.add(t);
        return out;
    }

    public Task findById(int id) {
        for (Task t : tasks) if (t.getId() == id) return t;
        return null;
    }

    public boolean delete(int id) {
        Task t = findById(id);
        if (t == null) return false;
        return tasks.remove(t);
    }

    public boolean markStatus(int id, Task.Status status) {
        Task t = findById(id);
        if (t == null) return false;
        t.setStatus(status);
        return true;
    }
}


