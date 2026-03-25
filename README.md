# ai-test-automation-framework



## 🚀 How to Run the Framework from Command Line

Follow the steps below to build and execute the framework using a thin JAR.

---

### 🧱 Step 1: Build the JAR (Skip Tests)

```bash
mvn clean package -DskipTests
```

---

### 📦 Step 2: Copy All Dependencies

```bash
mvn dependency:copy-dependencies
```

This will copy all required JARs into:

```
target/dependency/
```

---

### ▶️ Step 3: Run the Framework

#### Windows:

```bash
java -cp "target/ai-test-automation-framework-1.0.0.jar;target/dependency/*" com.yourcompany.automation.framework.runner.JsonTestRunner src/main/resources/test-config.json```

#### Linux/Mac:

```bash
java -cp "target/ai-test-automation-framework-1.0.0.jar:target/dependency/*" com.yourcompany.automation.framework.runner.JsonTestRunner test-config.json
```

---

### ⚠️ Notes

* Ensure `test-config.json` path is correct
* Use full path if file is not in project root
* All dependencies must be present in `target/dependency/`

---

### ✅ Example

```bash
java -cp "target/ai-test-automation-framework-1.0.0.jar;target/dependency/*" com.yourcompany.automation.framework.runner.JsonTestRunner src/main/resources/test-config.json
```


### ✅ Client must add the following entries in the following project

<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-launcher</artifactId>
</dependency>

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
</dependency>