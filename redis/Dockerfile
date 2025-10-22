FROM redis:6-alpine

# 复制自定义配置文件
COPY redis.conf /usr/local/etc/redis/redis.conf

# 创建数据目录
RUN mkdir -p /data && chown redis:redis /data

# 暴露端口
EXPOSE 6379

# 使用自定义配置启动Redis
CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]