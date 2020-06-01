FROM clojure:openjdk-11-lein AS build

RUN apt-get update -yq && apt-get upgrade -yq && \
apt-get install -yq curl git nano

RUN curl -sL https://deb.nodesource.com/setup_12.x |  bash - && \
apt-get install -yq nodejs build-essential
RUN npm install -g npm

COPY . /reading-list
WORKDIR "/reading-list"
ENV PORT 3000
EXPOSE 3000
RUN lein uberjar

FROM openjdk:11.0.6-jre-slim AS prod
ENV PORT 3000
EXPOSE 3000
COPY --from=build /reading-list/target/reading-list.jar /reading-list.jar
CMD ["java", "-jar", "reading-list.jar"]
