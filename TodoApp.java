import java.util.List;
import java.util.Scanner;

public class TodoApp {
    private static final String DATA_FILE = "tasks.txt";

    public static void main(String[] args) {
        TaskStore store = new TaskStore(DATA_FILE);
        store.load(); // 启动时加载

        Scanner sc = new Scanner(System.in);
        System.out.println("欢迎使用 Terminal To-Do 应用！");

        while (true) {
            printMenu();
            System.out.print("请选择操作：");
            String choice = sc.nextLine().trim();

            if (!choice.matches("\\d+")) {
                System.out.println("无效输入：请输入数字。");
                continue;
            }

            int op = Integer.parseInt(choice);
            switch (op) {
                case 1: // 添加任务
                    System.out.print("请输入任务描述：");
                    String desc = sc.nextLine().trim();
                    while (desc.isEmpty()) {
                        System.out.println("描述不能为空，请重新输入：");
                        desc = sc.nextLine().trim();
                    }
                    Task t = store.add(desc);
                    System.out.println("已添加：" + t);
                    break;

                case 2: // 列出全部
                    printTasks(store.listAll());
                    break;

                case 3: // 仅列出 TODO
                    printTasks(store.listByStatus(Task.Status.TODO));
                    break;

                case 4: // 仅列出 DONE
                    printTasks(store.listByStatus(Task.Status.DONE));
                    break;

                case 5: // 标记为 DONE
                    mark(sc, store, Task.Status.DONE);
                    break;

                case 6: // 标记为 TODO
                    mark(sc, store, Task.Status.TODO);
                    break;

                case 7: // 删除
                    System.out.print("请输入要删除的任务ID：");
                    Integer idDel = readInt(sc);
                    if (idDel == null) break;
                    if (store.delete(idDel)) System.out.println("已删除。");
                    else System.out.println("未找到该ID。");
                    break;

                case 8: // 退出并保存
                    store.save(); // 退出时保存
                    System.out.println("已保存到 " + DATA_FILE + "，再见！");
                    return;

                default:
                    System.out.println("无效选项，请重试。");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== 菜单 ===");
        System.out.println("1) 添加任务");
        System.out.println("2) 列出全部任务");
        System.out.println("3) 列出 TODO");
        System.out.println("4) 列出 DONE");
        System.out.println("5) 将某任务标记为 DONE");
        System.out.println("6) 将某任务标记为 TODO");
        System.out.println("7) 删除任务");
        System.out.println("8) 退出并保存");
    }

    private static void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("（空空如也）");
            return;
        }
        for (Task t : tasks) System.out.println(t);
    }

    private static void mark(Scanner sc, TaskStore store, Task.Status st) {
        System.out.print("请输入任务ID：");
        Integer id = readInt(sc);
        if (id == null) return;
        boolean ok = store.markStatus(id, st);
        if (ok) System.out.println("已更新。");
        else System.out.println("未找到该ID。");
    }

    private static Integer readInt(Scanner sc) {
        String s = sc.nextLine().trim();
        if (!s.matches("-?\\d+")) {
            System.out.println("请输入整数ID。");
            return null;
        }
        return Integer.parseInt(s);
    }
}

