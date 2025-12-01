FROM mariadb:10.7

ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=fullstack

COPY init.sql /docker-entrypoint-initdb.d/
