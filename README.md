# java-netsign
模拟签名服务器
接口包括：生成P10（sm2），签名，验签，上传证书，哈希，删除密钥对，清空signCount和verifyCount

### 编译镜像（optional,可以使用docker-compose.yaml中的默认镜像）

```bash
# 编译数据库
make db-docker
# 编译 netsign
make docker
```

### docker 启动

```bash
# 修改1. applocation.yml 中的 ip 和 端口
# 修改2. docker-compose.yaml 中的 ip
cd script/docker
docker-compose up -d
```

### 清理 docker 环境

```bash
cd script/docker
docker-compose down
rm -rf ./netsign-data
```
