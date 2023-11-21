#syntax=docker/dockerfile:1
FROM ubuntu:20.04
LABEL maintainer="Taka Obsid"
COPY . /code
WORKDIR /code
ARG DEBIAN_FRONTEND=noninteractive
RUN apt update && apt -y install clojure leiningen
RUN cd /code
RUN lein install
