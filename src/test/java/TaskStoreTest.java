import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TaskStoreTest {

    private TaskStore store;
    private File testFile;

    @BeforeEach
    public void setUp(@TempDir Path tempDir) {
        testFile = tempDir.resolve("test_tasks.txt").toFile();
        store = new TaskStore(testFile.getAbsolutePath());
    }

    @AfterEach
    public void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    @DisplayName("测试添加任务")
    public void testAddTask() {
        Task task = store.add("测试任务");
        assertNotNull(task);
        assertEquals(1, task.getId());
        assertEquals("测试任务", task.getDescription());
        assertEquals(Task.Status.TODO, task.getStatus());
    }

    @Test
    @DisplayName("测试添加多个任务 - ID 自动递增")
    public void testAddMultipleTasks() {
        Task task1 = store.add("任务1");
        Task task2 = store.add("任务2");
        Task task3 = store.add("任务3");
        
        assertEquals(1, task1.getId());
        assertEquals(2, task2.getId());
        assertEquals(3, task3.getId());
    }

    @Test
    @DisplayName("测试添加空描述任务")
    public void testAddEmptyDescription() {
        Task task = store.add("");
        assertNotNull(task);
        assertEquals("", task.getDescription());
    }

    @Test
    @DisplayName("测试 listAll - 空列表")
    public void testListAllEmpty() {
        List<Task> tasks = store.listAll();
        assertTrue(tasks.isEmpty());
    }

    @Test
    @DisplayName("测试 listAll - 多个任务")
    public void testListAllMultiple() {
        store.add("任务1");
        store.add("任务2");
        store.add("任务3");
        
        List<Task> tasks = store.listAll();
        assertEquals(3, tasks.size());
    }

    @Test
    @DisplayName("测试 listAll - 返回新列表（防御性拷贝）")
    public void testListAllReturnsNewList() {
        store.add("任务1");
        List<Task> tasks1 = store.listAll();
        List<Task> tasks2 = store.listAll();
        
        assertNotSame(tasks1, tasks2, "应该返回新的列表实例");
        assertEquals(tasks1.size(), tasks2.size());
    }

    @Test
    @DisplayName("测试 listByStatus - TODO")
    public void testListByStatusTODO() {
        Task task1 = store.add("任务1");
        Task task2 = store.add("任务2");
        store.markStatus(task2.getId(), Task.Status.DONE);
        Task task3 = store.add("任务3");
        
        List<Task> todos = store.listByStatus(Task.Status.TODO);
        assertEquals(2, todos.size());
        assertTrue(todos.contains(task1));
        assertTrue(todos.contains(task3));
        assertFalse(todos.contains(task2));
    }

    @Test
    @DisplayName("测试 listByStatus - DONE")
    public void testListByStatusDONE() {
        Task task1 = store.add("任务1");
        Task task2 = store.add("任务2");
        store.markStatus(task2.getId(), Task.Status.DONE);
        Task task3 = store.add("任务3");
        store.markStatus(task3.getId(), Task.Status.DONE);
        
        List<Task> dones = store.listByStatus(Task.Status.DONE);
        assertEquals(2, dones.size());
        assertTrue(dones.contains(task2));
        assertTrue(dones.contains(task3));
        assertFalse(dones.contains(task1));
    }

    @Test
    @DisplayName("测试 listByStatus - 空结果")
    public void testListByStatusEmpty() {
        store.add("任务1");
        List<Task> dones = store.listByStatus(Task.Status.DONE);
        assertTrue(dones.isEmpty());
    }

    @Test
    @DisplayName("测试 findById - 存在")
    public void testFindByIdExists() {
        Task task = store.add("测试任务");
        Task found = store.findById(task.getId());
        assertNotNull(found);
        assertEquals(task.getId(), found.getId());
        assertEquals(task.getDescription(), found.getDescription());
    }

    @Test
    @DisplayName("测试 findById - 不存在")
    public void testFindByIdNotExists() {
        store.add("任务1");
        Task found = store.findById(999);
        assertNull(found);
    }

    @Test
    @DisplayName("测试 findById - 负数 ID")
    public void testFindByIdNegative() {
        Task found = store.findById(-1);
        assertNull(found);
    }

    @Test
    @DisplayName("测试 findById - 零 ID")
    public void testFindByIdZero() {
        Task found = store.findById(0);
        assertNull(found);
    }

    @Test
    @DisplayName("测试 delete - 存在")
    public void testDeleteExists() {
        Task task = store.add("要删除的任务");
        boolean deleted = store.delete(task.getId());
        assertTrue(deleted);
        assertNull(store.findById(task.getId()));
        assertTrue(store.listAll().isEmpty());
    }

    @Test
    @DisplayName("测试 delete - 不存在")
    public void testDeleteNotExists() {
        store.add("任务1");
        boolean deleted = store.delete(999);
        assertFalse(deleted);
        assertEquals(1, store.listAll().size());
    }

    @Test
    @DisplayName("测试 delete - 删除后 ID 不重用")
    public void testDeleteIdNotReused() {
        Task task1 = store.add("任务1");
        Task task2 = store.add("任务2");
        store.delete(task1.getId());
        Task task3 = store.add("任务3");
        
        assertEquals(3, task3.getId(), "新任务应该使用下一个 ID，而不是重用已删除的 ID");
    }

    @Test
    @DisplayName("测试 markStatus - 存在")
    public void testMarkStatusExists() {
        Task task = store.add("任务");
        boolean updated = store.markStatus(task.getId(), Task.Status.DONE);
        assertTrue(updated);
        assertEquals(Task.Status.DONE, task.getStatus());
    }

    @Test
    @DisplayName("测试 markStatus - 不存在")
    public void testMarkStatusNotExists() {
        store.add("任务1");
        boolean updated = store.markStatus(999, Task.Status.DONE);
        assertFalse(updated);
    }

    @Test
    @DisplayName("测试 markStatus - 状态切换")
    public void testMarkStatusToggle() {
        Task task = store.add("任务");
        store.markStatus(task.getId(), Task.Status.DONE);
        assertEquals(Task.Status.DONE, task.getStatus());
        
        store.markStatus(task.getId(), Task.Status.TODO);
        assertEquals(Task.Status.TODO, task.getStatus());
    }

    @Test
    @DisplayName("测试 load - 文件不存在")
    public void testLoadFileNotExists() {
        // 确保文件不存在
        if (testFile.exists()) {
            testFile.delete();
        }
        store.load();
        assertTrue(store.listAll().isEmpty(), "文件不存在时应加载空列表");
    }

    @Test
    @DisplayName("测试 load - 空文件")
    public void testLoadEmptyFile() throws IOException {
        testFile.createNewFile();
        store.load();
        assertTrue(store.listAll().isEmpty());
        assertEquals(1, getNextId(store), "空文件后 nextId 应该是 1");
    }

    @Test
    @DisplayName("测试 load - 正常数据")
    public void testLoadNormalData() throws IOException {
        Files.write(testFile.toPath(), 
            ("1|TODO|任务1\n" +
             "2|DONE|任务2\n" +
             "3|TODO|任务3\n").getBytes("UTF-8"));
        
        store.load();
        List<Task> tasks = store.listAll();
        assertEquals(3, tasks.size());
        
        Task task1 = store.findById(1);
        assertNotNull(task1);
        assertEquals("任务1", task1.getDescription());
        assertEquals(Task.Status.TODO, task1.getStatus());
        
        Task task2 = store.findById(2);
        assertNotNull(task2);
        assertEquals(Task.Status.DONE, task2.getStatus());
    }

    @Test
    @DisplayName("测试 load - 包含无效行")
    public void testLoadWithInvalidLines() throws IOException {
        Files.write(testFile.toPath(), 
            ("1|TODO|任务1\n" +
             "无效行\n" +
             "2|DONE|任务2\n" +
             "3|INVALID|任务3\n" +
             "4|TODO|任务4\n").getBytes("UTF-8"));
        
        store.load();
        List<Task> tasks = store.listAll();
        // 应该只加载有效的行：1, 2, 4
        assertEquals(3, tasks.size());
        assertNotNull(store.findById(1));
        assertNotNull(store.findById(2));
        assertNotNull(store.findById(4));
        assertNull(store.findById(3));
    }

    @Test
    @DisplayName("测试 load - nextId 正确设置")
    public void testLoadNextId() throws IOException {
        Files.write(testFile.toPath(), 
            ("1|TODO|任务1\n" +
             "5|DONE|任务5\n" +
             "10|TODO|任务10\n").getBytes("UTF-8"));
        
        store.load();
        Task newTask = store.add("新任务");
        assertEquals(11, newTask.getId(), "nextId 应该是最大 ID + 1");
    }

    @Test
    @DisplayName("测试 load - 包含分隔符的任务描述")
    public void testLoadWithSeparatorInDescription() throws IOException {
        Files.write(testFile.toPath(), 
            ("1|TODO|任务|包含|分隔符\n").getBytes("UTF-8"));
        
        store.load();
        Task task = store.findById(1);
        assertNotNull(task);
        assertEquals("任务|包含|分隔符", task.getDescription());
    }

    @Test
    @DisplayName("测试 save - 正常保存")
    public void testSaveNormal() throws IOException {
        store.add("任务1");
        store.add("任务2");
        store.markStatus(2, Task.Status.DONE);
        
        store.save();
        assertTrue(testFile.exists());
        
        // 重新加载验证
        TaskStore newStore = new TaskStore(testFile.getAbsolutePath());
        newStore.load();
        List<Task> tasks = newStore.listAll();
        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("测试 save - 空列表")
    public void testSaveEmpty() throws IOException {
        store.save();
        assertTrue(testFile.exists());
        
        List<String> lines = Files.readAllLines(testFile.toPath());
        assertTrue(lines.isEmpty(), "空列表应保存为空文件");
    }

    @Test
    @DisplayName("测试 save 和 load 往返")
    public void testSaveLoadRoundTrip() throws IOException {
        Task task1 = store.add("任务1");
        Task task2 = store.add("任务2");
        store.markStatus(task2.getId(), Task.Status.DONE);
        Task task3 = store.add("任务|包含|分隔符");
        
        store.save();
        
        TaskStore newStore = new TaskStore(testFile.getAbsolutePath());
        newStore.load();
        
        List<Task> tasks = newStore.listAll();
        assertEquals(3, tasks.size());
        
        Task loaded1 = newStore.findById(1);
        assertEquals("任务1", loaded1.getDescription());
        assertEquals(Task.Status.TODO, loaded1.getStatus());
        
        Task loaded2 = newStore.findById(2);
        assertEquals(Task.Status.DONE, loaded2.getStatus());
        
        Task loaded3 = newStore.findById(3);
        assertEquals("任务|包含|分隔符", loaded3.getDescription());
    }

    @Test
    @DisplayName("测试 save - 覆盖写入")
    public void testSaveOverwrite() throws IOException {
        // 第一次保存
        store.add("旧任务");
        store.save();
        
        // 修改后再次保存
        TaskStore newStore = new TaskStore(testFile.getAbsolutePath());
        newStore.load();
        newStore.delete(1);
        newStore.add("新任务");
        newStore.save();
        
        // 验证覆盖
        TaskStore finalStore = new TaskStore(testFile.getAbsolutePath());
        finalStore.load();
        List<Task> tasks = finalStore.listAll();
        assertEquals(1, tasks.size());
        assertEquals("新任务", tasks.get(0).getDescription());
    }

    @Test
    @DisplayName("测试 load - UTF-8 编码支持中文")
    public void testLoadUTF8Chinese() throws IOException {
        Files.write(testFile.toPath(), 
            ("1|TODO|中文任务测试\n" +
             "2|DONE|完成的任务✓\n").getBytes("UTF-8"));
        
        store.load();
        Task task1 = store.findById(1);
        assertEquals("中文任务测试", task1.getDescription());
        
        Task task2 = store.findById(2);
        assertEquals("完成的任务✓", task2.getDescription());
    }

    @Test
    @DisplayName("测试 load - 处理文件读取异常（模拟）")
    public void testLoadIOException() {
        // 创建一个无法读取的文件（通过使用目录路径）
        File invalidFile = new File("/invalid/path/tasks.txt");
        TaskStore invalidStore = new TaskStore(invalidFile.getAbsolutePath());
        
        // 应该不会抛出异常，而是静默处理
        assertDoesNotThrow(() -> invalidStore.load());
        assertTrue(invalidStore.listAll().isEmpty());
    }

    @Test
    @DisplayName("测试多个操作组合")
    public void testMultipleOperations() {
        // 添加任务
        Task task1 = store.add("任务1");
        Task task2 = store.add("任务2");
        Task task3 = store.add("任务3");
        
        // 标记状态
        store.markStatus(task2.getId(), Task.Status.DONE);
        
        // 删除任务
        store.delete(task1.getId());
        
        // 验证最终状态
        List<Task> all = store.listAll();
        assertEquals(2, all.size());
        
        List<Task> todos = store.listByStatus(Task.Status.TODO);
        assertEquals(1, todos.size());
        assertEquals(task3.getId(), todos.get(0).getId());
        
        List<Task> dones = store.listByStatus(Task.Status.DONE);
        assertEquals(1, dones.size());
        assertEquals(task2.getId(), dones.get(0).getId());
    }

    @Test
    @DisplayName("测试 listByStatus - 返回新列表（防御性拷贝）")
    public void testListByStatusReturnsNewList() {
        store.add("任务1");
        List<Task> list1 = store.listByStatus(Task.Status.TODO);
        List<Task> list2 = store.listByStatus(Task.Status.TODO);
        assertNotSame(list1, list2);
    }

    // 辅助方法：通过添加任务来推断 nextId
    private int getNextId(TaskStore store) {
        Task task = store.add("test");
        int id = task.getId();
        store.delete(id);
        return id;
    }
}

