#!/bin/bash

# installation configuration
SRC_PATH=/home/resources
NGINX_VERSION=1.12.2
JAVA_VERSION=17.0.1
NGINX_BIN_PATH=/usr/local/nginx

mkdir -p ${SRC_PATH}

# installation dependence
yum install -y pcre-devel zlib-devel openssl-devel wget gcc
# download nginx source package
cd ${SRC_PATH} || exit
wget http://nginx.org/download/nginx-${NGINX_VERSION}.tar.gz
# unzip source package
tar -xzvf nginx-${NGINX_VERSION}.tar.gz
cd ./nginx-${NGINX_VERSION} || exit
# install nginx
./configure --prefix=${NGINX_BIN_PATH} --with-http_ssl_module
make & make install

cd ${SRC_PATH} || exit
rpm -ivh jdk-${JAVA_VERSION}_linux-x64_bin.rpm

sudo dnf install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-8-x86_64/pgdg-redhat-repo-latest.noarch.rpm
sudo dnf -qy module disable postgresql
sudo dnf install -y postgresql14-server
sudo /usr/pgsql-14/bin/postgresql-14-setup initdb
sudo systemctl enable postgresql-14
sudo systemctl start postgresql-14
# END