DOCKER_DB=cncc/netsign-db
DOCKER_TAG=cncc/netsign

# DB 镜像
db-docker:
	docker build --platform=linux/amd64 -t ${DOCKER_DB} -f ./script/docker/Dockerfile.netsigndb .
.PHONY: db-docker

# brilliance-ca 镜像
docker:
	docker build --platform=linux/amd64 -t ${DOCKER_TAG} -f ./script/docker/Dockerfile .
.PHONY: docker