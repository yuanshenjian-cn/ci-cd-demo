# CI/CD 完整方案与实施指南

## 📋 **项目分析总结**

### 当前技术栈
- **Java 17** + **Spring Boot 3.3.5**
- **MySQL** 数据库 + **Flyway** 数据库迁移
- **Spring Data JPA** + **Bean Validation**
- **22个测试用例**，包含单元测试、控制器测试、集成测试
- 标准的分层架构：Controller → Service → Repository → Model/DTO

---

## 🎯 **CI/CD 流水线设计**

### 分支策略
- **主干开发**：日常开发在 `main` 分支
- **发布分支**：命名以 `release/` 开头，用于发布管理

### 流水线架构

```mermaid
graph TD
    A[开发人员推送代码] --> B{分支类型}
    
    B -->|main分支| C[Main分支流水线]
    B -->|release/*分支| D[Release分支流水线]
    
    C --> C1[Checkout代码]
    C1 --> C2[编译构建]
    C2 --> C3[运行测试]
    C3 --> C4[SonarCloud扫描]
    C4 --> C5[构建Docker镜像]
    C5 --> C6[推送到容器仓库]
    C6 --> C7[自动部署到Dev环境]
    
    D --> D1[Checkout代码]
    D1 --> D2[编译构建]
    D2 --> D3[运行测试]
    D3 --> D4[SonarCloud扫描]
    D4 --> D5[构建Docker镜像]
    D5 --> D6[推送到容器仓库]
    D6 --> D7[手动触发部署到UAT]
    D7 --> D8[手动触发部署到PROD]
    
    C7 --> E1[Railway Dev环境<br/>预配置MySQL数据库]
    D7 --> E2[Railway UAT环境<br/>预配置MySQL数据库]
    D8 --> E3[Railway PROD环境<br/>预配置MySQL数据库]
    
    F[基础设施预配置] --> E1
    F --> E2
    F --> E3
```

### Main 分支流水线
**触发条件**：任何推送到 `main` 分支的提交

**流程**：
1. **Checkout代码** - 检出最新代码
2. **编译构建** - Maven/Gradle 构建项目
3. **运行测试** - 执行所有单元测试和集成测试
4. **SonarCloud扫描** - 代码质量和安全分析
5. **构建Docker镜像** - 构建应用容器镜像
6. **推送镜像** - 推送到容器仓库（Docker Hub/GitHub Registry）
7. **自动部署到Dev** - 自动部署到开发环境

### Release 分支流水线
**触发条件**：任何推送到 `release/*` 分支的提交

**流程**：
1. **Checkout代码** - 检出最新代码
2. **编译构建** - Maven/Gradle 构建项目
3. **运行测试** - 执行所有单元测试和集成测试
4. **SonarCloud扫描** - 代码质量和安全分析
5. **构建Docker镜像** - 构建应用容器镜像
6. **推送镜像** - 推送到容器仓库
7. **手动部署到UAT** - 需要手动触发一键部署
8. **手动部署到PROD** - 需要手动触发一键部署

**关键特性**：
- ✅ **制品晋级**：UAT和PROD使用同一个Docker镜像
- ✅ **手动控制**：部署步骤需要人工审批和触发
- ✅ **环境隔离**：每个环境使用独立的数据库和配置

---

## 🔧 **基础设施服务介绍与Setup指南**

### 1. **Railway 云平台**

#### 📝 **产品介绍**
Railway 是一个现代化的云部署平台，专注于简化应用部署和基础设施管理。

**核心特点**：
- ⚡ **零配置部署**：直接从GitHub仓库部署
- 🔄 **自动扩容**：根据流量自动调整资源
- 🗄️ **内置数据库**：提供PostgreSQL、MySQL、Redis等
- 💰 **简单定价**：按使用量计费，有免费额度
- 🔗 **原生CI/CD**：与GitHub深度集成

#### 💰 **免费额度**
- 每月 **$5** 免费使用额度
- 包含：CPU时间、内存、网络流量、数据库存储
- 足够支撑培训期间的使用需求

#### 🚀 **Railway Setup 指南**

##### Step 1: 注册账号
```bash
1. 访问 https://railway.app
2. 点击 "Sign up"
3. 使用GitHub账号登录（推荐）
4. 验证邮箱地址
```

##### Step 2: 创建项目
```bash
1. 点击 "New Project"
2. 选择 "Empty Project"（我们将手动配置）
3. Railway会自动生成项目名称（如 impartial-perfection）
4. 创建后，进入项目设置重命名为：employee-management-system
```

##### Step 3: 为每个环境创建MySQL数据库
```bash
# Dev环境数据库
1. 在项目中点击 "New Service"
2. 选择 "Database" → "MySQL"
3. 命名：mysql-dev
4. 记录连接信息

# UAT环境数据库
1. 重复上述步骤
2. 命名：mysql-uat

# PROD环境数据库
1. 重复上述步骤
2. 命名：mysql-prod
```

##### Step 4: 获取数据库连接信息
每个MySQL实例创建后，Railway会提供：
```bash
MYSQL_HOST=viaduct.proxy.rlwy.net
MYSQL_PORT=12345
MYSQL_DATABASE=railway
MYSQL_USERNAME=root
MYSQL_PASSWORD=generated_password
MYSQL_URL=mysql://root:password@host:port/database
```

##### Step 5: 创建应用服务
```bash
1. 点击 "New Service"
2. 选择 "GitHub Repo"
3. 连接你的GitHub账号
4. 选择员工管理系统仓库
5. 为不同环境创建不同服务：
   - employee-app-dev
   - employee-app-uat  
   - employee-app-prod
```

##### Step 6: 配置环境变量
为每个应用服务配置对应的环境变量：

**Dev环境变量**：
```bash
SPRING_PROFILES_ACTIVE=dev
MYSQL_HOST=dev数据库host
MYSQL_PORT=dev数据库port
MYSQL_DATABASE=railway
MYSQL_USERNAME=root
MYSQL_PASSWORD=dev数据库密码
```

**UAT环境变量**：
```bash
SPRING_PROFILES_ACTIVE=uat
MYSQL_HOST=uat数据库host
MYSQL_PORT=uat数据库port
MYSQL_DATABASE=railway
MYSQL_USERNAME=root
MYSQL_PASSWORD=uat数据库密码
```

**PROD环境变量**：
```bash
SPRING_PROFILES_ACTIVE=prod
MYSQL_HOST=prod数据库host
MYSQL_PORT=prod数据库port
MYSQL_DATABASE=railway
MYSQL_USERNAME=root
MYSQL_PASSWORD=prod数据库密码
```

---

### 2. **SonarCloud 代码质量平台**

#### 📝 **产品介绍**
SonarCloud 是SonarSource提供的云端代码质量和安全分析服务。

**功能特点**：
- 🐛 **代码质量检测**：Bug、漏洞、代码异味
- 🔒 **安全扫描**：OWASP Top 10、CWE等安全规则
- 📊 **测试覆盖率**：与测试框架集成
- 🚪 **质量门禁**：设置代码质量标准
- 🌐 **多语言支持**：Java、JavaScript、Python等

#### 💰 **免费方案**
- ✅ **开源项目完全免费**
- 🔒 私有项目：每月100,000行代码免费分析
- ♾️ 无限制的公共仓库分析

#### 🚀 **SonarCloud Setup 指南**

##### Step 1: 注册账号
```bash
1. 访问 https://sonarcloud.io
2. 点击 "Sign up for free"
3. 使用GitHub账号登录
4. 授权SonarCloud访问GitHub
```

##### Step 2: 导入项目
```bash
1. 点击 "+" → "Analyze new project"
2. 选择GitHub组织/账号
3. 选择员工管理系统仓库
4. 设置项目密钥（Project Key）：employee-management-system
5. 选择组织（Organization）
```

##### Step 3: 获取Token
```bash
1. 点击右上角头像 → "My Account"
2. 进入 "Security" 标签页
3. 点击 "Generate Tokens"
4. 输入Token名称：github-actions-token
5. 点击 "Generate"
6. 复制并保存Token（稍后在GitHub Secrets中使用）
```

##### Step 4: 配置质量门禁
```bash
1. 进入项目 → "Quality Gates"
2. 创建自定义质量门禁或使用默认配置
3. 建议设置：
   - Coverage: > 80%
   - Duplicated Lines: < 3%
   - Maintainability Rating: A
   - Reliability Rating: A
   - Security Rating: A
```

---

## ⚙️ **GitHub Actions 配置指南**

GitHub Actions是我们CI/CD流水线的核心，负责自动化构建、测试、扫描和部署流程。

### 🔐 **GitHub Secrets 配置**

首先需要在GitHub仓库中配置必要的Secrets，用于存储敏感信息。

#### 配置步骤：
```bash
1. 进入GitHub仓库页面
2. 点击 "Settings" 标签页
3. 在左侧菜单选择 "Secrets and variables" → "Actions"
4. 点击 "New repository secret" 添加以下Secrets
```

#### 必需的Secrets：
```bash
# SonarCloud配置
SONAR_TOKEN=your_sonarcloud_token

# Docker Hub配置（用于镜像存储）
DOCKER_HUB_USERNAME=your_dockerhub_username
DOCKER_HUB_ACCESS_TOKEN=your_dockerhub_token

# Railway配置（用于部署）
RAILWAY_TOKEN=your_railway_token

# Railway服务ID（从Railway控制台获取）
RAILWAY_SERVICE_ID_DEV=your_dev_service_id
RAILWAY_SERVICE_ID_UAT=your_uat_service_id
RAILWAY_SERVICE_ID_PROD=your_prod_service_id

# 数据库连接信息（可选，也可在Railway环境变量中配置）
DB_HOST_DEV=your_dev_db_host
DB_PASSWORD_DEV=your_dev_db_password
# UAT和PROD环境类似...
```

### 📁 **工作流文件结构**

在项目根目录创建以下文件结构：
```
.github/
└── workflows/
    ├── main.yml      # main分支自动部署流水线
    └── release.yml   # release分支手动部署流水线
```

### 🔄 **Main分支工作流 (.github/workflows/main.yml)**

main分支的任何提交都会触发自动部署到Dev环境：

```yaml
name: Main Branch CI/CD

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

env:
  REGISTRY: docker.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: testpassword
          MYSQL_DATABASE: employee_db_test
        ports:
          - 3306:3306
        options: --health-cmd="mysqladmin ping" --health-interval=10s --health-timeout=5s --health-retries=3

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-

    - name: Wait for MySQL
      run: |
        for i in {30..0}; do
          if mysqladmin ping -h"127.0.0.1" --silent; then
            break
          fi
          echo 'MySQL is unavailable - sleeping'
          sleep 1
        done

    - name: Run tests
      run: ./gradlew test
      env:
        SPRING_DATASOURCE_URL: jdbc:mysql://localhost:3306/employee_db_test
        SPRING_DATASOURCE_USERNAME: root
        SPRING_DATASOURCE_PASSWORD: testpassword

    - name: Generate test report
      uses: dorny/test-reporter@v1
      if: success() || failure()
      with:
        name: Maven Tests
        path: build/test-results/test/*.xml
        reporter: java-junit

  sonar:
    needs: test
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      with:
        fetch-depth: 0

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Cache SonarCloud packages
      uses: actions/cache@v4
      with:
        path: ~/.sonar/cache
        key: ${{ runner.os }}-sonar
        restore-keys: ${{ runner.os }}-sonar

    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-

    - name: Build and analyze
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: ./gradlew build sonar --info

  build-and-deploy-dev:
    needs: [test, sonar]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Build application
      run: ./gradlew build -x test

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3

    - name: Log in to Docker Hub
      uses: docker/login-action@v3
      with:
        username: ${{ secrets.DOCKER_HUB_USERNAME }}
        password: ${{ secrets.DOCKER_HUB_ACCESS_TOKEN }}

    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v5
      with:
        images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
        tags: |
          type=ref,event=branch
          type=sha,prefix={{branch}}-

    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        context: .
        push: true
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}
        cache-from: type=gha
        cache-to: type=gha,mode=max

    - name: Deploy to Railway Dev
      uses: bervProject/railway-deploy@v1
      with:
        service: ${{ secrets.RAILWAY_SERVICE_ID_DEV }}
        token: ${{ secrets.RAILWAY_TOKEN }}
```

### 🚀 **Release分支工作流 (.github/workflows/release.yml)**

release分支的提交触发构建，但部署需要手动批准：

```yaml
name: Release Branch CI/CD

on:
  push:
    branches: [ 'release/*' ]

env:
  REGISTRY: docker.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: testpassword
          MYSQL_DATABASE: employee_db_test
        ports:
          - 3306:3306
        options: --health-cmd="mysqladmin ping" --health-interval=10s --health-timeout=5s --health-retries=3

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-

    - name: Wait for MySQL
      run: |
        for i in {30..0}; do
          if mysqladmin ping -h"127.0.0.1" --silent; then
            break
          fi
          echo 'MySQL is unavailable - sleeping'
          sleep 1
        done

    - name: Run tests
      run: ./gradlew test
      env:
        SPRING_DATASOURCE_URL: jdbc:mysql://localhost:3306/employee_db_test
        SPRING_DATASOURCE_USERNAME: root
        SPRING_DATASOURCE_PASSWORD: testpassword

  sonar:
    needs: test
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      with:
        fetch-depth: 0

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Cache SonarCloud packages
      uses: actions/cache@v4
      with:
        path: ~/.sonar/cache
        key: ${{ runner.os }}-sonar
        restore-keys: ${{ runner.os }}-sonar

    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-

    - name: Build and analyze
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: ./gradlew build sonar --info

  build:
    needs: [test, sonar]
    runs-on: ubuntu-latest

    outputs:
      image-tag: ${{ steps.meta.outputs.tags }}

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Build application
      run: ./gradlew build -x test

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3

    - name: Log in to Docker Hub
      uses: docker/login-action@v3
      with:
        username: ${{ secrets.DOCKER_HUB_USERNAME }}
        password: ${{ secrets.DOCKER_HUB_ACCESS_TOKEN }}

    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v5
      with:
        images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
        tags: |
          type=ref,event=branch
          type=sha,prefix={{branch}}-

    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        context: .
        push: true
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}
        cache-from: type=gha
        cache-to: type=gha,mode=max

  deploy-uat:
    needs: build
    runs-on: ubuntu-latest
    environment: 
      name: uat
      url: https://your-uat-app.railway.app
    
    steps:
    - name: Deploy to Railway UAT
      uses: bervProject/railway-deploy@v1
      with:
        service: ${{ secrets.RAILWAY_SERVICE_ID_UAT }}
        token: ${{ secrets.RAILWAY_TOKEN }}

  deploy-prod:
    needs: [build, deploy-uat]
    runs-on: ubuntu-latest
    environment: 
      name: production
      url: https://your-prod-app.railway.app
    
    steps:
    - name: Deploy to Railway Production
      uses: bervProject/railway-deploy@v1
      with:
        service: ${{ secrets.RAILWAY_SERVICE_ID_PROD }}
        token: ${{ secrets.RAILWAY_TOKEN }}
```

### 🔧 **环境保护规则设置**

为了实现手动部署控制，需要在GitHub中设置环境保护规则：

```bash
1. 进入GitHub仓库 → Settings → Environments
2. 创建环境：
   - uat
   - production
3. 为每个环境设置保护规则：
   - Required reviewers: 指定需要审批的人员
   - Wait timer: 可选的等待时间
   - Deployment branches: 限制只能从release分支部署
```

### 📋 **SonarQube配置文件**

在项目根目录创建 `sonar-project.properties`：

```properties
# SonarCloud项目配置
sonar.projectKey=employee-management-system
sonar.organization=your-sonarcloud-org
sonar.projectName=Employee Management System
sonar.projectVersion=1.0

# 源码路径
sonar.sources=src/main/java
sonar.tests=src/test/java

# Java配置
sonar.java.source=17
sonar.java.target=17
sonar.java.binaries=build/classes/java/main
sonar.java.test.binaries=build/classes/java/test

# 测试覆盖率报告
sonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml

# 排除不需要扫描的文件
sonar.exclusions=**/*Test.java,**/*Tests.java,**/test/**,**/build/**
```

### 🛠️ **Gradle构建文件更新**

在 `build.gradle` 中添加SonarQube插件：

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.5'
    id 'io.spring.dependency-management' version '1.1.6'
    id 'org.sonarqube' version '4.4.1.3373'
    id 'jacoco'
}

// SonarQube配置
sonar {
    properties {
        property "sonar.projectKey", "employee-management-system"
        property "sonar.organization", "your-sonarcloud-org"
        property "sonar.host.url", "https://sonarcloud.io"
    }
}

// Jacoco代码覆盖率配置
jacocoTestReport {
    reports {
        xml.required = true
        csv.required = false
        html.required = true
    }
}

test.finalizedBy jacocoTestReport
```

---

## 📊 **完整实施计划**

### Phase 1: 基础设施准备（1-2小时）
- [ ] 注册Railway和SonarCloud账号
- [ ] 在Railway创建3套MySQL数据库（dev/uat/prod）
- [ ] 在Railway创建3个应用服务
- [ ] 配置所有环境变量
- [ ] 获取SonarCloud Token

### Phase 2: 项目配置（1小时）
- [ ] 添加Dockerfile
- [ ] 创建多环境配置文件（application-dev.yml等）
- [ ] 添加sonar-project.properties配置
- [ ] 在GitHub仓库设置Secrets

### Phase 3: CI/CD流水线开发（2-3小时）
- [ ] 创建.github/workflows/main.yml（main分支工作流）
- [ ] 创建.github/workflows/release.yml（release分支工作流）
- [ ] 配置Docker镜像构建和推送
- [ ] 配置Railway部署集成
- [ ] 实现制品晋级策略

### Phase 4: 验证和优化（1小时）
- [ ] 测试main分支自动部署
- [ ] 测试release分支手动部署
- [ ] 验证SonarCloud集成
- [ ] 检查所有环境的应用可用性

---

## 🎓 **培训课程大纲**

### 第一部分：CI/CD理论基础（30分钟）
1. **CI/CD概念介绍**
   - 持续集成（CI）
   - 持续交付（CD）
   - 持续部署（CD）

2. **主干开发模式**
   - 分支策略
   - 代码合并策略
   - 冲突解决

3. **制品晋级概念**
   - 一次构建，多环境部署
   - 环境一致性保证
   - 部署风险降低

### 第二部分：工具链介绍（45分钟）
1. **GitHub Actions**
   - 工作流语法
   - 触发器配置
   - 步骤编排

2. **Railway平台**
   - 服务创建
   - 环境管理
   - 部署策略

3. **SonarCloud**
   - 代码质量指标
   - 质量门禁设置
   - 报告解读

### 第三部分：实践操作（90分钟）
1. **基础设施搭建**（30分钟）
   - Railway账号注册
   - 数据库创建
   - SonarCloud配置

2. **流水线配置**（40分钟）
   - GitHub Actions编写
   - Secrets配置
   - 工作流测试

3. **部署验证**（20分钟）
   - 代码推送测试
   - 环境访问验证
   - 问题排查

### 第四部分：高级特性（30分钟）
1. **手动部署控制**
   - 审批流程
   - 一键部署
   - 回滚策略

2. **监控与告警**
   - 部署状态监控
   - 失败通知
   - 性能监控

---

## 💡 **成本估算**

### Railway费用
```bash
免费额度：$5/月
预估使用：
- 3个MySQL数据库实例：~$3/月
- 3个应用服务：~$2/月
- 总计：$5/月（在免费额度内）
```

### SonarCloud费用
```bash
- 私有仓库：完全免费（< 100k行代码）
- 公共仓库：完全免费
```

### GitHub Actions费用
```bash
- 公共仓库：完全免费
- 私有仓库：每月2000分钟免费
```

**总成本分析**：
- ✅ **完全免费**，非常适合培训使用
- ✅ 无需信用卡，学员零成本学习
- ✅ 企业级工具链体验

---

## 🔗 **相关链接**

### 官方文档
- [Railway Documentation](https://docs.railway.app/)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

### 视频教程
- [Railway部署指南](https://railway.app/learn)
- [SonarCloud集成教程](https://www.sonarqube.org/sonarcloud/)
- [GitHub Actions最佳实践](https://github.com/features/actions)

---

## ❓ **常见问题 FAQ**

### Q1: 为什么Railway不让我自定义项目名称？
**A**: Railway会自动生成项目名称（如 impartial-perfection），这是正常行为。创建项目后，你可以在项目设置中重命名。这样设计是为了简化创建流程和避免名称冲突。

### Q2: Railway免费额度用完了怎么办？
**A**: Railway按使用量计费，$5通常足够培训使用。如需更多资源，可以升级付费计划或优化资源使用。

### Q3: 如何处理数据库迁移？
**A**: Flyway会在应用启动时自动执行数据库迁移脚本，确保数据库结构与代码版本同步。

### Q4: SonarCloud扫描失败怎么办？
**A**: 检查代码质量问题，修复后重新推送。也可以调整质量门禁标准。

### Q5: 部署失败如何排查？
**A**: 查看GitHub Actions日志和Railway部署日志，通常是环境变量配置或网络连接问题。

### Q6: 如何回滚到之前版本？
**A**: 在Railway控制台可以选择之前的部署版本进行回滚，或者在GitHub上revert代码后重新部署。

---

## 📝 **实施清单**

### 准备阶段
- [ ] GitHub仓库已创建并包含完整代码
- [ ] Railway账号已注册
- [ ] SonarCloud账号已注册
- [ ] 本地开发环境可正常运行项目

### 基础设施配置
- [ ] Railway MySQL数据库（dev/uat/prod）已创建
- [ ] Railway应用服务已创建
- [ ] 环境变量已正确配置
- [ ] SonarCloud项目已导入
- [ ] SonarCloud Token已获取

### 项目配置文件
- [ ] Dockerfile已添加
- [ ] 多环境配置文件已创建
- [ ] sonar-project.properties已配置
- [ ] GitHub Secrets已设置

### CI/CD流水线
- [ ] main分支工作流已创建并测试
- [ ] release分支工作流已创建并测试
- [ ] 制品晋级策略已实现
- [ ] 手动部署控制已配置

### 验收测试
- [ ] 推送main分支代码，验证自动部署到dev环境
- [ ] 创建release分支，验证构建流程
- [ ] 手动触发UAT部署，验证功能
- [ ] 手动触发PROD部署，验证最终效果
- [ ] SonarCloud报告正常显示
- [ ] 所有环境应用可正常访问

---

**本指南将帮助你完成从零开始搭建企业级CI/CD流水线，为软件开发技能培训提供完整的实践环境。**
