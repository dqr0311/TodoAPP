# TodoAPP

基于命令行的待办事项管理应用，使用 Java 开发。

## 运行

### 直接编译运行
```bash
javac src/main/java/*.java
java -cp src/main/java TodoApp
```

### 使用 Maven
```bash
mvn compile
mvn exec:java -Dexec.mainClass="TodoApp"
```

### 运行测试
```bash
mvn test
```

## 依赖

- **运行时**: 无外部依赖，仅使用 Java 标准库
- **测试**: JUnit 5（通过 Maven 管理）

## 项目结构

```
src/
├── main/java/
│   ├── Task.java
│   ├── TaskStore.java
│   └── TodoApp.java
└── test/java/
    ├── TaskTest.java
    └── TaskStoreTest.java
```

数据文件：`tasks.txt`（运行后自动生成）
