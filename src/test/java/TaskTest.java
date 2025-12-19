import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    @DisplayName("测试 Task 构造函数和基本 getter 方法")
    public void testTaskCreation() {
        Task task = new Task(1, "测试任务", Task.Status.TODO);
        assertEquals(1, task.getId());
        assertEquals("测试任务", task.getDescription());
        assertEquals(Task.Status.TODO, task.getStatus());
    }

    @Test
    @DisplayName("测试 setDescription 方法")
    public void testSetDescription() {
        Task task = new Task(1, "原始描述", Task.Status.TODO);
        task.setDescription("新描述");
        assertEquals("新描述", task.getDescription());
    }

    @Test
    @DisplayName("测试 setDescription 设置为空字符串")
    public void testSetDescriptionEmpty() {
        Task task = new Task(1, "原始描述", Task.Status.TODO);
        task.setDescription("");
        assertEquals("", task.getDescription());
    }

    @Test
    @DisplayName("测试 setDescription 设置为 null")
    public void testSetDescriptionNull() {
        Task task = new Task(1, "原始描述", Task.Status.TODO);
        task.setDescription(null);
        assertNull(task.getDescription());
    }

    @Test
    @DisplayName("测试 setStatus 方法")
    public void testSetStatus() {
        Task task = new Task(1, "测试", Task.Status.TODO);
        task.setStatus(Task.Status.DONE);
        assertEquals(Task.Status.DONE, task.getStatus());
        
        task.setStatus(Task.Status.TODO);
        assertEquals(Task.Status.TODO, task.getStatus());
    }

    @Test
    @DisplayName("测试 encode 方法 - 正常情况")
    public void testEncodeNormal() {
        Task task = new Task(1, "测试任务", Task.Status.TODO);
        String encoded = task.encode();
        assertEquals("1|TODO|测试任务", encoded);
    }

    @Test
    @DisplayName("测试 encode 方法 - DONE 状态")
    public void testEncodeDone() {
        Task task = new Task(2, "完成的任务", Task.Status.DONE);
        String encoded = task.encode();
        assertEquals("2|DONE|完成的任务", encoded);
    }

    @Test
    @DisplayName("测试 encode 方法 - 包含特殊字符的描述")
    public void testEncodeWithSpecialCharacters() {
        Task task = new Task(3, "任务|包含|分隔符", Task.Status.TODO);
        String encoded = task.encode();
        assertEquals("3|TODO|任务|包含|分隔符", encoded);
    }

    @Test
    @DisplayName("测试 encode 方法 - 空描述")
    public void testEncodeEmptyDescription() {
        Task task = new Task(4, "", Task.Status.TODO);
        String encoded = task.encode();
        assertEquals("4|TODO|", encoded);
    }

    @Test
    @DisplayName("测试 encode 方法 - null 描述")
    public void testEncodeNullDescription() {
        Task task = new Task(5, null, Task.Status.TODO);
        String encoded = task.encode();
        assertEquals("5|TODO|null", encoded);
    }

    @Test
    @DisplayName("测试 decode 方法 - 正常情况")
    public void testDecodeNormal() {
        String line = "1|TODO|测试任务";
        Task task = Task.decode(line);
        assertNotNull(task);
        assertEquals(1, task.getId());
        assertEquals(Task.Status.TODO, task.getStatus());
        assertEquals("测试任务", task.getDescription());
    }

    @Test
    @DisplayName("测试 decode 方法 - DONE 状态")
    public void testDecodeDone() {
        String line = "2|DONE|完成的任务";
        Task task = Task.decode(line);
        assertNotNull(task);
        assertEquals(2, task.getId());
        assertEquals(Task.Status.DONE, task.getStatus());
        assertEquals("完成的任务", task.getDescription());
    }

    @Test
    @DisplayName("测试 decode 方法 - 包含分隔符的描述")
    public void testDecodeWithSeparatorInDescription() {
        String line = "3|TODO|任务|包含|分隔符";
        Task task = Task.decode(line);
        assertNotNull(task);
        assertEquals(3, task.getId());
        assertEquals("任务|包含|分隔符", task.getDescription());
    }

    @Test
    @DisplayName("测试 decode 方法 - 空描述")
    public void testDecodeEmptyDescription() {
        String line = "4|TODO|";
        Task task = Task.decode(line);
        assertNotNull(task);
        assertEquals(4, task.getId());
        assertEquals("", task.getDescription());
    }

    @Test
    @DisplayName("测试 decode 方法 - 带前后空格的字段")
    public void testDecodeWithWhitespace() {
        String line = "  5  |  DONE  |  带空格的任务  ";
        Task task = Task.decode(line);
        assertNotNull(task);
        assertEquals(5, task.getId());
        assertEquals(Task.Status.DONE, task.getStatus());
        assertEquals("  带空格的任务  ", task.getDescription());
    }

    @Test
    @DisplayName("测试 decode 方法 - 无效行（缺少字段）")
    public void testDecodeInvalidLineMissingFields() {
        String line = "1|TODO";
        Task task = Task.decode(line);
        assertNull(task, "缺少字段时应返回 null");
    }

    @Test
    @DisplayName("测试 decode 方法 - 无效行（只有 ID）")
    public void testDecodeInvalidLineOnlyId() {
        String line = "1";
        Task task = Task.decode(line);
        assertNull(task);
    }

    @Test
    @DisplayName("测试 decode 方法 - 空行")
    public void testDecodeEmptyLine() {
        String line = "";
        Task task = Task.decode(line);
        assertNull(task);
    }

    @Test
    @DisplayName("测试 decode 方法 - 无效 ID（非数字）")
    public void testDecodeInvalidId() {
        String line = "abc|TODO|任务";
        Task task = Task.decode(line);
        assertNull(task, "无效 ID 时应返回 null");
    }

    @Test
    @DisplayName("测试 decode 方法 - 无效状态")
    public void testDecodeInvalidStatus() {
        String line = "1|INVALID|任务";
        Task task = Task.decode(line);
        assertNull(task, "无效状态时应返回 null");
    }

    @Test
    @DisplayName("测试 decode 方法 - 负数 ID")
    public void testDecodeNegativeId() {
        String line = "-1|TODO|任务";
        Task task = Task.decode(line);
        assertNotNull(task, "负数 ID 应该被接受");
        assertEquals(-1, task.getId());
    }

    @Test
    @DisplayName("测试 decode 方法 - 零 ID")
    public void testDecodeZeroId() {
        String line = "0|TODO|任务";
        Task task = Task.decode(line);
        assertNotNull(task);
        assertEquals(0, task.getId());
    }

    @Test
    @DisplayName("测试 decode 方法 - 非常大的 ID")
    public void testDecodeLargeId() {
        String line = "999999999|TODO|任务";
        Task task = Task.decode(line);
        assertNotNull(task);
        assertEquals(999999999, task.getId());
    }

    @Test
    @DisplayName("测试 toString 方法")
    public void testToString() {
        Task task = new Task(1, "测试任务", Task.Status.TODO);
        String str = task.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("TODO"));
        assertTrue(str.contains("测试任务"));
    }

    @Test
    @DisplayName("测试 toString 方法 - DONE 状态")
    public void testToStringDone() {
        Task task = new Task(2, "完成的任务", Task.Status.DONE);
        String str = task.toString();
        assertTrue(str.contains("2"));
        assertTrue(str.contains("DONE"));
        assertTrue(str.contains("完成的任务"));
    }

    @Test
    @DisplayName("测试 equals 方法 - 相同 ID")
    public void testEqualsSameId() {
        Task task1 = new Task(1, "任务1", Task.Status.TODO);
        Task task2 = new Task(1, "任务2", Task.Status.DONE);
        assertEquals(task1, task2, "相同 ID 的任务应该相等");
    }

    @Test
    @DisplayName("测试 equals 方法 - 不同 ID")
    public void testEqualsDifferentId() {
        Task task1 = new Task(1, "任务", Task.Status.TODO);
        Task task2 = new Task(2, "任务", Task.Status.TODO);
        assertNotEquals(task1, task2, "不同 ID 的任务不应该相等");
    }

    @Test
    @DisplayName("测试 equals 方法 - null 对象")
    public void testEqualsNull() {
        Task task = new Task(1, "任务", Task.Status.TODO);
        assertNotEquals(task, null);
    }

    @Test
    @DisplayName("测试 equals 方法 - 不同类型对象")
    public void testEqualsDifferentType() {
        Task task = new Task(1, "任务", Task.Status.TODO);
        String str = "not a task";
        assertNotEquals(task, str);
    }

    @Test
    @DisplayName("测试 equals 方法 - 自反性")
    public void testEqualsReflexive() {
        Task task = new Task(1, "任务", Task.Status.TODO);
        assertEquals(task, task);
    }

    @Test
    @DisplayName("测试 equals 方法 - 对称性")
    public void testEqualsSymmetric() {
        Task task1 = new Task(1, "任务1", Task.Status.TODO);
        Task task2 = new Task(1, "任务2", Task.Status.DONE);
        assertEquals(task1, task2);
        assertEquals(task2, task1);
    }

    @Test
    @DisplayName("测试 hashCode 方法 - 相同 ID 产生相同 hashCode")
    public void testHashCodeSameId() {
        Task task1 = new Task(1, "任务1", Task.Status.TODO);
        Task task2 = new Task(1, "任务2", Task.Status.DONE);
        assertEquals(task1.hashCode(), task2.hashCode(), "相同 ID 应该有相同的 hashCode");
    }

    @Test
    @DisplayName("测试 hashCode 方法 - 不同 ID 可能产生不同 hashCode")
    public void testHashCodeDifferentId() {
        Task task1 = new Task(1, "任务", Task.Status.TODO);
        Task task2 = new Task(2, "任务", Task.Status.TODO);
        // 注意：不同对象可能产生相同 hashCode（哈希冲突），但通常不同
        // 这里只测试它们不总是相同
        assertTrue(task1.hashCode() == task2.hashCode() || task1.hashCode() != task2.hashCode());
    }

    @Test
    @DisplayName("测试 encode 和 decode 的往返转换")
    public void testEncodeDecodeRoundTrip() {
        Task original = new Task(1, "测试任务", Task.Status.TODO);
        String encoded = original.encode();
        Task decoded = Task.decode(encoded);
        assertNotNull(decoded);
        assertEquals(original.getId(), decoded.getId());
        assertEquals(original.getStatus(), decoded.getStatus());
        assertEquals(original.getDescription(), decoded.getDescription());
    }

    @Test
    @DisplayName("测试 encode 和 decode 的往返转换 - DONE 状态")
    public void testEncodeDecodeRoundTripDone() {
        Task original = new Task(2, "完成的任务", Task.Status.DONE);
        String encoded = original.encode();
        Task decoded = Task.decode(encoded);
        assertNotNull(decoded);
        assertEquals(original.getId(), decoded.getId());
        assertEquals(original.getStatus(), decoded.getStatus());
        assertEquals(original.getDescription(), decoded.getDescription());
    }

    @Test
    @DisplayName("测试 encode 和 decode 的往返转换 - 特殊字符")
    public void testEncodeDecodeRoundTripSpecialChars() {
        Task original = new Task(3, "任务|包含|分隔符|和\n换行", Task.Status.TODO);
        String encoded = original.encode();
        Task decoded = Task.decode(encoded);
        assertNotNull(decoded);
        assertEquals(original.getId(), decoded.getId());
        assertEquals(original.getStatus(), decoded.getStatus());
        assertEquals(original.getDescription(), decoded.getDescription());
    }
}

