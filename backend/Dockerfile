# ============================================================
# T47 AI 订单异常识别助手 - 后端镜像
# 构建（在 backend 目录、先执行 mvn clean package）:
#   docker build -t t47-order-anomaly:1.0.0 .
# 运行:
#   docker run -d -p 8080:8080 \
#     -e DB_URL="jdbc:mysql://host.docker.internal:3306/t47_anomaly?..." \
#     -e DB_USERNAME=root -e DB_PASSWORD=*** -e JWT_SECRET=*** \
#     t47-order-anomaly:1.0.0
# 说明: 数据库与 JWT 密钥全部通过环境变量注入，镜像内不含任何密钥
# ============================================================
FROM eclipse-temurin:21-jre

LABEL maintainer="T47 Team" \
      description="AI 订单异常识别助手 - 后端服务"

WORKDIR /app

# 只拷贝可执行 jar，减小镜像体积
COPY target/t47-order-anomaly.jar app.jar

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

# 健康检查接口：容器探针访问 GET /api/health
# 注意：eclipse-temurin 基础镜像不含 curl，如需 HEALTHCHECK 可改用
#   HEALTHCHECK --interval=30s --timeout=3s CMD ["java","-version"]
# 或换成自带 curl/wget 的基础镜像
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
